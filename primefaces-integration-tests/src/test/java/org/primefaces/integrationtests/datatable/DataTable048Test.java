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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.SelectCheckboxMenu;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable048Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: In filter BigDecimal")
    public void testInEqualsFilterBigDecimal(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        SelectCheckboxMenu filter1 = getFilter1();
        SelectOneMenu filter2 = getFilter2();
        assertNotNull(dataTable);
        assertNotNull(filter1);
        assertNotNull(filter2);

        filter1.show();
        List<WebElement> filterTypeCheckboxes = filter1.getPanel().findElements(By.cssSelector(".ui-chkbox-box"));
        // In filter all
//        filter1.checkAll(); // alternative to the following line probably without Ajax guard
        PrimeSelenium.guardAjax(filterTypeCheckboxes.get(0)).click();
        filter1.hide();

        // Equals filter 2
        filter2.show();
        filter2.select("2");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(1, rows.size()); //one page
        assertEquals("2", rows.get(0).getCell(0).getText());

        // Arrange
        filter2.show();
        // Equals filter null (all)
        filter2.select("Empty");

        filter1.togglePanel();
        filterTypeCheckboxes = filter1.getPanel().findElements(By.cssSelector(".ui-chkbox-box"));
        PrimeSelenium.guardAjax(filterTypeCheckboxes.get(0)).click();
        // In filter null
        PrimeSelenium.guardAjax(filterTypeCheckboxes.get(1)).click();
        filter1.togglePanel();

        // Assert
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(3, rows.size()); //one page
        assertEquals("1", rows.get(0).getCell(0).getText());
        assertEquals("3", rows.get(1).getCell(0).getText());
        assertEquals("5", rows.get(2).getCell(0).getText());

        // Assert
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable048.xhtml";
        }

    }

    private SelectCheckboxMenu getFilter1() {
        return PrimeSelenium.createFragment(SelectCheckboxMenu.class, By.id("form:datatable:filterPopularity1"));
    }

    private SelectOneMenu getFilter2() {
        return PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:datatable:filterPopularity2"));
    }
}
