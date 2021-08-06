/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.primefaces.util.LangUtils;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import org.primefaces.util.BeanUtils;
import org.primefaces.util.Lazy;

public class JpaLazyDataModel<T> extends LazyDataModel<T> {

    private final Class<T> entityClass;
    private final Supplier<EntityManager> entityManager;
    private Lazy<Field> rowKeyField;
    private Lazy<Method> rowKeyGetter;

    public JpaLazyDataModel(Class<T> entityClass, Supplier<EntityManager> entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    public JpaLazyDataModel(Class<T> entityClass, Supplier<EntityManager> entityManager, String rowKeyField) {
        this(entityClass, entityManager);
        this.rowKeyField = new Lazy<>(() -> LangUtils.getField(entityClass, rowKeyField));
        this.rowKeyGetter = new Lazy<>(() -> {
            try {
                return new PropertyDescriptor(rowKeyField, entityClass).getReadMethod();
            }
            catch (IntrospectionException e) {
                throw new RuntimeException("Could not access " + rowKeyField + " on " + entityClass.getName(), e);
            }
        });
    }

    public JpaLazyDataModel(Class<T> entityClass, Supplier<EntityManager> entityManager, Converter<T> converter) {
        super(converter);
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        setPageSize(pageSize);

        EntityManager entityManager = this.entityManager.get();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq = cq.select(root);

        if (filterBy != null && !filterBy.isEmpty()) {
            applyFilters(cb, cq, root, filterBy);
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            applySort(cb, cq, root, sortBy);
        }

        TypedQuery<T> query = entityManager.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        List<T> result = query.getResultList();

        setRowCount(result.size());

        return result;
    }

    protected void applyFilters(CriteriaBuilder cb,
                                CriteriaQuery<T> cq,
                                Root<T> root,
                                Map<String, FilterMeta> filterBy) {

        List<Predicate> predicates = new ArrayList<>();
        for (FilterMeta filter : filterBy.values()) {
            if (filter.getField() == null || filter.getFilterValue() == null) {
                continue;
            }

            String filterValue = filter.getFilterValue().toString();
            Field filterField = LangUtils.getField(entityClass, filter.getField());
            Object convertedFilterValue = convertToType(filterValue, filterField.getType());
            Predicate predicate = createPredicate(filter, filterField, root, cb, (Comparable) convertedFilterValue);
            predicates.add(predicate);
        }

        if (predicates.size() > 0) {
            cq.where(
                cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }
    }

    protected <F extends Comparable> Predicate createPredicate(FilterMeta filter, Field filterField, Root<T> root, CriteriaBuilder cb, F filterValue) {
        Expression field = root.get(filter.getField());
        Lazy<Expression<String>> fieldAsString = new Lazy(() -> field.as(String.class));

        switch (filter.getMatchMode()) {
            case STARTS_WITH:
                return cb.like(fieldAsString.get(), filterValue.toString() + "%");
            case ENDS_WITH:
                return cb.like(fieldAsString.get(), "%" + filterValue);
            case CONTAINS:
                return cb.like(fieldAsString.get(), "%" + filterValue + "%");
            case EXACT:
                return cb.equal(field, filterValue);
            case LESS_THAN:
                return cb.lessThan(field, filterValue);
            case LESS_THAN_EQUALS:
                return cb.lessThanOrEqualTo(field, filterValue);
            case GREATER_THAN:
                return cb.greaterThan(field, filterValue);
            case GREATER_THAN_EQUALS:
                return cb.greaterThanOrEqualTo(field, filterValue);
            case EQUALS:
                return cb.equal(cb.lower(field), filterValue);
            case IN:
                break;
            case RANGE:
                break;
            case GLOBAL:
                break;
        }

        return null;
    }

    protected void applySort(CriteriaBuilder cb,
                             CriteriaQuery<T> cq,
                             Root<T> root,
                             Map<String, SortMeta> sortBy) {

        for (SortMeta sort : sortBy.values()) {
            if (sort.getField() == null || sort.getOrder() == SortOrder.UNSORTED) {
                continue;
            }

            Expression<String> field = root.get(sort.getField()).as(String.class);
            cq.orderBy(sort.getOrder() == SortOrder.ASCENDING ? cb.asc(field) : cb.desc(field));
        }
    }

    @Override
    public T getRowData(String rowKey) {
        Converter converter = getConverter();
        if (converter != null) {
            return super.getRowData(rowKey);
        }

        if (rowKeyField != null) {
            Object convertedRowKey = convertToType(rowKey, rowKeyField.get().getType());

            EntityManager entityManager = this.entityManager.get();

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root).where(criteriaBuilder.equal(root.get(rowKeyField.get().getName()), convertedRowKey));

            TypedQuery<T> query = entityManager.createQuery(cq);
            return query.getSingleResult();
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a Converter or rowKeyField via constructor or implement getRowData(String rowKey) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    @Override
    public String getRowKey(T object) {
        Converter converter = getConverter();
        if (converter != null) {
            return super.getRowKey(object);
        }

        if (rowKeyGetter != null) {
            try {
                Object rowKey = rowKeyGetter.get().invoke(object);
                return rowKey == null ? null : rowKey.toString();
            }
            catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Could not invoke getter for " + rowKeyField.get().getName() + " on " + entityClass.getName(), e);
            }
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a Converter or rowKeyField via constructor or implement getRowKey(T object) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    protected <V> V convertToType(String value, Class<V> valueType) {
        FacesContext context = FacesContext.getCurrentInstance();
        Converter<V> converter = context.getApplication().createConverter(valueType);
        if (converter != null) {
            return converter.getAsObject(context, UIComponent.getCurrentComponent(context), value);
        }

        if (valueType == String.class) {
            return (V) value;
        }

        if (BeanUtils.isPrimitiveOrPrimitiveWrapper(valueType)) {
            return (V) FacesContext.getCurrentInstance().getELContext().convertToType(value, valueType);
        }

        throw new RuntimeException("Can not convert " + value
            + " to type "
            + valueType
            + " currently not supported; please overwrite Object convertToType(String value, Class<?> valueType)!");
    }

}
