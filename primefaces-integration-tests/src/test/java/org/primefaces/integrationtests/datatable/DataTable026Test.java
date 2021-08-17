/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.List;
import java.util.stream.Collectors;

public class DataTable026Test extends AbstractDataTableTest {

    protected final List<Employee> employees = new EmployeeService().getEmployees();

    @Test
    @Order(1)
    @DisplayName("DataTable: filter: lt")
    public void testFilterLt(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("ID lt", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId()<5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: filter: lte")
    public void testFilterLte(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("ID lte", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId()<=5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: filter: gt")
    public void testFilterGt(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("ID gt", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId()>5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: filter: gte")
    public void testFilterGte(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("ID gte", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId()>=5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertEquals("wgtTable", cfg.getString("widgetVar"));
        Assertions.assertEquals(0, cfg.getInt("tabindex"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable026.xhtml";
        }
    }

    private void assertEmployeeRows(DataTable dataTable, List<Employee> employees) {
        List<Row> rows = dataTable.getRows();
        assertEmployeeRows(rows, employees);
    }

    private void assertEmployeeRows(List<Row> rows, List<Employee> employees) {
        int expectedSize = employees.size();
        Assertions.assertNotNull(rows);
        if (expectedSize == 0) {
            expectedSize = 1; // No records found.
        }
        Assertions.assertEquals(expectedSize, rows.size());

        int row = 0;
        for (Employee employee : employees) {
            String rowText = rows.get(row).getCell(0).getText();
            if (!rowText.equalsIgnoreCase("No records found.")) {
                Assertions.assertEquals(employee.getId(), Integer.parseInt(rowText.trim()));
                row++;
            }
        }
    }
}
