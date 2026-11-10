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
package org.primefaces.integrationtests.datalist;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataList;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.data.Paginator;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataList001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataList: renders the first page as ordered list with a paginator")
    void render(Page page) {
        // Arrange
        DataList dataList = page.dataList;

        // Assert
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript"), texts(dataList));
        assertEquals("li", dataList.getRowWebElement(0).getTagName());
        assertEquals("ol", dataList.getRowWebElement(0).findElement(By.xpath("..")).getTagName());

        Paginator paginator = dataList.getPaginator();
        assertEquals(2, paginator.getPages().size());
        assertEquals(1, paginator.getActivePage().getNumber());
        assertTrue(page.messages.isEmpty());
        assertConfiguration(dataList.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataList: select a page with page AJAX event")
    void selectPage(Page page) {
        // Arrange
        DataList dataList = page.dataList;

        // Act
        dataList.selectPage(2);

        // Assert
        assertEquals(List.of("4 - TypeScript", "5 - Python"), texts(dataList));
        assertEquals(2, dataList.getPaginator().getActivePage().getNumber());
        assertEquals("Paged", page.messages.getMessage(0).getSummary());
        assertEquals("Page:1 Rows:3", page.messages.getMessage(0).getDetail());

        // Act
        dataList.selectPage(1);

        // Assert
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript"), texts(dataList));
        assertEquals("Page:0 Rows:3", page.messages.getMessage(0).getDetail());
        assertConfiguration(dataList.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataList: current page must not be lost after AJAX update")
    void pageAfterUpdate(Page page) {
        // Arrange
        DataList dataList = page.dataList;
        dataList.selectPage(2);

        // Act
        page.update.click();

        // Assert
        assertEquals(List.of("4 - TypeScript", "5 - Python"), texts(dataList));
        assertEquals(2, dataList.getPaginator().getActivePage().getNumber());

        // Act - the refreshed widget still pages
        dataList.selectPage(1);

        // Assert
        assertEquals(List.of("1 - Java", "2 - C#", "3 - JavaScript"), texts(dataList));
        assertConfiguration(dataList.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataList: definition list with description facet")
    void definition(Page page) {
        // Arrange
        DataList dataList = page.definitionList;

        // Assert
        assertEquals(List.of("Java", "C#", "JavaScript", "TypeScript", "Python"), texts(dataList));
        WebElement first = dataList.getRowWebElement(0);
        assertEquals("dt", first.getTagName());
        assertEquals("First appeared: 1995", first.findElement(By.xpath("following-sibling::dd[1]")).getText());
        assertFalse(dataList.getWidgetConfiguration().has("paginator"));
        assertNoJavascriptErrors();
    }

    private List<String> texts(DataList dataList) {
        return dataList.getRowsWebElement().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:dataList")
        DataList dataList;

        @FindBy(id = "form:definitionList")
        DataList definitionList;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "datalist/dataList001.xhtml";
        }
    }
}
