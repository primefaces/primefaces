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
package org.primefaces.integrationtests.dragdrop;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Draggable;
import org.primefaces.selenium.component.Droppable;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DragDrop001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DragDrop: drop an accepted draggable with drop AJAX event")
    void drop(Page page) {
        // Arrange
        Droppable droppable = page.droppable;
        WebElement accepted = page.dragAccepted.getTarget();
        assertTrue(droppable.isDropAjaxified());
        assertTrue(droppable.accepts(accepted));
        assertEquals("0", page.dropCount.getText());

        // Act
        droppable.drop(page.dragAccepted);

        // Assert
        assertEquals("Dropped", page.messages.getMessage(0).getSummary());
        assertEquals("form:accepted on form:dropArea", page.messages.getMessage(0).getDetail());
        assertEquals("1", page.dropCount.getText());
        // dropped on a droppable, so it does not revert
        assertCenterInside(accepted, droppable.getTarget());
        assertConfiguration(droppable.getWidgetConfiguration());
        assertTrue(page.dragAccepted.getWidgetConfiguration().has("revert"));
    }

    @Test
    @Order(2)
    @DisplayName("DragDrop: draggable reverts when not dropped on a droppable")
    void revert(Page page) {
        // Arrange
        WebElement accepted = page.dragAccepted.getTarget();
        Point location = accepted.getLocation();

        // Act
        page.dragAccepted.dragBy(0, 250);

        // Assert
        assertEquals(location, accepted.getLocation());
        assertTrue(page.messages.isEmpty());
        assertEquals("0", page.dropCount.getText());
        assertConfiguration(page.droppable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DragDrop: droppable does not accept a draggable not matching accept")
    void notAccepted(Page page) {
        // Arrange
        Droppable droppable = page.droppable;
        WebElement rejected = page.dragRejected.getTarget();
        Point location = rejected.getLocation();
        assertFalse(droppable.accepts(rejected));

        // Act
        droppable.drop(page.dragRejected);

        // Assert - not dropped, so it reverts
        assertEquals(location, rejected.getLocation());
        assertTrue(page.messages.isEmpty());
        assertEquals("0", page.dropCount.getText());
        assertConfiguration(droppable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DragDrop: axis restricts the movement to one direction")
    void axis(Page page) {
        // Arrange
        WebElement horizontal = page.dragHorizontal.getTarget();
        Point location = horizontal.getLocation();
        assertEquals("x", page.dragHorizontal.getWidgetConfiguration().getString("axis"));

        // Act
        page.dragHorizontal.dragBy(100, 80);

        // Assert
        assertEquals(location.getX() + 100, horizontal.getLocation().getX(), 1);
        assertEquals(location.getY(), horizontal.getLocation().getY());
        assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    private void assertCenterInside(WebElement element, WebElement container) {
        Rectangle rect = element.getRect();
        Rectangle area = container.getRect();
        int x = rect.getX() + rect.getWidth() / 2;
        int y = rect.getY() + rect.getHeight() / 2;
        assertTrue(x > area.getX() && x < area.getX() + area.getWidth(), "x " + x + " not inside " + area.getX() + "+" + area.getWidth());
        assertTrue(y > area.getY() && y < area.getY() + area.getHeight(), "y " + y + " not inside " + area.getY() + "+" + area.getHeight());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("form:dropArea", cfg.getString("target"));
        assertEquals(".accepted", cfg.getString("accept"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:dropCount")
        WebElement dropCount;

        @FindBy(id = "form:dragAccepted")
        Draggable dragAccepted;

        @FindBy(id = "form:dragRejected")
        Draggable dragRejected;

        @FindBy(id = "form:dragHorizontal")
        Draggable dragHorizontal;

        @FindBy(id = "form:drop")
        Droppable droppable;

        @Override
        public String getLocation() {
            return "dragdrop/dragDrop001.xhtml";
        }
    }
}
