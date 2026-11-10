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
package org.primefaces.integrationtests.progressbar;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.ProgressBar;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ProgressBar001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ProgressBar: set value on the client")
    void clientValue(Page page) {
        // Arrange
        ProgressBar progressBar = page.client;
        assertEquals(0, progressBar.getValue());
        assertEquals("progressbar", progressBar.getDomAttribute("role"));
        assertEquals("0", progressBar.getDomAttribute("aria-valuemin"));
        assertEquals("100", progressBar.getDomAttribute("aria-valuemax"));

        // Act
        progressBar.setValue(50);

        // Assert
        assertEquals(50, progressBar.getValue());
        assertEquals("50%", progressBar.getLabel().getText());

        // Act - out of range is ignored
        progressBar.setValue(150);

        // Assert
        assertEquals(50, progressBar.getValue());

        // Act
        progressBar.cancel();

        // Assert
        assertEquals(0, progressBar.getValue());
        assertEquals("0%", progressBar.getLabel().getText());
        assertNoJavascriptErrors();
        assertFalse(progressBar.isAjax());
    }

    @Test
    @Order(2)
    @DisplayName("ProgressBar: AJAX polling until complete")
    void ajax(Page page) {
        // Arrange
        ProgressBar progressBar = page.ajax;
        assertEquals(0, progressBar.getValue());

        // Act
        progressBar.start();
        progressBar.waitUntilComplete();

        // Assert
        assertEquals(100, progressBar.getValue());
        assertEquals("100%", progressBar.getLabel().getText());
        assertEquals("Progress Completed", page.messages.getMessage(0).getSummary());
        assertConfiguration(progressBar.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.getBoolean("ajax"));
        assertEquals(200, cfg.getInt("interval"));
        assertFalse(cfg.getBoolean("global"));
        assertEquals("{value}%", cfg.getString("labelTemplate"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:client")
        ProgressBar client;

        @FindBy(id = "form:ajax")
        ProgressBar ajax;

        @Override
        public String getLocation() {
            return "progressbar/progressBar001.xhtml";
        }
    }
}
