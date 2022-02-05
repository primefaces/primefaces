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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.SelectManyMenu;
import org.primefaces.selenium.component.model.datatable.Row;

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
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() < 5).collect(Collectors.toList());
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
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() <= 5).collect(Collectors.toList());
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
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() > 5).collect(Collectors.toList());
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
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() >= 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: filter: equals")
    public void testFilterEquals(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("ID equals", "5");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getId() == 5).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: filter: startsWith")
    public void testFilterStartsWith(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("last name startsWith", "Ma");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getLastName().startsWith("Ma")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filter("last name startsWith", "Mas");

        // Assert
        employeesFiltered = employees.stream().filter(e -> e.getLastName().startsWith("Mas")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: filter: endsWith")
    public void testFilterEndsWith(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("last name endsWith", "er");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getLastName().endsWith("er")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filter("last name endsWith", "yer");

        // Assert
        employeesFiltered = employees.stream().filter(e -> e.getLastName().endsWith("yer")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: filter: contains")
    public void testFilterContains(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("last name contains", "s");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getLastName().contains("s")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filter("last name contains", "sh");

        // Assert
        employeesFiltered = employees.stream().filter(e -> e.getLastName().contains("sh")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: filter: exact")
    public void testFilterExact(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter("last name exact", "Mayer");

        // Assert
        List<Employee> employeesFiltered = employees.stream().filter(e -> e.getLastName().equalsIgnoreCase("Mayer")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        dataTable.filter("last name exact", "Mayr");

        // Assert
        employeesFiltered = employees.stream().filter(e -> e.getLastName().equalsIgnoreCase("Mayr")).collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: filter: range (part 1)")
    public void testFilterRange1(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        page.birthdateRangeFilter.getInput().sendKeys("1/1/1970 - 1/5/1970");
        PrimeSelenium.guardAjax(page.birthdateRangeFilter.getInput()).sendKeys(Keys.TAB);

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getBirthDate().isAfter(LocalDate.of(1969, 12, 31)) && e.getBirthDate().isBefore(LocalDate.of(1970, 1, 6)))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Setting range via Selenium (sendKeys) causes JS-errors. So we don´t check for them.
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("DataTable: filter: range (part 2)")
    public void testFilterRange2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        page.birthdateRangeFilter.getInput().sendKeys("12/25/1969 - 1/3/1970");
        PrimeSelenium.guardAjax(page.birthdateRangeFilter.getInput()).sendKeys(Keys.TAB);

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getBirthDate().isAfter(LocalDate.of(1969, 12, 24))
                        && e.getBirthDate().isBefore(LocalDate.of(1970, 1, 4)))
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Setting range via Selenium (sendKeys) causes JS-errors. So we don´t check for them.
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("DataTable: filter: in")
    public void testFilterIn(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        page.roleFilter.deselect(Employee.Role.MANAGER.toString(), true);
        page.roleFilter.deselect(Employee.Role.SALES.toString(), true);
        page.roleFilter.deselect(Employee.Role.FINANCE.toString(), true);
        page.roleFilter.deselect(Employee.Role.HR.toString(), true);
        page.roleFilter.deselect(Employee.Role.QS.toString(), true);

        // Assert
        List<Employee> employeesFiltered = employees.stream()
                .filter(e -> e.getRole() == Employee.Role.DEVELOPER)
                .collect(Collectors.toList());
        assertEmployeeRows(dataTable, employeesFiltered);

        // Act
        page.roleFilter.select(Employee.Role.QS.toString(), true, true);

        // Assert
        employeesFiltered = employees.stream()
                .filter(e -> e.getRole() == Employee.Role.DEVELOPER || e.getRole() == Employee.Role.QS)
                .collect(Collectors.toList());
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

        @FindBy(id = "form:datatable:birthdateRangeFilter")
        DatePicker birthdateRangeFilter;

        @FindBy(id = "form:datatable:roleFilter")
        SelectManyMenu roleFilter;

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
        Assertions.assertEquals(expectedSize, rows.size());

        int row = 0;
        for (Employee employee : employees) {
            String rowText = rows.get(row).getCell(0).getText();
            Assertions.assertEquals(employee.getId(), Integer.parseInt(rowText.trim()));
            row++;
        }
    }
}
