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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.HeaderCell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DataTable: filterValueType lets the end user pick a filter comparator
 * (e.g., equals, not equals, less than, greater than) at runtime from a dropdown next to the filter input.
 */
@Tag("DataTable-filter")
class DataTable051Test extends AbstractDataTableTest {

    private static final DateTimeFormatter REVIEW_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // mirrors DataTable051#init() exactly - the synthetic null/blank-lastName rows ("is (not)
    // empty" / "is (not) null" need one of each to be distinguishable) also have ids > 5, so every existing
    // numeric "ID" filter assertion below must account for them, too.
    protected final List<Employee> employees = buildEmployeesWithSyntheticRows();

    private static List<Employee> buildEmployeesWithSyntheticRows() {
        List<Employee> list = new ArrayList<>(new EmployeeService().getEmployees());

        // "true"/"false"/"is (not) null" boolean modes need a mix of true, false, and untouched (null)
        list.get(0).setActive(true);   // id 1, Mike Master
        list.get(1).setActive(false);  // id 2, Susan Pepper
        list.get(3).setActive(true);   // id 4, Chris Clark
        list.get(4).setActive(false);  // id 5, James Bush
        list.get(6).setActive(true);   // id 11, Margret Johnson
        list.get(7).setActive(false);  // id 533, Mary March

        // relative-date match modes - computed against LocalDate.now() the same way as DataTable051#init()
        LocalDate today = LocalDate.now();
        list.get(0).setReviewDate(today);                  // id 1, Mike Master - today
        list.get(1).setReviewDate(today.minusDays(1));      // id 2, Susan Pepper - yesterday
        list.get(3).setReviewDate(today.plusDays(1));       // id 4, Chris Clark - tomorrow
        list.get(4).setReviewDate(today.minusWeeks(1));     // id 5, James Bush - last week
        list.get(5).setReviewDate(today.plusWeeks(1));      // id 6, Trish Mayer - next week
        list.get(6).setReviewDate(today.minusMonths(1));    // id 11, Margret Johnson - last month
        list.get(7).setReviewDate(today.plusMonths(1));     // id 533, Mary March - next month

        list.add(Employee.builder().id(900).firstName("Nolan").lastName(null).birthDate(LocalDate.of(1975, 6, 15)).build());
        list.add(Employee.builder().id(901).firstName("Blanche").lastName("").birthDate(LocalDate.of(1985, 9, 20))
                .reviewDate(today.minusDays(100)).build());
        list.add(Employee.builder().id(902).firstName("Yolanda").lastName("Young").reviewDate(today.minusYears(1)).build());
        list.add(Employee.builder().id(903).firstName("Zack").lastName("Zimmer").reviewDate(today.plusYears(1)).build());

        // "last/next N minutes/hours" - mirrors DataTable051#init() exactly, same offset-from-"now"
        // rationale, including the truncation to whole seconds (see DataTable051#init() for why)
        LocalTime now = LocalTime.now().withNano(0);
        LocalDateTime nowDateTime = LocalDateTime.now().withNano(0);
        list.get(0).setCheckInTime(now.minusMinutes(10));
        list.get(0).setLastLoginDateTime(nowDateTime.minusMinutes(10));
        list.get(1).setCheckInTime(now.minusHours(1));
        list.get(1).setLastLoginDateTime(nowDateTime.minusHours(1));
        list.get(3).setCheckInTime(now.plusMinutes(10));
        list.get(3).setLastLoginDateTime(nowDateTime.plusMinutes(10));
        list.get(4).setCheckInTime(now.plusHours(3));
        list.get(4).setLastLoginDateTime(nowDateTime.plusHours(3));

        // "array" preset - mirrors DataTable051#init() exactly
        list.get(0).setSkills(List.of("Java", "Leadership"));
        list.get(1).setSkills(List.of("Communication", "Java"));
        list.get(2).setSkills(List.of("Java", "Python", "Go"));
        list.get(3).setSkills(List.of("Python", "Go"));
        list.get(4).setSkills(List.of("Java"));
        list.get(5).setSkills(List.of("Testing"));
        list.get(6).setSkills(List.of());
        // id 533, Mary March - skills left null (never assigned)
        return list;
    }

    @Test
    @Order(1)
    @DisplayName("DataTable: numeric filterValueType defaults to the column's filterMatchMode")
    void numericFilterDefaultMatchMode(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - column declares filterMatchMode="gt" as its initial/default comparator
        dataTable.filter("ID", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() > 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: numeric filterValueType lets the user switch the comparator")
    void numericFilterSwitchMatchMode(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filter("ID", "5");

        // Act - switch comparator to "equals"
        dataTable.filterMatchMode("ID", "equals");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() == 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch comparator to "lt"
        dataTable.filterMatchMode("ID", "lt");

        // Assert
        employeesFiltered = employees.stream().filter(e -> e.getId() < 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch comparator to "notEquals"
        dataTable.filterMatchMode("ID", "notEquals");

        // Assert
        employeesFiltered = employees.stream().filter(e -> e.getId() != 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: numeric filterValueType comparator survives an unrelated AJAX update")
    void numericFilterMatchModeSurvivesUpdate(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filter("ID", "5");
        dataTable.filterMatchMode("ID", "gte");

        // Act
        page.buttonUpdate.click();

        // Assert - the "gte" comparator (and the filter value) must still be applied after the update
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() >= 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: filter value input stays usable next to a narrow column's match-mode dropdown")
    void numericFilterInputStaysUsableInNarrowColumn(Page page) {
        // Arrange - "ID" is a narrow (style="width:120px"), fixed-layout column: the match-mode <select> and the
        // value <input> share the same cramped space, which is exactly what made the value input collapse to
        // zero width in the showcase's "Activity" column.
        DataTable dataTable = page.dataTable;
        HeaderCell idHeader = dataTable.getHeader().getCell("ID").orElseThrow();

        // Act - switch to the option with the longest label so the <select> is at its widest
        dataTable.filterMatchMode("ID", "gte");

        // Assert - the value input must still render with a real, usable width, not be squeezed to ~0
        int filterInputWidth = idHeader.getColumnFilter().getSize().getWidth();
        assertTrue(filterInputWidth > 20, "Filter value input width was " + filterInputWidth
                + "px - too narrow to be usable, likely squeezed out by the match-mode dropdown");

        // Act - and it must still be possible to actually type a value into it
        dataTable.filter("ID", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() >= 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: text filterValueType defaults to the column's filterMatchMode")
    void textFilterDefaultMatchMode(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - column declares filterMatchMode="contains" as its initial/default comparator
        dataTable.filter("last name", "ar");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null && e.getLastName().toLowerCase().contains("ar"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: text filterValueType lets the user switch the comparator")
    void textFilterSwitchMatchMode(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filter("last name", "ma");

        // Act - switch comparator to "startsWith"
        dataTable.filterMatchMode("last name", "startsWith");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null && e.getLastName().toLowerCase().startsWith("ma"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch comparator to "equals" with an exact value
        dataTable.filterMatchMode("last name", "equals");
        dataTable.filter("last name", "Clark");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null && e.getLastName().equalsIgnoreCase("Clark"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: numeric and text filterValueType both use spelled-out labels in the "
            + "filter match-mode overlay menu")
    void matchModeLabels(Page page) {
        // Arrange
        HeaderCell idHeader = page.dataTable.getHeader().getCell("ID").orElseThrow();
        HeaderCell lastNameHeader = page.dataTable.getHeader().getCell("last name").orElseThrow();

        // Act
        List<String> idOptionLabels = idHeader.getFilterMatchModeLabels();
        List<String> lastNameOptionLabels = lastNameHeader.getFilterMatchModeLabels();

        // Assert - the overlay menu always shows spelled-out labels (e.g. "Equals" rather than "=") now that
        // it's no longer a cramped inline <select> competing with the value input for space
        assertTrue(idOptionLabels.contains("Equals"), "Expected spelled-out labels, got: " + idOptionLabels);
        assertTrue(idOptionLabels.contains("Between"), "Expected spelled-out labels, got: " + idOptionLabels);
        assertTrue(idOptionLabels.contains("Is Null"), "Expected spelled-out labels, got: " + idOptionLabels);
        assertTrue(idOptionLabels.contains("In List"), "Expected spelled-out labels, got: " + idOptionLabels);

        // Assert - the text preset mixes comparison operators with string-matching ones, so it keeps words
        assertTrue(lastNameOptionLabels.contains("Contains"), "Expected spelled-out labels, got: " + lastNameOptionLabels);
        assertTrue(lastNameOptionLabels.contains("Equals"), "Expected spelled-out labels, got: " + lastNameOptionLabels);
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: \"is empty\"/\"is not empty\" hide the value input and match null/blank values")
    void textFilterIsEmptyIsNotEmpty(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();

        // Act - switch to the value-less "is empty" mode
        dataTable.filterMatchMode("last name", "empty");

        // Assert - the value input is hidden and disabled since the mode alone is the entire predicate
        WebElement valueInput = lastNameHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"),
                "Value input should be hidden while a value-less match mode is selected");
        assertFalse(valueInput.isEnabled(), "Value input should be disabled while a value-less match mode is selected");

        // Assert - matches the null and blank lastName rows, and only those
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() == null || e.getLastName().trim().isEmpty())
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - the hidden/disabled state must also survive a full server-side re-render, not just the live JS toggle
        page.buttonUpdate.click();

        // Assert - re-fetch the header cell, the update="datatable" full refresh replaced the old DOM elements
        lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();
        valueInput = lastNameHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"));
        assertFalse(valueInput.isEnabled());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "is not empty"
        dataTable.filterMatchMode("last name", "notEmpty");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null && !e.getLastName().trim().isEmpty())
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: \"is null\"/\"is not null\" distinguish null from a blank value")
    void textFilterIsNullIsNotNull(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filterMatchMode("last name", "null");

        // Assert - strictly null, unlike "is empty" which also matches the blank row
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() == null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("last name", "notNull");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: \"matches regex\" filters using the typed value as a regular expression")
    void textFilterMatchesRegex(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("last name", "regex");

        // Act
        dataTable.filter("last name", "^M.*");

        // Assert
        Pattern pattern = Pattern.compile("^M.*");
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null && pattern.matcher(e.getLastName()).matches())
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("DataTable: \"in list\"/\"not in list\" match against a comma-separated value")
    void textFilterInListNotInList(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("last name", "in");

        // Act
        dataTable.filter("last name", "Paul, Bush, Johnson");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() != null
                        && (e.getLastName().equals("Paul") || e.getLastName().equals("Bush") || e.getLastName().equals("Johnson")))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "not in list" keeping the same typed value
        dataTable.filterMatchMode("last name", "notIn");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getLastName() == null
                        || !(e.getLastName().equals("Paul") || e.getLastName().equals("Bush") || e.getLastName().equals("Johnson")))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("DataTable: numeric \"between\"/\"not between\" match a \"min,max\" typed range")
    void numericFilterBetweenNotBetween(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell salaryHeader = dataTable.getHeader().getCell("salary").orElseThrow();
        dataTable.filterMatchMode("salary", "between");

        // Assert - the value input hints at the expected "min,max" syntax
        assertEquals("min,max", salaryHeader.getColumnFilter().getAttribute("placeholder"));

        // Act
        dataTable.filter("salary", "2500,3000");

        // Assert - inclusive on both ends; a null salary never matches "between"
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSalary() != null && e.getSalary() >= 2500 && e.getSalary() <= 3000)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "not between" keeping the same typed range
        dataTable.filterMatchMode("salary", "notBetween");

        // Assert - the negation of "between", so a null salary DOES match (it was never "between" to begin with)
        employeesFiltered = employees.stream()
                .filter(e -> !(e.getSalary() != null && e.getSalary() >= 2500 && e.getSalary() <= 3000))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(13)
    @DisplayName("DataTable: numeric \"between\" stays inactive while only one value has been typed")
    void numericFilterBetweenIncompleteRange(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("salary", "between");

        // Act - only the first half of the range typed so far (e.g., still typing)
        dataTable.filter("salary", "2500,");

        // Assert - not yet an active filter, so every row is still shown
        assertEmployeeRows(dataTable, employees);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(14)
    @DisplayName("DataTable: numeric \"is null\"/\"is not null\" hide the value input")
    void numericFilterIsNullIsNotNull(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell salaryHeader = dataTable.getHeader().getCell("salary").orElseThrow();

        // Act
        dataTable.filterMatchMode("salary", "null");

        // Assert - value-less, same generic mechanism as the text preset's "is null"
        WebElement valueInput = salaryHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"));
        assertFalse(valueInput.isEnabled());

        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSalary() == null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("salary", "notNull");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getSalary() != null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(15)
    @DisplayName("DataTable: numeric \"in list\"/\"not in list\" convert each comma-separated token")
    void numericFilterInListNotInList(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell salaryHeader = dataTable.getHeader().getCell("salary").orElseThrow();
        dataTable.filterMatchMode("salary", "in");

        // Assert - the value input hints at the expected comma-separated syntax
        assertEquals("value1, value2, ...", salaryHeader.getColumnFilter().getAttribute("placeholder"));

        // Act
        dataTable.filter("salary", "2500, 3000, 2200");

        // Assert - each token is converted to an Integer individually, not the whole string as one value
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSalary() != null && (e.getSalary() == 2500 || e.getSalary() == 3000 || e.getSalary() == 2200))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "not in list" keeping the same typed value
        dataTable.filterMatchMode("salary", "notIn");

        // Assert - a null salary never equals any converted token, so it "is not in" the list either
        employeesFiltered = employees.stream()
                .filter(e -> e.getSalary() == null || !(e.getSalary() == 2500 || e.getSalary() == 3000 || e.getSalary() == 2200))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(16)
    @DisplayName("DataTable: boolean \"true\"/\"false\" hide the value input and match strictly")
    void booleanFilterTrueFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell activeHeader = dataTable.getHeader().getCell("active").orElseThrow();

        // Assert - "All" is the boolean preset's first (and thus default) option: a fresh, untouched "active"
        // column must NOT silently filter the table before the user ever picks true/false/is null/is not null
        WebElement valueInput = activeHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"));
        assertFalse(valueInput.isEnabled());
        assertEmployeeRows(dataTable, employees);

        // Act
        dataTable.filterMatchMode("active", "true");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> Boolean.TRUE.equals(e.getActive()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "false"
        dataTable.filterMatchMode("active", "false");

        // Assert - strictly false, a null "active" (untouched employees) matches neither "true" nor "false"
        employeesFiltered = employees.stream()
                .filter(e -> Boolean.FALSE.equals(e.getActive()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(17)
    @DisplayName("DataTable: boolean \"is null\"/\"is not null\" match an untouched (unset) value")
    void booleanFilterIsNullIsNotNull(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("active", "null");

        // Assert - the untouched employees (never assigned true or false)
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getActive() == null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("active", "notNull");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getActive() != null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(18)
    @DisplayName("DataTable: date \"is\"/\"is not\" (labeled differently than the shared numeric Equals/Not "
            + "Equals) match an exact date")
    void dateFilterIsIsNot(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();

        // Act - "equals" ("Is") is already the column's declared default match mode, so just filter directly
        dataTable.filter("review date", today.format(REVIEW_DATE_FORMAT));

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> today.equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "is not" keeping the same typed value
        dataTable.filterMatchMode("review date", "notEquals");

        // Assert - a null reviewDate is (like every other match mode) also "not equal" to any given date
        employeesFiltered = employees.stream()
                .filter(e -> !today.equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(19)
    @DisplayName("DataTable: date \"before\"/\"before or on\"/\"after\"/\"after or on\"")
    void dateFilterBeforeAfterVariants(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();
        String todayStr = today.format(REVIEW_DATE_FORMAT);

        // Act - "before"
        dataTable.filterMatchMode("review date", "lt");
        dataTable.filter("review date", todayStr);

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && e.getReviewDate().isBefore(today))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - "before or on"
        dataTable.filterMatchMode("review date", "lte");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isAfter(today))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - "after"
        dataTable.filterMatchMode("review date", "gt");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && e.getReviewDate().isAfter(today))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - "after or on"
        dataTable.filterMatchMode("review date", "gte");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(today))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(20)
    @DisplayName("DataTable: date \"between\"/\"not between\" match a \"min,max\" typed range")
    void dateFilterBetweenNotBetween(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();
        LocalDate rangeStart = today.minusDays(7);
        LocalDate rangeEnd = today.plusDays(7);
        dataTable.filterMatchMode("review date", "between");

        // Act
        dataTable.filter("review date", rangeStart.format(REVIEW_DATE_FORMAT) + "," + rangeEnd.format(REVIEW_DATE_FORMAT));

        // Assert - inclusive on both ends; a null reviewDate never matches "between"
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(rangeStart) && !e.getReviewDate().isAfter(rangeEnd))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "not between" keeping the same typed range
        dataTable.filterMatchMode("review date", "notBetween");

        // Assert - the negation of "between", so a null reviewDate DOES match (it was never "between" to begin with)
        employeesFiltered = employees.stream()
                .filter(e -> !(e.getReviewDate() != null && !e.getReviewDate().isBefore(rangeStart) && !e.getReviewDate().isAfter(rangeEnd)))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(21)
    @DisplayName("DataTable: date \"is empty\"/\"is not empty\" behave like \"is (not) null\" for a "
            + "non-string field")
    void dateFilterIsEmptyIsNotEmpty(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("review date", "empty");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() == null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "notEmpty");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(22)
    @DisplayName("DataTable: date \"today\"/\"yesterday\"/\"tomorrow\" are value-less and hide the input")
    void dateFilterTodayYesterdayTomorrow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell reviewDateHeader = dataTable.getHeader().getCell("review date").orElseThrow();
        LocalDate today = LocalDate.now();

        // Act
        dataTable.filterMatchMode("review date", "today");

        // Assert - value-less, same generic mechanism as every other preset's value-less modes
        WebElement valueInput = reviewDateHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"));
        assertFalse(valueInput.isEnabled());

        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> today.equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "yesterday");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> today.minusDays(1).equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "tomorrow");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> today.plusDays(1).equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(23)
    @DisplayName("DataTable: date \"this week\"/\"last week\"/\"next week\"")
    void dateFilterThisLastNextWeek(Page page) {
        // Arrange - compute week boundaries the same way DateFilterUtils.startOfWeek() does, rather than
        // assuming a fixed relationship between "yesterday"/"tomorrow" and "this week" - which day of the week
        // "today" happens to be when this test runs changes whether they fall in this week or the adjacent one
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = startOfWeek(today);
        LocalDate endOfThisWeek = startOfThisWeek.plusDays(6);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);
        LocalDate startOfNextWeek = startOfThisWeek.plusWeeks(1);
        LocalDate endOfNextWeek = startOfNextWeek.plusDays(6);

        // Act
        dataTable.filterMatchMode("review date", "thisWeek");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(startOfThisWeek) && !e.getReviewDate().isAfter(endOfThisWeek))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "lastWeek");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(startOfLastWeek) && !e.getReviewDate().isAfter(endOfLastWeek))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "nextWeek");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(startOfNextWeek) && !e.getReviewDate().isAfter(endOfNextWeek))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(24)
    @DisplayName("DataTable: date \"this month\"/\"last month\"/\"next month\"")
    void dateFilterThisLastNextMonth(Page page) {
        // Arrange - month boundaries computed dynamically for the same reason as the week test above
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();
        LocalDate startOfThisMonth = today.withDayOfMonth(1);
        LocalDate endOfThisMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate startOfLastMonth = startOfThisMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfThisMonth.minusDays(1);
        LocalDate startOfNextMonth = startOfThisMonth.plusMonths(1);
        LocalDate endOfNextMonth = startOfNextMonth.withDayOfMonth(startOfNextMonth.lengthOfMonth());

        // Act
        dataTable.filterMatchMode("review date", "thisMonth");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(startOfThisMonth) && !e.getReviewDate().isAfter(endOfThisMonth))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "lastMonth");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(startOfLastMonth) && !e.getReviewDate().isAfter(endOfLastMonth))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "nextMonth");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(startOfNextMonth) && !e.getReviewDate().isAfter(endOfNextMonth))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(25)
    @DisplayName("DataTable: date \"this year\"/\"last year\"/\"next year\"")
    void dateFilterThisLastNextYear(Page page) {
        // Arrange - a full year offset is always safely within its bucket regardless of where "today" falls
        // within its own year (unlike a quarter offset, which needs care implementation notes)
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();

        // Act
        dataTable.filterMatchMode("review date", "thisYear");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && e.getReviewDate().getYear() == today.getYear())
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "lastYear");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && e.getReviewDate().getYear() == today.getYear() - 1)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("review date", "nextYear");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && e.getReviewDate().getYear() == today.getYear() + 1)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(26)
    @DisplayName("DataTable: date \"last N days\"/\"next N days\" - the typed value is a plain number, "
            + "not a date, so it bypasses the column's date converter")
    void dateFilterLastNDaysNextNDays(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell reviewDateHeader = dataTable.getHeader().getCell("review date").orElseThrow();
        LocalDate today = LocalDate.now();
        dataTable.filterMatchMode("review date", "lastNDays");

        // Assert - the value input hints at the expected "number of days" syntax
        assertEquals("e.g., 30", reviewDateHeader.getColumnFilter().getAttribute("placeholder"));

        // Act
        dataTable.filter("review date", "30");

        // Assert - [today - 30, today]
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(today.minusDays(30)) && !e.getReviewDate().isAfter(today))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "next N days" keeping the same typed value
        dataTable.filterMatchMode("review date", "nextNDays");

        // Assert - [today, today + 30]
        employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(today) && !e.getReviewDate().isAfter(today.plusDays(30)))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(27)
    @DisplayName("DataTable: date \"relative date\" matches within N days of today in either direction")
    void dateFilterRelativeDate(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();
        dataTable.filterMatchMode("review date", "relativeDate");

        // Act
        dataTable.filter("review date", "10");

        // Assert - [today - 10, today + 10]
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getReviewDate() != null && !e.getReviewDate().isBefore(today.minusDays(10)) && !e.getReviewDate().isAfter(today.plusDays(10)))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(28)
    @DisplayName("DataTable: date filterValueType labels the shared comparators \"Is\"/\"Before\"/"
            + "\"After\" instead of the numeric preset's \"Equals\"/\"Less Than\"/\"Greater Than\"")
    void dateFilterMatchModeLabels(Page page) {
        // Arrange
        HeaderCell reviewDateHeader = page.dataTable.getHeader().getCell("review date").orElseThrow();

        // Act
        List<String> labels = reviewDateHeader.getFilterMatchModeLabels();

        // Assert - date-specific overrides for the modes shared with the numeric preset
        assertTrue(labels.contains("Is"), "Expected date-flavored labels, got: " + labels);
        assertTrue(labels.contains("Is Not"), "Expected date-flavored labels, got: " + labels);
        assertTrue(labels.contains("Before"), "Expected date-flavored labels, got: " + labels);
        assertTrue(labels.contains("Before or On"), "Expected date-flavored labels, got: " + labels);
        assertTrue(labels.contains("After"), "Expected date-flavored labels, got: " + labels);
        assertTrue(labels.contains("After or On"), "Expected date-flavored labels, got: " + labels);
        assertFalse(labels.contains("Equals"), "Should not fall back to the numeric preset's label: " + labels);

        // Assert - the new relative-date modes use their own (preset-independent) labels
        assertTrue(labels.contains("Today"), "Expected relative-date labels, got: " + labels);
        assertTrue(labels.contains("Last N Days"), "Expected relative-date labels, got: " + labels);
    }

    @Test
    @Order(29)
    @DisplayName("DataTable: time filterValueType (bare LocalTime) defaults to the column's "
            + "filterMatchMode")
    void timeFilterDefaultMatchMode(Page page) {
        // Arrange - read Mike Master's (id 1, row 0) checkInTime straight from its rendered cell rather than
        // recomputing it independently: the bean's fixture is captured relative to "now" when the page
        // loads for THIS test, which is a different instant than when the test class's own `employees` field
        // was built - fine for reviewDate's day-granularity fixtures elsewhere in this class, but not for a
        // second-granularity value, so an independently computed value here would drift and never match.
        DataTable dataTable = page.dataTable;
        String checkInTimeText = dataTable.getRow(0).getCell(5).getText();

        // Act - column declares filterMatchMode="equals" as its initial/default comparator
        dataTable.filter("check-in time", checkInTimeText);

        // Assert - only Mike Master has this exact checkInTime
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() == 1).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(30)
    @DisplayName("DataTable: datetime filterValueType (full LocalDateTime) defaults to the "
            + "column's filterMatchMode")
    void datetimeFilterDefaultMatchMode(Page page) {
        // Arrange - same rationale as timeFilterDefaultMatchMode above: read the rendered value back rather
        // than trust a second-granularity value computed independently of the bean's own "now"
        DataTable dataTable = page.dataTable;
        String lastLoginText = dataTable.getRow(0).getCell(6).getText();

        // Act - column declares filterMatchMode="equals" as its initial/default comparator
        dataTable.filter("last login", lastLoginText);

        // Assert - only Mike Master has this exact lastLoginDateTime
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() == 1).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(31)
    @DisplayName("DataTable: time \"last N minutes\"/\"next N minutes\"/\"last N hours\"/\"next N hours\" "
            + "match a bare LocalTime value on a cyclic 24h clock")
    void timeFilterLastNextMinutesAndHours(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell checkInHeader = dataTable.getHeader().getCell("check-in time").orElseThrow();
        dataTable.filterMatchMode("check-in time", "lastNMinutes");

        // Assert - the value input hints at the expected "number of minutes/hours" syntax, same as the date preset
        assertEquals("e.g., 30", checkInHeader.getColumnFilter().getAttribute("placeholder"));

        // Act - "last 30 minutes"
        dataTable.filter("check-in time", "30");

        // Assert - mirrors RelativeMinutesOrHoursFilterConstraint#isWithinCyclicRange exactly, since a bare
        // LocalTime is a cyclic 24h clock and the window can wrap past midnight depending on real "now"
        {
            LocalTime now = LocalTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getCheckInTime() != null && isWithinCyclicRange(e.getCheckInTime(), now.minusMinutes(30), now))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        // Act - "next 30 minutes"
        dataTable.filterMatchMode("check-in time", "nextNMinutes");

        // Assert
        {
            LocalTime now = LocalTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getCheckInTime() != null && isWithinCyclicRange(e.getCheckInTime(), now, now.plusMinutes(30)))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        // Act - "last 2 hours"
        dataTable.filterMatchMode("check-in time", "lastNHours");
        dataTable.filter("check-in time", "2");

        // Assert
        {
            LocalTime now = LocalTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getCheckInTime() != null && isWithinCyclicRange(e.getCheckInTime(), now.minusHours(2), now))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        // Act - "next 2 hours" (keeps the same typed value "2")
        dataTable.filterMatchMode("check-in time", "nextNHours");

        // Assert
        {
            LocalTime now = LocalTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getCheckInTime() != null && isWithinCyclicRange(e.getCheckInTime(), now, now.plusHours(2)))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(32)
    @DisplayName("DataTable: time \"last N minutes\" wraps past midnight on the cyclic 24h clock")
    void timeFilterLastNMinutesWrapsPastMidnight(Page page) {
        // Arrange - a window wide enough (23h59m) that it wraps past midnight for virtually any time of day the
        // suite happens to run at - only the ~1-minute window right before midnight would not wrap, which is
        // negligible for CI purposes. See RelativeMinutesOrHoursFilterConstraint#isWithinCyclicRange.
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("check-in time", "lastNMinutes");

        // Act
        dataTable.filter("check-in time", "1439");

        // Assert
        LocalTime now = LocalTime.now();
        LocalTime start = now.minusMinutes(1439);
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getCheckInTime() != null && isWithinCyclicRange(e.getCheckInTime(), start, now))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(33)
    @DisplayName("DataTable: datetime \"last N minutes\"/\"next N minutes\"/\"last N hours\"/"
            + "\"next N hours\" match a full LocalDateTime value using ordinary (linear, non-cyclic) range logic")
    void datetimeFilterLastNextMinutesAndHours(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell lastLoginHeader = dataTable.getHeader().getCell("last login").orElseThrow();
        dataTable.filterMatchMode("last login", "lastNMinutes");
        assertEquals("e.g., 30", lastLoginHeader.getColumnFilter().getAttribute("placeholder"));

        // Act - "last 30 minutes"
        dataTable.filter("last login", "30");

        // Assert - a full LocalDateTime is linear, not cyclic - ordinary isBefore/isAfter range logic applies
        {
            LocalDateTime now = LocalDateTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getLastLoginDateTime() != null
                            && !e.getLastLoginDateTime().isBefore(now.minusMinutes(30)) && !e.getLastLoginDateTime().isAfter(now))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        // Act - "next 30 minutes"
        dataTable.filterMatchMode("last login", "nextNMinutes");

        // Assert
        {
            LocalDateTime now = LocalDateTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getLastLoginDateTime() != null
                            && !e.getLastLoginDateTime().isBefore(now) && !e.getLastLoginDateTime().isAfter(now.plusMinutes(30)))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        // Act - "last 2 hours"
        dataTable.filterMatchMode("last login", "lastNHours");
        dataTable.filter("last login", "2");

        // Assert
        {
            LocalDateTime now = LocalDateTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getLastLoginDateTime() != null
                            && !e.getLastLoginDateTime().isBefore(now.minusHours(2)) && !e.getLastLoginDateTime().isAfter(now))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        // Act - "next 2 hours" (keeps the same typed value "2")
        dataTable.filterMatchMode("last login", "nextNHours");

        // Assert
        {
            LocalDateTime now = LocalDateTime.now();
            List<Employee> employeesFiltered = employees.stream()
                    .filter(e -> e.getLastLoginDateTime() != null
                            && !e.getLastLoginDateTime().isBefore(now) && !e.getLastLoginDateTime().isAfter(now.plusHours(2)))
                    .collect(Collectors.toList());
            assertEmployeeRows(dataTable, employeesFiltered);
        }

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(34)
    @DisplayName("DataTable: time filterValueType drops every calendar-day/week/month/... "
            + "predicate the date preset has but keeps the shared comparators and adds last/next N minutes/hours; "
            + "datetime keeps every date predicate as well")
    void timeAndDatetimePresetLabels(Page page) {
        // Arrange
        HeaderCell checkInHeader = page.dataTable.getHeader().getCell("check-in time").orElseThrow();
        HeaderCell lastLoginHeader = page.dataTable.getHeader().getCell("last login").orElseThrow();

        // Act
        List<String> timeLabels = checkInHeader.getFilterMatchModeLabels();
        List<String> datetimeLabels = lastLoginHeader.getFilterMatchModeLabels();

        // Assert - "time" reuses the date-flavored "Is"/"Before"/"After" labels for the shared comparators and
        // adds the 4 new minute/hour modes, but has no day/week/month/... predicates - a bare LocalTime has no
        // date component for those to apply to
        assertTrue(timeLabels.contains("Is"), "Expected date-flavored labels, got: " + timeLabels);
        assertTrue(timeLabels.contains("Before"), "Expected date-flavored labels, got: " + timeLabels);
        assertTrue(timeLabels.contains("Last N Minutes"), "Expected new minute/hour labels, got: " + timeLabels);
        assertTrue(timeLabels.contains("Next N Hours"), "Expected new minute/hour labels, got: " + timeLabels);
        assertFalse(timeLabels.contains("Today"), "time preset should have no calendar predicates: " + timeLabels);
        assertFalse(timeLabels.contains("Last N Days"), "time preset should have no calendar predicates: " + timeLabels);

        // Assert - "datetime" keeps every "date" predicate (a calendar day/week/month/... is still meaningful for
        // a full date+time value) plus the 4 new minute/hour modes
        assertTrue(datetimeLabels.contains("Today"), "Expected calendar predicates, got: " + datetimeLabels);
        assertTrue(datetimeLabels.contains("Last N Days"), "Expected calendar predicates, got: " + datetimeLabels);
        assertTrue(datetimeLabels.contains("Last N Minutes"), "Expected new minute/hour labels, got: " + datetimeLabels);
        assertTrue(datetimeLabels.contains("Next N Hours"), "Expected new minute/hour labels, got: " + datetimeLabels);
    }

    @Test
    @Order(35)
    @DisplayName("DataTable: enum \"is\"/\"is not\" (labeled differently than the shared "
            + "Equals/Not Equals) match an exact Employee.Role value with no converter needed")
    void enumFilterIsIsNot(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - "equals" ("Is") is already the column's declared default match mode
        dataTable.filter("role", "MANAGER");

        // Assert - only Mike Master (id 1) is a MANAGER
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getRole() == Employee.Role.MANAGER)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "is not" keeping the same typed value
        dataTable.filterMatchMode("role", "notEquals");

        // Assert - a null role (the synthetic rows) is also "not equal" to any given role
        employeesFiltered = employees.stream()
                .filter(e -> e.getRole() != Employee.Role.MANAGER)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(36)
    @DisplayName("DataTable: enum \"is any of\"/\"is none of\" (In/NotIn) match against a "
            + "comma-separated list of role names")
    void enumFilterIsAnyOfIsNoneOf(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell roleHeader = dataTable.getHeader().getCell("role").orElseThrow();
        dataTable.filterMatchMode("role", "in");

        // Assert - the value input hints at the expected comma-separated syntax, same as the other IN presets
        assertEquals("value1, value2, ...", roleHeader.getColumnFilter().getAttribute("placeholder"));

        // Act
        dataTable.filter("role", "MANAGER, DEVELOPER");

        // Assert - the 3 developers (Alfred Paul, Chris Clark, James Bush) plus the 1 manager (Mike Master)
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getRole() == Employee.Role.MANAGER || e.getRole() == Employee.Role.DEVELOPER)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "is none of" keeping the same typed value
        dataTable.filterMatchMode("role", "notIn");

        // Assert - a null role never equals any listed token, so it "is none of" the given list either
        employeesFiltered = employees.stream()
                .filter(e -> e.getRole() != Employee.Role.MANAGER && e.getRole() != Employee.Role.DEVELOPER)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(37)
    @DisplayName("DataTable: enum \"is empty\"/\"is not empty\" distinguish a null role from an "
            + "assigned one, same generic mechanism as every other preset's value-less modes")
    void enumFilterIsEmptyIsNotEmpty(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell roleHeader = dataTable.getHeader().getCell("role").orElseThrow();

        // Act
        dataTable.filterMatchMode("role", "empty");

        // Assert - value-less, the value input is hidden and disabled
        WebElement valueInput = roleHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"));
        assertFalse(valueInput.isEnabled());

        // Assert - only the 4 synthetic rows (900-903) have no role assigned
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getRole() == null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("role", "notEmpty");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getRole() != null)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(38)
    @DisplayName("DataTable: enum filterValueType labels the shared comparators \"Is\"/\"Is Not\" "
            + "and In/NotIn as \"Is Any Of\"/\"Is None Of\" instead of the generic \"Equals\"/\"In List\"/\"Not In List\"")
    void enumFilterMatchModeLabels(Page page) {
        // Arrange
        HeaderCell roleHeader = page.dataTable.getHeader().getCell("role").orElseThrow();

        // Act
        List<String> labels = roleHeader.getFilterMatchModeLabels();

        // Assert - enum-specific overrides for the modes shared with other presets
        assertTrue(labels.contains("Is"), "Expected enum-flavored labels, got: " + labels);
        assertTrue(labels.contains("Is Not"), "Expected enum-flavored labels, got: " + labels);
        assertTrue(labels.contains("Is Any Of"), "Expected enum-flavored labels, got: " + labels);
        assertTrue(labels.contains("Is None Of"), "Expected enum-flavored labels, got: " + labels);
        assertFalse(labels.contains("Equals"), "Should not fall back to the generic label: " + labels);
        assertFalse(labels.contains("In List"), "Should not fall back to the generic label: " + labels);
        assertFalse(labels.contains("Not In List"), "Should not fall back to the generic label: " + labels);

        // Assert - "is (not) empty" has no enum-specific override, keeps the generic label
        assertTrue(labels.contains("Is Empty"), "Expected the generic label, got: " + labels);
        assertTrue(labels.contains("Is Not Empty"), "Expected the generic label, got: " + labels);
    }

    @Test
    @Order(39)
    @DisplayName("DataTable: array \"contains\"/\"does not contain\" match against a single value "
            + "in a List<String> field")
    void arrayFilterContainsDoesNotContain(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - "arrayContains" ("Contains") is already the column's declared default match mode
        dataTable.filter("skills", "Java");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() != null && e.getSkills().contains("Java"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "does not contain" keeping the same typed value
        dataTable.filterMatchMode("skills", "arrayNotContains");

        // Assert - a null or empty skills list also "does not contain" any given value
        employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() == null || !e.getSkills().contains("Java"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(40)
    @DisplayName("DataTable: array \"contains any\"/\"contains all\" match against a comma-separated "
            + "list of values with OR vs AND semantics")
    void arrayFilterContainsAnyContainsAll(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell skillsHeader = dataTable.getHeader().getCell("skills").orElseThrow();
        dataTable.filterMatchMode("skills", "containsAny");

        // Assert - the value input hints at the expected comma-separated syntax, same as the other multi-value presets
        assertEquals("value1, value2, ...", skillsHeader.getColumnFilter().getAttribute("placeholder"));

        // Act
        dataTable.filter("skills", "Java, Go");

        // Assert - "contains any" (OR): Java OR Go - Mike, Susan, Alfred, Chris, James
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() != null && (e.getSkills().contains("Java") || e.getSkills().contains("Go")))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act - switch to "contains all" keeping the same typed value
        dataTable.filterMatchMode("skills", "containsAll");

        // Assert - "contains all" (AND): Java AND Go - only Alfred Paul has both
        employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() != null && e.getSkills().contains("Java") && e.getSkills().contains("Go"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(41)
    @DisplayName("DataTable: array \"contains none\" is the negation of \"contains any\"")
    void arrayFilterContainsNone(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("skills", "containsNone");

        // Act
        dataTable.filter("skills", "Java, Go");

        // Assert - neither Java nor Go - Trish, Margret, Mary, and the 4 synthetic (skill-less) rows
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() == null || (!e.getSkills().contains("Java") && !e.getSkills().contains("Go")))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(42)
    @DisplayName("DataTable: array \"is empty\"/\"is not empty\" treat both a null and an empty "
            + "List<String> as empty")
    void arrayFilterIsEmptyIsNotEmpty(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell skillsHeader = dataTable.getHeader().getCell("skills").orElseThrow();
        dataTable.filterMatchMode("skills", "empty");

        // Assert - value-less, the value input is hidden and disabled
        WebElement valueInput = skillsHeader.getColumnFilter();
        assertTrue(Objects.requireNonNull(valueInput.getAttribute("class")).contains("ui-helper-hidden"));
        assertFalse(valueInput.isEnabled());

        // Assert - Margret (empty list) and Mary (null list) plus the 4 synthetic (skill-less) rows
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() == null || e.getSkills().isEmpty())
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filterMatchMode("skills", "notEmpty");

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getSkills() != null && !e.getSkills().isEmpty())
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(43)
    @DisplayName("DataTable: array filterValueType labels: Contains/Does Not Contain/Contains "
            + "Any/Contains All/Contains None, plus the generic Is Empty/Is Not Empty")
    void arrayFilterMatchModeLabels(Page page) {
        // Arrange
        HeaderCell skillsHeader = page.dataTable.getHeader().getCell("skills").orElseThrow();

        // Act
        List<String> labels = skillsHeader.getFilterMatchModeLabels();

        // Assert
        assertTrue(labels.contains("Contains"), "Expected array-preset labels, got: " + labels);
        assertTrue(labels.contains("Does Not Contain"), "Expected array-preset labels, got: " + labels);
        assertTrue(labels.contains("Contains Any"), "Expected array-preset labels, got: " + labels);
        assertTrue(labels.contains("Contains All"), "Expected array-preset labels, got: " + labels);
        assertTrue(labels.contains("Contains None"), "Expected array-preset labels, got: " + labels);
        assertTrue(labels.contains("Is Empty"), "Expected the generic label, got: " + labels);
        assertTrue(labels.contains("Is Not Empty"), "Expected the generic label, got: " + labels);
    }

    @Test
    @Order(44)
    @DisplayName("DataTable: the filter match-mode icon is outlined while the column's default match mode is "
            + "selected and fills in as soon as any other mode is picked - independent of whether a value is typed")
    void filterMatchModeIconActiveState(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();

        // Assert - the column's default mode ("contains") is selected on a fresh page load, icon starts outlined
        WebElement icon = lastNameHeader.getColumnFilterMatchModeIcon();
        assertFalse(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        // Act - type a value without changing the mode
        dataTable.filter("last name", "ar");

        // Assert - typing alone does not fill the icon; only the selected mode drives it, not the typed value
        icon = lastNameHeader.getColumnFilterMatchModeIcon();
        assertFalse(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        // Act - switch to a different (still value-requiring) mode
        dataTable.filterMatchMode("last name", "startsWith");

        // Assert - the icon fills in as soon as a non-default mode is picked
        icon = lastNameHeader.getColumnFilterMatchModeIcon();
        assertTrue(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        // Act - back to the column's default mode, with the same value ("ar") still typed
        dataTable.filterMatchMode("last name", "contains");

        // Assert - outlined again - mode-driven, not value-driven, so the still-present value doesn't matter
        icon = lastNameHeader.getColumnFilterMatchModeIcon();
        assertFalse(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        // Act - switch to a value-less mode ("is empty")
        dataTable.filterMatchMode("last name", "empty");

        // Assert - fills too, value-less modes are no exception
        icon = lastNameHeader.getColumnFilterMatchModeIcon();
        assertTrue(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(45)
    @DisplayName("DataTable: The filter match-mode icon is outlined on a fresh load when the column's "
            + "configured default (e.g. \"gt\") differs from the matchModeOptions list's first entry (\"equals\") - "
            + "comparing against raw list order rather than the actual configured default previously fooled the "
            + "icon into starting out filled")
    void filterMatchModeIconActiveStateWithNonFirstDefault(Page page) {
        // Arrange - the "ID" column is configured with filterMatchMode="gt", filterValueType="numeric",
        // whose first entry is "equals" - a mismatch that used to spuriously fill the icon on a fresh load
        DataTable dataTable = page.dataTable;
        HeaderCell idHeader = dataTable.getHeader().getCell("ID").orElseThrow();

        // Assert - outlined on a fresh load: "gt" IS the column's own default, even though it isn't first in the list
        WebElement icon = idHeader.getColumnFilterMatchModeIcon();
        assertFalse(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));
        assertEquals("gt", idHeader.getColumnFilterMatchModeValue());

        // Act - switch to a different mode
        dataTable.filterMatchMode("ID", "lt");

        // Assert - now fills, since "lt" differs from the column's default ("gt")
        icon = idHeader.getColumnFilterMatchModeIcon();
        assertTrue(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        // Act - reset all filters
        PrimeSelenium.executeScript(true, "PF('wgtTable').clearFilters()");

        // Assert - resets back to the column's own default ("gt"), not the list's first entry ("equals"), and outlined again
        idHeader = dataTable.getHeader().getCell("ID").orElseThrow();
        assertEquals("gt", idHeader.getColumnFilterMatchModeValue());
        icon = idHeader.getColumnFilterMatchModeIcon();
        assertFalse(Objects.requireNonNull(icon.getAttribute("class")).contains("ui-column-filter-mode-icon-active"));

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(46)
    @DisplayName("DataTable: GitHub #7427 a column with no filterMatchMode/filterValueType at all still gets a "
            + "filter match-mode picker by default, auto-derived from its Java type - here a plain String field "
            + "auto-derives the \"text\" preset")
    void filterValueTypeAutoDerivedText(Page page) {
        // Arrange - the "first name" column declares neither filterMatchMode nor filterValueType
        DataTable dataTable = page.dataTable;
        HeaderCell firstNameHeader = dataTable.getHeader().getCell("first name").orElseThrow();

        // Assert - a picker is still rendered, with the "text" preset's labels
        List<String> labels = firstNameHeader.getFilterMatchModeLabels();
        assertTrue(labels.contains("Contains"), "Expected the auto-derived \"text\" preset, got: " + labels);
        assertTrue(labels.contains("Starts With"), "Expected the auto-derived \"text\" preset, got: " + labels);
        assertTrue(labels.contains("Matches Regex"), "Expected the auto-derived \"text\" preset, got: " + labels);

        // Act/Assert - filtering still works through the auto-derived picker; the column's implicit default
        // match mode ("startsWith" - see ColumnBase#getFilterMatchMode()) is already selected, so switch to a
        // genuinely different mode rather than re-selecting the same one (a no-op that fires no AJAX request)
        dataTable.filterMatchMode("first name", "contains");
        dataTable.filter("first name", "a");
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getFirstName() != null && e.getFirstName().toLowerCase().contains("a"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(47)
    @DisplayName("DataTable: GitHub #7427 a column with its own custom <f:facet name=\"filter\"> gets no "
            + "auto-derived picker, even though its Java type (LocalDate) would otherwise auto-derive to \"date\" "
            + "- a bespoke filter UI shouldn't get a second, competing picker layered on top")
    void filterValueTypeSkipsCustomFacet(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell birthDateHeader = dataTable.getHeader().getCell("birth date").orElseThrow();

        // Assert - no match-mode icon at all for this column
        assertNull(birthDateHeader.getColumnFilterMatchModeIcon());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(48)
    @DisplayName("DataTable: GitHub #7427 a numeric column's filter value input rejects non-numeric characters "
            + "as the user types")
    void numericFilterInputRejectsNonNumericCharacters(Page page) {
        // Arrange - the "ID" column auto-/explicitly resolves to the "numeric" preset
        DataTable dataTable = page.dataTable;
        HeaderCell idHeader = dataTable.getHeader().getCell("ID").orElseThrow();
        WebElement input = idHeader.getColumnFilter();

        // Act - type a mix of letters and digits
        input.sendKeys("a1b2c3");

        // Assert - only the digits stuck
        assertEquals("123", input.getAttribute("value"));

        // Act - a comma-separated pair (valid for "between"/"in list") is still accepted
        input.clear();
        input.sendKeys("1, 2");

        // Assert
        assertEquals("1, 2", input.getAttribute("value"));

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(49)
    @DisplayName("DataTable: GitHub #7427 a date column's shadow calendar-picker widget shows/hides correctly "
            + "across representative match modes")
    void dateFilterCalendarPickerWidgetVisibility(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell reviewDateHeader = dataTable.getHeader().getCell("review date").orElseThrow();
        WebElement th = reviewDateHeader.getWebElement();
        WebElement plainInput = reviewDateHeader.getColumnFilter();
        WebElement datePicker = th.findElement(By.className("ui-column-filter-date"));
        WebElement rangePicker = th.findElement(By.className("ui-column-filter-date-range"));
        waitUntilDisplayed(datePicker);

        // Assert - "equals" is the column's default already, so no need to re-select it: single-date picker only
        assertFalse(plainInput.isDisplayed());
        assertTrue(datePicker.isDisplayed());
        assertFalse(rangePicker.isDisplayed());

        // Act/Assert - "between": range picker only
        reviewDateHeader.setFilterMatchMode("between");
        assertFalse(plainInput.isDisplayed());
        assertFalse(datePicker.isDisplayed());
        assertTrue(rangePicker.isDisplayed());

        // Act/Assert - "lastNDays": a day count, not a date - plain (numeric) input, neither picker
        reviewDateHeader.setFilterMatchMode("lastNDays");
        assertTrue(plainInput.isDisplayed());
        assertFalse(datePicker.isDisplayed());
        assertFalse(rangePicker.isDisplayed());

        // Act/Assert - "empty" ("Is Empty"): value-less - nothing is shown
        reviewDateHeader.setFilterMatchMode("empty");
        assertFalse(plainInput.isDisplayed());
        assertFalse(datePicker.isDisplayed());
        assertFalse(rangePicker.isDisplayed());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(50)
    @DisplayName("DataTable: GitHub #7427 picking a date via the shadow single-date calendar picker writes into "
            + "the (still submitted) filter value and filters correctly")
    void dateFilterCalendarPickerSingleValueInteraction(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell reviewDateHeader = dataTable.getHeader().getCell("review date").orElseThrow();
        // "equals" is the column's default already - no need to re-select it
        LocalDate today = LocalDate.now();
        String todayStr = today.format(REVIEW_DATE_FORMAT);
        WebElement pickerInput = reviewDateHeader.getWebElement()
                .findElement(By.cssSelector(".ui-column-filter-date input"));
        waitUntilDisplayed(pickerInput);

        // Act - open the calendar overlay and click on today's cell, exactly as a real user would
        pickerInput.click();
        WebElement panel = PrimeSelenium.getWebDriver().findElement(By.id(pickerInput.getDomAttribute("aria-controls")));
        WebElement todayLink = panel.findElement(By.cssSelector(".ui-datepicker-today a"));
        PrimeSelenium.guardAjax(todayLink).click();

        // Assert - the real (hidden but still submitted) filter input picked up the picker's value, and the
        // table filtered accordingly - the header itself is untouched by a filter round trip (only the tbody
        // re-renders for this table's default partialUpdate="true"), so the original reference is still live.
        // getAttribute("value"), not getDomAttribute() - the latter reads the static HTML attribute, which a
        // client-side .val() assignment never updates; only the live DOM property does.
        WebElement realFilterInput = reviewDateHeader.getColumnFilter();
        assertEquals(todayStr, realFilterInput.getAttribute("value"));
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> today.equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(51)
    @DisplayName("DataTable: GitHub #7427 picking a range via the shadow range-date calendar picker normalizes "
            + "its own \"date1 - date2\" display into the \"date1,date2\" filter value and filters correctly")
    void dateFilterCalendarPickerRangeInteraction(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell reviewDateHeader = dataTable.getHeader().getCell("review date").orElseThrow();
        waitUntilDisplayed(reviewDateHeader.getWebElement().findElement(By.className("ui-column-filter-date")));
        reviewDateHeader.setFilterMatchMode("between");
        LocalDate today = LocalDate.now();
        String todayStr = today.format(REVIEW_DATE_FORMAT);
        WebElement rangePickerInput = reviewDateHeader.getWebElement()
                .findElement(By.cssSelector(".ui-column-filter-date-range input"));

        // Act - open the calendar overlay and click on today's cell twice: the range picker's first click sets
        // only the range start (and deliberately doesn't fire a filter yet - a range with just one end set is
        // not submitted, see DatePicker's own fireDateSelectEvent()), so a second click on a date that's not
        // before the start closes the range - clicking the same "today" cell again keeps this test independent
        // of the calendar's current month layout, forming the (valid) single-day range [today, today]. The
        // panel re-renders after each pick (even the first, incomplete one), so the "today" cell must be
        // re-found in between.
        rangePickerInput.click();
        WebElement panel = PrimeSelenium.getWebDriver().findElement(By.id(rangePickerInput.getDomAttribute("aria-controls")));
        panel.findElement(By.cssSelector(".ui-datepicker-today a")).click();
        panel = PrimeSelenium.getWebDriver().findElement(By.id(rangePickerInput.getDomAttribute("aria-controls")));
        PrimeSelenium.guardAjax(panel.findElement(By.cssSelector(".ui-datepicker-today a"))).click();

        // Assert - normalized to the comma-joined wire format on the real filter input - the header itself is
        // untouched by a filter round trip (only the tbody re-renders for this table's default
        // partialUpdate="true"), so the original reference is still live. getAttribute("value"), not
        // getDomAttribute() - the latter reads the static HTML attribute, which a client-side .val()
        // assignment never updates; only the live DOM property does.
        WebElement realFilterInput = reviewDateHeader.getColumnFilter();
        assertEquals(todayStr + "," + todayStr, realFilterInput.getAttribute("value"));
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> today.equals(e.getReviewDate()))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(52)
    @DisplayName("DataTable: GitHub #7427 time/datetime columns also render a shadow calendar picker for a "
            + "single-value match mode, timeOnly for \"time\"")
    void timeAndDatetimeFilterCalendarPickerWidgetVisibility(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        HeaderCell checkInTimeHeader = dataTable.getHeader().getCell("check-in time").orElseThrow();
        HeaderCell lastLoginHeader = dataTable.getHeader().getCell("last login").orElseThrow();
        waitUntilDisplayed(checkInTimeHeader.getWebElement().findElement(By.className("ui-column-filter-date")));

        // Assert - both columns declare filterMatchMode="equals" as their default already (a single-value
        // mode), so there's no need to re-select it
        assertFalse(checkInTimeHeader.getColumnFilter().isDisplayed());
        assertTrue(checkInTimeHeader.getWebElement().findElement(By.className("ui-column-filter-date")).isDisplayed());

        assertFalse(lastLoginHeader.getColumnFilter().isDisplayed());
        assertTrue(lastLoginHeader.getWebElement().findElement(By.className("ui-column-filter-date")).isDisplayed());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertEquals("wgtTable", cfg.getString("widgetVar"));
    }

    private void assertEmployeeRows(DataTable dataTable, List<Employee> employees) {
        assertRows(dataTable, employees, Employee::getId);
    }

    // the shadow DatePicker widgets (see DataTableRenderer#encodeDateFilterWidgets()) initialize as part of the
    // page's own client-side widget setup, which - unlike an AJAX round trip - has no guard to wait on; give it
    // a moment on a freshly-loaded page rather than asserting immediately.
    private void waitUntilDisplayed(WebElement element) {
        PrimeSelenium.waitGui().until(d -> element.isDisplayed());
    }

    private static LocalDate startOfWeek(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
    }

    // mirrors RelativeMinutesOrHoursFilterConstraint#isWithinCyclicRange exactly - a bare LocalTime has no date
    // component, so the inclusive [start, end] range wraps past midnight (OR instead of AND) when start > end
    private static boolean isWithinCyclicRange(LocalTime value, LocalTime start, LocalTime end) {
        if (!start.isAfter(end)) {
            return !value.isBefore(start) && !value.isAfter(end);
        }
        return !value.isBefore(start) || !value.isAfter(end);
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "datatable/dataTable051.xhtml";
        }
    }
}
