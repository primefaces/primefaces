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
import org.primefaces.selenium.component.OutputLabel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataTable027Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #7954 verify virtual scrolling works with count() method.")
    void filterByName(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        // Assert
        assertEquals("0", page.beforeCount.getText());
        assertEquals("75", page.aferCount.getText());
        assertCss(dataTable, "ui-datatable", "ui-widget", "ui-datatable-scrollable");
        assertNotNull(
                dataTable.findElement(
                    By.cssSelector(".ui-datatable-scrollable-body > .ui-datatable-virtualscroll-wrapper > .ui-datatable-virtualscroll-table")),
                "Datatable virtual scrolling CSS has changed!");
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:lblBefore")
        OutputLabel beforeCount;

        @FindBy(id = "form:lblAfter")
        OutputLabel aferCount;

        @Override
        public String getLocation() {
            return "datatable/dataTable027.xhtml";
        }
    }
}
