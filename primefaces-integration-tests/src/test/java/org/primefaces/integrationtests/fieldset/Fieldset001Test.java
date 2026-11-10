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
package org.primefaces.integrationtests.fieldset;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Fieldset;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Fieldset001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Fieldset: collapse and expand with toggle AJAX event")
    void toggle(Page page) {
        // Arrange
        Fieldset fieldset = page.toggleable;
        assertTrue(fieldset.isToggleable());
        assertFalse(fieldset.isCollapsed());
        assertTrue(fieldset.getContent().isDisplayed());

        // Act
        fieldset.collapse();

        // Assert
        assertTrue(fieldset.isCollapsed());
        assertFalse(fieldset.getContent().isDisplayed());
        assertEquals("toggleable toggled", page.messages.getMessage(0).getSummary());
        assertEquals("Status:HIDDEN", page.messages.getMessage(0).getDetail());

        // Act
        fieldset.expand();

        // Assert
        assertFalse(fieldset.isCollapsed());
        assertTrue(fieldset.getContent().isDisplayed());
        assertEquals("Status:VISIBLE", page.messages.getMessage(0).getDetail());
        assertConfiguration(fieldset.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Fieldset: collapsed state must not be lost after AJAX update")
    void collapsedAfterUpdate(Page page) {
        // Arrange
        Fieldset fieldset = page.toggleable;
        fieldset.collapse();

        // Act
        page.update.click();

        // Assert
        assertTrue(fieldset.isCollapsed());
        assertFalse(fieldset.getContent().isDisplayed());

        // Act - the refreshed widget still toggles
        fieldset.expand();

        // Assert
        assertFalse(fieldset.isCollapsed());
        assertTrue(fieldset.getContent().isDisplayed());
        assertConfiguration(fieldset.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Fieldset: dynamic content is loaded on the first expand")
    void dynamic(Page page) {
        // Arrange
        Fieldset fieldset = page.dynamic;
        assertTrue(fieldset.isCollapsed());
        assertFalse(PrimeSelenium.isElementPresent(By.id("form:dynamicText")));

        // Act
        fieldset.expand();

        // Assert
        assertFalse(fieldset.isCollapsed());
        assertEquals("Dynamic content loaded", fieldset.getContent().getText());

        // Act
        fieldset.collapse();
        fieldset.expand();

        // Assert
        assertEquals("Dynamic content loaded", fieldset.getContent().getText());
        assertConfiguration(fieldset.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Fieldset: not toggleable")
    void notToggleable(Page page) {
        // Arrange
        Fieldset fieldset = page.staticFieldset;

        // Act
        fieldset.collapse();

        // Assert
        assertFalse(fieldset.isToggleable());
        assertFalse(fieldset.isCollapsed());
        assertEquals("Static content", fieldset.getContent().getText());
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.getBoolean("toggleable"));
        assertTrue(cfg.has("toggleSpeed"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:toggleable")
        Fieldset toggleable;

        @FindBy(id = "form:dynamic")
        Fieldset dynamic;

        @FindBy(id = "form:static")
        Fieldset staticFieldset;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "fieldset/fieldset001.xhtml";
        }
    }
}
