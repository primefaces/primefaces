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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GitHub #15165 and #15166: a component which iterates must not leave its iteration variable in the request map, so
 * that whatever renders after it resolves the variable of its own scope instead of the last row it rendered.
 */
class DataTable052Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #15166 p:columns does not leak its var into what renders after the table")
    void dynamicColumnsVarDoesNotLeakAfterRender(Page page) {
        // Arrange
        assertTrue(page.dynamic.isDisplayed());

        // Assert
        assertEquals("afterCols=[]", page.afterCols.getText());

        // Act
        PrimeSelenium.guardAjax(page.buttonUpdate).click();

        // Assert
        assertEquals("afterCols=[]", page.afterCols.getText());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #15165 p:subTable does not leak its var into what renders after the table")
    void subTableVarDoesNotLeakAfterRender(Page page) {
        // Arrange
        assertTrue(page.grouped.isDisplayed());

        // Assert
        assertEquals("afterSub=[]", page.afterSub.getText());

        // Act
        PrimeSelenium.guardAjax(page.buttonUpdate).click();

        // Assert
        assertEquals("afterSub=[]", page.afterSub.getText());

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:dynamic")
        DataTable dynamic;

        @FindBy(id = "form:afterCols")
        WebElement afterCols;

        @FindBy(id = "form:grouped")
        DataTable grouped;

        @FindBy(id = "form:afterSub")
        WebElement afterSub;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "datatable/dataTable052.xhtml";
        }
    }
}
