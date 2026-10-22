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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression: the float label of p:chips / p:autoComplete multiple with values on page load must be in its active state
 * without any interaction (the widgets do not mark them as filled, the theme detects the tokens).
 */
class Theming007Test extends AbstractThemingTest {

    @Test
    @Order(1)
    @DisplayName("Theming: float label of p:chips with values is active on page load")
    void chips(Page page) {
        // Arrange
        setColorScheme("light");
        assertFalse(page.chips.findElements(By.cssSelector(".ui-chips-token")).isEmpty(), "chips have no tokens");

        // Assert
        assertActive("Chips", page.chips, page.chipsLabel, page.emptyChipsLabel);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: float label of p:autoComplete multiple with values is active on page load")
    void autoComplete(Page page) {
        // Arrange
        setColorScheme("light");
        assertFalse(page.autoComplete.findElements(By.cssSelector(".ui-autocomplete-token")).isEmpty(), "autoComplete has no tokens");

        // Assert
        assertActive("AutoComplete multiple", page.autoComplete, page.autoCompleteLabel, page.emptyChipsLabel);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: float label of an empty p:chips stays inactive on page load")
    void empty(Page page) {
        // Arrange
        setColorScheme("light");
        assertTrue(page.emptyChips.findElements(By.cssSelector(".ui-chips-token")).isEmpty(), "empty chips have tokens");

        // Assert: the control case, the label keeps the normal font size
        assertTokenValue("Empty chips label", page.emptyChipsLabel, "font-size", "--p-floatlabel-font-size");
        assertNoJavascriptErrors();
    }

    private void assertActive(String description, WebElement field, WebElement label, WebElement inactiveLabel) {
        assertTokenValue(description + " label", label, "font-size", "--p-floatlabel-active-font-size");
        assertTokenWired(description + " label", label, "color", "--p-floatlabel-active-color");
        assertNotEquals(computedStyle(inactiveLabel, "font-size"), computedStyle(label, "font-size"),
                description + ": label has the font size of an inactive label");
        // the active label sits above the field, the inactive one in the middle of it
        double labelTop = label.getLocation().getY();
        double fieldTop = field.getLocation().getY();
        assertTrue(labelTop < fieldTop, description + ": label (" + labelTop + ") is not above the field (" + fieldTop + ")");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:chips")
        WebElement chips;

        @FindBy(id = "form:chipsLabel")
        WebElement chipsLabel;

        @FindBy(id = "form:autoComplete")
        WebElement autoComplete;

        @FindBy(id = "form:autoCompleteLabel")
        WebElement autoCompleteLabel;

        @FindBy(id = "form:emptyChips")
        WebElement emptyChips;

        @FindBy(id = "form:emptyChipsLabel")
        WebElement emptyChipsLabel;

        @Override
        public String getLocation() {
            return "theming/theming007.xhtml";
        }
    }
}
