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
import org.primefaces.selenium.component.DataTable;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("DataTable-rendering")
class DataTable059Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #15428 p:columns columnIndexVar must be exposed in header and cells")
    void columnIndexVar(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - nothing, the index is rendered on initial load

        // Assert
        assertEquals(List.of("0:ID", "1:Name", "2:First Appeared"), dataTable.getHeader().getCellTexts());
        assertEquals(List.of("0:1", "1:Java", "2:1995"), dataTable.getRow(0).getCellTexts());
        assertEquals(List.of("0:2", "1:C#", "2:2000"), dataTable.getRow(1).getCellTexts());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #15428 p:columns columnIndexVar must be exposed after paging")
    void columnIndexVarAfterPaging(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.selectPage(2);

        // Assert
        assertEquals(List.of("0:ID", "1:Name", "2:First Appeared"), dataTable.getHeader().getCellTexts());
        assertEquals("0:4", dataTable.getRow(0).getCellTexts().get(0));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable059.xhtml";
        }
    }
}
