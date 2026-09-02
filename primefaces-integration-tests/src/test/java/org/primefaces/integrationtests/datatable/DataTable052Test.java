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
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.HeaderCell;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DataTable: the table-level {@code filterValueType}, the single place to switch the end user's filter
 * match-mode picker off (or force one type) for every column of a table at once, instead of repeating the
 * column-level attribute on each of them. {@link DataTable051Test} covers the column-level attribute and the
 * individual match modes themselves; this class covers only the precedence between the two levels and the
 * auto-derivation they override.
 */
@Tag("DataTable-filter")
class DataTable052Test extends AbstractDataTableTest {

    protected final List<Employee> employees = new EmployeeService().getEmployees();

    @Test
    @Order(1)
    @DisplayName("DataTable: table-level filterValueType=\"none\" suppresses the picker on every column, "
            + "including ones that would otherwise auto-derive a preset from their Java type")
    void tableLevelNoneSuppressesPickerOnEveryColumn(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableNone;

        // Assert - "ID" is an Integer field with a converter (would auto-derive "numeric"), "first name" a
        // plain String (would auto-derive "text"), and "last name" carries an explicit filterMatchMode -
        // the table-level "none" suppresses the picker on all three
        for (String header : List.of("ID", "last name", "first name")) {
            HeaderCell cell = dataTable.getHeader().getCell(header).orElseThrow();
            assertNull(cell.getColumnFilterMatchModeIcon(), "Expected no match-mode picker on '" + header + "'");
            assertNull(cell.getColumnFilterMatchModeValue(), "Expected no match-mode input on '" + header + "'");
        }

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtTableNone");
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: a column left plain by table-level filterValueType=\"none\" still filters, "
            + "fixed to its own filterMatchMode")
    void tableLevelNoneKeepsPlainColumnFiltering(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableNone;

        // Act - "last name" is fixed to filterMatchMode="contains"
        dataTable.filter("last name", "ma");

        // Assert
        List<Employee> expected = employees.stream()
                .filter(e -> e.getLastName().toLowerCase().contains("ma"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, expected);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtTableNone");
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: a column's own filterValueType wins over table-level \"none\", so a single "
            + "column can opt back in - and the picker it gets still filters")
    void columnFilterValueTypeOverridesTableLevelNone(Page page) {
        // Arrange - "salary" declares filterValueType="numeric" against the table's "none"
        DataTable dataTable = page.dataTableNone;
        HeaderCell salaryHeader = dataTable.getHeader().getCell("salary").orElseThrow();

        // Assert - this one column does get a picker, offering the numeric preset
        List<String> labels = salaryHeader.getFilterMatchModeLabels();
        assertTrue(labels.contains("Less Than"), "Expected the \"numeric\" preset, got: " + labels);
        assertTrue(labels.contains("Between"), "Expected the \"numeric\" preset, got: " + labels);
        assertEquals("gt", salaryHeader.getColumnFilterMatchModeValue(), "Expected the column's own filterMatchMode");

        // Act - switch the comparator and filter through it
        dataTable.filterMatchMode("salary", "lt");
        dataTable.filter("salary", "2500");

        // Assert - null salaries (Alfred Paul) are not "less than" anything
        List<Employee> expected = employees.stream()
                .filter(e -> e.getSalary() != null && e.getSalary() < 2500)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, expected);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtTableNone");
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: a typed table-level filterValueType overrides what a column would otherwise "
            + "auto-derive from its Java type")
    void tableLevelFilterValueTypeOverridesAutoDerivation(Page page) {
        // Arrange - "ID" is an Integer field WITH a converter, so it would auto-derive the "numeric" preset;
        // the table declares filterValueType="text", so the "text" preset can only come from that attribute
        DataTable dataTable = page.dataTableText;
        HeaderCell idHeader = dataTable.getHeader().getCell("ID").orElseThrow();

        // Assert
        List<String> labels = idHeader.getFilterMatchModeLabels();
        assertTrue(labels.contains("Contains"), "Expected the table-level \"text\" preset, got: " + labels);
        assertTrue(labels.contains("Matches Regex"), "Expected the table-level \"text\" preset, got: " + labels);
        assertFalse(labels.contains("Less Than"), "Expected \"numeric\" NOT to be auto-derived, got: " + labels);
        assertFalse(labels.contains("Between"), "Expected \"numeric\" NOT to be auto-derived, got: " + labels);

        // Act - the column's filterMatchMode="contains" is already a member of the "text" preset and is
        // preselected, so filter straight through it
        dataTable.filter("ID", "53");

        // Assert - a string "contains" against the id, not a numeric comparison
        List<Employee> expected = employees.stream()
                .filter(e -> String.valueOf(e.getId()).contains("53"))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, expected);

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtTableText");
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: a column may still opt out with its own filterValueType=\"none\" on a table "
            + "that declares a typed table-level default")
    void columnNoneOptsOutOfTypedTable(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableText;
        HeaderCell lastNameHeader = dataTable.getHeader().getCell("last name").orElseThrow();

        // Assert
        assertNull(lastNameHeader.getColumnFilterMatchModeIcon());
        assertNull(lastNameHeader.getColumnFilterMatchModeValue());

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtTableText");
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: a column with its own custom <f:facet name=\"filter\"> gets no picker even on "
            + "a table that declares a typed table-level default - the table-wide value must not be able to "
            + "layer a second, competing picker on a bespoke filter UI")
    void customFilterFacetBeatsTableLevelDefault(Page page) {
        // Arrange
        DataTable dataTable = page.dataTableText;
        HeaderCell birthDateHeader = dataTable.getHeader().getCell("birth date").orElseThrow();

        // Assert
        assertNull(birthDateHeader.getColumnFilterMatchModeIcon());
        assertNull(birthDateHeader.getColumnFilterMatchModeValue());

        assertConfiguration(dataTable.getWidgetConfiguration(), "wgtTableText");
    }

    private void assertConfiguration(JSONObject cfg, String expectedWidgetVar) {
        assertNoJavascriptErrors();
        assertEquals(expectedWidgetVar, cfg.getString("widgetVar"));
    }

    private void assertEmployeeRows(DataTable dataTable, List<Employee> employees) {
        assertRows(dataTable, employees, Employee::getId);
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatableNone")
        DataTable dataTableNone;

        @FindBy(id = "form:datatableText")
        DataTable dataTableText;

        @Override
        public String getLocation() {
            return "datatable/dataTable052.xhtml";
        }
    }
}
