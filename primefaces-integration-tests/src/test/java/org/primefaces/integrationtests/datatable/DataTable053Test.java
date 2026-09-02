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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

/**
 * DataTable: the filterValueType match modes on a lazy table backed by
 * {@link org.primefaces.model.JPALazyDataModel} - dataTable051.xhtml covers the very same modes against an
 * in-memory List, so what is asserted here is that each of them also survives the trip through a criteria
 * query and the database.
 * <p>
 * The fixture is described in {@code JpaEmployeeRepository#reseed}; the ids are asserted rather than the
 * employees themselves, as the rows come from the database rather than from a list the test could mirror.
 */
@Tag("DataTable-filter")
class DataTable053Test extends AbstractDataTableTest {

    private static final DateTimeFormatter REVIEW_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Test
    @Order(1)
    @DisplayName("DataTable: JPALazyDataModel loads and sorts server side")
    void loadAndSort(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Assert - the whole fixture, and the count query behind the paginator
        assertIds(dataTable, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals("9 employees", page.currentPageReport().getText());

        // Act - sorting is part of the same criteria query
        dataTable.sort("id");
        dataTable.sort("id");

        // Assert - descending, so the order itself is asserted this time
        assertEquals(List.of(9, 8, 7, 6, 5, 4, 3, 2, 1), ids(dataTable));

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: numeric match modes are translated into the query")
    void numericMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - "greater than", the configured default of the salary column
        dataTable.filter("salary", "6000");

        // Assert
        assertIds(dataTable, 4, 5);

        // Act
        dataTable.filterMatchMode("salary", "lte");
        dataTable.filter("salary", "3000");

        // Assert
        assertIds(dataTable, 2, 7, 9);

        // Act - a comma-separated range
        dataTable.filterMatchMode("salary", "between");
        dataTable.filter("salary", "4000,6000");

        // Assert
        assertIds(dataTable, 1, 6, 8);

        // Act - and a comma-separated list
        dataTable.filterMatchMode("salary", "in");
        dataTable.filter("salary", "2000, 9000");

        // Assert
        assertIds(dataTable, 5, 7);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: text match modes are translated into the query")
    void textMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - "contains", the configured default of the name column
        dataTable.filter("name", "Ma");

        // Assert
        assertIds(dataTable, 1, 6, 8);

        // Act
        dataTable.filterMatchMode("name", "startsWith");
        dataTable.filter("name", "Mi");

        // Assert
        assertIds(dataTable, 1);

        // Act
        dataTable.filterMatchMode("name", "endsWith");
        dataTable.filter("name", "Clark");

        // Assert
        assertIds(dataTable, 5);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: \"is (not) empty\"/\"is (not) null\" are applied even though no value is typed")
    void valueLessTextMatchModes(Page page) {
        // Arrange - before the fix these modes were dropped for a lazy JPA model, silently showing every row
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filterMatchMode("name", "empty");

        // Assert - the null and the blank name
        assertIds(dataTable, 3, 4);

        // Act
        dataTable.filterMatchMode("name", "notEmpty");

        // Assert
        assertIds(dataTable, 1, 2, 5, 6, 7, 8, 9);

        // Act
        dataTable.filterMatchMode("name", "null");

        // Assert - strictly null, unlike "is empty" which also matches the blank row
        assertIds(dataTable, 3);

        // Act
        dataTable.filterMatchMode("name", "notNull");

        // Assert
        assertIds(dataTable, 1, 2, 4, 5, 6, 7, 8, 9);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: \"matches regex\" works once the model brings the regex function of its database")
    void matchesRegex(Page page) {
        // Arrange - the page's model is an H2RegexJPALazyDataModel, since regex has no portable JPA translation
        DataTable dataTable = page.dataTable;
        dataTable.filterMatchMode("name", "regex");

        // Act
        dataTable.filter("name", "^M.*");

        // Assert
        assertIds(dataTable, 1, 8);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: the boolean match modes are applied even though none of them takes a value")
    void booleanMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filterMatchMode("active", "true");

        // Assert
        assertIds(dataTable, 1, 4, 6, 8);

        // Act
        dataTable.filterMatchMode("active", "false");

        // Assert
        assertIds(dataTable, 2, 5, 7, 9);

        // Act
        dataTable.filterMatchMode("active", "null");

        // Assert
        assertIds(dataTable, 3);

        // Act - "all" is the "no filter selected" placeholder of a preset without a value taking mode
        dataTable.filterMatchMode("active", "all");

        // Assert
        assertIds(dataTable, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: the relative date match modes are translated into a date range in the query")
    void relativeDateMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filterMatchMode("review date", "today");

        // Assert
        assertIds(dataTable, 1);

        // Act
        dataTable.filterMatchMode("review date", "yesterday");

        // Assert
        assertIds(dataTable, 2);

        // Act
        dataTable.filterMatchMode("review date", "tomorrow");

        // Assert
        assertIds(dataTable, 4);

        // Act
        dataTable.filterMatchMode("review date", "lastWeek");

        // Assert
        assertIds(dataTable, 5);

        // Act
        dataTable.filterMatchMode("review date", "nextMonth");

        // Assert
        assertIds(dataTable, 8);

        // Act
        dataTable.filterMatchMode("review date", "lastYear");

        // Assert
        assertIds(dataTable, 7);

        // Act - "last N days" is one of the three relative date modes that do take a value
        dataTable.filterMatchMode("review date", "lastNDays");
        dataTable.filter("review date", "3");

        // Assert - today and yesterday, not the row a week ago
        assertIds(dataTable, 1, 2);

        // Act
        dataTable.filterMatchMode("review date", "relativeDate");
        dataTable.filter("review date", "3");

        // Assert - within three days in either direction
        assertIds(dataTable, 1, 2, 4);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: an absolute date filter still works next to the relative ones")
    void absoluteDateMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        LocalDate today = LocalDate.now();

        // Act
        dataTable.filter("review date", REVIEW_DATE_FORMAT.format(today));

        // Assert - "equals", the configured default of the column
        assertIds(dataTable, 1);

        // Act
        dataTable.filterMatchMode("review date", "lt");
        dataTable.filter("review date", REVIEW_DATE_FORMAT.format(today));

        // Assert
        assertIds(dataTable, 2, 5, 7);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: \"last/next N minutes/hours\" are translated into a timestamp range in the query")
    void relativeTimeMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filterMatchMode("last login", "lastNMinutes");
        dataTable.filter("last login", "15");

        // Assert - the row logged in ten minutes ago, not the one logging in ten minutes from now
        assertIds(dataTable, 1);

        // Act
        dataTable.filterMatchMode("last login", "nextNMinutes");
        dataTable.filter("last login", "15");

        // Assert
        assertIds(dataTable, 4);

        // Act
        dataTable.filterMatchMode("last login", "lastNHours");
        dataTable.filter("last login", "2");

        // Assert
        assertIds(dataTable, 1, 2);

        // Act - the same modes on a bare time of day, a cyclic 24h clock. Clearing the value is what switches
        // the last login filter off again: unlike the boolean preset, the datetime one has no "all" placeholder,
        // as every one of its modes has a "nothing typed yet" resting state of its own
        dataTable.removeFilter("last login");
        dataTable.filterMatchMode("start time", "lastNHours");
        dataTable.filter("start time", "2");

        // Assert
        assertIds(dataTable, 1, 2);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: the enum match modes are translated into the query")
    void enumMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("department", "ENGINEERING");

        // Assert
        assertIds(dataTable, 1, 5, 8);

        // Act
        dataTable.filterMatchMode("department", "in");
        dataTable.filter("department", "MARKETING, SALES");

        // Assert
        assertIds(dataTable, 2, 4, 6, 7, 9);

        // Act
        dataTable.filterMatchMode("department", "notEmpty");

        // Assert
        assertIds(dataTable, 1, 2, 4, 5, 6, 7, 8, 9);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("DataTable: the array match modes become MEMBER OF predicates on the mapped collection")
    void arrayMatchModes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - "contains", the configured default of the tags column
        dataTable.filter("tags", "java");

        // Assert
        assertIds(dataTable, 1, 2, 8);

        // Act
        dataTable.filterMatchMode("tags", "arrayNotContains");
        dataTable.filter("tags", "java");

        // Assert - including the rows without any tag at all
        assertIds(dataTable, 3, 4, 5, 6, 7, 9);

        // Act
        dataTable.filterMatchMode("tags", "containsAny");
        dataTable.filter("tags", "java, xml");

        // Assert
        assertIds(dataTable, 1, 2, 4, 8);

        // Act
        dataTable.filterMatchMode("tags", "containsAll");
        dataTable.filter("tags", "java, xml");

        // Assert
        assertIds(dataTable, 8);

        // Act
        dataTable.filterMatchMode("tags", "containsNone");
        dataTable.filter("tags", "java, xml");

        // Assert
        assertIds(dataTable, 3, 5, 6, 7, 9);

        // Act
        dataTable.filterMatchMode("tags", "empty");

        // Assert
        assertIds(dataTable, 3, 6, 7, 9);

        // Act
        dataTable.filterMatchMode("tags", "notEmpty");

        // Assert
        assertIds(dataTable, 1, 2, 4, 5, 8);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("DataTable: two filters are combined, and the count query filters the same way")
    void combinedFilters(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filterMatchMode("active", "true");
        dataTable.filter("salary", "5000");

        // Assert - active AND salary greater than 5000
        assertIds(dataTable, 4, 8);
        assertEquals("2 employees", page.currentPageReport().getText());

        // Act - a full server-side re-render must keep both filters applied
        page.buttonUpdate.click();

        // Assert
        assertIds(page.dataTable, 4, 8);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertEquals("wgtTable", cfg.getString("widgetVar"));
    }

    private List<Integer> ids(DataTable dataTable) {
        List<Row> rows = dataTable.getRows();
        return rows == null
                ? List.of()
                : rows.stream().map(row -> Integer.parseInt(row.getCell(0).getText().trim())).collect(Collectors.toList());
    }

    /**
     * Asserts the ids of the displayed rows, ignoring their order - the rows come from the database in
     * whatever order it returns them, unless the table is explicitly sorted.
     */
    private void assertIds(DataTable dataTable, Integer... expected) {
        Set<Integer> actual = new TreeSet<>(ids(dataTable));
        assertEquals(new TreeSet<>(Arrays.asList(expected)), actual);
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        WebElement currentPageReport() {
            return dataTable.findElement(By.className("ui-paginator-current"));
        }

        @Override
        public String getLocation() {
            return "datatable/dataTable053.xhtml";
        }
    }
}
