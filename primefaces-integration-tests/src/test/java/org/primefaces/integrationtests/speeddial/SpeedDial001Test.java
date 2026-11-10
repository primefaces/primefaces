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
package org.primefaces.integrationtests.speeddial;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SpeedDial;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SpeedDial001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SpeedDial: open and close with the button")
    void openClose(Page page) {
        // Arrange
        SpeedDial speedDial = page.speedDial;
        assertFalse(speedDial.isOpen());
        assertEquals(2, speedDial.getItems().size());
        assertEquals("-1", speedDial.getItems().get(0).getDomAttribute("tabindex"));

        // Act
        speedDial.open();

        // Assert
        assertTrue(speedDial.isOpen());
        assertEquals("0", speedDial.getItems().get(0).getDomAttribute("tabindex"));

        // Act
        speedDial.close();

        // Assert
        assertFalse(speedDial.isOpen());
        assertEquals("-1", speedDial.getItems().get(0).getDomAttribute("tabindex"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SpeedDial: a click on an item runs its action and closes the items")
    void itemClick(Page page) {
        // Arrange
        SpeedDial speedDial = page.speedDial;
        speedDial.open();

        // Act
        PrimeSelenium.guardAjax(speedDial.getItems().get(0)).click();

        // Assert
        assertFalse(speedDial.isOpen());
        assertEquals("Edit", page.messages.getMessage(0).getSummary());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:speedDial")
        SpeedDial speedDial;

        @Override
        public String getLocation() {
            return "speeddial/speedDial001.xhtml";
        }
    }
}
