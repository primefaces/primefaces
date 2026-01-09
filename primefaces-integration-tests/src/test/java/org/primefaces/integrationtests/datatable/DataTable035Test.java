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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DataTable035Test extends AbstractDataTableTest {

    @Test
    @DisplayName("DataTable: Manually assign selection doesnt show")
    void test(Page page) throws InterruptedException {
        assertDoesNotThrow(() -> {

            // trigger selection on row1
            WebElement nameRow1 = getWebDriver().findElement(By.id("form:datatable:1:name"));
            PrimeSelenium.guardAjax(nameRow1).click();

            // assert selection
            WebElement row1 = page.dataTable.findElement(By.cssSelector("tr[data-ri='1']"));
            assertCss(row1, "ui-state-highlight");

            // trigger selection on row2 via button
            WebElement buttonRow2 = getWebDriver().findElement(By.id("form:datatable:2:select"));
            PrimeSelenium.guardAjax(buttonRow2).click();

            // assert selection
            WebElement row2 = page.dataTable.findElement(By.cssSelector("tr[data-ri='2']"));
            assertCss(row2, "ui-state-highlight");
        });
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable035.xhtml";
        }
    }
}
