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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;

public class DataTable014Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: stickyHeader")
    public void testStickyHeader(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);
        int dataTableWidth = dataTable.getSize().getWidth();
        int dataTableLocationX = dataTable.getLocation().getX();

        WebElement dataTableSticky = dataTable.findElement(By.className("ui-datatable-sticky"));
        WebElement dataTableWrapper = dataTable.findElement(By.className("ui-datatable-tablewrapper"));
        Assertions.assertNotNull(dataTableSticky);
        Assertions.assertNotNull(dataTableWrapper);

        // Act
        JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
        int scrollDown = 2000;
        js.executeScript("window.scrollBy(0," + scrollDown + ")");
        TestUtils.wait(1000); //compensate weird Firefox (81) timing issue with assigning ui-sticky

        // Assert
        Assertions.assertTrue(PrimeSelenium.hasCssClass(dataTableSticky, "ui-sticky"));
        Assertions.assertEquals(dataTableWidth, dataTableSticky.getSize().getWidth());
        Assertions.assertEquals(dataTableWidth, dataTableWrapper.getSize().getWidth());
        Assertions.assertEquals(dataTableLocationX, dataTableWrapper.getLocation().getX());
        Assertions.assertEquals(dataTable.getLocation().getY(), dataTableWrapper.getLocation().getY());
        if (!PrimeSelenium.isSafari()) {
            Assertions.assertEquals(scrollDown, dataTableSticky.getLocation().getY());
        }

        // Act
        js.executeScript("window.scrollTo(0,0)");
        TestUtils.wait(1000); //compensate weird Firefox (81) timing issue with removing ui-sticky

        // Assert
        Assertions.assertFalse(PrimeSelenium.hasCssClass(dataTableSticky, "ui-sticky"));

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("stickyHeader"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable014.xhtml";
        }
    }
}
