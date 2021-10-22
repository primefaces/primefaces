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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.OutputLabel;

public class DataTable028Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: filter/sort - wrong manipulation of list elements - https://github.com/primefaces/primefaces/issues/7999")
    public void testFilterByName(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.commandButtonSave.click();

        // Assert
        Assertions.assertEquals("Result:\n" +
                "509, EUR, BB, BB2, A\n" +
                "512, EUR, BB, BB2, B\n" +
                "515, EUR, BB, BB2, C\n" +
                "516, USA, AA, AA, D\n" +
                "517, USA, AA, AA, E", page.eltDebugActual.getText());

        // Act 1 - filter on name with value BB2
        dataTable.filter("Name", "bb2");

        // Act 2 - change all BB2 row values to BB3, press Save
        for (int row=0; row<=2; row++) {
            WebElement eltName = dataTable.getRow(row).getCell(2).getWebElement().findElement(By.tagName("input"));
            eltName.clear();
            eltName.sendKeys("BB3");
        }
        page.commandButtonSave.click();

        // Assert
        assertAfterBb3Update(page);

        // Act 3 - remove filter BB2, press Save
        dataTable.filter("Name", "");
        page.commandButtonSave.click();

        // Assert
        assertAfterBb3Update(page);

        // Act 4 - sort on code, press Save
        dataTable.sort("Code");
        page.commandButtonSave.click();

        // Assert
        assertAfterBb3Update(page);

        assertNoJavascriptErrors();
    }

    private void assertAfterBb3Update(Page page) {
        Assertions.assertEquals("Result:\n" +
                "509, EUR, BB, BB3, A\n" +
                "512, EUR, BB, BB3, B\n" +
                "515, EUR, BB, BB3, C\n" +
                "516, USA, AA, AA, D\n" +
                "517, USA, AA, AA, E", page.eltDebugActual.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:referenceTable")
        DataTable dataTable;

        @FindBy(id = "form:cmdSave")
        CommandButton commandButtonSave;

        @FindBy(id = "debugInitial")
        WebElement eltDebugInitial;

        @FindBy(id = "debugActual")
        WebElement eltDebugActual;

        @Override
        public String getLocation() {
            return "datatable/dataTable028.xhtml";
        }
    }
}
