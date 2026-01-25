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

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable017Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: RowGroup - rowspan")
    void rowGroup(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        //page.button.click();

        // Assert
        assertNotNull(dataTable.getHeaderWebElement());

        List<WebElement> rowElts = dataTable.getRowsWebElement();
        assertNotNull(rowElts);
        assertEquals(languages.size(), rowElts.size());

        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(languages.size(), rows.size());

        assertEquals("2", dataTable.getCell(0, 0).getWebElement().getAttribute("rowspan"));
        assertEquals("COMPILED", dataTable.getCell(0, 0).getText());
        assertEquals("3", dataTable.getCell(2, 0).getWebElement().getAttribute("rowspan"));
        assertEquals("INTERPRETED", dataTable.getCell(2, 0).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("groupColumnIndexes"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable017.xhtml";
        }
    }
}
