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

public class DataTable031 extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: filter display old value - https://github.com/primefaces/primefaces/issues/7845")
    public void testFilterSortEdit(Page page) {
        Assertions.assertSame(page.dataTable.getRows().size(), 0);

        // display "Data 1 *"
        page.toogle.click();
        Assertions.assertSame(page.dataTable.getRows().size(), 4);
        Assertions.assertEquals("Data 1 1", page.dataTable.getRows().get(0).getCell(0).getText());
        
        // filter for "Data 1 *"
        page.dataTable.filter("data", "Data 1");
        Assertions.assertSame(page.dataTable.getRows().size(), 4);
        Assertions.assertEquals("Data 1 1", page.dataTable.getRows().get(0).getCell(0).getText());

        // display "Data 2", filter is still "Data 1", so no data should be displayed
        page.toogle.click();
        Assertions.assertSame(page.dataTable.getRows().size(), 0);
        
        // reset filter
        page.dataTable.removeFilter("data");
        Assertions.assertSame(page.dataTable.getRows().size(), 4);
        Assertions.assertEquals("Data 2 1", page.dataTable.getRows().get(0).getCell(0).getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:dt")
        DataTable dataTable;

        @FindBy(id = "form:toggle")
        CommandButton toogle;

        @Override
        public String getLocation() {
            return "datatable/dataTable031.xhtml";
        }
    }
}
