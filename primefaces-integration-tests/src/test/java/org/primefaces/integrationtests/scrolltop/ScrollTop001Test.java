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
package org.primefaces.integrationtests.scrolltop;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.ScrollTop;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ScrollTop001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ScrollTop: shown beyond the threshold, scrolls the window to the top")
    void scrollToTop(Page page) {
        // Arrange
        ScrollTop scrollTop = page.scrollTop;
        assertFalse(scrollTop.isVisible());

        // Act - scroll the page itself, not part of the component
        PrimeSelenium.executeScript("window.scrollTo(0, 1000);");
        scrollTop.waitUntilVisible();

        // Assert
        assertTrue(scrollTop.isVisible());
        assertEquals(1000, scrollTop.getTargetScrollTop());

        // Act
        scrollTop.scrollToTop();

        // Assert
        assertEquals(0, scrollTop.getTargetScrollTop());
        assertFalse(scrollTop.isVisible());
        assertConfiguration(scrollTop.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("window", cfg.getString("target"));
        assertEquals(400, cfg.getInt("threshold"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:scrollTop")
        ScrollTop scrollTop;

        @Override
        public String getLocation() {
            return "scrolltop/scrollTop001.xhtml";
        }
    }
}
