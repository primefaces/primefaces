/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

public class DataTable029Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: Sort is lost after data refresh - https://github.com/primefaces/primefaces/issues/7951")
    public void testSortLostAfterRefresh(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.sort("Number");

        // Assert
        assertSortOrder(dataTable);

        // Act
        page.cmdRefresh.click();

        // Assert
        assertSortOrder(dataTable);
        assertNoJavascriptErrors();
    }

    private void assertSortOrder(DataTable dt) {
        int lastNumber = -1;

        for (Row row: dt.getRows()) {
            int actNumber = Integer.parseInt(row.getCell(1).getText());
            Assertions.assertTrue(actNumber >= lastNumber);
            lastNumber = actNumber;
        }
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:cmdRefresh")
        CommandButton cmdRefresh;

        @Override
        public String getLocation() {
            return "datatable/dataTable029.xhtml";
        }
    }
}
