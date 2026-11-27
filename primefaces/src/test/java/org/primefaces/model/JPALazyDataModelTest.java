/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.model;

import org.primefaces.mock.FacesContextMock;

import java.util.Arrays;
import java.util.Collections;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JPALazyDataModelTest {

    @BeforeEach
    void setUp() {
        new FacesContextMock();
    }

    @Test
    void countResolvesCreateQueryForAvailableJpaVersion() {
        Fixture fixture = createMocksForCountWithFilters();

        assertEquals(5, fixture.model.count(Collections.emptyMap()));

        Mockito.verify(fixture.entityManager).createQuery(fixture.countQuery);
        Mockito.verify(fixture.countQuery, Mockito.never()).where(Mockito.any(Expression.class));
    }

    @Test
    void filtersAreCombinedWithBinaryAnd() {
        // CriteriaBuilder#and(Predicate...) was removed in Jakarta Persistence 4.0,
        // whereas and(Expression, Expression) exists in all versions
        Predicate p1 = Mockito.mock(Predicate.class);
        Predicate p2 = Mockito.mock(Predicate.class);
        Predicate p3 = Mockito.mock(Predicate.class);
        Predicate p1AndP2 = Mockito.mock(Predicate.class);
        Predicate all = Mockito.mock(Predicate.class);
        Fixture fixture = createMocksForCountWithFilters(p1, p2, p3);
        Mockito.when(fixture.criteriaBuilder.and(p1, p2)).thenReturn(p1AndP2);
        Mockito.when(fixture.criteriaBuilder.and(p1AndP2, p3)).thenReturn(all);

        assertEquals(5, fixture.model.count(Collections.emptyMap()));

        Mockito.verify(fixture.countQuery).where(all);
        Mockito.verify(fixture.criteriaBuilder, Mockito.never()).and(Mockito.<Predicate[]>any());
    }

    @Test
    void singleFilterIsAppliedWithoutAnd() {
        Predicate p1 = Mockito.mock(Predicate.class);
        Fixture fixture = createMocksForCountWithFilters(p1);

        assertEquals(5, fixture.model.count(Collections.emptyMap()));

        Mockito.verify(fixture.countQuery).where(p1);
        Mockito.verify(fixture.criteriaBuilder, Mockito.never()).and(Mockito.any(), Mockito.any());
        Mockito.verify(fixture.criteriaBuilder, Mockito.never()).and(Mockito.<Predicate[]>any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void createTypedQueryRethrowsPersistenceExceptionsUnwrapped() {
        Fixture fixture = createMocksForCountWithFilters();
        CriteriaQuery<TestEntity> entityQuery = Mockito.mock(CriteriaQuery.class);
        Mockito.when(fixture.entityManager.createQuery(entityQuery)).thenThrow(new IllegalArgumentException("invalid query"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fixture.model.createTypedQuery(fixture.entityManager, entityQuery));
        assertEquals("invalid query", ex.getMessage());
    }

    @SuppressWarnings("unchecked")
    private static Fixture createMocksForCountWithFilters(Predicate... filters) {
        EntityManager em = Mockito.mock(EntityManager.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Root<TestEntity> root = Mockito.mock(Root.class);
        Mockito.when(em.getCriteriaBuilder()).thenReturn(cb);

        CriteriaQuery<Long> countQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = Mockito.mock(TypedQuery.class);
        Expression<Long> countExpression = Mockito.mock(Expression.class);
        Mockito.when(cb.createQuery(Long.class)).thenReturn(countQuery);
        Mockito.when(countQuery.from(TestEntity.class)).thenReturn(root);
        Mockito.when(cb.count(root)).thenReturn(countExpression);
        Mockito.when(countQuery.select(countExpression)).thenReturn(countQuery);
        Mockito.when(em.createQuery(countQuery)).thenReturn(countTypedQuery);
        Mockito.when(countTypedQuery.getSingleResult()).thenReturn(5L);

        JPALazyDataModel<TestEntity> model = new JPALazyDataModel<>();
        new JPALazyDataModel.Builder<>(model)
                .entityClass(TestEntity.class)
                .entityManager(() -> em)
                .rowKeyField("id")
                .rowKeyType(String.class)
                .rowKeyProvider(TestEntity::getId)
                .filterEnricher((filterBy, criteriaBuilder, cq, r, predicates) -> predicates.addAll(Arrays.asList(filters)))
                .build();
        return new Fixture(model, em, cb, countQuery);
    }

    private static class Fixture {
        final JPALazyDataModel<TestEntity> model;
        final EntityManager entityManager;
        final CriteriaBuilder criteriaBuilder;
        final CriteriaQuery<Long> countQuery;

        Fixture(JPALazyDataModel<TestEntity> model, EntityManager entityManager, CriteriaBuilder criteriaBuilder,
                CriteriaQuery<Long> countQuery) {
            this.model = model;
            this.entityManager = entityManager;
            this.criteriaBuilder = criteriaBuilder;
            this.countQuery = countQuery;
        }
    }

    public static class TestEntity {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
