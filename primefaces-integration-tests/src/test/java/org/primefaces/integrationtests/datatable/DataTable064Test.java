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

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-rowreorder")
@Tag("DataTable-rendering")
class DataTable064Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: #7444 the drag helper of a reflow table keeps the classes of the table and the column titles")
    void reflowDragHelper(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        WebElement targetElement = dataTable.getRow(2).getWebElement();

        // Act - start dragging "TypeScript" (index 3) and keep holding it
        WebElement helper = startDrag(page, 3);

        // Assert - the helper lives in the body, outside of the table, so it must carry the classes of the table itself
        for (String styleClass : List.of("p-datatable", "p-datatable-reflow", "p-datatable-sm", "my-table")) {
            assertTrue(PrimeSelenium.hasCssClass(helper, styleClass), "helper must have the class " + styleClass);
        }
        List<String> titles = helper.findElements(By.cssSelector("td:not(.p-hidden) .p-column-title")).stream()
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

    @Test
    @Order(2)
    @DisplayName("DataTable: #7444 on a narrow screen the drag helper and the placeholder are stacked like the reflowed row")
    void reflowDragHelperNarrowScreen(Page page) {
        WebDriver.Window window = page.getWebDriver().manage().window();
        Dimension size = window.getSize();
        try {
            // Arrange - the reflow styles only apply up to 640px
            window.setSize(new Dimension(560, size.getHeight()));
            Number viewport = PrimeSelenium.executeScript("return window.innerWidth;");
            Assumptions.assumeTrue(viewport.intValue() <= 640, "the window cannot be made narrow enough: " + viewport);
            WebElement row = page.dataTable.getRow(3).getWebElement();
            int rowWidth = row.getSize().getWidth();
            int rowHeight = row.getSize().getHeight();

            // Act
            WebElement helper = startDrag(page, 3);

            // Assert - the helper has the width of the row and its visible cells are stacked
            assertEquals(rowWidth, helper.getSize().getWidth(), 1, "helper width");
            List<WebElement> helperCells = helper.findElements(By.cssSelector("td:not(.p-hidden)"));
            assertEquals(2, helperCells.size());
            assertTrue(helperCells.get(1).getLocation().getY() >= helperCells.get(0).getLocation().getY()
                    + helperCells.get(0).getSize().getHeight(), "helper cells must be stacked");

            // Assert - the placeholder is stacked too, without a line for the hidden column
            WebElement placeholder = page.dataTable.findElement(By.cssSelector("tr.p-datatable-rowordering"));
            List<WebElement> placeholderCells = placeholder.findElements(By.tagName("td"));
            assertEquals(3, placeholderCells.size());
            assertFalse(placeholderCells.get(2).isDisplayed(), "placeholder cell of the hidden column must be hidden");
            assertTrue(placeholderCells.get(1).getLocation().getY() > placeholderCells.get(0).getLocation().getY(),
                    "placeholder cells must be stacked");
            int placeholderHeight = placeholder.getSize().getHeight();
            assertTrue(placeholderHeight <= rowHeight,
                    "placeholder (" + placeholderHeight + ") must not be taller than the row (" + rowHeight + ")");

            new Actions(page.getWebDriver()).release().perform();
        }
        finally {
            window.setSize(size);
        }
        assertNoJavascriptErrors();
    }

    private static WebElement startDrag(Page page, int rowIndex) {
        WebElement sourceHandle = page.dataTable.getRow(rowIndex).getCell(1).getWebElement();
        new Actions(page.getWebDriver())
                .moveToElement(sourceHandle)
                .clickAndHold()
                .moveByOffset(0, -10)
                .perform();
        return page.getWebDriver().findElement(By.cssSelector("body > .p-sortable-helper"));
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
            return "datatable/dataTable064.xhtml";
        }
    }
}
