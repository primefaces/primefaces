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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("DataTable-rowgroup")
class DataTable052Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: RowGroup - summary row grouping by its own field, without a header row; see GitHub #14295")
    void summaryRowWithoutHeaderRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Assert
        assertSummaryRows(dataTable);
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: RowGroup - summary row survives an update, without a header row; see GitHub #14295")
    void summaryRowWithoutHeaderRowAfterUpdate(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        page.button.click();

        // Assert
        assertSummaryRows(dataTable);
    }

    private void assertSummaryRows(DataTable dataTable) {
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(languages.size() + 2, rows.size()); //plus 2 summary-rows

        //check summary-rows, one after each of the 2 groups (2 COMPILED, 3 INTERPRETED)
        assertEquals("3", dataTable.getCell(2, 0).getWebElement().getAttribute("colspan"));
        assertEquals("Total programming languages:", dataTable.getCell(2, 0).getText());
        assertEquals("2", dataTable.getCell(2, 1).getText());
        assertEquals("3", dataTable.getCell(6, 0).getWebElement().getAttribute("colspan"));
        assertEquals("Total programming languages:", dataTable.getCell(6, 0).getText());
        assertEquals("3", dataTable.getCell(6, 1).getText());

        //remove summary-rows
        rows.remove(6); //second summary-row
        rows.remove(2); //first summary-row

        assertRows(rows, languages);

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datatable/dataTable052.xhtml";
        }
    }
}
