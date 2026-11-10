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
package org.primefaces.integrationtests.inplace;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Inplace;
import org.primefaces.selenium.component.InputText;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Inplace001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Inplace: click on the display shows the editor, save updates the value")
    void save(Page page) {
        // Arrange
        Inplace inplace = page.editor;
        assertEquals("PrimeFaces", inplace.getDisplayText());
        assertFalse(inplace.isContentVisible());

        // Act
        inplace.show();

        // Assert
        assertTrue(inplace.isContentVisible());
        assertFalse(inplace.getDisplay().isDisplayed());

        // Act
        page.name.clear();
        page.name.type("Faces");
        inplace.save();

        // Assert
        assertFalse(inplace.isContentVisible());
        assertEquals("Faces", inplace.getDisplayText());
        assertConfiguration(inplace.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Inplace: cancel discards the changes")
    void cancel(Page page) {
        // Arrange
        Inplace inplace = page.editor;
        inplace.show();
        page.name.clear();
        page.name.type("Changed");

        // Act
        inplace.cancel();

        // Assert
        assertFalse(inplace.isContentVisible());
        assertEquals("PrimeFaces", inplace.getDisplayText());
        assertConfiguration(inplace.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Inplace: show and hide via widget API")
    void showHide(Page page) {
        // Arrange
        Inplace inplace = page.editor;

        // Act
        inplace.show();
        inplace.hide();

        // Assert
        assertFalse(inplace.isContentVisible());
        assertTrue(inplace.getDisplay().isDisplayed());
        assertConfiguration(inplace.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Inplace: disabled inplace does not show its content")
    void disabled(Page page) {
        // Arrange
        Inplace inplace = page.disabled;

        // Act
        inplace.getDisplay().click();

        // Assert
        assertTrue(inplace.isDisabled());
        assertEquals("Disabled", inplace.getDisplayText());
        assertTrue(inplace.getDisplay().isDisplayed());
        assertTrue(PrimeSelenium.hasCssClass(inplace.getDisplay(), "p-inplace-display-disabled"));
        assertFalse(inplace.isContentVisible());
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.getBoolean("editor"));
        assertEquals("click", cfg.getString("event"));
        assertEquals("fade", cfg.getString("effect"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:editor")
        Inplace editor;

        @FindBy(id = "form:name")
        InputText name;

        @FindBy(id = "form:disabled")
        Inplace disabled;

        @Override
        public String getLocation() {
            return "inplace/inplace001.xhtml";
        }
    }
}
