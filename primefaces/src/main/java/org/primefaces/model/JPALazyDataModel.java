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

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.BeanUtils;
import org.primefaces.util.Callbacks;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.PropertyDescriptorResolver;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type;

/**
 * Basic {@link LazyDataModel} implementation with JPA and Criteria API.
 *
 * @param <T> The model class.
 */
public class JPALazyDataModel<T> extends LazyDataModel<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(JPALazyDataModel.class.getName());

    protected Class<T> entityClass;
    protected String rowKeyField;
    protected boolean caseSensitive = true;
    protected boolean wildcardSupport = false;
    protected Class<?> rowKeyType;
    protected QueryEnricher<T> queryEnricher;
    protected FilterEnricher<T> filterEnricher;
    protected AdditionalFilterMeta additionalFilterMeta;
    protected SortEnricher<T> sortEnricher;
    protected Callbacks.SerializableSupplier<EntityManager> entityManager;
    protected Callbacks.SerializableFunction<T, Object> rowKeyProvider;
    protected Callbacks.SerializableConsumer<List<T>> resultEnricher;

    /**
     * For serialization only
     */
    public JPALazyDataModel() {
        // NOOP
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        EntityManager em = entityManager.get();

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
        EntityManager em = entityManager.get();

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
        if (resultEnricher != null) {
            resultEnricher.accept(result);
        }
        return result;
    }

    protected void applyFilters(CriteriaBuilder cb,
                                CriteriaQuery<?> cq,
                                Root<T> root,
                                Map<String, FilterMeta> filterBy) {

        List<Predicate> predicates = new ArrayList<>();

        applyFiltersFromFilterMeta(entityClass, filterBy.values(), cb, cq, root, predicates);

        if (filterEnricher != null) {
            filterEnricher.enrich(filterBy, cb, cq, root, predicates);
        }

        if (additionalFilterMeta != null) {
            applyFiltersFromFilterMeta(entityClass, additionalFilterMeta.process(), cb, cq, root, predicates);
        }

        if (!predicates.isEmpty()) {
            cq.where(
                cb.and(predicates.toArray(new Predicate[0])));
        }
    }

    protected void applyFiltersFromFilterMeta(Class<T> entityClass, Collection<FilterMeta> filterBy, CriteriaBuilder cb,
                                              CriteriaQuery<?> cq,
                                              Root<T> root, List<Predicate> predicates) {
        if (filterBy != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            Locale locale = LocaleUtils.getCurrentLocale(context);
            PropertyDescriptorResolver propResolver = PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();
            for (FilterMeta filter : filterBy) {
                if (filter.getField() == null || filter.getFilterValue() == null || filter.isGlobalFilter()) {
                    continue;
                }

                PropertyDescriptor pd = propResolver.get(entityClass, filter.getField());
                Object filterValue = filter.getFilterValue();
                Object convertedFilterValue;

                Class<?> filterValueClass = filterValue.getClass();
                if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
                    convertedFilterValue = filterValue;
                }
                else {
                    convertedFilterValue = ComponentUtils.convertToType(filterValue, pd.getPropertyType(), LOGGER);
                }

                Expression fieldExpression = resolveFieldExpression(cb, cq, root, filter.getField());

                Predicate predicate = createPredicate(filter, pd, root, cb, fieldExpression, convertedFilterValue, locale);
                predicates.add(predicate);
            }
        }
    }

    protected Predicate createPredicate(FilterMeta filter,
                                        PropertyDescriptor pd,
                                        Root<T> root,
                                        CriteriaBuilder cb,
                                        Expression fieldExpression,
                                        Object filterValue,
                                        Locale locale) {

        boolean isCaseSensitive = caseSensitive || !(CharSequence.class.isAssignableFrom(pd.getPropertyType()) || pd.getPropertyType() == char.class);
        Supplier<Expression<String>> fieldExpressionAsString = () -> isCaseSensitive
                ? fieldExpression.as(String.class)
                : cb.upper(fieldExpression.as(String.class));
        Supplier<Collection<Object>> filterValueAsCollection = () -> filterValue.getClass().isArray()
                        ? Arrays.asList((Object[]) filterValue)
                        : (Collection<Object>) filterValue;

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
        if (rowKeyConverter != null) {
            return super.getRowData(rowKey);
        }

        Object convertedRowKey = ComponentUtils.convertToType(rowKey, rowKeyType, LOGGER);

        EntityManager em = entityManager.get();

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root).where(criteriaBuilder.equal(root.get(rowKeyField), convertedRowKey));

        TypedQuery<T> query = em.createQuery(cq);
        T result = query.getSingleResult();
        if (resultEnricher != null) {
            resultEnricher.accept(List.of(result));
        }
        return result;
    }

    @Override
    public String getRowKey(T obj) {
        Object rowKey = rowKeyProvider.apply(obj);
        return rowKey == null ? null : String.valueOf(rowKey);
    }

    public static <T> Builder<T, ? extends JPALazyDataModel<T>> builder() {
        return new Builder<>(new JPALazyDataModel<>());
    }

    public static class Builder<T, TM extends JPALazyDataModel<T>> {
        protected TM model;

        public Builder(TM model) {
            this.model = model;
        }

        public Builder<T, TM> entityClass(Class<T> entityClass) {
            model.entityClass = entityClass;
            return this;
        }

        public Builder<T, TM> entityManager(Callbacks.SerializableSupplier<EntityManager> entityManager) {
            model.entityManager = entityManager;
            return this;
        }

        public Builder<T, TM> rowKeyConverter(Converter<T> rowKeyConverter) {
            model.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T, TM> rowKeyProvider(Callbacks.SerializableFunction<T, Object> rowKeyProvider) {
            model.rowKeyProvider = rowKeyProvider;
            return this;
        }

        public Builder<T, TM> rowKeyField(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        public Builder<T, TM> rowKeyField(SingularAttribute<T, ?> rowKeyMetamodel) {
            model.rowKeyField = rowKeyMetamodel.getName();
            model.rowKeyType = rowKeyMetamodel.getJavaType();
            return this;
        }

        public Builder<T, TM> rowKeyType(Class<?> rowKeyType) {
            model.rowKeyType = rowKeyType;
            return this;
        }

        public Builder<T, TM> caseSensitive(boolean caseSensitive) {
            model.caseSensitive = caseSensitive;
            return this;
        }

        public Builder<T, TM> wildcardSupport(boolean wildcardSupport) {
            model.wildcardSupport = wildcardSupport;
            return this;
        }

        public Builder<T, TM> queryEnricher(QueryEnricher<T> queryEnricher) {
            model.queryEnricher = queryEnricher;
            return this;
        }

        public Builder<T, TM> filterEnricher(FilterEnricher<T> filterEnricher) {
            model.filterEnricher = filterEnricher;
            return this;
        }

        public Builder<T, TM> additionalFilterMeta(AdditionalFilterMeta additionalFilterMeta) {
            model.additionalFilterMeta = additionalFilterMeta;
            return this;
        }

        public Builder<T, TM> sortEnricher(SortEnricher<T> sortEnricher) {
            model.sortEnricher = sortEnricher;
            return this;
        }

        public Builder<T, TM> resultEnricher(Callbacks.SerializableConsumer<List<T>> resultEnricher) {
            model.resultEnricher = resultEnricher;
            return this;
        }

        public TM build() {
            Objects.requireNonNull(model.entityClass, "entityClass not set");
            Objects.requireNonNull(model.entityManager, "entityManager not set");

            // some notes about required options for the rowKey to implement #getRowData/#getRowKey,
            // which is actually mandatory as required for selection
            // - rowKeyConverter
            //      this is the easiest way and often already available in applications for entities, we just reuse all of it
            // - rowKeyField
            //      this is now required but we can try to read it via JPA metamodel first
            //      #getRowData needs it to fire a query with the rowKey in the WHERE clause
            // - rowKeyType
            //      we will get the info from JPA or via reflection from rowKeyField
            //      it's required for the internal implementation of #getRowData
            // - rowKeyProvider
            //      it's just the internal implementation of #getRowKey

            // rowKeyConverter (either rowKeyConverter or rowKeyField are required)
            if (model.rowKeyConverter != null) {
                model.rowKeyProvider = model::getRowKeyFromConverter;
            }
            // rowKeyField
            else {
                FacesContext context = FacesContext.getCurrentInstance();

                // try to lookup from JPA metamodel, if not defined by user
                if (model.rowKeyField == null) {
                    EntityManagerFactory emf = model.entityManager.get().getEntityManagerFactory();

                    EntityType<T> entityType = emf.getMetamodel().entity(model.entityClass);
                    Type<?> idType = entityType.getIdType();
                    if (idType.getPersistenceType() != Type.PersistenceType.BASIC) {
                        throw new FacesException("Entity @Id is not a basic type. Define a rowKeyField!");
                    }

                    if (!BeanUtils.isPrimitiveOrPrimitiveWrapper(idType.getJavaType())) {
                        Converter<?> converter = context.getApplication().createConverter(idType.getJavaType());
                        if (converter == null) {
                            throw new FacesException("Entity @Id is not a primitive and no Converter found for " + idType.getJavaType().getName()
                                    + "! Either define a rowKeyField or create a Converter for it!");
                        }
                    }

                    SingularAttribute<?, ?> idAttribute = entityType.getId(idType.getJavaType());
                    model.rowKeyField = idAttribute.getName();
                    if (model.rowKeyType == null) {
                        model.rowKeyType = idType.getJavaType();
                    }
                    if (model.rowKeyProvider == null) {
                        model.rowKeyProvider = obj -> emf.getPersistenceUnitUtil().getIdentifier(obj);
                    }
                }
                // user-defined rowKeyField
                else {
                    PropertyDescriptorResolver propResolver =
                            PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();

                    if (model.rowKeyType == null) {
                        model.rowKeyType = propResolver.get(model.entityClass, model.rowKeyField).getPropertyType();
                    }
                    if (model.rowKeyProvider == null) {
                        model.rowKeyProvider = obj -> propResolver.getValue(obj, model.rowKeyField);
                    }
                }
            }

            return model;
        }
    }

    @FunctionalInterface
    public interface QueryEnricher<T> extends Serializable {

        void enrich(TypedQuery<T> query);
    }

    @FunctionalInterface
    public interface SortEnricher<T> extends Serializable {

        void enrich(Map<String, SortMeta> sortBy, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root, List<Order> orders);
    }

    @FunctionalInterface
    public interface FilterEnricher<T> extends Serializable {

        void enrich(Map<String, FilterMeta> filterBy, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<T> root, List<Predicate> predicates);
    }

    @FunctionalInterface
    public interface AdditionalFilterMeta extends Serializable {

        Collection<FilterMeta> process();
    }
}
