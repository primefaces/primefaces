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

@Tag("DataTable-rowgroup")
@Tag("DataTable-sort")
class DataTable065Test extends AbstractDataTableTest {

    private static final Comparator<ProgrammingLanguage> BY_YEAR = Comparator.comparing(ProgrammingLanguage::getFirstAppeared);
    private static final Comparator<ProgrammingLanguage> BY_NAME = Comparator.comparing(ProgrammingLanguage::getName);

    @Test
    @Order(1)
    @DisplayName("DataTable: RowGroup - multiple sort by a column next to a headerRow survives a full update")
    void multiSortWithHeaderRow(Page page) {
        // Act
        page.dataTable.sort("Name");
        PrimeSelenium.guardAjax(page.refresh).click();

        // Assert - the client gets no sort order entry for the headerRow, which has no column header
        assertEquals(List.of("form:datatable:name"), getSortMetaOrder());
        assertEquals(List.of("1990", "1995", "2000", "2012"), getHeaderRows(page.dataTable));
        assertRows(getDataRows(page.dataTable), sortByNoLimit(BY_YEAR.thenComparing(BY_NAME)));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: RowGroup - multiple sort by the column with the field of the headerRow survives a full update")
    void multiSortByHeaderRowFieldColumn(Page page) {
        // Act
        page.dataTable.sort("First appeared");
        PrimeSelenium.guardAjax(page.refresh).click();

        // Assert - the column is not dropped for sharing its field with the headerRow
        assertEquals(List.of("form:datatable:firstAppeared"), getSortMetaOrder());
        assertEquals(List.of("1990", "1995", "2000", "2012"), getHeaderRows(page.dataTable));
        assertNoJavascriptErrors();
    }

    private List<String> getSortMetaOrder() {
        return PrimeSelenium.executeScript("return PrimeFaces.getWidgetById('form:datatable').cfg.sortMetaOrder;");
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

        @FindBy(id = "form:refresh")
        CommandButton refresh;

        @Override
        public String getLocation() {
            return "datatable/dataTable065.xhtml";
        }
    }
}
