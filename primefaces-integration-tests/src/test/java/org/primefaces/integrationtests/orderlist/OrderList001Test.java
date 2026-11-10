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
package org.primefaces.integrationtests.orderlist;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.OrderList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class OrderList001Test extends AbstractPrimePageTest {

    private static final List<String> LANGUAGES = Arrays.asList("Java", "C#", "JavaScript", "TypeScript", "Python");

    @Test
    @Order(1)
    @DisplayName("OrderList: select and unselect items fire the AJAX events")
    void selectUnselect(Page page) {
        // Arrange
        OrderList orderList = page.ajax;
        assertEquals(LANGUAGES, orderList.getItemValues());
        assertEquals(LANGUAGES, orderList.getItemLabels());
        assertTrue(orderList.getSelectedValues().isEmpty());

        // Act
        orderList.select("JavaScript");

        // Assert
        assertTrue(orderList.isSelected("JavaScript"));
        assertEquals("Item Selected", page.messages.getMessage(0).getSummary());
        assertEquals("JavaScript", page.messages.getMessage(0).getDetail());

        // Act - add an item to the selection
        orderList.toggleSelection("Python");

        // Assert
        assertEquals(Arrays.asList("JavaScript", "Python"), orderList.getSelectedValues());
        assertEquals("Item Selected", page.messages.getMessage(0).getSummary());
        assertEquals("Python", page.messages.getMessage(0).getDetail());

        // Act - remove an item from the selection
        orderList.toggleSelection("JavaScript");

        // Assert
        assertEquals(Collections.singletonList("Python"), orderList.getSelectedValues());
        assertEquals("Item Unselected", page.messages.getMessage(0).getSummary());
        assertEquals("JavaScript", page.messages.getMessage(0).getDetail());

        // Act - a plain click replaces the selection
        orderList.select("Java");

        // Assert
        assertEquals(Collections.singletonList("Java"), orderList.getSelectedValues());
        assertEquals(LANGUAGES, orderList.getItemValues());
        assertConfiguration(orderList.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("OrderList: move up and move top fire the reorder AJAX event with the new order")
    void reorder(Page page) {
        // Arrange
        OrderList orderList = page.ajax;
        orderList.select("Python");

        // Act
        orderList.moveUp();

        // Assert
        List<String> expected = Arrays.asList("Java", "C#", "JavaScript", "Python", "TypeScript");
        assertEquals(expected, orderList.getItemValues());
        assertEquals("List Reordered", page.messages.getMessage(0).getSummary());
        assertEquals(String.join(", ", expected), page.messages.getMessage(0).getDetail());

        // Act
        orderList.moveTop();

        // Assert
        expected = Arrays.asList("Python", "Java", "C#", "JavaScript", "TypeScript");
        assertEquals(expected, orderList.getItemValues());
        assertTrue(orderList.isSelected("Python"));
        assertEquals("List Reordered", page.messages.getMessage(0).getSummary());
        assertEquals(String.join(", ", expected), page.messages.getMessage(0).getDetail());

        // Act - the top item cannot be moved up
        orderList.moveUp();

        // Assert
        assertEquals(expected, orderList.getItemValues());
        assertConfiguration(orderList.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("OrderList: move multiple selected items down and to the bottom, submit the new order")
    void moveMultipleAndSubmit(Page page) {
        // Arrange
        OrderList orderList = page.basic;
        orderList.select("Java");
        orderList.toggleSelection("C#");
        assertEquals(Arrays.asList("Java", "C#"), orderList.getSelectedValues());

        // Act
        orderList.moveDown();

        // Assert
        assertEquals(Arrays.asList("JavaScript", "Java", "C#", "TypeScript", "Python"), orderList.getItemValues());

        // Act
        orderList.moveBottom();

        // Assert
        List<String> expected = Arrays.asList("JavaScript", "TypeScript", "Python", "Java", "C#");
        assertEquals(expected, orderList.getItemValues());

        // Act
        page.submit.click();

        // Assert - the bean got the new order and the updated orderlist keeps it
        assertEquals("Submitted", page.messages.getMessage(0).getSummary());
        assertEquals(String.join(", ", expected), page.messages.getMessage(0).getDetail());
        assertEquals(expected, orderList.getItemValues());
        assertTrue(orderList.getSelectedValues().isEmpty());

        // Act - the updated widget still reorders
        orderList.select("C#");
        orderList.moveTop();

        // Assert
        assertEquals(Arrays.asList("C#", "JavaScript", "TypeScript", "Python", "Java"), orderList.getItemValues());
        assertConfiguration(orderList.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("OrderList: disabled orderlist does not select items")
    void disabled(Page page) {
        // Arrange
        OrderList orderList = page.disabled;
        assertTrue(orderList.isDisabled());

        // Act - the mouse cannot reach the items (pointer-events: none), so trigger the mousedown the widget selects on
        // by script: a disabled orderlist must not have bound it
        PrimeSelenium.executeScript("jQuery(arguments[0]).trigger('mousedown');", orderList.getItem("Java"));

        // Assert
        assertFalse(orderList.isSelected("Java"));
        assertTrue(orderList.getSelectedValues().isEmpty());
        assertEquals(LANGUAGES, orderList.getItemValues());
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("fade", cfg.getString("effect"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:ajax")
        OrderList ajax;

        @FindBy(id = "form:basic")
        OrderList basic;

        @FindBy(id = "form:disabled")
        OrderList disabled;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "orderlist/orderList001.xhtml";
        }
    }
}
