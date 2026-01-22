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
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.SelectBooleanButton;
import org.primefaces.selenium.component.model.datatable.Header;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

class DataTable047Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: frozen column JavaScript widget")
    public void testFrozenColumnsJavaScriptWidget(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        SelectBooleanButton button = page.frozenColumnToggleButton;
        Assertions.assertNotNull(dataTable);
        Assertions.assertNotNull(button);

        // Assert
        assertNoJavascriptErrors();

        // Act
        button.click();

        // Assert
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: frozen column scroll container width")
    public void testFrozenColumnsScrollableContainerWidth(Page page) {
        // Arrange
        WebElement scrollableTableContainer = page.scrollableTableContainer;
        SelectBooleanButton button = page.frozenColumnToggleButton;
        Assertions.assertNotNull(scrollableTableContainer);
        Assertions.assertNotNull(button);

        // Assert
        Assertions.assertEquals(500, scrollableTableContainer.findElement(By.className("ui-datatable-scrollable-header")).getSize().getWidth());
        Assertions.assertEquals(500, scrollableTableContainer.findElement(By.className("ui-datatable-scrollable-body")).getSize().getWidth());
        Assertions.assertEquals(500, scrollableTableContainer.findElement(By.className("ui-datatable-scrollable-footer")).getSize().getWidth());

        // Act
        button.click();

        // Assert
        Assertions.assertEquals(500, scrollableTableContainer.findElement(By.className("ui-datatable-scrollable-header")).getSize().getWidth());
        Assertions.assertEquals(500, scrollableTableContainer.findElement(By.className("ui-datatable-scrollable-body")).getSize().getWidth());
        Assertions.assertEquals(500, scrollableTableContainer.findElement(By.className("ui-datatable-scrollable-footer")).getSize().getWidth());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: frozen column header row")
    public void testFrozenColumnsHeaderRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        SelectBooleanButton button = page.frozenColumnToggleButton;
        Assertions.assertNotNull(dataTable);
        Assertions.assertNotNull(button);

        Header frozenHeader = dataTable.getFrozenHeader();
        Header scrollableHeader = dataTable.getScrollableHeader();
        Assertions.assertNotNull(frozenHeader);
        Assertions.assertNotNull(scrollableHeader);

        // Assert
        Assertions.assertEquals(1, frozenHeader.getCells().size());
        Assertions.assertEquals(2, scrollableHeader.getCells().size());

        Assertions.assertEquals("ID", frozenHeader.getCells().get(0).getColumnTitle().getText());
        Assertions.assertEquals("Name", scrollableHeader.getCells().get(0).getColumnTitle().getText());
        Assertions.assertEquals("First appeared", scrollableHeader.getCells().get(1).getColumnTitle().getText());

        // Act
        button.click();

        // Arrange
        frozenHeader = dataTable.getFrozenHeader();
        scrollableHeader = dataTable.getScrollableHeader();
        Assertions.assertNotNull(frozenHeader);
        Assertions.assertNotNull(scrollableHeader);

        // Assert
        Assertions.assertEquals(1, frozenHeader.getCells().size());
        Assertions.assertEquals(2, scrollableHeader.getCells().size());

        Assertions.assertEquals("ID", scrollableHeader.getCell(0).getColumnTitle().getText());
        Assertions.assertEquals("Name", scrollableHeader.getCell(1).getColumnTitle().getText());
        Assertions.assertEquals("First appeared", frozenHeader.getCell(0).getColumnTitle().getText());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: frozen column frozen body")
    public void testFrozenColumnsFrozenBody(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        SelectBooleanButton button = page.frozenColumnToggleButton;
        Assertions.assertNotNull(dataTable);
        Assertions.assertNotNull(button);

        // Assert
        Assertions.assertEquals("1", dataTable.getFrozenBody().getRow(0).getCell(0).getText());
        Assertions.assertEquals("2", dataTable.getFrozenBody().getRow(1).getCell(0).getText());
        Assertions.assertEquals("3", dataTable.getFrozenBody().getRow(2).getCell(0).getText());
        Assertions.assertEquals("4", dataTable.getFrozenBody().getRow(3).getCell(0).getText());
        Assertions.assertEquals("5", dataTable.getFrozenBody().getRow(4).getCell(0).getText());

        // Act
        button.click();

        // Assert
        Assertions.assertEquals("1995", dataTable.getFrozenBody().getRow(0).getCell(0).getText());
        Assertions.assertEquals("2000", dataTable.getFrozenBody().getRow(1).getCell(0).getText());
        Assertions.assertEquals("1995", dataTable.getFrozenBody().getRow(2).getCell(0).getText());
        Assertions.assertEquals("2012", dataTable.getFrozenBody().getRow(3).getCell(0).getText());
        Assertions.assertEquals("1990", dataTable.getFrozenBody().getRow(4).getCell(0).getText());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: frozen column scrollable body")
    public void testFrozenColumnsScrollableBody(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        SelectBooleanButton button = page.frozenColumnToggleButton;
        Assertions.assertNotNull(dataTable);
        Assertions.assertNotNull(button);

        // Assert
        Assertions.assertEquals("Java", dataTable.getScrollableBody().getRow(0).getCell(0).getText());
        Assertions.assertEquals("1995", dataTable.getScrollableBody().getRow(0).getCell(1).getText());
        Assertions.assertEquals("C#", dataTable.getScrollableBody().getRow(1).getCell(0).getText());
        Assertions.assertEquals("2000", dataTable.getScrollableBody().getRow(1).getCell(1).getText());
        Assertions.assertEquals("JavaScript", dataTable.getScrollableBody().getRow(2).getCell(0).getText());
        Assertions.assertEquals("1995", dataTable.getScrollableBody().getRow(2).getCell(1).getText());
        Assertions.assertEquals("TypeScript", dataTable.getScrollableBody().getRow(3).getCell(0).getText());
        Assertions.assertEquals("2012", dataTable.getScrollableBody().getRow(3).getCell(1).getText());
        Assertions.assertEquals("Python", dataTable.getScrollableBody().getRow(4).getCell(0).getText());
        Assertions.assertEquals("1990", dataTable.getScrollableBody().getRow(4).getCell(1).getText());

        // Act
        button.click();

        // Assert
        Assertions.assertEquals("1", dataTable.getScrollableBody().getRow(0).getCell(0).getText());
        Assertions.assertEquals("Java", dataTable.getScrollableBody().getRow(0).getCell(1).getText());
        Assertions.assertEquals("2", dataTable.getScrollableBody().getRow(1).getCell(0).getText());
        Assertions.assertEquals("C#", dataTable.getScrollableBody().getRow(1).getCell(1).getText());
        Assertions.assertEquals("3", dataTable.getScrollableBody().getRow(2).getCell(0).getText());
        Assertions.assertEquals("JavaScript", dataTable.getScrollableBody().getRow(2).getCell(1).getText());
        Assertions.assertEquals("4", dataTable.getScrollableBody().getRow(3).getCell(0).getText());
        Assertions.assertEquals("TypeScript", dataTable.getScrollableBody().getRow(3).getCell(1).getText());
        Assertions.assertEquals("5", dataTable.getScrollableBody().getRow(4).getCell(0).getText());
        Assertions.assertEquals("Python", dataTable.getScrollableBody().getRow(4).getCell(1).getText());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(className = "ui-datatable-frozenlayout-left")
        WebElement frozenTableContainer;

        @FindBy(className = "ui-datatable-frozenlayout-right")
        WebElement scrollableTableContainer;

        @FindBy(id = "form:frozenColumnToggleButton")
        SelectBooleanButton frozenColumnToggleButton;

        @Override
        public String getLocation() {
            return "datatable/dataTable047.xhtml";
        }
    }
}
