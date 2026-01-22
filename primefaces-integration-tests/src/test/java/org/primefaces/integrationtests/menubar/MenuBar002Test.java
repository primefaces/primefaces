/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.integrationtests.menubar;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Menubar;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuBar002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("MenuBar: ViewScoped model executing AJAX menu item using MenuItem ID")
    void modelAjaxMenuItem(Page page) {
        // Arrange
        Menubar menubar = page.menubar;

        // Act
        menubar.selectMenuitemByValue("Options");
        menubar.selectMenuitemByValue("Update");

        // Assert
        assertMessage(page, "Data updated");
        assertConfiguration(menubar.getWidgetConfiguration());
    }

    @Test
    @Order(21)
    @DisplayName("MenuBar: ViewScoped model executing non-AJAX menu item using MenuItem ID")
    void modelNonAjaxMenuItem(Page page) {
        // Arrange
        Menubar menubar = page.menubar;

        // Act
        menubar.selectMenuitemByValue("Options");
        menubar.selectMenuitemByValue("Save (Non-Ajax)");

        // Assert
        assertMessage(page, "Data saved");
        assertConfiguration(menubar.getWidgetConfiguration());
    }

    private void assertMessage(Page page, String message) {
        Messages messages = page.messages;
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(messages));
        Msg msg = messages.getMessage(0);
        assertEquals(message, msg.getDetail());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("MenuBar Config = " + cfg);
        assertTrue(cfg.has("toggleEvent"));
        assertTrue(cfg.has("autoDisplay"));
        assertEquals("click", cfg.getString("toggleEvent"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:menubar")
        Menubar menubar;

        @Override
        public String getLocation() {
            return "menubar/menuBar002.xhtml";
        }
    }
}
