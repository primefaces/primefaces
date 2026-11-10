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
package org.primefaces.integrationtests.stack;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Stack;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Stack002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Stack: expanded initially, items are clickable without toggling and the toggler collapses them")
    void expanded(Page page) {
        // Arrange
        Stack stack = page.stack;
        assertTrue(stack.isExpanded());
        assertTrue(getLabel(stack, "Home").isDisplayed());

        // Act
        stack.clickItem("Home");

        // Assert
        assertEquals("Home", page.selected.getText());
        assertEquals("Home", page.messages.getMessage(0).getDetail());
        assertTrue(stack.isExpanded());

        // Act
        stack.collapse();

        // Assert
        assertFalse(stack.isExpanded());
        assertFalse(getLabel(stack, "Home").isDisplayed());
        assertConfiguration(stack.getWidgetConfiguration());
    }

    private WebElement getLabel(Stack stack, String value) {
        return stack.findItemByValue(value).findElement(By.cssSelector("a > span"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertFalse(cfg.getBoolean("expanded"));
        assertEquals(50, cfg.getInt("openSpeed"));
        assertEquals(50, cfg.getInt("closeSpeed"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:stack")
        Stack stack;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selected")
        WebElement selected;

        @Override
        public String getLocation() {
            return "stack/stack002.xhtml";
        }
    }
}
