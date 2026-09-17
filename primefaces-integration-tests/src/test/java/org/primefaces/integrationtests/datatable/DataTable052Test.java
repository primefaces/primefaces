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
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("DataTable-columnreorder")
@Tag("DataTable-scrolling")
@Tag("DataTable-lazy")
class DataTable052Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #14991 column order must be retained by live scroll after a column was dragged")
    void columnOrderRetainedAfterLiveScroll(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertEquals(List.of("1", "Language 1", "1991"), rowValues(dataTable, 0));

        // Act - drag the "ID" column onto the "Name" column, then live scroll the next chunk in
        dragColumnOnto(page, 0, 1);
        assertEquals(List.of("Name", "ID", "First Appeared"), headerTexts(page));
        assertEquals(List.of("Language 1", "1", "1991"), rowValues(dataTable, 0));

        PrimeSelenium.executeScript(true, "PF('dtWidget').loadLiveRows()");

        // Assert - the rows loaded by the scroll must follow the dragged column order, like the already visible ones
        assertEquals(List.of("Language 1", "1", "1991"), rowValues(dataTable, 0));
        assertEquals(List.of("Language 6", "6", "1996"), rowValues(dataTable, 5));
        assertEquals(List.of("Language 10", "10", "1990"), rowValues(dataTable, 9));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: live scroll without any column reorder keeps the declared column order")
    void declaredColumnOrderRetainedAfterLiveScroll(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertEquals(List.of("1", "Language 1", "1991"), rowValues(dataTable, 0));

        // Act
        PrimeSelenium.executeScript(true, "PF('dtWidget').loadLiveRows()");

        // Assert
        assertEquals(List.of("6", "Language 6", "1996"), rowValues(dataTable, 5));
        assertNoJavascriptErrors();
    }

    private List<String> headerTexts(Page page) {
        return page.dataTable.findElements(By.cssSelector(".ui-datatable-scrollable-header th")).stream().map(WebElement::getText).toList();
    }

    private List<String> rowValues(DataTable dataTable, int rowIndex) {
        return dataTable.getRow(rowIndex).getWebElement().findElements(By.tagName("td"))
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    /**
     * Drags the header of the column at {@code fromIndex} onto the header of the column at {@code toIndex}. jQuery UI draggable is driven by mouse
     * events, so the drag has to be performed step by step: press, exceed the drag threshold, move over the target header and release there.
     *
     * @param page the page under test
     * @param fromIndex index of the column to drag
     * @param toIndex index of the column to drop it on
     */
    private void dragColumnOnto(Page page, int fromIndex, int toIndex) {
        List<WebElement> headers = page.dataTable.findElements(By.cssSelector(".ui-datatable-scrollable-header th"));
        WebElement source = headers.get(fromIndex);
        WebElement target = headers.get(toIndex);

        // grab the column title, not the header itself: the draggable cancels on ':input', which would be hit when the
        // pointer lands in the center of a header carrying a filter input
        new Actions(page.getWebDriver())
                .moveToElement(source.findElement(By.cssSelector(".ui-column-title")))
                .clickAndHold()
                .moveByOffset(10, 0)
                .moveToElement(target)
                .moveByOffset((target.getSize().getWidth() / 2) - 1, 0)
                .release()
                .perform();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable052.xhtml";
        }
    }
}
