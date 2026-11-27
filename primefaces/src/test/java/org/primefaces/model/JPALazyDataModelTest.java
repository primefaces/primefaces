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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

class JPALazyDataModelTest {

    @BeforeEach
    void setUp() {
        new FacesContextMock();
    }

    @Test
    @SuppressWarnings("unchecked")
    void overriddenHooksReplaceVersionSpecificJpaCalls() {
        // Simulates a subclass for Jakarta Persistence 4.0, where EntityManager#createQuery(CriteriaQuery)
        // and CriteriaBuilder#and(Predicate...) no longer exist.
        EntityManager em = Mockito.mock(EntityManager.class);
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Root<TestEntity> root = Mockito.mock(Root.class);
        Mockito.when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);

        CriteriaQuery<Long> countQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = Mockito.mock(TypedQuery.class);
        Expression<Long> countExpression = Mockito.mock(Expression.class);
        Mockito.when(criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        Mockito.when(countQuery.from(TestEntity.class)).thenReturn(root);
        Mockito.when(criteriaBuilder.count(root)).thenReturn(countExpression);
        Mockito.when(countQuery.select(countExpression)).thenReturn(countQuery);
        Mockito.when(countTypedQuery.getSingleResult()).thenReturn(7L);

        CriteriaQuery<TestEntity> entityQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<TestEntity> entityTypedQuery = Mockito.mock(TypedQuery.class);
        Mockito.when(criteriaBuilder.createQuery(TestEntity.class)).thenReturn(entityQuery);
        Mockito.when(entityQuery.from(TestEntity.class)).thenReturn(root);
        Mockito.when(entityQuery.select(root)).thenReturn(entityQuery);
        Mockito.when(entityTypedQuery.getResultList()).thenReturn(Collections.singletonList(new TestEntity("1")));

        Predicate filterPredicate = Mockito.mock(Predicate.class);
        Predicate combinedPredicate = Mockito.mock(Predicate.class);
        List<CriteriaQuery<?>> createdQueries = new ArrayList<>();
        List<List<Predicate>> combinedPredicates = new ArrayList<>();

        JPALazyDataModel<TestEntity> model = new JPALazyDataModel<TestEntity>() {
            @Override
            protected <R> TypedQuery<R> createTypedQuery(EntityManager em, CriteriaQuery<R> cq) {
                createdQueries.add(cq);
                return (TypedQuery<R>) (cq == countQuery ? countTypedQuery : entityTypedQuery);
            }

            @Override
            protected Predicate and(CriteriaBuilder cb, List<Predicate> predicates) {
                combinedPredicates.add(new ArrayList<>(predicates));
                return combinedPredicate;
            }
        };
        new JPALazyDataModel.Builder<>(model)
                .entityClass(TestEntity.class)
                .entityManager(() -> em)
                .rowKeyField("id")
                .rowKeyType(String.class)
                .rowKeyProvider(TestEntity::getId)
                .filterEnricher((filterBy, cb, cq, r, predicates) -> predicates.add(filterPredicate))
                .build();

        assertEquals(7, model.count(Collections.emptyMap()));
        assertEquals(1, model.load(0, 10, Collections.emptyMap(), Collections.emptyMap()).size());

        assertEquals(Arrays.asList(countQuery, entityQuery), createdQueries);
        assertEquals(Arrays.asList(Collections.singletonList(filterPredicate), Collections.singletonList(filterPredicate)),
                combinedPredicates);
        Mockito.verify(countQuery).where(combinedPredicate);
        Mockito.verify(entityQuery).where(combinedPredicate);
        Mockito.verify(em, Mockito.never()).createQuery(Mockito.any(CriteriaQuery.class));
        Mockito.verify(criteriaBuilder, Mockito.never()).and(Mockito.<Predicate[]>any());
    }

    public static class TestEntity {
        private String id;

        public TestEntity() {
        }

        public TestEntity(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
