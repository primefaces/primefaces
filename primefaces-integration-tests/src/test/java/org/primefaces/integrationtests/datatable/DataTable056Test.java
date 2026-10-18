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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("DataTable-filter")
@Tag("DataTable-sort")
class DataTable056Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #15268 row action after clearing a filter must use the sorted row, even if the value was replaced")
    void rowActionAfterRefreshAndClearFilter(Page page) {
        // Arrange - sort by name and filter
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");
        dataTable.filter("Name", "a");
        assertEquals("Ada", dataTable.getCell(0, 1).getText());

        // Act - reload the (unsorted) data, re-apply the filter, then clear it
        page.buttonRefresh.click();
        PrimeSelenium.executeScript(true, "PF('datatable').filter()");
        dataTable.removeFilter("Name");

        // Assert - the first rendered row is "Ada" and the action must receive exactly this row
        assertEquals(5, dataTable.getRows().size());
        assertEquals("Ada", dataTable.getCell(0, 1).getText());

        PrimeSelenium.guardAjax(dataTable.getCell(0, 2).getWebElement().findElement(By.tagName("button"))).click();
        assertEquals("Ada", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:buttonRefresh")
        CommandButton buttonRefresh;

        @Override
        public String getLocation() {
            return "datatable/dataTable056.xhtml";
        }
    }
}
