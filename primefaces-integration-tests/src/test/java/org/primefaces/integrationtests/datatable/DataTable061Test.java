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
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-rowreorder")
@Tag("DataTable-rendering")
class DataTable061Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: #7444 the drag helper of a reflow table keeps the reflow class and the column titles")
    void reflowDragHelper(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        WebElement sourceHandle = dataTable.getRow(3).getCell(1).getWebElement();
        WebElement targetElement = dataTable.getRow(2).getWebElement();

        // Act - start dragging "TypeScript" (index 3) and keep holding it
        new Actions(page.getWebDriver())
                .moveToElement(sourceHandle)
                .clickAndHold()
                .moveByOffset(0, -10)
                .perform();

        // Assert - the helper lives in the body, outside of the table, so it must carry the reflow class itself
        WebElement helper = page.getWebDriver().findElement(By.cssSelector("body > .p-sortable-helper"));
        assertTrue(PrimeSelenium.hasCssClass(helper, "p-datatable-reflow"), "helper must have the reflow class");
        List<String> titles = helper.findElements(By.cssSelector("td .p-column-title")).stream()
                .map(e -> e.getAttribute("textContent"))
                .collect(Collectors.toList());
        assertEquals(List.of("Id", "Name"), titles);

        // Act - drop it on the row above
        Actions drop = new Actions(page.getWebDriver())
                .moveToElement(targetElement)
                .moveByOffset(0, -(targetElement.getSize().getHeight() / 2) + 1)
                .release();
        PrimeSelenium.guardAjax(drop.build()).perform();

        // Assert - reordering still works and the rendered rows keep a single column title per cell
        assertEquals("3", page.fromIndex.getText());
        assertEquals("2", page.toIndex.getText());
        assertEquals("Java,C#,TypeScript,JavaScript,Python", page.order.getText());
        assertEquals(1, dataTable.getRow(2).getCell(1).getWebElement().findElements(By.className("p-column-title")).size());
        assertNoJavascriptErrors();
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
            return "datatable/dataTable061.xhtml";
        }
    }
}
