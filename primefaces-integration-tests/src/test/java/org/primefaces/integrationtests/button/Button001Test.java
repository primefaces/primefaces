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
package org.primefaces.integrationtests.button;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Button;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Button001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Button: rendering and disabled state from the server")
    void render(Page page) {
        // Assert
        assertTrue(page.button.isEnabled());
        assertEquals("Button", page.button.getLabelElement().getText());
        assertNotNull(page.button.getIcon());
        assertFalse(page.disabled.isEnabled());
        assertNull(page.disabled.getIcon());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Button: disable and enable via widget")
    void disableEnable(Page page) {
        // Act
        page.button.disable();

        // Assert
        assertFalse(page.button.isEnabled());
        assertEquals("true", page.button.getDomAttribute("aria-disabled"));

        // Act
        page.button.enable();

        // Assert
        assertTrue(page.button.isEnabled());
        assertNull(page.button.getDomAttribute("aria-disabled"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:button")
        Button button;

        @FindBy(id = "form:disabled")
        Button disabled;

        @Override
        public String getLocation() {
            return "button/button001.xhtml";
        }
    }
}
