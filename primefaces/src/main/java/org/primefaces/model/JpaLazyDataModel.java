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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.el.ELException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.util.BeanUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.LocaleUtils;
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
    protected String rowKey;
    protected boolean caseSensitive = true;
    protected boolean wildcardSupport = false;
    protected QueryEnricher<T> queryEnricher;
    protected FilterEnricher<T> filterEnricher;
    protected SortEnricher<T> sortEnricher;

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
     * @param rowKey The name of the rowKey property (e.g. "id")
     * @deprecated use the builder instead
     */
    @Deprecated
    public JpaLazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager, String rowKey) {
        this(entityClass, entityManager);
        this.rowKey = rowKey;
    }

    /**
     * Constructs a JpaLazyDataModel with selection support, with an already existing {@link Converter}.
     *
     * @param entityClass The entity class
     * @param entityManager The {@link EntityManager}
     * @param rowKeyConverter The converter, which will be used for converting the entity to a rowKey and vice versa
     * @deprecated use the builder instead
     */
    @Deprecated
    public JpaLazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager, Converter<T> rowKeyConverter) {
        super(rowKeyConverter);
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

        TypedQuery<Long> query = em.createQuery(cq);
        return query.getSingleResult().intValue();
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

        if (queryEnricher != null) {
            queryEnricher.enrich(query);
        }

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
            FacesContext context = FacesContext.getCurrentInstance();
            Locale locale = LocaleUtils.getCurrentLocale(context);

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

                Predicate predicate = createPredicate(filter, filterField, root, cb, fieldExpression, convertedFilterValue, locale);
                predicates.add(predicate);
            }
        }

        if (filterEnricher != null) {
            filterEnricher.enrich(filterBy, cb, cq, root, predicates);
        }

        if (!predicates.isEmpty()) {
            cq.where(
                cb.and(predicates.toArray(new Predicate[predicates.size()])));
        }
    }

    /**
     * @deprecated use the builder and filterEnricher instead
     */
    @Deprecated
    protected void applyGlobalFilters(Map<String, FilterMeta> filterBy, CriteriaBuilder cb, CriteriaQuery<?> cq,
            Root<T> root, List<Predicate> predicates) {

    }

    protected Predicate createPredicate(FilterMeta filter, Field filterField,
            Root<T> root, CriteriaBuilder cb, Expression fieldExpression, Object filterValue, Locale locale) {

        Lazy<Expression<String>> fieldExpressionAsString = new Lazy(() -> caseSensitive
                ? fieldExpression.as(String.class)
                : cb.upper(fieldExpression.as(String.class)));
        Lazy<Collection<Object>> filterValueAsCollection = new Lazy(
                () -> filterValue.getClass().isArray() ? Arrays.asList((Object[]) filterValue)
                        : (Collection<Object>) filterValue);

        switch (filter.getMatchMode()) {
            case STARTS_WITH:
                return cb.like(fieldExpressionAsString.get(), getStringFilterValue(filterValue, locale) + "%");
            case NOT_STARTS_WITH:
                return cb.notLike(fieldExpressionAsString.get(), getStringFilterValue(filterValue, locale) + "%");
            case ENDS_WITH:
                return cb.like(fieldExpressionAsString.get(), "%" + getStringFilterValue(filterValue, locale));
            case NOT_ENDS_WITH:
                return cb.notLike(fieldExpressionAsString.get(), "%" + getStringFilterValue(filterValue, locale));
            case CONTAINS:
                return cb.like(fieldExpressionAsString.get(), "%" + getStringFilterValue(filterValue, locale) + "%");
            case NOT_CONTAINS:
                return cb.notLike(fieldExpressionAsString.get(), "%" + getStringFilterValue(filterValue, locale) + "%");
            case EXACT:
                String exactValue = getStringFilterValue(filterValue, locale);
                if (wildcardSupport && (exactValue.contains("%") || exactValue.contains("_"))) {
                    return cb.like(fieldExpressionAsString.get(), exactValue);
                }
                else {
                    return cb.equal(fieldExpressionAsString.get(), exactValue);
                }
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

    protected String getStringFilterValue(Object filterValue, Locale locale) {
        String value = Objects.toString(filterValue, Constants.EMPTY_STRING);
        value = caseSensitive ? value : value.toUpperCase(locale);
        if (wildcardSupport) {
            value = value.replace("*", "%");
            value = value.replace("?", "_");
        }
        return value;
    }

    protected void applySort(CriteriaBuilder cb,
                             CriteriaQuery<T> cq,
                             Root<T> root,
                             Map<String, SortMeta> sortBy) {

        List<Order> orders = new ArrayList<>();

        if (sortBy != null) {
            for (SortMeta sort : sortBy.values().stream().sorted().collect(Collectors.toList())) {
                if (sort.getField() == null || sort.getOrder() == SortOrder.UNSORTED) {
                    continue;
                }

                Expression<?> fieldExpression = resolveFieldExpression(cb, cq, root, sort.getField());
                orders.add(sort.getOrder() == SortOrder.ASCENDING ? cb.asc(fieldExpression) : cb.desc(fieldExpression));
            }
        }

        if (sortEnricher != null) {
            sortEnricher.enrich(sortBy, cb, cq, root, orders);
        }

        if (!orders.isEmpty()) {
            cq.orderBy(orders);
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
        Converter<T> rowKeyConverter = getRowKeyConverter();
        if (rowKeyConverter != null) {
            return super.getRowData(rowKey);
        }

        if (this.rowKey != null) {
            Object convertedRowKey = convertToType(rowKey, getRowKeyGetter().getReturnType());

            EntityManager em = this.entityManager.get();

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root).where(criteriaBuilder.equal(root.get(this.rowKey), convertedRowKey));

            TypedQuery<T> query = em.createQuery(cq);
            return query.getSingleResult();
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a rowKey / rowKey-Converter or implement getRowData(String rowKey) in %s"
                        + ", when basic rowKey algorithm is not used [component=%s,view=%s]."));
    }

    @Override
    public String getRowKey(T object) {
        Converter<T> rowKeyConverter = getRowKeyConverter();
        if (rowKeyConverter != null) {
            return super.getRowKey(object);
        }

        if (this.rowKey != null) {
            try {
                Object rowKeyValue = getRowKeyGetter().invoke(object);
                return rowKeyValue == null ? null : rowKeyValue.toString();
            }
            catch (InvocationTargetException | IllegalAccessException e) {
                throw new FacesException("Could not invoke getter for " + this.rowKey + " on " + entityClass.getName(), e);
            }
        }

        throw new UnsupportedOperationException(
                getMessage("Provide a rowKey / rowKey-Converter or implement getRowKey(T object) in %s"
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

        // primivites dont need complex conversion, try the ELContext first
        if (BeanUtils.isPrimitiveOrPrimitiveWrapper(valueType)) {
            try {
                return context.getELContext().convertToType(value, valueType);
            }
            catch (ELException e) {
                LOG.log(Level.INFO, e, () -> "Could not convert '" + value + "' to " + valueType + " via ELContext!");
            }
        }

        Converter targetConverter = context.getApplication().createConverter(valueType);
        if (targetConverter == null) {
            LOG.log(Level.FINE, () -> "Skip conversion as no converter was found for " + valueType
                    + "; Create a JSF Converter for it or overwrite Object convertToType(String value, Class<?> valueType)!");
            return value;
        }

        Converter sourceConverter = context.getApplication().createConverter(value.getClass());
        if (sourceConverter == null) {
            LOG.log(Level.FINE, () -> "Skip conversion as no converter was found for " + value.getClass()
                    + "; Create a JSF Converter for it or overwrite Object convertToType(String value, Class<?> valueType)!");
        }

        // first convert the object to string
        String stringValue = sourceConverter == null
                ? value.toString()
                : sourceConverter.getAsString(context, UIComponent.getCurrentComponent(context), value);

        // now convert the string to the required target
        try {
            return targetConverter.getAsObject(context, UIComponent.getCurrentComponent(context), stringValue);
        }
        catch (ConverterException e) {
            LOG.log(Level.INFO, e, () -> "Could not convert '" + stringValue + "' to " + valueType + " via " + targetConverter.getClass().getName());
            return value;
        }
    }

    protected Method getRowKeyGetter() {
        if (rowKeyGetter == null) {
            rowKeyGetter = new Lazy<>(() -> {
                try {
                    return new PropertyDescriptor(rowKey, entityClass).getReadMethod();
                }
                catch (IntrospectionException e) {
                    throw new FacesException("Could not access " + rowKey + " on " + entityClass.getName(), e);
                }
            });
        }
        return rowKeyGetter.get();
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        // reset cache
        if (!Objects.equals(rowKey, this.rowKey)) {
            rowKeyGetter = null;
        }
        this.rowKey = rowKey;
    }

    public void setRowKey(SingularAttribute<T, ?> rowKeyMetamodel) {
        setRowKey(rowKeyMetamodel.getName());
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isWildcardSupport() {
        return wildcardSupport;
    }

    public void setWildcardSupport(boolean wildcardSupport) {
        this.wildcardSupport = wildcardSupport;
    }

    public QueryEnricher<T> getQueryEnricher() {
        return queryEnricher;
    }

    public void setQueryEnricher(QueryEnricher<T> queryEnricher) {
        this.queryEnricher = queryEnricher;
    }

    public FilterEnricher<T> getFilterEnricher() {
        return filterEnricher;
    }

    public void setFilterEnricher(FilterEnricher<T> filterEnricher) {
        this.filterEnricher = filterEnricher;
    }

    public SortEnricher<T> getSortEnricher() {
        return sortEnricher;
    }

    public void setSortEnricher(SortEnricher<T> sortEnricher) {
        this.sortEnricher = sortEnricher;
    }





    public static <T> Builder<T> builder(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager) {
        return new Builder<>(entityClass, entityManager);
    }

    public static class Builder<T> {
        private final Class<T> entityClass;
        private final SerializableSupplier<EntityManager> entityManager;
        private String rowKey;
        private Converter<T> rowKeyConverter;
        private boolean caseSensitive = true;
        private boolean wildcardSupport = false;
        private QueryEnricher<T> queryEnricher;
        private FilterEnricher<T> filterEnricher;
        private SortEnricher<T> sortEnricher;

        public Builder(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager) {
            this.entityClass = entityClass;
            this.entityManager = entityManager;
        }

        public Builder<T> rowKeyConverter(Converter<T> rowKeyConverter) {
            this.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T> rowKey(String rowKey) {
            this.rowKey = rowKey;
            return this;
        }

        public Builder<T> rowKey(SingularAttribute<T, ?> rowKeyMetamodel) {
            this.rowKey = rowKeyMetamodel.getName();
            return this;
        }

        public Builder<T> caseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public Builder<T> wildcardSupport(boolean wildcardSupport) {
            this.wildcardSupport = wildcardSupport;
            return this;
        }

        public Builder<T> queryEnricher(QueryEnricher<T> queryEnricher) {
            this.queryEnricher = queryEnricher;
            return this;
        }

        public Builder<T> filterEnricher(FilterEnricher<T> filterEnricher) {
            this.filterEnricher = filterEnricher;
            return this;
        }

        public Builder<T> sortEnricher(SortEnricher<T> sortEnricher) {
            this.sortEnricher = sortEnricher;
            return this;
        }

        public JpaLazyDataModel<T> build() {
            JpaLazyDataModel<T> model = new JpaLazyDataModel<>(entityClass, entityManager);
            model.rowKey = rowKey;
            model.caseSensitive = caseSensitive;
            model.wildcardSupport = wildcardSupport;
            model.queryEnricher = queryEnricher;
            model.filterEnricher = filterEnricher;
            model.sortEnricher = sortEnricher;
            model.setRowKeyConverter(rowKeyConverter);

            return model;
        }
    }

    @FunctionalInterface
    public static interface QueryEnricher<T> extends Serializable {

        void enrich(TypedQuery<T> query);
    }

    @FunctionalInterface
    public static interface SortEnricher<T> extends Serializable {

        void enrich(Map<String, SortMeta> sortBy, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, List<Order> orders);
    }

    @FunctionalInterface
    public static interface FilterEnricher<T> extends Serializable {

        void enrich(Map<String, FilterMeta> filterBy, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<T> root, List<Predicate> predicates);
    }
}
