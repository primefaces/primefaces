/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
import java.util.List;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class JPALazyDataModelTest {

    @BeforeEach
    void setUp() {
        new FacesContextMock();
    }

    @Test
    void countExecutesSingleQuery() {
        Fixture fixture = createFixture(null);

        int count = fixture.model.count(Collections.emptyMap());

        assertEquals(5, count);
        Mockito.verify(fixture.countTypedQuery, Mockito.times(1)).getSingleResult();
    }

    @Test
    void loadExecutesSingleQuery() {
        Fixture fixture = createFixture(null);

        List<TestEntity> result = fixture.model.load(0, 3, Collections.emptyMap(), Collections.emptyMap());

        assertEquals(3, result.size());
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).setFirstResult(0);
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).setMaxResults(3);
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).getResultList();
    }

    @Test
    void getRowDataWithoutConverterQueriesByRowKey() {
        Fixture fixture = createFixture(null);

        TestEntity entity = fixture.model.getRowData("1");

        assertNotNull(entity);
        assertEquals("1", entity.getId());
        Mockito.verify(fixture.entityManager, Mockito.times(1)).createQuery(fixture.entityQuery);
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).getSingleResult();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRowDataWithConverterDoesNotQueryDatabase() {
        Fixture fixture = createFixture(new TestEntityConverter());

        TestEntity entity = fixture.model.getRowData("1");

        assertNotNull(entity);
        assertEquals("1", entity.getId());
        Mockito.verify(fixture.entityManager, Mockito.never()).createQuery(Mockito.any(CriteriaQuery.class));
        Mockito.verify(fixture.criteriaBuilder, Mockito.never()).createQuery(Mockito.any(Class.class));
    }

    @SuppressWarnings("unchecked")
    private static Fixture createFixture(Converter<TestEntity> converter) {
        JPALazyDataModel<TestEntity> model = new JPALazyDataModel<>();
        EntityManager em = Mockito.mock(EntityManager.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<Long> countQuery = Mockito.mock(CriteriaQuery.class);
        CriteriaQuery<TestEntity> entityQuery = Mockito.mock(CriteriaQuery.class);
        Root<TestEntity> root = Mockito.mock(Root.class);
        TypedQuery<Long> countTypedQuery = Mockito.mock(TypedQuery.class);
        TypedQuery<TestEntity> entityTypedQuery = Mockito.mock(TypedQuery.class);
        Expression<Long> countExpression = Mockito.mock(Expression.class);
        Path<Object> idPath = Mockito.mock(Path.class);
        Predicate predicate = Mockito.mock(Predicate.class);

        Mockito.when(em.getCriteriaBuilder()).thenReturn(cb);
        Mockito.when(cb.createQuery(Long.class)).thenReturn(countQuery);
        Mockito.when(cb.createQuery(TestEntity.class)).thenReturn(entityQuery);
        Mockito.when(countQuery.from(TestEntity.class)).thenReturn(root);
        Mockito.when(entityQuery.from(TestEntity.class)).thenReturn(root);
        Mockito.when(cb.count(root)).thenReturn(countExpression);
        Mockito.when(countQuery.select(countExpression)).thenReturn(countQuery);
        Mockito.when(entityQuery.select(root)).thenReturn(entityQuery);
        Mockito.when(root.get("id")).thenReturn(idPath);
        Mockito.when(cb.equal(idPath, "1")).thenReturn(predicate);
        Mockito.when(entityQuery.where(predicate)).thenReturn(entityQuery);
        Mockito.when(em.createQuery(countQuery)).thenReturn(countTypedQuery);
        Mockito.when(em.createQuery(entityQuery)).thenReturn(entityTypedQuery);
        Mockito.when(countTypedQuery.getSingleResult()).thenReturn(5L);
        Mockito.when(entityTypedQuery.getResultList()).thenReturn(Arrays.asList(
                new TestEntity("1", "a"),
                new TestEntity("2", "b"),
                new TestEntity("3", "c")));
        Mockito.when(entityTypedQuery.getSingleResult()).thenReturn(new TestEntity("1", "a"));

        JPALazyDataModel.Builder<TestEntity, JPALazyDataModel<TestEntity>> builder =
                new JPALazyDataModel.Builder<>(model)
                        .entityClass(TestEntity.class)
                        .entityManager(() -> em);
        if (converter != null) {
            builder.rowKeyConverter(converter);
        }
        else {
            builder.rowKeyField("id")
                    .rowKeyType(String.class)
                    .rowKeyProvider(entity -> entity.getId());
        }
        builder.build();

        return new Fixture(model, em, cb, countTypedQuery, entityQuery, entityTypedQuery);
    }

    private static class Fixture {
        final JPALazyDataModel<TestEntity> model;
        final EntityManager entityManager;
        final CriteriaBuilder criteriaBuilder;
        final TypedQuery<Long> countTypedQuery;
        final CriteriaQuery<TestEntity> entityQuery;
        final TypedQuery<TestEntity> entityTypedQuery;

        Fixture(JPALazyDataModel<TestEntity> model, EntityManager entityManager, CriteriaBuilder criteriaBuilder,
                TypedQuery<Long> countTypedQuery,
                CriteriaQuery<TestEntity> entityQuery, TypedQuery<TestEntity> entityTypedQuery) {
            this.model = model;
            this.entityManager = entityManager;
            this.criteriaBuilder = criteriaBuilder;
            this.countTypedQuery = countTypedQuery;
            this.entityQuery = entityQuery;
            this.entityTypedQuery = entityTypedQuery;
        }
    }

    public static class TestEntity {
        private String id;
        private String name;

        public TestEntity() {
        }

        public TestEntity(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class TestEntityConverter implements Converter<TestEntity> {

        @Override
        public String getAsString(FacesContext context, UIComponent component, TestEntity value) {
            return value == null ? null : value.getId();
        }

        @Override
        public TestEntity getAsObject(FacesContext context, UIComponent component, String value) {
            return value == null ? null : new TestEntity(value, "name-" + value);
        }
    }
}
