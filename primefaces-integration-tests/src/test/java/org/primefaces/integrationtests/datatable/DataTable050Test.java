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
import org.primefaces.selenium.component.model.datatable.Row;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("DataTable-rowreorder")
@Tag("DataTable-rowexpansion")
class DataTable050Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: rowReorder of a collapsed row reports the dropped position")
    void reorderCollapsedRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - drag "TypeScript" (index 3) one position up
        dragRowOnto(page, 3, 2);

        // Assert
        assertEquals("3", page.fromIndex.getText());
        assertEquals("2", page.toIndex.getText());
        assertEquals("Java,C#,TypeScript,JavaScript,Python", page.order.getText());
        assertEquals("TypeScript", dataTable.getRow(2).getCell(1).getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: rowReorder of an expanded row reports the dropped position, not one row too far")
    void reorderExpandedRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.getRow(3).expand();

        // Act - drag the expanded "TypeScript" (index 3) one position up
        dragRowOnto(page, 3, 2);

        // Assert - the expansion row of the dragged row must not shift the reported index
        assertEquals("3", page.fromIndex.getText());
        assertEquals("2", page.toIndex.getText());
        assertEquals("Java,C#,TypeScript,JavaScript,Python", page.order.getText());
        assertEquals("TypeScript", dataTable.getRow(2).getCell(1).getText());
        assertNoJavascriptErrors();
    }

    /**
     * Drags the row at {@code fromIndex} onto the row at {@code toIndex}. jQuery UI sortable is driven by mouse events, so the drag has to be performed
     * step by step: press, exceed the drag threshold, move over the target row and release there.
     *
     * @param page the page under test
     * @param fromIndex index of the row to drag
     * @param toIndex index of the row to drop it on
     */
    private void dragRowOnto(Page page, int fromIndex, int toIndex) {
        Row source = page.dataTable.getRow(fromIndex);
        Row target = page.dataTable.getRow(toIndex);
        WebElement sourceHandle = source.getCell(1).getWebElement();
        WebElement targetElement = target.getWebElement();

        Actions actions = new Actions(page.getWebDriver());
        actions.moveToElement(sourceHandle)
                .clickAndHold()
                .moveByOffset(0, -10)
                .moveToElement(targetElement)
                .moveByOffset(0, -(targetElement.getSize().getHeight() / 2) + 1)
                .release();

        PrimeSelenium.guardAjax(actions.build()).perform();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:order")
        WebElement order;

        @FindBy(id = "form:fromIndex")
        WebElement fromIndex;

        @FindBy(id = "form:toIndex")
        WebElement toIndex;

        @Override
        public String getLocation() {
            return "datatable/dataTable050.xhtml";
        }
    }
}
