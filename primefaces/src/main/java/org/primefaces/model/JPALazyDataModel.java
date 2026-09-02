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

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.filter.FilterConstraints;
import org.primefaces.util.BeanUtils;
import org.primefaces.util.Callbacks;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.PropertyDescriptorResolver;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
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
 * <p>
 * Every {@link MatchMode} is translated to a Criteria {@link Predicate}, except:
 * <ul>
 *   <li>{@link MatchMode#MATCHES_REGEX}, which has no portable JPA equivalent - override
 *   {@link #createRegexPredicate} to use the regex function of your database;</li>
 *   <li>{@link MatchMode#GLOBAL}, which is not supported at all.</li>
 * </ul>
 * The match modes matching against a {@code Collection} field ({@link MatchMode#ARRAY_CONTAINS} and friends)
 * require the field to be a mapped collection (e.g., an {@code @ElementCollection}), as they are translated to
 * {@code MEMBER OF} predicates.
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
                // #isActive instead of a plain null-check on the filter value: a value-less match mode
                // (e.g., "is null", "is today") is its own complete predicate and never carries a value
                if (filter.getField() == null || filter.isGlobalFilter() || !filter.isActive()) {
                    continue;
                }

                PropertyDescriptor pd = propResolver.get(entityClass, filter.getField());
                Object convertedFilterValue = convertFilterValue(filter, pd);
                Expression fieldExpression = resolveFieldExpression(cb, cq, root, filter.getField());

                Predicate predicate = createPredicate(filter, pd, root, cb, fieldExpression, convertedFilterValue, locale);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
        }
    }

    /**
     * Converts the user-typed filter value to what the {@code WHERE} clause of the filter's match mode needs:
     * the type of the filtered field for an ordinary comparison, its element type for a multivalue field, or
     * no conversion at all where the value is not a domain value in the first place (a plain count of
     * days/minutes/hours for the relative date/time match modes, a pattern for the regex one).
     *
     * @param filter the filter to convert the value of
     * @param pd the property descriptor of the filtered field
     * @return the converted filter value, or {@code null} for a value-less match mode
     */
    protected Object convertFilterValue(FilterMeta filter, PropertyDescriptor pd) {
        MatchMode matchMode = filter.getMatchMode();
        Object filterValue = filter.getFilterValue();

        // a value-less match mode (e.g., "is empty", "is this month") ignores the filter value entirely
        if (filterValue == null || !matchMode.requiresValue()) {
            return null;
        }

        switch (matchMode) {
            // the typed value is a plain count of days/minutes/hours instead of a date, or a regex pattern -
            // converting it to the type of the field would either fail or mangle it
            case LAST_N_DAYS:
            case NEXT_N_DAYS:
            case RELATIVE_DATE:
            case LAST_N_MINUTES:
            case NEXT_N_MINUTES:
            case LAST_N_HOURS:
            case NEXT_N_HOURS:
            case MATCHES_REGEX:
                return filterValue;
            // the field is multivalue (e.g., a List<String> of tags), so the value belongs to its element type
            case ARRAY_CONTAINS:
            case ARRAY_NOT_CONTAINS:
                return convertToElementType(filterValue, pd);
            case CONTAINS_ANY:
            case CONTAINS_ALL:
            case CONTAINS_NONE:
                return filterValueAsTokens(filterValue).stream()
                        .map(token -> convertToElementType(token, pd))
                        .collect(Collectors.toList());
            default:
                break;
        }

        Class<?> filterValueClass = filterValue.getClass();
        if (filterValueClass.isArray() || Collection.class.isAssignableFrom(filterValueClass)) {
            // a multi-value filter (e.g., "in list", "between") is already converted value by value by UITable
            return filterValue;
        }

        return ComponentUtils.convertToType(filterValue, pd.getPropertyType(), LOGGER);
    }

    /**
     * Converts a single filter value to the element type of a multivalue field (e.g., to {@code Status} for a
     * {@code List<Status>}), as required by the {@code MEMBER OF} predicates of the "array" match modes. Falls
     * back to the type of the field itself if the element type cannot be resolved.
     */
    protected Object convertToElementType(Object filterValue, PropertyDescriptor pd) {
        Class<?> elementType = resolveElementType(pd);
        return ComponentUtils.convertToType(filterValue, elementType == null ? pd.getPropertyType() : elementType, LOGGER);
    }

    /**
     * The element type of a multivalue field, read from the generic return type of its getter
     * (e.g., {@code String} for a {@code List<String>}).
     *
     * @param pd the property descriptor of the filtered field
     * @return the element type, or {@code null} if the field is not a generic single-parameter type
     */
    protected Class<?> resolveElementType(PropertyDescriptor pd) {
        Method readMethod = pd.getReadMethod();
        if (readMethod != null && readMethod.getGenericReturnType() instanceof ParameterizedType) {
            java.lang.reflect.Type[] typeArguments = ((ParameterizedType) readMethod.getGenericReturnType()).getActualTypeArguments();
            if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
                return (Class<?>) typeArguments[0];
            }
        }
        return null;
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

        MatchMode matchMode = filter.getMatchMode();

        // the relative date match modes ("today", "this week", "last N days", ...) all boil down to a day
        // range computed against "now"; the ranges are taken from the very definitions the in-memory
        // FilterConstraints use, so the query cannot drift from what an in-memory filter would match
        if (FilterConstraints.isRelativeDateMode(matchMode)) {
            LocalDate[] range = FilterConstraints.dateRange(matchMode, LocalDate.now(), locale, toIntegerFilterValue(filterValue));
            // no parsable N (e.g., a non-numeric filter value): nothing matches, as in-memory
            return range == null
                    ? cb.disjunction()
                    : createDateRangePredicate(cb, pd, fieldExpression, range[0], range[1]);
        }

        switch (matchMode) {
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
            case IS_NULL:
                return cb.isNull(fieldExpression);
            case NOT_NULL:
                return cb.isNotNull(fieldExpression);
            case IS_EMPTY:
                return createIsEmptyPredicate(cb, pd, fieldExpression);
            case NOT_EMPTY:
                return createIsEmptyPredicate(cb, pd, fieldExpression).not();
            case IS_TRUE:
                return createBooleanPredicate(cb, pd, fieldExpression, fieldExpressionAsString, true, locale);
            case IS_FALSE:
                return createBooleanPredicate(cb, pd, fieldExpression, fieldExpressionAsString, false, locale);
            case MATCHES_REGEX:
                return createRegexPredicate(cb, fieldExpressionAsString.get(), getStringFilterValue(filterValue, locale));
            case LAST_N_MINUTES:
                return createRelativeTimePredicate(cb, pd, fieldExpression, filterValue, ChronoUnit.MINUTES, false);
            case NEXT_N_MINUTES:
                return createRelativeTimePredicate(cb, pd, fieldExpression, filterValue, ChronoUnit.MINUTES, true);
            case LAST_N_HOURS:
                return createRelativeTimePredicate(cb, pd, fieldExpression, filterValue, ChronoUnit.HOURS, false);
            case NEXT_N_HOURS:
                return createRelativeTimePredicate(cb, pd, fieldExpression, filterValue, ChronoUnit.HOURS, true);
            case ARRAY_CONTAINS:
                return createArrayContainsPredicate(cb, pd, fieldExpression, filterValue);
            case ARRAY_NOT_CONTAINS:
                return createArrayContainsPredicate(cb, pd, fieldExpression, filterValue).not();
            case CONTAINS_ANY:
                return createArrayTokensPredicate(cb, pd, fieldExpression, filterValueAsTokens(filterValue), false);
            case CONTAINS_ALL:
                return createArrayTokensPredicate(cb, pd, fieldExpression, filterValueAsTokens(filterValue), true);
            case CONTAINS_NONE:
                return createArrayTokensPredicate(cb, pd, fieldExpression, filterValueAsTokens(filterValue), false).not();
            case ALL:
                // "no filter selected" placeholder; unreachable via #applyFiltersFromFilterMeta, as
                // FilterMeta#isActive always treats it as inactive
                return null;
            case GLOBAL:
                throw new UnsupportedOperationException("MatchMode.GLOBAL currently not supported!");
        }

        return null;
    }

    /**
     * Mirrors {@code IsEmptyFilterConstraint}: an empty collection for a multivalue field, {@code null} or blank
     * for a string field, plain {@code null} for anything else.
     */
    protected Predicate createIsEmptyPredicate(CriteriaBuilder cb, PropertyDescriptor pd, Expression fieldExpression) {
        Class<?> type = pd.getPropertyType();
        if (Collection.class.isAssignableFrom(type)) {
            return cb.isEmpty(fieldExpression);
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return cb.or(cb.isNull(fieldExpression), cb.equal(cb.trim(fieldExpression.as(String.class)), Constants.EMPTY_STRING));
        }
        return cb.isNull(fieldExpression);
    }

    /**
     * Mirrors {@code IsTrueFilterConstraint}/{@code IsFalseFilterConstraint}: a boolean field is compared to the
     * boolean itself, any other field to the string {@code "true"}/{@code "false"}.
     */
    protected Predicate createBooleanPredicate(CriteriaBuilder cb, PropertyDescriptor pd, Expression fieldExpression,
                                               Supplier<Expression<String>> fieldExpressionAsString, boolean expected, Locale locale) {
        Class<?> type = pd.getPropertyType();
        if (type == boolean.class || Boolean.class.isAssignableFrom(type)) {
            return cb.equal(fieldExpression, expected);
        }
        return cb.equal(fieldExpressionAsString.get(), getStringFilterValue(Boolean.toString(expected), locale));
    }

    /**
     * Regular expression matching ({@link MatchMode#MATCHES_REGEX}) has no portable JPA equivalent - every
     * database exposes it as its own function or operator - so it is unsupported by default. Override to emit
     * the one your database understands, e.g., for Oracle:
     * <pre>
     * return cb.isTrue(cb.function("regexp_like", Boolean.class, fieldExpression, cb.literal(pattern)));
     * </pre>
     *
     * @param cb the criteria builder
     * @param fieldExpression the filtered field, as a string expression
     * @param pattern the user-typed regular expression
     * @return the predicate matching {@code fieldExpression} against {@code pattern}
     */
    protected Predicate createRegexPredicate(CriteriaBuilder cb, Expression<String> fieldExpression, String pattern) {
        throw new UnsupportedOperationException("MatchMode.MATCHES_REGEX has no portable JPA equivalent; "
                + "override JPALazyDataModel#createRegexPredicate to use the regex function of your database!");
    }

    /**
     * Half-open {@code [start, end + 1 day)} range predicate for the relative date match modes. Half-open
     * rather than {@code BETWEEN}, so that a field carrying a time component (e.g., a {@code Timestamp}) matches
     * on the whole of the last day of the range instead of just its midnight.
     */
    protected Predicate createDateRangePredicate(CriteriaBuilder cb, PropertyDescriptor pd, Expression fieldExpression,
                                                 LocalDate start, LocalDate end) {
        Class<?> type = pd.getPropertyType();
        Object lower = isTimeOnly(type) ? null : toTemporalValue(type, start.atStartOfDay());
        Object upper = isTimeOnly(type) ? null : toTemporalValue(type, end.plusDays(1).atStartOfDay());
        if (lower == null || upper == null) {
            // not a date field - a bare time-of-day has no date to compare - so nothing matches, as in-memory
            return cb.disjunction();
        }

        return cb.and(cb.greaterThanOrEqualTo(fieldExpression, (Comparable) lower),
                cb.lessThan(fieldExpression, (Comparable) upper));
    }

    /**
     * Inclusive range predicate for the "last/next N minutes/hours" match modes, mirroring
     * {@code RelativeMinutesOrHoursFilterConstraint}: a field with a date component is compared linearly, while
     * a bare time-of-day field is a cyclic 24h clock whose window may wrap past midnight.
     */
    protected Predicate createRelativeTimePredicate(CriteriaBuilder cb, PropertyDescriptor pd, Expression fieldExpression,
                                                    Object filterValue, ChronoUnit unit, boolean forward) {
        Integer amount = toIntegerFilterValue(filterValue);
        if (amount == null) {
            return cb.disjunction();
        }

        Class<?> type = pd.getPropertyType();
        if (isTimeOnly(type)) {
            LocalTime now = LocalTime.now();
            LocalTime start = forward ? now : now.minus(amount, unit);
            LocalTime end = forward ? now.plus(amount, unit) : now;
            Comparable lower = (Comparable) toTemporalValue(type, LocalDate.now().atTime(start));
            Comparable upper = (Comparable) toTemporalValue(type, LocalDate.now().atTime(end));

            // a window wrapped past midnight (e.g., [23:35, 00:05]) has its bounds on either side of midnight,
            // so the range check becomes an OR
            return start.isAfter(end)
                    ? cb.or(cb.greaterThanOrEqualTo(fieldExpression, lower), cb.lessThanOrEqualTo(fieldExpression, upper))
                    : cb.between(fieldExpression, lower, upper);
        }

        LocalDateTime now = LocalDateTime.now();
        Object lower = toTemporalValue(type, forward ? now : now.minus(amount, unit));
        Object upper = toTemporalValue(type, forward ? now.plus(amount, unit) : now);
        if (lower == null || upper == null) {
            // not a date/time field - nothing matches, as in-memory
            return cb.disjunction();
        }

        return cb.between(fieldExpression, (Comparable) lower, (Comparable) upper);
    }

    /**
     * {@code MEMBER OF} predicate for a multivalue field ({@link MatchMode#ARRAY_CONTAINS}), degrading to plain
     * equality for a single-valued field, just as {@code ArrayContainsFilterConstraint} does.
     */
    protected Predicate createArrayContainsPredicate(CriteriaBuilder cb, PropertyDescriptor pd, Expression fieldExpression, Object filterValue) {
        if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
            return cb.isMember(filterValue, fieldExpression);
        }
        return cb.equal(fieldExpression, filterValue);
    }

    /**
     * Combines one {@code MEMBER OF} predicate per typed value, for {@link MatchMode#CONTAINS_ANY}
     * ({@code matchAll = false}), {@link MatchMode#CONTAINS_ALL} ({@code matchAll = true}) and
     * {@link MatchMode#CONTAINS_NONE} (the negation of "contains any").
     */
    protected Predicate createArrayTokensPredicate(CriteriaBuilder cb, PropertyDescriptor pd, Expression fieldExpression,
                                                   Collection<?> filterValues, boolean matchAll) {
        if (filterValues.isEmpty()) {
            // nothing typed yet - nothing matches, as in-memory
            return cb.disjunction();
        }

        Predicate[] memberOf = filterValues.stream()
                .map(value -> createArrayContainsPredicate(cb, pd, fieldExpression, value))
                .toArray(Predicate[]::new);

        return matchAll ? cb.and(memberOf) : cb.or(memberOf);
    }

    /**
     * Whether {@code type} is a bare time-of-day, which - unlike all the other supported date/time types - has
     * no date component to compare a date range to.
     */
    protected boolean isTimeOnly(Class<?> type) {
        return LocalTime.class.isAssignableFrom(type) || java.sql.Time.class.isAssignableFrom(type);
    }

    /**
     * Converts a computed date/time range bound to the Java type of the filtered field: a criteria literal has
     * to be of the type of the attribute it is compared to, e.g., a {@code java.util.Date} field cannot be
     * compared to a {@link LocalDateTime}.
     *
     * @param type the type of the filtered field
     * @param value the computed range bound
     * @return the bound as an instance of {@code type}, or {@code null} if {@code type} is not a supported
     *         date/time type
     */
    protected Object toTemporalValue(Class<?> type, LocalDateTime value) {
        if (type == LocalDateTime.class) {
            return value;
        }
        if (type == LocalDate.class) {
            return value.toLocalDate();
        }
        if (type == LocalTime.class) {
            return value.toLocalTime();
        }
        // the java.sql types are subclasses of java.util.Date, so they have to be checked first
        if (type == java.sql.Date.class) {
            return java.sql.Date.valueOf(value.toLocalDate());
        }
        if (type == java.sql.Time.class) {
            return java.sql.Time.valueOf(value.toLocalTime());
        }
        if (type == java.sql.Timestamp.class) {
            return java.sql.Timestamp.valueOf(value);
        }
        if (Date.class.isAssignableFrom(type)) {
            return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
        }
        if (type == Instant.class) {
            return value.atZone(ZoneId.systemDefault()).toInstant();
        }
        if (type == OffsetDateTime.class) {
            return value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
        }
        if (type == ZonedDateTime.class) {
            return value.atZone(ZoneId.systemDefault());
        }
        if (Calendar.class.isAssignableFrom(type)) {
            return GregorianCalendar.from(value.atZone(ZoneId.systemDefault()));
        }
        return null;
    }

    /**
     * The filter value of the relative date/time match modes is a plain count of days/minutes/hours, normally
     * already parsed to an {@link Integer} by {@code UITable}; a raw {@link String} is tolerated too, e.g., when
     * the filter value is set programmatically.
     *
     * @param filterValue the filter value
     * @return the count, or {@code null} if it is not parsable
     */
    protected Integer toIntegerFilterValue(Object filterValue) {
        if (filterValue instanceof Number) {
            return ((Number) filterValue).intValue();
        }
        if (filterValue instanceof String) {
            try {
                return Integer.valueOf(((String) filterValue).trim());
            }
            catch (NumberFormatException e) {
                LOGGER.log(Level.FINE, e, () -> "Filter value '" + filterValue + "' is not a number!");
            }
        }
        return null;
    }

    /**
     * Splits a multivalue filter into its individual values: a bean-bound {@code Collection}/array as-is, or
     * free text typed next to the match mode dropdown split on comma (e.g., {@code "Acme, Globex"}), the way the
     * in-memory constraints of the "contains any/all/none" match modes read their filter value.
     *
     * @param filterValue the filter value
     * @return the individual values
     */
    protected Collection<?> filterValueAsTokens(Object filterValue) {
        if (filterValue == null) {
            return Collections.emptyList();
        }
        if (filterValue.getClass().isArray()) {
            return Arrays.asList((Object[]) filterValue);
        }
        if (filterValue instanceof Collection) {
            return (Collection<?>) filterValue;
        }
        if (filterValue instanceof String) {
            return Arrays.stream(((String) filterValue).split(","))
                    .map(String::trim)
                    .filter(LangUtils::isNotBlank)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(filterValue);
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

        // join if required; e.g., company.name -> join to company and get "name" field from the joined table
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
