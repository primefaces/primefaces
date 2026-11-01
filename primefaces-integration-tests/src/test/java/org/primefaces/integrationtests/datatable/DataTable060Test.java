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
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-rendering")
@Tag("DataTable-selection")
@Tag("DataTable-paginator")
class DataTable060Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #2931 rowHeader column renders th scope=row")
    void rowHeader(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - nothing, rendered on initial load

        // Assert
        Row row = dataTable.getRow(0);
        assertEquals(4, row.getCells().size());

        WebElement header = row.getCell(1).getWebElement();
        assertEquals("th", header.getTagName());
        assertEquals("row", header.getDomAttribute("scope"));
        assertEquals("rowheader", header.getDomAttribute("role"));
        assertEquals("Java", header.getText());

        assertEquals("td", row.getCell(0).getWebElement().getTagName());
        assertEquals("gridcell", row.getCell(0).getWebElement().getDomAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #2931 hidden column stays hidden after paging with a rowHeader column")
    void hiddenColumnAfterPaging(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.selectPage(2);

        // Assert
        Row row = dataTable.getRow(0);
        assertEquals("TypeScript", row.getCell(1).getWebElement().getText());
        assertTrue(PrimeSelenium.hasCssClass(row.getCell(2).getWebElement(), "p-hidden"));
        assertFalse(PrimeSelenium.hasCssClass(row.getCell(1).getWebElement(), "p-hidden"));
        assertFalse(PrimeSelenium.hasCssClass(row.getCell(3).getWebElement(), "p-hidden"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #2931 clicking the rowHeader cell selects the row")
    void selectByRowHeader(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.getRow(1).getCell(1).getWebElement().click();

        // Assert
        Row row = dataTable.getRow(1);
        assertTrue(PrimeSelenium.hasCssClass(row.getWebElement(), "p-state-highlight"));
        assertEquals("true", row.getWebElement().getDomAttribute("aria-selected"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable060.xhtml";
        }
    }
}
