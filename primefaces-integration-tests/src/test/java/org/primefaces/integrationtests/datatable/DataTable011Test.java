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
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;

public class DataTable011Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: sort and delete (button with fixed id) - https://stackoverflow.com/questions/24754118/after-sorting-it-deletes-the-wrong-line-of-primefaces-datatable")
    public void testSortAndDelete(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        dataTable.sort("Name");
        page.buttonDeleteId1.click();

        // Assert
        page.dataTable.getRows()
                    .forEach(row -> Assertions.assertNotEquals("1", row.getCell(0).getText()));
        assertConfiguration(page.dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: sort and delete (delete-button get´s object as parameter) - https://stackoverflow.com/questions/24754118/after-sorting-it-deletes-the-wrong-line-of-primefaces-datatable")
    public void testSortAndDeleteV2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        dataTable.sort("Name");
        PrimeSelenium.guardAjax(dataTable.getCell(1, 3).getWebElement().findElement(By.tagName("button"))).click();

        // Assert
        page.dataTable.getRows()
                    .forEach(row -> Assertions.assertNotEquals("1", row.getCell(0).getText()));
        assertConfiguration(page.dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonDeleteId1")
        CommandButton buttonDeleteId1;

        @Override
        public String getLocation() {
            return "datatable/dataTable011.xhtml";
        }
    }
}
