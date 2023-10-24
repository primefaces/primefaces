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

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.*;

/**
 * Basic {@link LazyDataModel} implementation with JPA and Criteria API.
 *
 * @param <T> The model class.
 */
public class JPALazyDataModel<T> extends LazyDataModel<T> implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(JPALazyDataModel.class.getName());

    protected Class<T> entityClass;
    protected String rowKeyField;
    protected boolean caseSensitive = true;
    protected boolean wildcardSupport = false;
    protected Class<?> rowKeyType = Long.class;
    protected QueryEnricher<T> queryEnricher;
    protected FilterEnricher<T> filterEnricher;
    protected SortEnricher<T> sortEnricher;
    protected SerializableSupplier<EntityManager> entityManager;
    protected SerializableFunction<T, Object> rowKeyProvider;

    /**
     * For serialization only
     */
    public JPALazyDataModel() {
        // NOOP
    }

    /**
     * Constructs a JpaLazyDataModel for usage without enabled selection.
     *
     * @param entityClass The entity class
     * @param entityManager The {@link EntityManager}
     */
    @Deprecated
    public JPALazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    /**
     * Constructs a JpaLazyDataModel with selection support.
     *
     * @param entityClass The entity class
     * @param entityManager The {@link EntityManager}
     * @param rowKeyField The name of the rowKey property (e.g. "id")
     * @deprecated use the builder instead
     */
    @Deprecated
    public JPALazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager, String rowKeyField) {
        this(entityClass, entityManager);
        this.rowKeyField = rowKeyField;
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
    public JPALazyDataModel(Class<T> entityClass, SerializableSupplier<EntityManager> entityManager, Converter<T> rowKeyConverter) {
        super(rowKeyConverter);
        this.entityClass = entityClass;
        this.entityManager = entityManager;
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

        return query.getResultList();
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
            PropertyDescriptorResolver propResolver = PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();
            for (FilterMeta filter : filterBy.values()) {
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

        if (filterEnricher != null) {
            filterEnricher.enrich(filterBy, cb, cq, root, predicates);
        }

        if (!predicates.isEmpty()) {
            cq.where(
                cb.and(predicates.toArray(new Predicate[0])));
        }
    }

    /**
     * @deprecated use the builder and filterEnricher instead
     */
    @Deprecated
    protected void applyGlobalFilters(Map<String, FilterMeta> filterBy, CriteriaBuilder cb, CriteriaQuery<?> cq,
            Root<T> root, List<Predicate> predicates) {

    }

    protected Predicate createPredicate(FilterMeta filter,
                                        PropertyDescriptor pd,
                                        Root<T> root,
                                        CriteriaBuilder cb,
                                        Expression fieldExpression,
                                        Object filterValue,
                                        Locale locale) {

        Supplier<Expression<String>> fieldExpressionAsString = () -> caseSensitive
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
        return query.getSingleResult();
    }

    @Override
    public String getRowKey(T obj) {
        return String.valueOf(rowKeyProvider.apply(obj));
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private final JPALazyDataModel<T> model;

        public Builder() {
            model = new JPALazyDataModel<>(null, null, (String) null);
        }

        public Builder<T> entityClass(Class<T> entityClass) {
            model.entityClass = entityClass;
            return this;
        }

        public Builder<T> entityManager(SerializableSupplier<EntityManager> entityManager) {
            model.entityManager = entityManager;
            return this;
        }

        public Builder<T> rowKeyConverter(Converter<T> rowKeyConverter) {
            model.rowKeyConverter = rowKeyConverter;
            return this;
        }

        public Builder<T> rowKeyProvider(SerializableFunction<T, Object> rowKeyProvider) {
            model.rowKeyProvider = rowKeyProvider;
            return this;
        }

        @Deprecated
        public Builder<T> rowKey(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        @Deprecated
        public Builder<T> rowKey(SingularAttribute<T, ?> rowKeyMetamodel) {
            model.rowKeyField = rowKeyMetamodel.getName();
            return this;
        }

        public Builder<T> rowKeyField(String rowKey) {
            model.rowKeyField = rowKey;
            return this;
        }

        public Builder<T> rowKeyField(SingularAttribute<T, ?> rowKeyMetamodel) {
            model.rowKeyField = rowKeyMetamodel.getName();
            model.rowKeyType = rowKeyMetamodel.getJavaType();
            return this;
        }

        public Builder<T> caseSensitive(boolean caseSensitive) {
            model.caseSensitive = caseSensitive;
            return this;
        }

        public Builder<T> wildcardSupport(boolean wildcardSupport) {
            model.wildcardSupport = wildcardSupport;
            return this;
        }

        public Builder<T> queryEnricher(QueryEnricher<T> queryEnricher) {
            model.queryEnricher = queryEnricher;
            return this;
        }

        public Builder<T> filterEnricher(FilterEnricher<T> filterEnricher) {
            model.filterEnricher = filterEnricher;
            return this;
        }

        public Builder<T> sortEnricher(SortEnricher<T> sortEnricher) {
            model.sortEnricher = sortEnricher;
            return this;
        }

        public Builder<T> rowKeyType(Class<?> rowKeyType) {
            model.rowKeyType = rowKeyType;
            return this;
        }

        public JPALazyDataModel<T> build() {
            Objects.requireNonNull(model.entityClass, "entityClass not set");
            Objects.requireNonNull(model.entityManager, "entityManager not set");

            FacesContext context = FacesContext.getCurrentInstance();
            PropertyDescriptorResolver propResolver =
                    PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver();

            // some notes about required options for the rowKey to implement #getRowData/#getRowKey,
            //      which is actually mandatory as required for selection
            // 1) rowKeyConverter
            //      this is the easiest way and often already available in applications for entities, we just reuse all of it
            // 2) rowKeyField / rowKeyType
            //      this is now required but we can try to read it via JPA metamodel first
            //      #getRowData needs it to fire a query with the rowKey in the WHERE clause
            // 3) rowKeyProvider
            //      it's just the internal implementation of #getRowKey

            // try to lookup rowKeyField from JPA metamodel, if not defined by user
            if (model.rowKeyField == null) {
                EntityManagerFactory emf = model.entityManager.get().getEntityManagerFactory();

                EntityType<T> entityType = emf.getMetamodel().entity(model.entityClass);
                Type<?> idType = entityType.getIdType();
                if (idType.getPersistenceType() != Type.PersistenceType.BASIC) {
                    throw new FacesException("Entity @Id is not a basic type. Define a rowKeyField!");
                }

                if (!BeanUtils.isPrimitiveOrPrimitiveWrapper(idType.getJavaType())) {
                    Converter converter = context.getApplication().createConverter(idType.getJavaType());
                    if (converter == null) {
                        throw new FacesException("Entity @Id is not a primitive and no Converter found for " + idType.getJavaType().getName()
                                + "! Either define a rowKeyField or create a JSF Converter for it!");
                    }
                }

                SingularAttribute<?, ?> idAttribute = entityType.getDeclaredId(idType.getJavaType());
                model.rowKeyField = idAttribute.getName();
                model.rowKeyType = idType.getJavaType();
                if (model.rowKeyProvider == null) {
                    model.rowKeyProvider = obj -> emf.getPersistenceUnitUtil().getIdentifier(obj);
                }
            }

            // lookup rowKeyType via reflection by rowKeyField, if not defined by user
            if (model.rowKeyType == null) {
                model.rowKeyType = propResolver.get(model.entityClass, model.rowKeyField).getPropertyType();
            }

            // build internal implementation of #getRowKey, if not defined by user
            if (model.rowKeyProvider == null) {
                if (model.rowKeyConverter != null) {
                    model.rowKeyProvider = model::getRowKeyFromConverter;
                }
                else {
                    model.rowKeyProvider = obj -> propResolver.getValue(obj, model.rowKeyField);
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
}
