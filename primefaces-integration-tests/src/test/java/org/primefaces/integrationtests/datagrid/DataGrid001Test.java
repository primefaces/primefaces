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
package org.primefaces.integrationtests.datagrid;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataGrid;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.data.Paginator;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataGrid001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataGrid: renders the first page in columns with a paginator")
    void render(Page page) {
        // Arrange
        DataGrid dataGrid = page.dataGrid;

        // Assert
        assertFalse(dataGrid.isEmpty());
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript"), dataGrid.getRowTexts());
        assertTrue(PrimeSelenium.hasCssClass(dataGrid.getRowWebElement(0), "p-datagrid-column"));
        assertTrue(dataGrid.isPageAjaxified());

        Paginator paginator = dataGrid.getPaginator();
        assertEquals(2, paginator.getPages().size());
        assertEquals(1, paginator.getActivePage().getNumber());
        assertEquals("3", paginator.getRowsPerPageDropdown().getFirstSelectedOption().getText());
        assertTrue(page.messages.isEmpty());
        assertConfiguration(dataGrid.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataGrid: select a page with page AJAX event")
    void selectPage(Page page) {
        // Arrange
        DataGrid dataGrid = page.dataGrid;

        // Act
        dataGrid.selectPage(2);

        // Assert
        assertEquals(List.of("4 - TypeScript", "5 - Python"), dataGrid.getRowTexts());
        assertEquals(2, dataGrid.getPaginator().getActivePage().getNumber());
        assertEquals("Paged", page.messages.getMessage(0).getSummary());
        assertEquals("Page:1 Rows:3", page.messages.getMessage(0).getDetail());

        // Act
        dataGrid.selectPage(1);

        // Assert
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript"), dataGrid.getRowTexts());
        assertEquals(1, dataGrid.getPaginator().getActivePage().getNumber());
        assertEquals("Page:0 Rows:3", page.messages.getMessage(0).getDetail());
        assertConfiguration(dataGrid.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataGrid: change the rows per page")
    void rowsPerPage(Page page) {
        // Arrange
        DataGrid dataGrid = page.dataGrid;

        // Act
        dataGrid.selectRowsPerPage(6);

        // Assert
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript", "4 - TypeScript", "5 - Python"), dataGrid.getRowTexts());
        Paginator paginator = dataGrid.getPaginator();
        assertEquals(1, paginator.getPages().size());
        assertEquals("6", paginator.getRowsPerPageDropdown().getFirstSelectedOption().getText());
        assertEquals("Page:0 Rows:6", page.messages.getMessage(0).getDetail());
        assertConfiguration(dataGrid.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataGrid: current page must not be lost after AJAX update")
    void pageAfterUpdate(Page page) {
        // Arrange
        DataGrid dataGrid = page.dataGrid;
        dataGrid.selectPage(2);

        // Act
        page.update.click();

        // Assert
        assertEquals(List.of("4 - TypeScript", "5 - Python"), dataGrid.getRowTexts());
        assertEquals(2, dataGrid.getPaginator().getActivePage().getNumber());

        // Act - the refreshed widget still pages
        dataGrid.selectPage(1);

        // Assert
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript"), dataGrid.getRowTexts());
        assertConfiguration(dataGrid.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:dataGrid")
        DataGrid dataGrid;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "datagrid/dataGrid001.xhtml";
        }
    }
}
