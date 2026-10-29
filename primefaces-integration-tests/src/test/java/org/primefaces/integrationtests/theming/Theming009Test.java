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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Float label of p:selectCheckboxMenu: the widget sets no filled class, the theme detects checked options (hidden inputs)
 * and the focus class of the widget.
 */
class Theming009Test extends AbstractThemingTest {

    @Test
    @Order(1)
    @DisplayName("Theming: float label of p:selectCheckboxMenu with selected options is active on page load")
    void filled(Page page) {
        // Arrange
        setColorScheme("light");

        // Assert
        assertTokenValue("Filled label", page.filledLabel, "font-size", "--p-floatlabel-active-font-size");
        assertTokenWired("Filled label", page.filledLabel, "color", "--p-floatlabel-active-color");
        assertAbove("Filled", page.filledLabel, page.filled);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: float label of an empty p:selectCheckboxMenu is inactive, focus makes it active")
    void emptyAndFocus(Page page) {
        // Arrange
        setColorScheme("light");
        assertTokenValue("Empty label", page.emptyLabel, "font-size", "--p-floatlabel-font-size");
        String inactiveSize = computedStyle(page.emptyLabel, "font-size");

        // Act: the widget sets p-state-focus on focus
        PrimeSelenium.executeScript("arguments[0].classList.add('p-state-focus');", page.empty);

        // Assert
        assertTokenValue("Focused label", page.emptyLabel, "font-size", "--p-floatlabel-active-font-size");
        assertTokenWired("Focused label", page.emptyLabel, "color", "--p-floatlabel-focus-color");
        assertNotEquals(inactiveSize, computedStyle(page.emptyLabel, "font-size"));
        assertAbove("Focused", page.emptyLabel, page.empty);

        // Act
        PrimeSelenium.executeScript("arguments[0].classList.remove('p-state-focus');", page.empty);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: float label of an empty p:selectCheckboxMenu stays active while the panel is open (focus in the panel)")
    void open(Page page) {
        // Arrange
        setColorScheme("light");
        assertTokenValue("Empty label", page.emptyLabel, "font-size", "--p-floatlabel-font-size");

        // Act: the widget marks the open panel on its hidden focus input, the root loses p-state-focus to the panel
        PrimeSelenium.executeScript("document.getElementById('form:empty_focus').setAttribute('aria-expanded', 'true');");

        // Assert
        assertTokenValue("Open label", page.emptyLabel, "font-size", "--p-floatlabel-active-font-size");
        assertTokenWired("Open label", page.emptyLabel, "color", "--p-floatlabel-focus-color");
        assertAbove("Open", page.emptyLabel, page.empty);

        // Act
        PrimeSelenium.executeScript("document.getElementById('form:empty_focus').setAttribute('aria-expanded', 'false');");
        assertNoJavascriptErrors();
    }

    private static void assertAbove(String description, WebElement label, WebElement field) {
        double labelTop = label.getLocation().getY();
        double fieldTop = field.getLocation().getY();
        assertTrue(labelTop < fieldTop, description + ": label (" + labelTop + ") is not above the field (" + fieldTop + ")");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:filled")
        WebElement filled;

        @FindBy(id = "form:filledLabel")
        WebElement filledLabel;

        @FindBy(id = "form:empty")
        WebElement empty;

        @FindBy(id = "form:emptyLabel")
        WebElement emptyLabel;

        @Override
        public String getLocation() {
            return "theming/theming009.xhtml";
        }
    }
}
