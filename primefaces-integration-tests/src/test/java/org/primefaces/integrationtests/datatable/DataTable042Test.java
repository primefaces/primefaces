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
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTable042Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: Custom sortFunction legacy - https://github.com/primefaces/primefaces/issues/10545")
    void sortFunctionLegacy(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.sort("TextA");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertEquals("", rows.get(0).getCell(0).getText());
        assertEquals("A1", rows.get(1).getCell(0).getText());
        assertEquals("A3", rows.get(2).getCell(0).getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: Custom sortFunction modern - https://github.com/primefaces/primefaces/issues/10545")
    void sortFunctionModern(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.sort("TextB");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertEquals("B3", rows.get(0).getCell(1).getText());
        assertEquals("B1", rows.get(1).getCell(1).getText());
        assertEquals("", rows.get(2).getCell(1).getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable042.xhtml";
        }
    }
}
