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
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.HeaderCell;
import org.primefaces.selenium.component.model.datatable.Row;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DataTable: clearing a filter match-mode picker again - the per-column "Clear" action inside the picker's own
 * overlay menu and the table-wide {@code clearFiltersButton} - and {@code filterValueType} on
 * {@code p:columns}, where the value comes from a dynamic column model instead of a literal attribute.
 * {@link DataTable051Test} covers the individual match modes and {@link DataTable052Test} the table-level
 * versus column-level precedence.
 */
@Tag("DataTable-filter")
class DataTable054Test extends AbstractDataTableTest {

    private static final Integer[] ALL_IDS = {1, 2, 3, 4, 5, 6, 11, 533};

    @Test
    @Order(1)
    @DisplayName("DataTable: the picker's \"Clear\" action resets the column's value and match mode back to "
            + "the column's own configured default, then re-filters")
    void columnClearActionResetsValueAndModeToColumnDefault(Page page) {
        // Arrange - "salary" declares filterMatchMode="lt", deliberately not the numeric preset's first
        // entry, so restoring it cannot be confused with simply picking the first option
        DataTable dataTable = page.dataTableClear;
        HeaderCell salaryHeader = dataTable.getHeader().getCell("salary").orElseThrow();
        assertEquals("lt", salaryHeader.getColumnFilterMatchModeValue());

        // Act - move away from that default in both respects: a different mode and a typed value
        dataTable.filterMatchMode("salary", "gt");
        dataTable.filter("salary", "2500");

        // Assert - Alfred Paul (id 3) has no salary and is not "greater than" anything
        assertEquals("gt", salaryHeader.getColumnFilterMatchModeValue());
        assertIds(dataTable, 1, 4, 11, 533);

        // Act
        dataTable.clearColumnFilter("salary");

        // Assert - value gone, mode back to the column's own default, and the table unfiltered again
        assertEquals("", salaryHeader.getColumnFilter().getDomProperty("value"));
        assertEquals("lt", salaryHeader.getColumnFilterMatchModeValue());
        assertIds(dataTable, ALL_IDS);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtClear");
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: the picker's \"Clear\" action clears its own column only, leaving the other "
            + "columns' filters in place")
    void columnClearActionLeavesOtherColumnsFiltered(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableClear;
        HeaderCell lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();

        // Act - two columns filtered at once: ids above 5, last name containing "ma"
        dataTable.filter("ID", "5");
        dataTable.filter("last name", "ma");

        // Assert - Mayer (6) and March (533); Master (1) is filtered out by the id
        assertIds(dataTable, 6, 533);

        // Act - clear the last-name column alone
        dataTable.clearColumnFilter("last name");

        // Assert - its own value is gone, but the id filter still applies
        assertEquals("", lastNameHeader.getColumnFilter().getDomProperty("value"));
        assertIds(dataTable, 6, 11, 533);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtClear");
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: the table-wide \"Clear Filters\" button resets every column at once - the "
            + "match-mode pickers and a plain filter input alike")
    void clearFiltersButtonClearsEveryColumnAtOnce(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableClear;
        HeaderCell idHeader = dataTable.getHeader().getCell("ID").orElseThrow();
        HeaderCell lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();
        HeaderCell salaryHeader = dataTable.getHeader().getCell("salary").orElseThrow();
        HeaderCell firstNameHeader = dataTable.getHeader().getCell("first name").orElseThrow();

        // Act - all four columns filtered, one of them ("first name") having no picker at all
        dataTable.filter("ID", "5");
        dataTable.filter("last name", "ma");
        dataTable.filterMatchMode("salary", "gt");
        dataTable.filter("salary", "2500");
        dataTable.filter("first name", "ar");

        // Assert - only Mary March (533) satisfies id > 5, last name "ma", salary > 2500 and first name "ar"
        assertIds(dataTable, 533);

        // Act
        PrimeSelenium.guardAjax(page.clearFiltersButton()).click();

        // Assert - every value input empty, every picker back to its column's configured default
        assertEquals("", idHeader.getColumnFilter().getDomProperty("value"));
        assertEquals("", lastNameHeader.getColumnFilter().getDomProperty("value"));
        assertEquals("", salaryHeader.getColumnFilter().getDomProperty("value"));
        assertEquals("", firstNameHeader.getColumnFilter().getDomProperty("value"));
        assertEquals("gt", idHeader.getColumnFilterMatchModeValue());
        assertEquals("contains", lastNameHeader.getColumnFilterMatchModeValue());
        assertEquals("lt", salaryHeader.getColumnFilterMatchModeValue());
        assertIds(dataTable, ALL_IDS);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtClear");
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: clearing a date column resets the shadow DatePicker widget as well, not just "
            + "the filter input that gets submitted")
    void clearResetsDatePickerWidgetState(Page page) {
        // Arrange - "review date" is filterValueType="date" with filterMatchMode="equals", so a shadow
        // single-date DatePicker is the visible widget from first paint and the plain input is hidden
        DataTable dataTable = page.dataTableClear;
        HeaderCell reviewDateHeader = dataTable.getHeader().getCell("review date").orElseThrow();
        WebElement pickerInput = reviewDateHeader.getWebElement().findElement(By.cssSelector(".ui-column-filter-date input"));
        PrimeSelenium.waitGui().until(d -> pickerInput.isDisplayed());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Act - pick today from the calendar overlay, exactly as a real user would
        pickerInput.click();
        WebElement panel = PrimeSelenium.getWebDriver()
                .findElement(By.id(Objects.requireNonNull(pickerInput.getDomAttribute("aria-controls"))));
        PrimeSelenium.guardAjax(panel.findElement(By.cssSelector(".ui-datepicker-today a"))).click();

        // Assert - the picker wrote its value into the real (hidden) filter input and the table filtered
        assertEquals(today, pickerInput.getAttribute("value"));
        assertEquals(today, reviewDateHeader.getColumnFilter().getAttribute("value"));
        assertIds(dataTable, 1);

        // Act
        dataTable.clearColumnFilter("review date");

        // Assert - both the submitted input AND the picker's own input are empty, and the widget itself no
        // longer holds a date: a widget left holding one re-displays it as soon as it next renders
        assertEquals("", reviewDateHeader.getColumnFilter().getAttribute("value"));
        assertEquals("", pickerInput.getAttribute("value"));
        assertFalse(datePickerHasDate(pickerInput), "Shadow DatePicker widget still holds a date after clearing");
        assertIds(dataTable, ALL_IDS);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtClear");
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: p:columns - each generated column gets the preset its own filterValueType names "
            + "in the column model, and filtering through the picker works")
    void dynamicColumnsGetTheirPickerFromTheColumnModel(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableColumns;
        HeaderCell idHeader = dataTable.getHeader().getCell("ID").orElseThrow();
        HeaderCell lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();

        // Assert - filterValueType="numeric" from the model, and the column's own filterMatchMode preselected
        List<String> idLabels = idHeader.getFilterMatchModeLabels();
        assertTrue(idLabels.contains("Less Than"), "Expected the \"numeric\" preset, got: " + idLabels);
        assertTrue(idLabels.contains("Between"), "Expected the \"numeric\" preset, got: " + idLabels);
        assertEquals("gt", idHeader.getColumnFilterMatchModeValue());

        // Assert - and filterValueType="text" on the very next generated column, so the presets are resolved
        // per column and not once for the whole p:columns
        List<String> lastNameLabels = lastNameHeader.getFilterMatchModeLabels();
        assertTrue(lastNameLabels.contains("Matches Regex"), "Expected the \"text\" preset, got: " + lastNameLabels);
        assertFalse(lastNameLabels.contains("Between"), "Expected no \"numeric\" modes, got: " + lastNameLabels);
        assertEquals("contains", lastNameHeader.getColumnFilterMatchModeValue());

        // Act - filter by value through the text column's picker
        dataTable.filterMatchMode("last name", "endsWith");
        dataTable.filter("last name", "er");

        // Assert - Master (1), Pepper (2) and Mayer (6) all end in "er"
        assertIds(dataTable, 1, 2, 6);

        // Act - switching a mode on the numeric column with no value typed leaves the filter inactive, but
        // the picker still records the choice (p:columns has no converter to parse a typed number with)
        dataTable.clearColumnFilter("last name");
        dataTable.filterMatchMode("ID", "lte");

        // Assert
        assertEquals("lte", idHeader.getColumnFilterMatchModeValue());
        assertIds(dataTable, ALL_IDS);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtColumns");
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: p:columns - a generated column with filterValueType=\"none\" gets no picker but "
            + "still filters, fixed to its own filterMatchMode")
    void dynamicColumnWithoutPickerStillFilters(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableColumns;
        HeaderCell firstNameHeader = dataTable.getHeader().getCell("first name").orElseThrow();

        // Assert
        assertNull(firstNameHeader.getColumnFilterMatchModeIcon(), "Expected no match-mode picker");
        assertNull(firstNameHeader.getColumnFilterMatchModeValue(), "Expected no match-mode input");

        // Act - still filters, fixed to the model's filterMatchMode="contains"
        dataTable.filter("first name", "ar");

        // Assert - Margret (11) and Mary (533)
        assertIds(dataTable, 11, 533);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtColumns");
    }

    /**
     * Asks the shadow DatePicker <em>widget</em> whether it still holds a date - the DOM input can read empty
     * while the widget's own internal state does not, and it is that state which gets re-displayed.
     */
    private boolean datePickerHasDate(WebElement pickerInput) {
        String pickerId = pickerInput.getDomAttribute("id");
        return Boolean.TRUE.equals(PrimeSelenium.executeScript(
                "var w = PrimeFaces.getWidgetById(arguments[0].replace(/_input$/, ''));"
                        + "return w ? w.hasDate() : false;",
                pickerId));
    }

    private void assertConfiguration(JSONObject cfg, String expectedWidgetVar) {
        assertNoJavascriptErrors();
        assertEquals(expectedWidgetVar, cfg.getString("widgetVar"));
    }

    private List<Integer> ids(DataTable dataTable) {
        List<Row> rows = dataTable.getRows();
        return rows == null
                ? List.of()
                : rows.stream().map(row -> Integer.parseInt(row.getCell(0).getText().trim())).collect(Collectors.toList());
    }

    /**
     * Asserts the ids of the displayed rows exactly, ignoring their order.
     */
    private void assertIds(DataTable dataTable, Integer... expected) {
        assertEquals(new TreeSet<>(Arrays.asList(expected)), new TreeSet<>(ids(dataTable)));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatableClear")
        DataTable dataTableClear;

        @FindBy(id = "form:datatableColumns")
        DataTable dataTableColumns;

        /**
         * The built-in "Clear Filters" button is rendered by the table itself (no developer-declared id to
         * bind with @FindBy), so it is located inside the table it belongs to.
         */
        WebElement clearFiltersButton() {
            return dataTableClear.findElement(By.className("ui-datatable-clear-filters-button"));
        }

        @Override
        public String getLocation() {
            return "datatable/dataTable054.xhtml";
        }
    }
}
