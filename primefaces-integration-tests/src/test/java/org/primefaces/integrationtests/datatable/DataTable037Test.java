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
import org.primefaces.selenium.component.DataTable;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataTable037Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: lazy paging with summary row grouping")
    void selectionMultipleWithPaging(Page page) throws InterruptedException {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Page1 - 10 Rows
        assertTrue(dataTable.getText().contains("Amy Elsner"));
        assertFalse(dataTable.getText().contains("Anna Fali"));
        assertTrue(dataTable.getText().contains("Total Customers: 10"));

        dataTable.selectPage(2);

        // Page2 - 10 Rows
        assertFalse(dataTable.getText().contains("Amy Elsner"));
        assertTrue(dataTable.getText().contains("Anna Fali"));
        assertTrue(dataTable.getText().contains("Total Customers: 10"));

        dataTable.selectRowsPerPage(15);

        // Page1 - 15 Rows
        assertTrue(dataTable.getText().contains("Amy Elsner"));
        assertTrue(dataTable.getText().contains("Anna Fali"));
        assertTrue(dataTable.getText().contains("Total Customers: 10"));

        dataTable.selectPage(2);

        // Page2 - 15 Rows
        assertTrue(dataTable.getText().contains("Anna Fali"));
        assertTrue(dataTable.getText().contains("Asiya Javayant"));
        assertSame(2, StringUtils.countMatches(dataTable.getText(), "Total Customers: 10"));
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #13453 - Paginator is not updating do to geRowCount()")
    void paginatorUpdating(Page page) throws InterruptedException {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Page1 - 10 Rows
        assertTrue(dataTable.getText().contains("Amy Elsner"));
        assertFalse(dataTable.getText().contains("Anna Fali"));
        assertTrue(dataTable.getText().contains("Total Customers: 10"));
        assertTrue(dataTable.getText().contains("Filtered Pages: 10 page(s)"));

        // Act: Filter to limit rows
        dataTable.filter("Name", "David");

        // Assert: check paginator is updated
        assertTrue(dataTable.getText().contains("Filtered Pages: 1 page(s)"));
    }
    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable037.xhtml";
        }
    }
}
