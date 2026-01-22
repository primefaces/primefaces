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
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable007CellTest extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: Cell Edit Add-Row")
    void addRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        List<Row> rows = dataTable.getRows();
        assertEquals(5, rows.size()); // only 5 rows to start

        // Act
        page.btnAddRow.click();

        // Assert
        rows = dataTable.getRows();
        assertEquals(6, rows.size()); // now has 6 after row added
        Row row = dataTable.getRow(5);
        assertEquals("Smalltalk", row.getCell(1).getText());
        assertEquals("6", row.getCell(0).getText());
        assertEquals("New Language added", page.messages.getMessage(0).getSummary());
        assertEquals("Smalltalk", page.messages.getMessage(0).getDetail());
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: Cell Edit Edit-Cell")
    void editCell(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act - edit and accept
        Row row = dataTable.getRow(2);
        updateCell(row, 1, "xyz");
        saveCell(page);
        updateCell(row, 2, "2000");
        saveCell(page);

        // Assert
        row = dataTable.getRow(2);
        assertEquals("xyz", row.getCell(1).getText());
        assertEquals("2000", row.getCell(2).getText());
        assertEquals("Cell Changed", page.messages.getMessage(0).getSummary());
        assertEquals("Old: 1995, New:2000", page.messages.getMessage(0).getDetail());

        // Act - submit
        page.btnSubmit.click();

        // Assert
        row = dataTable.getRow(2);
        assertEquals("xyz", row.getCell(1).getText());
        assertEquals("2000", row.getCell(2).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #1442 Filter combined with Cell Edit; https://github.com/primefaces/primefaces/issues/1442")
    void filterAndEditRow_1442(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act - filter
        dataTable.filter("Name", "Java");

        // Act - edit and accept
        Row row = dataTable.getRow(1);
        updateCell(row, 1, "abc");
        saveCell(page);
        updateCell(row, 2, "2020");
        saveCell(page);

        // Act - remove filter
        dataTable.filter("Name", "x");
        dataTable.removeFilter("Name");

        // Act - submit
        page.btnSubmit.click();

        // Assert
        row = dataTable.getRow(2);
        assertEquals("abc", row.getCell(1).getText());
        assertEquals("2020", row.getCell(2).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void saveCell(Page page) {
        PrimeSelenium.guardAjax(page.outside).click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(page.messages));
    }

    private void editCell(Row row, int cell) {
        row.getCell(cell).getWebElement().click();
    }

    private void updateCell(Row row, int cell, String value) {
        editCell(row, cell);
        row.getCell(cell).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(cell).getWebElement().findElement(By.tagName("input")).sendKeys(value);
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("editable"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:outside")
        WebElement outside;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:btnSubmit")
        CommandButton btnSubmit;

        @FindBy(id = "form:btnAddRow")
        CommandButton btnAddRow;

        @Override
        public String getLocation() {
            return "datatable/dataTable007Cell.xhtml";
        }
    }
}
