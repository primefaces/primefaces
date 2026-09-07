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
import org.primefaces.model.jpa.Employee;
import org.primefaces.model.jpa.Employee.Department;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the criteria queries {@link JPALazyDataModel} builds against a real JPA provider and an in-memory
 * database, so that each {@link MatchMode} is asserted on what it actually selects - a mocked
 * {@code EntityManager} would happily accept a predicate no database can execute.
 * <p>
 * The fixture holds one field per {@code filterValueType} preset (see {@link Employee}) and nine rows:
 * <pre>
 * id name           salary active reviewDate    lastLogin  startTime  legacyDate  department  tags
 *  1 "Mike Master"    5000 true   today         now-10min  now-10min  today 23:30 ENGINEERING java, sql
 *  2 "Susan Pepper"   3000 false  yesterday     now-3h     now-3h     yesterday   MARKETING   java
 *  3 null             null null   null          null       null       null        null        -
 *  4 "   " (blank)    7000 true   tomorrow      now+30min  now+30min  tomorrow    SALES       sql, xml
 *  5 "Chris Clark"    9000 false  today-1week   now-2d     null       null        ENGINEERING python
 *  6 "Trish Mayer"    4000 true   today+1week   now+2d     null       null        MARKETING   -
 *  7 "James Bush"     2000 false  today-1year   now-400d   null       null        SALES       -
 *  8 "Mary March"     6000 true   today+1month  now+400d   null       null        ENGINEERING java, xml
 *  9 "Nina Night"     1000 false  null          today23:30 23:30      today 23:30 MARKETING   -
 * </pre>
 * The dates are computed from "now" rather than hardcoded, as the relative date match modes are resolved
 * against {@code LocalDate.now()} at query time; a +/-1 week/month/year offset always lands in the
 * corresponding "last"/"next" bucket, wherever "today" falls within its own week/month/year.
 */
class JPALazyDataModelTest {

    private static EntityManagerFactory emf;

    private FacesContext context;
    private EntityManager em;
    private JPALazyDataModel<Employee> model;

    @BeforeAll
    static void initDatabase() {
        emf = Persistence.createEntityManagerFactory("primefaces-test");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        seed(em);
        em.getTransaction().commit();
        em.close();
    }

    @AfterAll
    static void closeDatabase() {
        emf.close();
    }

    @BeforeEach
    void setUp() {
        new FacesContextMock();
        // registers itself as the current FacesContext, which JPALazyDataModel needs for the
        // PropertyDescriptorResolver, the locale and the filter value conversion
        context = new FacesContextMock();
        em = emf.createEntityManager();
        model = JPALazyDataModel.<Employee>builder()
                .entityClass(Employee.class)
                .entityManager(() -> em)
                .build();
    }

    @AfterEach
    void tearDown() {
        em.close();
        context.release();
    }

    private static void seed(EntityManager em) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        em.persist(employee(1L, "Mike Master", 5000, true, today, now.minusMinutes(10), LocalTime.now().minusMinutes(10),
                today.atTime(23, 30), Department.ENGINEERING, "java", "sql"));
        em.persist(employee(2L, "Susan Pepper", 3000, false, today.minusDays(1), now.minusHours(3), LocalTime.now().minusHours(3),
                today.minusDays(1).atTime(12, 0), Department.MARKETING, "java"));
        em.persist(employee(3L, null, null, null, null, null, null,
                null, null));
        em.persist(employee(4L, "   ", 7000, true, today.plusDays(1), now.plusMinutes(30), LocalTime.now().plusMinutes(30),
                today.plusDays(1).atTime(8, 0), Department.SALES, "sql", "xml"));
        em.persist(employee(5L, "Chris Clark", 9000, false, today.minusWeeks(1), now.minusDays(2), null,
                null, Department.ENGINEERING, "python"));
        em.persist(employee(6L, "Trish Mayer", 4000, true, today.plusWeeks(1), now.plusDays(2), null,
                null, Department.MARKETING));
        em.persist(employee(7L, "James Bush", 2000, false, today.minusYears(1), now.minusDays(400), null,
                null, Department.SALES));
        em.persist(employee(8L, "Mary March", 6000, true, today.plusMonths(1), now.plusDays(400), null,
                null, Department.ENGINEERING, "java", "xml"));
        // the "end of the last day of the range" row: with an inclusive BETWEEN on midnight instead of a
        // half-open range, none of the date match modes would ever match its timestamps
        em.persist(employee(9L, "Nina Night", 1000, false, null, today.atTime(23, 30), LocalTime.of(23, 30),
                today.atTime(23, 30), Department.MARKETING));
    }

    private static Employee employee(Long id, String name, Integer salary, Boolean active, LocalDate reviewDate,
            LocalDateTime lastLogin, LocalTime startTime, LocalDateTime legacyDate, Department department, String... tags) {

        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setSalary(salary);
        employee.setActive(active);
        employee.setReviewDate(reviewDate);
        employee.setLastLogin(lastLogin);
        employee.setStartTime(startTime);
        employee.setLegacyDate(legacyDate == null ? null : Date.from(legacyDate.atZone(ZoneId.systemDefault()).toInstant()));
        employee.setDepartment(department);
        employee.setTags(new LinkedHashSet<>(Arrays.asList(tags)));
        return employee;
    }

    private static FilterMeta filter(String field, MatchMode matchMode) {
        return filter(field, matchMode, null);
    }

    private static FilterMeta filter(String field, MatchMode matchMode, Object filterValue) {
        return FilterMeta.builder()
                .field(field)
                .matchMode(matchMode)
                .filterValue(filterValue)
                .build();
    }

    private static Map<String, FilterMeta> filterBy(FilterMeta... filters) {
        Map<String, FilterMeta> filterBy = new LinkedHashMap<>();
        for (FilterMeta filter : filters) {
            filterBy.put(filter.getField(), filter);
        }
        return filterBy;
    }

    private Set<Long> load(FilterMeta... filters) {
        return load(model, filters);
    }

    private Set<Long> load(JPALazyDataModel<Employee> model, FilterMeta... filters) {
        List<Employee> employees = model.load(0, 100, Collections.emptyMap(), filterBy(filters));
        return employees.stream().map(Employee::getId).collect(Collectors.toCollection(TreeSet::new));
    }

    private static void assertIds(Set<Long> actual, Long... expected) {
        assertEquals(new TreeSet<>(Arrays.asList(expected)), actual);
    }

    // ------------------------------------------------------------------------------------------------------
    // the match modes that were already supported - regression cover for the mode-aware filter value
    // conversion, which every one of them now runs through
    // ------------------------------------------------------------------------------------------------------

    @Test
    void contains() {
        assertIds(load(filter("name", MatchMode.CONTAINS, "Master")), 1L);
    }

    @Test
    void startsWith() {
        assertIds(load(filter("name", MatchMode.STARTS_WITH, "Mi")), 1L);
    }

    @Test
    void equalsNumeric() {
        // the filter value arrives as a String and has to be converted to the Integer type of the field
        assertIds(load(filter("salary", MatchMode.EQUALS, "5000")), 1L);
    }

    @Test
    void greaterThan() {
        assertIds(load(filter("salary", MatchMode.GREATER_THAN, 6000)), 4L, 5L);
    }

    @Test
    void between() {
        assertIds(load(filter("salary", MatchMode.BETWEEN, Arrays.asList(3000, 5000))), 1L, 2L, 6L);
    }

    @Test
    void in() {
        assertIds(load(filter("salary", MatchMode.IN, Arrays.asList(3000, 9000))), 2L, 5L);
    }

    @Test
    void equalsEnum() {
        assertIds(load(filter("department", MatchMode.EQUALS, Department.ENGINEERING)), 1L, 5L, 8L);
    }

    @Test
    void twoFiltersAreCombinedWithAnd() {
        assertIds(load(filter("department", MatchMode.EQUALS, Department.ENGINEERING),
                filter("salary", MatchMode.GREATER_THAN, 5500)), 5L, 8L);
    }

    // ------------------------------------------------------------------------------------------------------
    // is null / is empty
    // ------------------------------------------------------------------------------------------------------

    @Test
    void isNull() {
        assertIds(load(filter("name", MatchMode.IS_NULL)), 3L);
    }

    @Test
    void notNull_appliesEvenThoughNoFilterValueIsTyped() {
        assertIds(load(filter("name", MatchMode.NOT_NULL)), 1L, 2L, 4L, 5L, 6L, 7L, 8L, 9L);
    }

    @Test
    void isEmpty_string_matchesNullAndBlank() {
        assertIds(load(filter("name", MatchMode.IS_EMPTY)), 3L, 4L);
    }

    @Test
    void notEmpty_string() {
        assertIds(load(filter("name", MatchMode.NOT_EMPTY)), 1L, 2L, 5L, 6L, 7L, 8L, 9L);
    }

    @Test
    void isEmpty_collection() {
        assertIds(load(filter("tags", MatchMode.IS_EMPTY)), 3L, 6L, 7L, 9L);
    }

    @Test
    void notEmpty_collection() {
        assertIds(load(filter("tags", MatchMode.NOT_EMPTY)), 1L, 2L, 4L, 5L, 8L);
    }

    // ------------------------------------------------------------------------------------------------------
    // boolean
    // ------------------------------------------------------------------------------------------------------

    @Test
    void isTrue() {
        assertIds(load(filter("active", MatchMode.IS_TRUE)), 1L, 4L, 6L, 8L);
    }

    @Test
    void isFalse() {
        assertIds(load(filter("active", MatchMode.IS_FALSE)), 2L, 5L, 7L, 9L);
    }

    @Test
    void isNull_boolean() {
        assertIds(load(filter("active", MatchMode.IS_NULL)), 3L);
    }

    // ------------------------------------------------------------------------------------------------------
    // relative date match modes, on a plain LocalDate field
    // ------------------------------------------------------------------------------------------------------

    @Test
    void isToday() {
        assertIds(load(filter("reviewDate", MatchMode.IS_TODAY)), 1L);
    }

    @Test
    void isYesterday() {
        assertIds(load(filter("reviewDate", MatchMode.IS_YESTERDAY)), 2L);
    }

    @Test
    void isTomorrow() {
        assertIds(load(filter("reviewDate", MatchMode.IS_TOMORROW)), 4L);
    }

    @Test
    void isThisWeek() {
        // whether yesterday/tomorrow fall into this week depends on today's day of the week, so only the
        // unambiguous rows are asserted
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_THIS_WEEK));
        assertTrue(ids.contains(1L), "today");
        assertFalse(ids.contains(5L), "last week");
        assertFalse(ids.contains(6L), "next week");
        assertFalse(ids.contains(7L), "last year");
    }

    @Test
    void isLastWeek() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_LAST_WEEK));
        assertTrue(ids.contains(5L), "last week");
        assertFalse(ids.contains(1L), "today");
        assertFalse(ids.contains(6L), "next week");
    }

    @Test
    void countExecutesSingleQuery() {
        Fixture fixture = createMocksForCount();

        int count = fixture.model.count(Collections.emptyMap());

        assertEquals(5, count);
        Mockito.verify(fixture.countTypedQuery, Mockito.times(1)).getSingleResult();
    void isNextWeek() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_NEXT_WEEK));
        assertTrue(ids.contains(6L), "next week");
        assertFalse(ids.contains(1L), "today");
        assertFalse(ids.contains(5L), "last week");
    }

    @Test
    void loadExecutesSingleQuery() {
        Fixture fixture = createMocksForLoad();

        List<TestEntity> result = fixture.loadAndWrap(0, 3);

        assertEquals(3, result.size());
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).setFirstResult(0);
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).setMaxResults(3);
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(1)).getResultList();
    void isThisMonth() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_THIS_MONTH));
        assertTrue(ids.contains(1L), "today");
        assertFalse(ids.contains(8L), "next month");
        assertFalse(ids.contains(7L), "last year");
    }

    @Test
    void getRowDataWithoutConverterResolvesFromWrappedData() {
        Fixture fixture = createMocksForGetRowDataWithoutConverter();

        TestEntity entity = fixture.model.getRowData("1");

        assertNotNull(entity);
        assertEquals("1", entity.getId());
        Mockito.verify(fixture.entityTypedQuery, Mockito.never()).getSingleResult();
    void isNextMonth() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_NEXT_MONTH));
        assertTrue(ids.contains(8L), "next month");
        assertFalse(ids.contains(1L), "today");
    }

    @Test
    void isThisQuarter() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_THIS_QUARTER));
        assertTrue(ids.contains(1L), "today");
        assertFalse(ids.contains(7L), "last year");
    void rowSelectionResolvesFromLastLoadedPage() {
        // Mirrors the full server-side call sequence of DataTable050Test#rowSelection:
        // initial render: count() + load(first=0, size=3)
        // checkbox click decodes selection: getRowData(rowKey=1) + getRowData(rowKey=3)
        // form submit re-renders table: count() + load(first=0, size=3)
        Fixture fixture = createMocksForRowSelectionWorkflow();

        // Arrange - initial render
        fixture.countLoadAndWrap(0, 3);

        // Act - row selection decodes each selected row key via a DB lookup
        TestEntity entity1 = fixture.model.getRowData("1");
        TestEntity entity3 = fixture.model.getRowData("3");

        // Assert - selection
        assertNotNull(entity1);
        assertEquals("1", entity1.getId());
        assertNotNull(entity3);
        assertEquals("3", entity3.getId());

        // Act - form submit re-renders the table
        fixture.countLoadAndWrap(0, 3);

        // Assert - full call counts over the whole workflow
        Mockito.verify(fixture.countTypedQuery, Mockito.times(2)).getSingleResult();
        Mockito.verify(fixture.entityTypedQuery, Mockito.times(2)).getResultList();
        Mockito.verify(fixture.entityTypedQuery, Mockito.never()).getSingleResult();
    }

    @Test
    void isThisYear() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_THIS_YEAR));
        assertTrue(ids.contains(1L), "today");
        assertFalse(ids.contains(7L), "last year");
    }

    @Test
    void isLastYear() {
        Set<Long> ids = load(filter("reviewDate", MatchMode.IS_LAST_YEAR));
        assertTrue(ids.contains(7L), "last year");
        assertFalse(ids.contains(1L), "today");
    }

    @Test
    void lastNDays() {
        assertIds(load(filter("reviewDate", MatchMode.LAST_N_DAYS, 3)), 1L, 2L);
    }

    @Test
    void nextNDays() {
        assertIds(load(filter("reviewDate", MatchMode.NEXT_N_DAYS, 3)), 1L, 4L);
    }

    @Test
    void relativeDate() {
        assertIds(load(filter("reviewDate", MatchMode.RELATIVE_DATE, 3)), 1L, 2L, 4L);
    @SuppressWarnings("unchecked")
    void getRowDataWithConverterDoesNotQueryDatabase() {
        Fixture fixture = createMocksForGetRowDataWithConverter();

        TestEntity entity = fixture.model.getRowData("1");

        assertNotNull(entity);
        assertEquals("1", entity.getId());
        Mockito.verify(fixture.entityManager, Mockito.never()).createQuery(Mockito.any(CriteriaQuery.class));
        Mockito.verify(fixture.criteriaBuilder, Mockito.never()).createQuery(Mockito.any(Class.class));
    }

    private static JPALazyDataModel<TestEntity> createModel(EntityManager em, Converter<TestEntity> converter) {
        JPALazyDataModel<TestEntity> model = new JPALazyDataModel<>();
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
        return model;
    }

    @SuppressWarnings("unchecked")
    private static JpaContext createJpaContext() {
        EntityManager em = Mockito.mock(EntityManager.class);
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Root<TestEntity> root = Mockito.mock(Root.class);
        Mockito.when(em.getCriteriaBuilder()).thenReturn(cb);
        return new JpaContext(em, cb, root);
    }

    @SuppressWarnings("unchecked")
    private static Fixture createMocksForCount() {
        JpaContext ctx = createJpaContext();
        CriteriaQuery<Long> countQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = Mockito.mock(TypedQuery.class);
        Expression<Long> countExpression = Mockito.mock(Expression.class);

        Mockito.when(ctx.criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        Mockito.when(countQuery.from(TestEntity.class)).thenReturn(ctx.root);
        Mockito.when(ctx.criteriaBuilder.count(ctx.root)).thenReturn(countExpression);
        Mockito.when(countQuery.select(countExpression)).thenReturn(countQuery);
        Mockito.when(ctx.entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        Mockito.when(countTypedQuery.getSingleResult()).thenReturn(5L);

        return new Fixture(createModel(ctx.entityManager, null), ctx.entityManager, ctx.criteriaBuilder,
                countTypedQuery, null, null);
    }

    @SuppressWarnings("unchecked")
    private static Fixture createMocksForLoad() {
        JpaContext ctx = createJpaContext();
        CriteriaQuery<TestEntity> entityQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<TestEntity> entityTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(ctx.criteriaBuilder.createQuery(TestEntity.class)).thenReturn(entityQuery);
        Mockito.when(entityQuery.from(TestEntity.class)).thenReturn(ctx.root);
        Mockito.when(entityQuery.select(ctx.root)).thenReturn(entityQuery);
        Mockito.when(ctx.entityManager.createQuery(entityQuery)).thenReturn(entityTypedQuery);
        Mockito.when(entityTypedQuery.getResultList()).thenReturn(Arrays.asList(
                new TestEntity("1", "a"),
                new TestEntity("2", "b"),
                new TestEntity("3", "c")));

        return new Fixture(createModel(ctx.entityManager, null), ctx.entityManager, ctx.criteriaBuilder,
                null, entityQuery, entityTypedQuery);
    }

    @Test
    void lastNDays_unparsableCountMatchesNothing() {
        assertTrue(load(filter("reviewDate", MatchMode.LAST_N_DAYS, "not a number")).isEmpty());
    @SuppressWarnings("unchecked")
    private static Fixture createMocksForGetRowDataWithoutConverter() {
        JpaContext ctx = createJpaContext();
        CriteriaQuery<TestEntity> entityQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<TestEntity> entityTypedQuery = Mockito.mock(TypedQuery.class);

        Mockito.when(ctx.criteriaBuilder.createQuery(TestEntity.class)).thenReturn(entityQuery);
        Mockito.when(entityQuery.from(TestEntity.class)).thenReturn(ctx.root);
        Mockito.when(entityQuery.select(ctx.root)).thenReturn(entityQuery);
        Mockito.when(ctx.entityManager.createQuery(entityQuery)).thenReturn(entityTypedQuery);
        Mockito.when(entityTypedQuery.getResultList()).thenReturn(
                Arrays.asList(new TestEntity("1", "a"), new TestEntity("2", "b"), new TestEntity("3", "c")));

        JPALazyDataModel<TestEntity> model = createModel(ctx.entityManager, null);
        // Simulate what DataTable does after load(): set wrappedData so getRowData() resolves in-memory
        model.setWrappedData(model.load(0, 3, Collections.emptyMap(), Collections.emptyMap()));

        return new Fixture(model, ctx.entityManager, ctx.criteriaBuilder, null, entityQuery, entityTypedQuery);
    }

    // ------------------------------------------------------------------------------------------------------
    // relative date match modes on fields carrying a time component: the range has to be half open, so that
    // the whole of the last day of the range matches, not just its midnight
    // ------------------------------------------------------------------------------------------------------

    @Test
    void isToday_dateTimeFieldIncludesEndOfDay() {
        Set<Long> ids = load(filter("lastLogin", MatchMode.IS_TODAY));
        assertTrue(ids.contains(9L), "today 23:30");
        assertFalse(ids.contains(7L), "400 days ago");
        assertFalse(ids.contains(8L), "400 days ahead");
    }
    @SuppressWarnings("unchecked")
    private static Fixture createMocksForRowSelectionWorkflow() {
        JpaContext ctx = createJpaContext();
        // count query
        CriteriaQuery<Long> countQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<Long> countTypedQuery = Mockito.mock(TypedQuery.class);
        Expression<Long> countExpression = Mockito.mock(Expression.class);
        Mockito.when(ctx.criteriaBuilder.createQuery(Long.class)).thenReturn(countQuery);
        Mockito.when(countQuery.from(TestEntity.class)).thenReturn(ctx.root);
        Mockito.when(ctx.criteriaBuilder.count(ctx.root)).thenReturn(countExpression);
        Mockito.when(countQuery.select(countExpression)).thenReturn(countQuery);
        Mockito.when(ctx.entityManager.createQuery(countQuery)).thenReturn(countTypedQuery);
        Mockito.when(countTypedQuery.getSingleResult()).thenReturn(100L);

    @Test
    void isToday_legacyDateFieldIncludesEndOfDay() {
        // java.util.Date, whose criteria literals cannot be a LocalDate/LocalDateTime
        Set<Long> ids = load(filter("legacyDate", MatchMode.IS_TODAY));
        assertTrue(ids.contains(9L), "today 23:30");
        assertFalse(ids.contains(2L), "yesterday 12:00");
        assertFalse(ids.contains(4L), "tomorrow 08:00");
    }

    @Test
    void isThisYear_dateTimeField() {
        Set<Long> ids = load(filter("lastLogin", MatchMode.IS_THIS_YEAR));
        assertTrue(ids.contains(9L), "today 23:30");
        assertFalse(ids.contains(7L), "400 days ago");
    }

    @Test
    void isToday_timeOnlyFieldMatchesNothing() {
        // a bare time of day has no date to compare a date range to
        assertTrue(load(filter("startTime", MatchMode.IS_TODAY)).isEmpty());
    }

    // ------------------------------------------------------------------------------------------------------
    // last/next N minutes/hours
    // ------------------------------------------------------------------------------------------------------

    @Test
    void lastNMinutes_dateTime() {
        Set<Long> ids = load(filter("lastLogin", MatchMode.LAST_N_MINUTES, 15));
        assertTrue(ids.contains(1L), "10 minutes ago");
        assertFalse(ids.contains(2L), "3 hours ago");
        assertFalse(ids.contains(4L), "in 30 minutes");
    }

    @Test
    void nextNMinutes_dateTime() {
        Set<Long> ids = load(filter("lastLogin", MatchMode.NEXT_N_MINUTES, 45));
        assertTrue(ids.contains(4L), "in 30 minutes");
        assertFalse(ids.contains(1L), "10 minutes ago");
    }

    @Test
    void lastNHours_dateTime() {
        Set<Long> ids = load(filter("lastLogin", MatchMode.LAST_N_HOURS, 4));
        assertTrue(ids.contains(1L), "10 minutes ago");
        assertTrue(ids.contains(2L), "3 hours ago");
        assertFalse(ids.contains(5L), "2 days ago");
    }

    @Test
    void nextNHours_dateTime() {
        Set<Long> ids = load(filter("lastLogin", MatchMode.NEXT_N_HOURS, 1));
        assertTrue(ids.contains(4L), "in 30 minutes");
        assertFalse(ids.contains(6L), "in 2 days");
    }

    @Test
    void lastNMinutes_timeOnlyField() {
        // a bare time of day is a cyclic clock; the window may wrap past midnight, which is covered
        // deterministically by RelativeMinutesOrHoursFilterConstraintTest with a fixed clock
        Set<Long> ids = load(filter("startTime", MatchMode.LAST_N_MINUTES, 15));
        assertTrue(ids.contains(1L), "10 minutes ago");
        assertFalse(ids.contains(2L), "3 hours ago");
    }

    @Test
    void lastNMinutes_unparsableCountMatchesNothing() {
        assertTrue(load(filter("lastLogin", MatchMode.LAST_N_MINUTES, "not a number")).isEmpty());
    }

    // ------------------------------------------------------------------------------------------------------
    // the "array" match modes, on a mapped collection
    // ------------------------------------------------------------------------------------------------------

    @Test
    void arrayContains() {
        assertIds(load(filter("tags", MatchMode.ARRAY_CONTAINS, "java")), 1L, 2L, 8L);
    }

    @Test
    void arrayNotContains() {
        assertIds(load(filter("tags", MatchMode.ARRAY_NOT_CONTAINS, "java")), 3L, 4L, 5L, 6L, 7L, 9L);
    }

    @Test
    void containsAny() {
        assertIds(load(filter("tags", MatchMode.CONTAINS_ANY, Arrays.asList("java", "xml"))), 1L, 2L, 4L, 8L);
    }

    @Test
    void containsAny_commaSeparatedText() {
        assertIds(load(filter("tags", MatchMode.CONTAINS_ANY, "java, xml")), 1L, 2L, 4L, 8L);
    }

    @Test
    void containsAll() {
        assertIds(load(filter("tags", MatchMode.CONTAINS_ALL, Arrays.asList("java", "xml"))), 8L);
    }

    @Test
    void containsNone() {
        assertIds(load(filter("tags", MatchMode.CONTAINS_NONE, Arrays.asList("java", "xml"))), 3L, 5L, 6L, 7L, 9L);
    }

    // ------------------------------------------------------------------------------------------------------
    // regex, count and the placeholder mode
    // ------------------------------------------------------------------------------------------------------

    @Test
    void matchesRegex_unsupportedByDefault() {
        assertThrows(UnsupportedOperationException.class,
                () -> load(filter("name", MatchMode.MATCHES_REGEX, "^M.*")));
    }

    @Test
    void matchesRegex_worksWhenOverridden() {
        JPALazyDataModel<Employee> h2Model = new JPALazyDataModel.Builder<Employee, H2RegexModel>(new H2RegexModel())
                .entityClass(Employee.class)
                .entityManager(() -> em)
                .build();

        assertIds(load(h2Model, filter("name", MatchMode.MATCHES_REGEX, "^M.*")), 1L, 8L);
    }

    @Test
    void all_isNoFilterAtAll() {
        assertIds(load(filter("name", MatchMode.ALL)), 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    }

    @Test
    void count_appliesTheSameFilters() {
        assertEquals(4, model.count(filterBy(filter("active", MatchMode.IS_TRUE))));
        assertEquals(3, model.count(filterBy(filter("tags", MatchMode.ARRAY_CONTAINS, "java"))));
        assertEquals(9, model.count(filterBy()));
    }

    /**
     * H2 spells regular expression matching {@code REGEXP_LIKE(value, pattern)}. Note that it - like most
     * database regex functions - matches a substring, while the in-memory {@code MatchesRegexFilterConstraint}
     * requires the whole value to match.
     */
    private static class H2RegexModel extends JPALazyDataModel<Employee> {

        private static final long serialVersionUID = 1L;
        // load + getRowData query (share the same CriteriaQuery<TestEntity> mock)
        CriteriaQuery<TestEntity> entityQuery = Mockito.mock(CriteriaQuery.class);
        TypedQuery<TestEntity> entityTypedQuery = Mockito.mock(TypedQuery.class);
        List<TestEntity> page1 = Arrays.asList(new TestEntity("1", "a"), new TestEntity("2", "b"), new TestEntity("3", "c"));

        Mockito.when(ctx.criteriaBuilder.createQuery(TestEntity.class)).thenReturn(entityQuery);
        Mockito.when(entityQuery.from(TestEntity.class)).thenReturn(ctx.root);
        Mockito.when(entityQuery.select(ctx.root)).thenReturn(entityQuery);
        Mockito.when(ctx.entityManager.createQuery(entityQuery)).thenReturn(entityTypedQuery);
        Mockito.when(entityTypedQuery.getResultList()).thenReturn(page1);

        return new Fixture(createModel(ctx.entityManager, null), ctx.entityManager, ctx.criteriaBuilder,
                countTypedQuery, entityQuery, entityTypedQuery);
    }

    private static Fixture createMocksForGetRowDataWithConverter() {
        JpaContext ctx = createJpaContext();
        return new Fixture(createModel(ctx.entityManager, new TestEntityConverter()), ctx.entityManager,
                ctx.criteriaBuilder, null, null, null);
    }

    private static class JpaContext {
        final EntityManager entityManager;
        final CriteriaBuilder criteriaBuilder;
        final Root<TestEntity> root;

        JpaContext(EntityManager entityManager, CriteriaBuilder criteriaBuilder, Root<TestEntity> root) {
            this.entityManager = entityManager;
            this.criteriaBuilder = criteriaBuilder;
            this.root = root;
        }
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

        List<TestEntity> loadAndWrap(int first, int pageSize) {
            // Mirror what DataTable does after load(): set wrappedData so that getRowData()
            // can resolve row keys in-memory without an extra database query.
            List<TestEntity> loaded = model.load(first, pageSize, Collections.emptyMap(), Collections.emptyMap());
            model.setWrappedData(loaded);
            return loaded;
        }

        void countLoadAndWrap(int first, int pageSize) {
            model.count(Collections.emptyMap());
            loadAndWrap(first, pageSize);
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

                                /**
                                 * H2 spells regular expression matching {@code REGEXP_LIKE(value, pattern)}. Note that it - like most
                                 * database regex functions - matches a substring, while the in-memory {@code MatchesRegexFilterConstraint}
                                 * requires the whole value to match.
                                 */
                                private static class H2RegexModel extends JPALazyDataModel<Employee> {

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    protected Predicate createRegexPredicate(CriteriaBuilder cb, Expression<String> fieldExpression, String pattern) {
                                        return cb.isTrue(cb.function("regexp_like", Boolean.class, fieldExpression, cb.literal(pattern)));
                                    }
}
