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

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.datatable.Row;

public class DataTable007Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: Add-Row")
    public void testAddRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        List<Row> rows = dataTable.getRows();
        Assertions.assertEquals(5, rows.size()); // only 5 rows to start

        // Act
        page.btnAddRow.click();

        // Assert
        rows = dataTable.getRows();
        Assertions.assertEquals(6, rows.size()); // now has 6 after row added
        Row row = dataTable.getRow(5);
        assertDisplayed(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-pencil")));
        assertNotDisplayed(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-close")));
        Assertions.assertEquals("Smalltalk", row.getCell(1).getText());
        Assertions.assertEquals("6", row.getCell(0).getText());
        Assertions.assertEquals("New Language added", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals("Smalltalk", page.messages.getMessage(0).getDetail());
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: Edit-Row")
    public void testEditRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act - edit and cancel
        Row row = dataTable.getRow(1);
        row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-pencil")).click();
        row.getCell(1).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(1).getWebElement().findElement(By.tagName("input")).sendKeys("xyz");
        row.getCell(2).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(2).getWebElement().findElement(By.tagName("input")).sendKeys("2000");
        PrimeSelenium.guardAjax(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-close"))).click();

        // Assert
        row = dataTable.getRow(1);
        assertDisplayed(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-pencil")));
        assertNotDisplayed(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-close")));
        Assertions.assertEquals(languages.get(1).getName(), row.getCell(1).getText());
        Assertions.assertEquals(Integer.toString(languages.get(1).getFirstAppeared()), row.getCell(2).getText());
        Assertions.assertEquals("Edit Cancelled", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals(Integer.toString(languages.get(1).getId()), page.messages.getMessage(0).getDetail());

        // Act - edit and accept
        row = dataTable.getRow(2);
        row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-pencil")).click();
        row.getCell(1).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(1).getWebElement().findElement(By.tagName("input")).sendKeys("abc");
        row.getCell(2).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(2).getWebElement().findElement(By.tagName("input")).sendKeys("2020");
        PrimeSelenium.guardAjax(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-check"))).click();

        // Assert
        row = dataTable.getRow(2);
        assertDisplayed(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-pencil")));
        assertNotDisplayed(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-close")));
        Assertions.assertEquals("abc", row.getCell(1).getText());
        Assertions.assertEquals("2020", row.getCell(2).getText());
        Assertions.assertEquals("ProgrammingLanguage Edited", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals(Integer.toString(languages.get(2).getId()), page.messages.getMessage(0).getDetail());

        // Act - submit
        page.btnSubmit.click();

        // Assert
        row = dataTable.getRow(2);
        Assertions.assertEquals("abc", row.getCell(1).getText());
        Assertions.assertEquals("2020", row.getCell(2).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #1442 Filter combined with Edit-Row; https://github.com/primefaces/primefaces/issues/1442")
    public void testFilterAndEditRow_1442(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act - filter
        dataTable.filter("Name", "Java");

        // Act - edit and accept
        Row row = dataTable.getRow(1);
        row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-pencil")).click();
        row.getCell(1).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(1).getWebElement().findElement(By.tagName("input")).sendKeys("abc");
        row.getCell(2).getWebElement().findElement(By.tagName("input")).clear();
        row.getCell(2).getWebElement().findElement(By.tagName("input")).sendKeys("2020");
        PrimeSelenium.guardAjax(row.getCell(3).getWebElement().findElement(By.className("ui-row-editor-check"))).click();

        // Act - remove filter
        dataTable.filter("Name", "x");
        dataTable.removeFilter("Name");

        // Act - submit
        page.btnSubmit.click();

        // Assert
        row = dataTable.getRow(2);
        Assertions.assertEquals("abc", row.getCell(1).getText());
        Assertions.assertEquals("2020", row.getCell(2).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("editable"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:btnSubmit")
        CommandButton btnSubmit;

        @FindBy(id = "form:btnAddRow")
        CommandButton btnAddRow;

        @Override
        public String getLocation() {
            return "datatable/dataTable007.xhtml";
        }
    }
}
