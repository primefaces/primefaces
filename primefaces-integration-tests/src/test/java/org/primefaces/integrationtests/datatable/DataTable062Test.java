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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-rowgroup")
class DataTable062Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: RowGroup - headerRow initially not rendered groups once it gets rendered; see GitHub #10481")
    void headerRowRenderedLater(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        assertTrue(getHeaderRows(dataTable).isEmpty());
        assertRows(dataTable, languages);

        // Act
        page.toggle.click();

        // Assert
        assertEquals(List.of("1990", "1995", "2000", "2012"), getHeaderRows(dataTable));
        assertRows(getDataRows(dataTable), sortByNoLimit(Comparator.comparing(ProgrammingLanguage::getFirstAppeared)));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: RowGroup - headerRow no longer rendered does not group the sorting anymore; see GitHub #10481")
    void headerRowNotRenderedAnymore(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        page.toggle.click();
        assertEquals(4, getHeaderRows(dataTable).size());

        // Act
        page.toggle.click();
        dataTable.sort("Name");

        // Assert
        assertTrue(getHeaderRows(dataTable).isEmpty());
        assertRows(dataTable, sortByNoLimit(Comparator.comparing(ProgrammingLanguage::getName)));
        assertNoJavascriptErrors();
    }

    private List<String> getHeaderRows(DataTable dataTable) {
        return dataTable.getRows().stream()
                .filter(this::isHeaderRow)
                .map(row -> row.getCell(0).getText())
                .collect(Collectors.toList());
    }

    private List<Row> getDataRows(DataTable dataTable) {
        return dataTable.getRows().stream()
                .filter(row -> !isHeaderRow(row))
                .collect(Collectors.toList());
    }

    private boolean isHeaderRow(Row row) {
        return PrimeSelenium.hasCssClass(row.getWebElement(), "p-rowgroup-header");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:toggle")
        CommandButton toggle;

        @Override
        public String getLocation() {
            return "datatable/dataTable062.xhtml";
        }
    }
}
