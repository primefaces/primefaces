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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression: the widget renders the panel content of p:selectOneMenu as div.p-selectonemenu-items &gt;
 * ul.p-selectonemenu-list, the ul had the browser default padding (40px) and the items were indented.
 */
class Theming006Test extends AbstractThemingTest {

    /** pixel tolerance for layout positions (sub pixel rounding) */
    private static final double TOLERANCE = 0.5;

    /**
     * Returns per ul.p-selectonemenu-list of the items container: [padding-inline-start, margin-top, margin-left,
     * ul left, ul width, [left of each item], [width of each item]].
     */
    private static final String LISTS_SCRIPT = ""
            + "return Array.prototype.map.call(arguments[0].querySelectorAll(':scope > ul.p-selectonemenu-list'), function(ul) {"
            + "  var cs = getComputedStyle(ul), r = ul.getBoundingClientRect();"
            + "  var items = Array.prototype.slice.call(ul.querySelectorAll(':scope > li'));"
            + "  return [cs.getPropertyValue('padding-inline-start'), cs.getPropertyValue('margin-top'),"
            + "    cs.getPropertyValue('margin-left'), r.left, r.width,"
            + "    items.map(function(li) { return li.getBoundingClientRect().left; }),"
            + "    items.map(function(li) { return li.getBoundingClientRect().width; })];"
            + "});";

    @Test
    @Order(1)
    @DisplayName("Theming: p:selectOneMenu panel list without browser padding, items flush (plain options)")
    void plain(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        page.plain.show();

        // Assert
        assertLists("plain", page.plain, 1, 3);
        page.plain.hide();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: p:selectOneMenu panel lists without browser padding, items flush (SelectItemGroup)")
    void grouped(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        page.grouped.show();

        // Assert: one ul per group with the group header and its options
        assertLists("grouped", page.grouped, 2, 6);
        assertEquals(2, page.grouped.getItems().findElements(By.cssSelector(".p-selectonemenu-item-group")).size());
        page.grouped.hide();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: p:selectOneMenu panel list without browser padding, items flush (dynamic=true)")
    void dynamic(Page page) {
        // Arrange
        setColorScheme("light");

        // Act: the first show loads the panel content by ajax
        PrimeSelenium.executeScript(true, "PF('dynamic').show();");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.dynamic.getPanel()));

        // Assert
        assertLists("dynamic", page.dynamic, 1, 3);
        page.dynamic.hide();
        assertNoJavascriptErrors();
    }

    private void assertLists(String description, SelectOneMenu menu, int expectedLists, int expectedItems) {
        WebElement items = menu.getItems();
        assertEquals("div", items.getTagName(), description + ": the widget did not render div.p-selectonemenu-items");
        assertTokenValue(description + " items container", items, "padding", "--p-select-list-padding");

        Map<String, Object> container = PrimeSelenium.executeScript("var cs = getComputedStyle(arguments[0]);"
                + "return {left: arguments[0].getBoundingClientRect().left + parseFloat(cs.borderLeftWidth) + parseFloat(cs.paddingLeft),"
                + " width: arguments[0].clientWidth - parseFloat(cs.paddingLeft) - parseFloat(cs.paddingRight)};", items);
        double contentLeft = ((Number) container.get("left")).doubleValue();
        double contentWidth = ((Number) container.get("width")).doubleValue();

        List<List<Object>> lists = PrimeSelenium.executeScript(LISTS_SCRIPT, items);
        assertEquals(expectedLists, lists.size(), description + ": number of ul.p-selectonemenu-list");

        int itemCount = 0;
        for (List<Object> list : lists) {
            assertEquals("0px", list.get(0), description + ": ul padding-inline-start (browser default 40px)");
            assertEquals("0px", list.get(1), description + ": ul margin-top (browser default 1em)");
            assertEquals("0px", list.get(2), description + ": ul margin-left");
            double ulLeft = ((Number) list.get(3)).doubleValue();
            double ulWidth = ((Number) list.get(4)).doubleValue();
            assertEquals(contentLeft, ulLeft, TOLERANCE, description + ": ul is not at the content start of the items container");
            assertEquals(contentWidth, ulWidth, TOLERANCE, description + ": ul does not span the items container");

            @SuppressWarnings("unchecked")
            List<Object> lefts = (List<Object>) list.get(5);
            @SuppressWarnings("unchecked")
            List<Object> widths = (List<Object>) list.get(6);
            assertFalse(lefts.isEmpty(), description + ": empty ul");
            for (int i = 0; i < lefts.size(); i++) {
                assertEquals(ulLeft, ((Number) lefts.get(i)).doubleValue(), TOLERANCE, description + ": item " + i + " is indented");
                assertEquals(ulWidth, ((Number) widths.get(i)).doubleValue(), TOLERANCE, description + ": item " + i + " is not full width");
            }
            itemCount += lefts.size();
        }
        assertEquals(expectedItems, itemCount, description + ": number of items (incl. group headers)");
        assertTrue(menu.getPanel().isDisplayed());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:plain")
        SelectOneMenu plain;

        @FindBy(id = "form:grouped")
        SelectOneMenu grouped;

        @FindBy(id = "form:dynamic")
        SelectOneMenu dynamic;

        @Override
        public String getLocation() {
            return "theming/theming006.xhtml";
        }
    }
}
