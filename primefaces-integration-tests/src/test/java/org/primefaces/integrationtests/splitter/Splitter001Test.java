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
package org.primefaces.integrationtests.splitter;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Splitter;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Splitter001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Splitter: drag the gutter, resizeEnd event via AJAX")
    void drag(Page page) {
        // Arrange
        Splitter splitter = page.ajax;
        assertEquals(2, splitter.getPanels().size());
        assertEquals(Arrays.asList(50.0, 50.0), splitter.getPanelSizes());
        assertTrue(splitter.isResizeEndAjaxified());

        // Act - 600px wide, 120px are 20%
        splitter.dragGutter(0, 120);

        // Assert
        double first = splitter.getPanelSizes().get(0);
        assertTrue(first > 60 && first < 80, "first panel size " + first);
        assertEquals(first, splitter.getPanelSizeBeforeGutter(0), 0.01);
        assertEquals("Resized", page.messages.getMessage(0).getSummary());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Splitter: Home and End on the gutter handle maximize the panels")
    void keyboard(Page page) {
        // Arrange
        Splitter splitter = page.keyboard;
        assertEquals(Arrays.asList(30.0, 70.0), splitter.getPanelSizes());
        assertEquals(30.0, splitter.getPanelSizeBeforeGutter(0), 0.01);

        // Act
        splitter.getGutterHandle(0).sendKeys(Keys.HOME);

        // Assert
        assertEquals(Arrays.asList(100.0, 0.0), splitter.getPanelSizes());
        assertEquals(100.0, splitter.getPanelSizeBeforeGutter(0), 0.01);

        // Act
        splitter.getGutterHandle(0).sendKeys(Keys.END);

        // Assert
        assertEquals(Arrays.asList(0.0, 100.0), splitter.getPanelSizes());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:ajax")
        Splitter ajax;

        @FindBy(id = "form:keyboard")
        Splitter keyboard;

        @Override
        public String getLocation() {
            return "splitter/splitter001.xhtml";
        }
    }
}
