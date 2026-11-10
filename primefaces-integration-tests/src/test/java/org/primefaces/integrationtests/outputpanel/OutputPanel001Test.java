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
package org.primefaces.integrationtests.outputpanel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.OutputPanel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class OutputPanel001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("OutputPanel: deferredMode=load loads the content after page load")
    void deferredLoad(Page page) {
        // Act
        page.load.waitUntilLoaded();

        // Assert
        assertEquals("Deferred content", page.load.getText());
        assertEquals("load", page.load.getWidgetConfiguration().getString("deferredMode"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("OutputPanel: deferredMode=visible loads the content once scrolled into view")
    void deferredVisible(Page page) {
        // Arrange
        OutputPanel panel = page.visible;
        assertTrue(panel.isLoading());

        // Act
        panel.scrollIntoViewAndLoad();

        // Assert
        assertFalse(panel.isLoading());
        assertEquals("Deferred content", panel.getText());
        assertEquals("visible", panel.getWidgetConfiguration().getString("deferredMode"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:load")
        OutputPanel load;

        @FindBy(id = "form:visible")
        OutputPanel visible;

        @Override
        public String getLocation() {
            return "outputpanel/outputPanel001.xhtml";
        }
    }
}
