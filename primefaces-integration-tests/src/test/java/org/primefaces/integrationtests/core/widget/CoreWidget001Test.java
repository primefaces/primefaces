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
package org.primefaces.integrationtests.core.widget;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreWidget001Test extends AbstractWidgetLifecycleTest {

    /** The widgets with markup, the script-only widgets (p:draggable, ...) live with their target. */
    private static final String[] WIDGETS = {
        "form:orderList", "form:pickList", "form:dataTable", "form:tree", "form:dashboard", "form:dlg"
    };

    @Test
    @Order(1)
    @DisplayName("Widget lifecycle: AJAX updates keep the interactions and don't leak global state")
    void update(Page page) {
        // Arrange
        JSONObject initialInteractions = interactions();
        for (String type : new String[] {"draggable", "droppable", "sortable", "resizable"}) {
            assertTrue(initialInteractions.getInt(type) > 0, "no " + type + " in " + initialInteractions);
        }
        JSONObject initialState = globalState();
        assertEquals(0, initialState.getInt("detachedDroppables"));

        // Act
        for (int i = 0; i < 3; i++) {
            ajaxClick(page.btnUpdate);
        }

        // Assert
        assertSameState(initialState, globalState());
        assertSameState(initialInteractions, interactions());
        assertWidgetCount(1, WIDGETS);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Widget lifecycle: sorting still works after AJAX updates")
    void sortAfterUpdate(Page page) {
        // Arrange
        ajaxClick(page.btnUpdate);
        ajaxClick(page.btnUpdate);
        List<WebElement> items = page.orderListItems();
        assertEquals("Alpha", items.get(0).getText());

        // Act: drag the first item below the second one
        int height = items.get(0).getSize().getHeight();
        new Actions(getWebDriver())
                    .clickAndHold(items.get(0))
                    .moveByOffset(0, 5)
                    .moveByOffset(0, height + height / 2)
                    .release()
                    .perform();

        // Assert
        List<String> labels = page.orderListItems().stream().map(WebElement::getText).collect(Collectors.toList());
        assertEquals(List.of("Beta", "Alpha", "Gamma", "Delta"), labels);
        assertEquals(0, globalState().getInt("detachedDroppables"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Widget lifecycle: removed widgets are destroyed with their interactions and global state")
    void remove(Page page) {
        // Arrange
        JSONObject initialInteractions = interactions();

        // Act: remove
        ajaxClick(page.btnToggle);

        // Assert
        JSONObject removed = globalState();
        assertEquals(0, removed.getInt("droppables"), removed.toString());
        assertSameState(new JSONObject("{draggable:0,droppable:0,sortable:0,resizable:0}"), interactions());
        assertWidgetCount(0, WIDGETS);

        // Act: render again
        ajaxClick(page.btnToggle);

        // Assert
        assertSameState(initialInteractions, interactions());
        assertWidgetCount(1, WIDGETS);

        // Act: remove again
        ajaxClick(page.btnToggle);

        // Assert
        assertSameState(removed, globalState());
        assertWidgetCount(0, WIDGETS);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Widget lifecycle: an AJAX update during a drag ends it cleanly")
    void updateDuringDrag(Page page) {
        // Arrange
        JSONObject initialState = globalState();
        List<WebElement> items = page.orderListItems();
        Actions actions = new Actions(getWebDriver());
        actions.clickAndHold(items.get(0)).moveByOffset(0, 10).perform();
        assertEquals(1L, (Long) PrimeSelenium.executeScript("return document.querySelectorAll('.p-sortable-helper').length;"));

        // Act: the list is replaced while the item is dragged
        PrimeSelenium.executeScript(true, "PrimeFaces.ab({s: 'form:btnUpdate', p: 'form:btnUpdate', u: 'form:wrapper'});");
        actions.moveByOffset(0, 20).release().perform();

        // Assert
        assertEquals(0L, (Long) PrimeSelenium.executeScript(
                    "return document.querySelectorAll('.p-sortable-helper, .p-sortable-placeholder').length;"));
        assertSameState(initialState, globalState());
        assertEquals("Alpha", page.orderListItems().get(0).getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Widget lifecycle: a Dialog can still be dragged and resized after AJAX updates")
    void dialogAfterUpdate(Page page) {
        // Arrange
        ajaxClick(page.btnUpdate);
        ajaxClick(page.btnUpdate);
        Point location = page.dialogBox.getLocation();
        Dimension size = page.dialogBox.getSize();

        // Act: drag the title bar to the left and up
        new Actions(getWebDriver())
                    .clickAndHold(page.dialogTitleBar)
                    .moveByOffset(-5, -5)
                    .moveByOffset(-50, -40)
                    .release()
                    .perform();

        // Assert
        Point moved = page.dialogBox.getLocation();
        assertTrue(Math.abs(moved.getX() - (location.getX() - 55)) <= 2, location + " -> " + moved);
        assertTrue(Math.abs(moved.getY() - (location.getY() - 45)) <= 2, location + " -> " + moved);

        // Act: resize with the south east handle
        new Actions(getWebDriver())
                    .clickAndHold(page.dialogResizeHandle)
                    .moveByOffset(5, 5)
                    .moveByOffset(30, 20)
                    .release()
                    .perform();

        // Assert
        Dimension resized = page.dialogBox.getSize();
        assertTrue(Math.abs(resized.getWidth() - (size.getWidth() + 35)) <= 2, size + " -> " + resized);
        assertTrue(resized.getHeight() > size.getHeight(), size + " -> " + resized);
        assertEquals(0L, (Long) PrimeSelenium.executeScript(
                    "return document.querySelectorAll('.p-draggable-dragging, .p-resizable-resizing').length;"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        /** The dialog box, the element with the client id is a container covering the viewport. */
        @FindBy(css = "[id='form:dlg'] > .p-dialog-box")
        WebElement dialogBox;

        @FindBy(css = "[id='form:dlg'] .p-dialog-titlebar")
        WebElement dialogTitleBar;

        @FindBy(css = "[id='form:dlg'] .p-resizable-se")
        WebElement dialogResizeHandle;

        @FindBy(id = "form:btnUpdate")
        WebElement btnUpdate;

        @FindBy(id = "form:btnToggle")
        WebElement btnToggle;

        List<WebElement> orderListItems() {
            return getWebDriver().findElements(By.cssSelector("[id='form:orderList'] .p-orderlist-list .p-orderlist-item"));
        }

        @Override
        public String getLocation() {
            return "core/widget/coreWidget001.xhtml";
        }
    }
}
