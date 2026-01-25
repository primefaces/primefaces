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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataTable038Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: #9171 not compatible with MyFaces tree manipulation")
    void dynamicColumnsMyFacesIssue(Page page) throws InterruptedException {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Page1 - check first row (25 rows)
        assertEquals("s2", dataTable.getCell(1, 0).getText());
        assertEquals("d2", dataTable.getCell(1, 1).getText());

        dataTable.selectPage(2);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Page2 - check first row (25 rows)
        assertEquals("s27", dataTable.getCell(1, 0).getText());
        assertEquals("d27", dataTable.getCell(1, 1).getText());

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable038.xhtml";
        }
    }
}
