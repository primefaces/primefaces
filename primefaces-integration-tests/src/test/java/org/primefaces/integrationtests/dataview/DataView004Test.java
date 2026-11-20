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
package org.primefaces.integrationtests.dataview;

import org.primefaces.integrationtests.datatable.ProgrammingLanguage;
import org.primefaces.integrationtests.datatable.ProgrammingLanguageService;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataView;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataView004Test extends AbstractPrimePageTest {

    private final List<ProgrammingLanguage> langs = new ProgrammingLanguageService().getLangs();

    @Test
    @Order(1)
    @DisplayName("DataView: varStatus and itemStyleClass in list layout, also after paging")
    void listLayout(Page page) {
        // Assert
        List<WebElement> rows = page.list.getRowsWebElement();
        assertEquals(2, rows.size());
        assertEquals("0: " + langs.get(0).getName(), rows.get(0).getText());
        assertEquals("1: " + langs.get(1).getName(), rows.get(1).getText());
        assertTrue(PrimeSelenium.hasCssClass(rows.get(0), "p-dataview-row"));
        assertTrue(PrimeSelenium.hasCssClass(rows.get(0), "lang-" + langs.get(0).getId()));
        assertTrue(PrimeSelenium.hasCssClass(rows.get(0), "first"));
        assertFalse(PrimeSelenium.hasCssClass(rows.get(1), "first"));

        // Act
        page.list.selectPage(3);

        // Assert
        rows = page.list.getRowsWebElement();
        assertEquals(1, rows.size());
        assertEquals("4: " + langs.get(4).getName(), rows.get(0).getText());
        assertTrue(PrimeSelenium.hasCssClass(rows.get(0), "lang-" + langs.get(4).getId()));
        assertTrue(PrimeSelenium.hasCssClass(rows.get(0), "last"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataView: varStatus and itemStyleClass in grid layout")
    void gridLayout(Page page) {
        // Assert
        List<WebElement> columns = page.grid.getRowsWebElement();
        assertEquals(langs.size(), columns.size());
        for (int i = 0; i < columns.size(); i++) {
            assertEquals(i + ": " + langs.get(i).getName(), columns.get(i).getText());
            assertTrue(PrimeSelenium.hasCssClass(columns.get(i), "p-dataview-column"));
            assertTrue(PrimeSelenium.hasCssClass(columns.get(i), i % 2 == 0 ? "even" : "odd"));
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataView: empty message")
    void emptyMessage(Page page) {
        // Assert
        WebElement message = page.empty.findElement(By.className("p-dataview-empty-message"));
        assertEquals("Nothing here", message.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:list")
        DataView list;

        @FindBy(id = "form:grid")
        DataView grid;

        @FindBy(id = "form:empty")
        DataView empty;

        @Override
        public String getLocation() {
            return "dataview/dataView004.xhtml";
        }
    }
}
