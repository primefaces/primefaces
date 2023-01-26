/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import org.primefaces.util.BeanUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.SerializableSupplier;

/**
 * Basic {@link LazyDataModel} implementation with JPA and Criteria API.
 *
 * @param <T> The model class.
 */
public class JpaLazyDataModel<T> extends LazyDataModel<T> implements Serializable {

    private static final Logger LOG = Logger.getLogger(JpaLazyDataModel.class.getName());

    protected Class<T> entityClass;
    protected SerializableSupplier<EntityManager> entityManager;
    protected String rowKeyField;

    private transient Lazy<Method> rowKeyGetter;

    /**
     * For serialization only
     */
    public JpaLazyDataModel() {

    }

    /**
     * Constructs a JpaLazyDataModel for usage without enabled selection.
     *
     * @param entityClass The entity class
     * @param entityManager The {@link EntityManager}
     */
    public JpaLazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    /**
     * Constructs a JpaLazyDataModel with selection support.
     *
     * @param entityClass The entity class
     * @param entityManager The {@link EntityManager}
     * @param rowKeyField The name of the rowKey property (e.g. "id")
     */
    public JpaLazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager, String rowKeyField) {
        this(entityClass, entityManager);
        this.rowKeyField = rowKeyField;
    }

    /**
     * Constructs a JpaLazyDataModel with selection support, with an already existing {@link Converter}.
     *
     * @param entityClass The entity class
     * @param entityManager The {@link EntityManager}
     * @param converter The converter, which will be used for converting the entity to a rowKey and vice versa
     */
    public JpaLazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager, Converter converter) {
        super(converter);
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        EntityManager em = this.entityManager.get();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq = cq.select(cb.count(root));

        applyFilters(cb, cq, root, filterBy);

        return em.createQuery(cq).getSingleResult().intValue();
    }

    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        EntityManager em = this.entityManager.get();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq = cq.select(root);

        applyFilters(cb, cq, root, filterBy);
        applySort(cb, cq, root, sortBy);

        TypedQuery<T> query = em.createQuery(cq);
        query.setFirstResult(first);
        query.setMaxResults(pageSize);

        List<T> result = query.getResultList();

        return result;
    }

    protected void applyFilters(CriteriaBuilder cb,
                                CriteriaQuery<?> cq,
                                Root<T> root,
                                Map<String, FilterMeta> filterBy) {

        List<Predicate> predicates = new ArrayList<>();

        applyGlobalFilters(filterBy, cb, cq, root, predicates);

        if (filterBy != null) {
            for (FilterMeta filter : filterBy.values()) {
                if (filter.getField() == null || filter.getFilterValue() == null || filter.isGlobalFilter()) {
                    continue;
                }

                Field filterField = LangUtils.getFieldRecursive(entityClass, filter.getField());
                Object filterValue = filter.getFilterValue();
                Object convertedFilterValue;

                Class<?> filterValueClass = filterValue.getClass();
                if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                    convertedFilterValue = filterValue;
                }
                else {
                    convertedFilterValue = convertToType(filterValue, filterField.getType());
                }

                Expression fieldExpression = resolveFieldExpression(cb, cq, root, filter.getField());

                Predicate predicate = createPredicate(filter, filterField, root, cb, fieldExpression, convertedFilterValue);
                predicates.add(predicate);
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(
                cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }
    }

    protected void applyGlobalFilters(Map<String, FilterMeta> filterBy, CriteriaBuilder cb, CriteriaQuery<?> cq,
            Root<T> root, List<Predicate> predicates) {

    }

    protected Predicate createPredicate(FilterMeta filter, Field filterField,
            Root<T> root, CriteriaBuilder cb, Expression fieldExpression, Object filterValue) {

        Lazy<Expression<String>> fieldExpressionAsString = new Lazy(() -> fieldExpression.as(String.class));
        Lazy<Collection<Object>> filterValueAsCollection = new Lazy(
                () -> filterValue.getClass().isArray() ? Arrays.asList((Object[]) filterValue)
                        : (Collection<Object>) filterValue);

        switch (filter.getMatchMode()) {
            case STARTS_WITH:
                return cb.like(fieldExpressionAsString.get(), filterValue + "%");
            case NOT_STARTS_WITH:
                return cb.notLike(fieldExpressionAsString.get(), filterValue + "%");
            case ENDS_WITH:
                return cb.like(fieldExpressionAsString.get(), "%" + filterValue);
            case NOT_ENDS_WITH:
                return cb.notLike(fieldExpressionAsString.get(), "%" + filterValue);
            case CONTAINS:
                return cb.like(fieldExpressionAsString.get(), "%" + filterValue + "%");
            case NOT_CONTAINS:
                return cb.notLike(fieldExpressionAsString.get(), "%" + filterValue + "%");
            case EXACT:
            case EQUALS:
                return cb.equal(fieldExpression, filterValue);
            case NOT_EXACT:
            case NOT_EQUALS:
                return cb.notEqual(fieldExpression, filterValue);
            case LESS_THAN:
                return cb.lessThan(fieldExpression, (Comparable) filterValue);
            case LESS_THAN_EQUALS:
                return cb.lessThanOrEqualTo(fieldExpression, (Comparable) filterValue);
            case GREATER_THAN:
                return cb.greaterThan(fieldExpression, (Comparable) filterValue);
            case GREATER_THAN_EQUALS:
                return cb.greaterThanOrEqualTo(fieldExpression, (Comparable) filterValue);
            case IN:
                return filterValueAsCollection.get().size() == 1
                        ? cb.equal(fieldExpression, filterValueAsCollection.get().iterator().next())
                        : fieldExpression.in(filterValueAsCollection.get());
            case NOT_IN:
                return filterValueAsCollection.get().size() == 1
                        ? cb.notEqual(fieldExpression, filterValueAsCollection.get().iterator().next())
                        : fieldExpression.in(filterValueAsCollection.get()).not();
            case BETWEEN:
                Iterator<Object> iterBetween = filterValueAsCollection.get().iterator();
                return cb.and(cb.greaterThanOrEqualTo(fieldExpression, (Comparable) iterBetween.next()),
                    cb.lessThanOrEqualTo(fieldExpression, (Comparable) iterBetween.next()));
            case NOT_BETWEEN:
                Iterator<Object> iterNotBetween = filterValueAsCollection.get().iterator();
                return cb.and(cb.greaterThanOrEqualTo(fieldExpression, (Comparable) iterNotBetween.next()),
                    cb.lessThanOrEqualTo(fieldExpression, (Comparable) iterNotBetween.next())).not();
            case GLOBAL:
                throw new UnsupportedOperationException("MatchMode.GLOBAL currently not supported!");
        }

        return null;
    }

    protected void applySort(CriteriaBuilder cb,
                             CriteriaQuery<T> cq,
                             Root<T> root,
                             Map<String, SortMeta> sortBy) {

        if (sortBy != null) {
            List<Order> orders = null;
            for (SortMeta sort : sortBy.values().stream().sorted().collect(Collectors.toList())) {
                if (sort.getField() == null || sort.getOrder() == SortOrder.UNSORTED) {
                    continue;
                }

                if (orders == null) {
                    orders = new ArrayList<>();
                }

                Expression<?> fieldExpression = resolveFieldExpression(cb, cq, root, sort.getField());
                orders.add(sort.getOrder() == SortOrder.ASCENDING ? cb.asc(fieldExpression) : cb.desc(fieldExpression));
            }

            if (orders != null) {
                cq.orderBy(orders);
            }
        }
    }

    protected Expression resolveFieldExpression(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<T> root, String fieldName) {
        Join<?, ?> join = null;

        // join if required; e.g. company.name -> join to company and get "name" field from the joined table
        while (fieldName.contains(".")) {
            String currentName = fieldName.substring(0, fieldName.indexOf("."));
            fieldName = fieldName.substring(currentName.length() + 1);

            if (join == null) {
                join = root.join(currentName, JoinType.INNER);
            }
            else {
                join = join.join(currentName, JoinType.INNER);
            }
        }

        return join == null ? root.get(fieldName) : join.get(fieldName);
    }

    @Override
    public T getRowData(String rowKey) {
        Converter converter = getConverter();
        if (converter != null) {
            return super.getRowData(rowKey);
        }

        if (rowKeyField != null) {
            Object convertedRowKey = convertToType(rowKey, getRowKeyGetter().getReturnType());

            EntityManager em = this.entityManager.get();

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root).where(criteriaBuilder.equal(root.get(rowKeyField), convertedRowKey));

            TypedQuery<T> query = em.createQuery(cq);
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

        if (rowKeyField != null) {
            try {
                Object rowKey = getRowKeyGetter().invoke(object);
                return rowKey == null ? null : rowKey.toString();
            }
            catch (InvocationTargetException | IllegalAccessException e) {
                throw new FacesException("Could not invoke getter for " + rowKeyField + " on " + entityClass.getName(), e);
            }
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a Converter or rowKeyField via constructor or implement getRowKey(T object) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    protected Object convertToType(Object value, Class valueType) {
        // skip null
        if (value == null) {
            return null;
        }

        // its already the same type
        if (valueType.isAssignableFrom(value.getClass())) {
            return value;
        }

        FacesContext context = FacesContext.getCurrentInstance();

        // primivites dont need complex conversion
        if (BeanUtils.isPrimitiveOrPrimitiveWrapper(valueType)) {
            return context.getELContext().convertToType(value, valueType);
        }

        Converter targetConverter = context.getApplication().createConverter(valueType);
        if (targetConverter == null) {
            LOG.info("Skip conversion as no converter was found for " + valueType
                    + "; Create a JSF Converter for it or overwrite Object convertToType(String value, Class<?> valueType)!");
            return value;
        }

        Converter sourceConverter = context.getApplication().createConverter(value.getClass());
        if (sourceConverter == null) {
            LOG.info("Skip conversion as no converter was found for " + value.getClass()
                    + "; Create a JSF Converter for it or overwrite Object convertToType(String value, Class<?> valueType)!");
        }

        // first convert the object to string
        String stringValue = sourceConverter == null
                ? value.toString()
                : sourceConverter.getAsString(context, UIComponent.getCurrentComponent(context), value);

        // now convert the string to the required target
        return targetConverter.getAsObject(context, UIComponent.getCurrentComponent(context), stringValue);
    }

    protected Method getRowKeyGetter() {
        if (rowKeyGetter == null) {
            rowKeyGetter = new Lazy<>(() -> {
                try {
                    return new PropertyDescriptor(rowKeyField, entityClass).getReadMethod();
                }
                catch (IntrospectionException e) {
                    throw new FacesException("Could not access " + rowKeyField + " on " + entityClass.getName(), e);
                }
            });
        }
        return rowKeyGetter.get();
    }
}
