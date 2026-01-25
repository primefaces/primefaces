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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.SelectManyMenu;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.component.model.datatable.Row;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataTable026NotTest extends AbstractDataTableTest {

    protected final List<Employee> employees = new EmployeeService().getEmployees();

    @Test
    @Order(1)
    @DisplayName("DataTable: filter: NOT equals")
    void filterNotEquals(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("salary notEquals", "3000");

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getSalary() == null || e.getSalary() != 3000)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: filter: NOT between")
    void filterNotBetween(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        LocalDate start = LocalDate.of(2021, 1, 1);
        LocalDate end = LocalDate.of(2021, 1, 10);
        page.lastLoginDateTimeFilter.clear();
        ComponentUtils.sendKeys(page.lastLoginDateTimeFilter.getInput(), "" + start + " - " + end);
        PrimeSelenium.guardAjax(page.lastLoginDateTimeFilter.getInput()).sendKeys(Keys.TAB);

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> {
                    if (e.getLastLoginDateTime() == null) {
                        return true;
                    }
                    LocalDate date = e.getLastLoginDateTime().toLocalDate();
                    return !(date.equals(start) || date.equals(end) || (date.isAfter(start) && date.isBefore(end)));
                })
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: filter: NOT between invalid filter")
    void filterNotBetweenInvalid(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        LocalDate start = LocalDate.of(2021, 1, 1);
        page.lastLoginDateTimeFilter.clear();
        ComponentUtils.sendKeys(page.lastLoginDateTimeFilter.getInput(), "" + start);
        PrimeSelenium.guardAjax(page.lastLoginDateTimeFilter.getInput()).sendKeys(Keys.TAB);

        // Assert
        assertEmployeeRows(dataTable, employees);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertEquals("wgtTable", cfg.getString("widgetVar"));
        assertEquals(0, cfg.getInt("tabindex"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;

        @FindBy(id = "form:datatable:birthdateRangeFilter")
        DatePicker birthdateRangeFilter;

        @FindBy(id = "form:datatable:roleFilter")
        SelectManyMenu roleFilter;

        @FindBy(id = "form:datatable:lastLoginDateTimeFilter")
        DatePicker lastLoginDateTimeFilter;

        @FindBy(id = "form:datatable:lastLoginDateFilter")
        DatePicker lastLoginDateFilter;

        @FindBy(id = "form:datatable:lastLoginDateTimeFilter2")
        DatePicker lastLoginDateTimeFilter2;

        @FindBy(id = "form:datatable:lastLoginDateFilter2")
        DatePicker lastLoginDateFilter2;

        @Override
        public String getLocation() {
            return "datatable/dataTable026Not.xhtml";
        }
    }

    private void assertEmployeeRows(DataTable dataTable, List<Employee> employees) {
        List<Row> rows = dataTable.getRows();
        assertEmployeeRows(rows, employees);
    }

    private void assertEmployeeRows(List<Row> rows, List<Employee> employees) {
        int expectedSize = employees.size();
        assertNotNull(rows);
        assertEquals(expectedSize, rows.size());

        int row = 0;
        for (Employee employee : employees) {
            String rowText = rows.get(row).getCell(0).getText();
            assertEquals(employee.getId(), Integer.parseInt(rowText.trim()));
            row++;
        }
    }
}
