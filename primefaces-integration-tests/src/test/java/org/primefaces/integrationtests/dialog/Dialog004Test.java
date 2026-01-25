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
package org.primefaces.integrationtests.dialog;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Dialog;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Dialog004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: #3210 Minimize reimplemented to mini")
    void minimize(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        assertTrue(dialog.isVisible());
        assertDisplayed(dialog.getMinimizeButton());
        assertNotPresent(page.minimizedClone);

        // Act
        dialog.toggleMinimize();

        // Assert
        assertPresent(page.minimizedClone);
        assertCss(page.minimizedClone, "ui-dialog-minimized");
        assertTrue(dialog.isVisible()); // visible is true even for minimized
        assertFalse(dialog.isDisplayed()); // dialog is in DOM but hidden
        assertConfiguration(dialog.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Dialog: #3210 Minimize reimplemented from mini")
    void minimizeRestore(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        assertTrue(dialog.isVisible());
        assertDisplayed(dialog.getMinimizeButton());
        dialog.toggleMinimize();
        assertFalse(dialog.isDisplayed()); // dialog is in DOM but hidden
        assertPresent(page.minimizedClone);

        // Act
        dialog.toggleMinimize(); // toggle minimize

        // Assert
        assertNotPresent(page.minimizedClone); // clone destroyed
        assertTrue(dialog.isVisible()); // visible is true even for minimized
        assertTrue(dialog.isDisplayed()); // dialog is in displayed
        assertConfiguration(dialog.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Dialog: Maximimize")
    void maximize(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        assertTrue(dialog.isVisible());
        assertDisplayed(dialog.getMaximizeButton());

        // Act
        dialog.toggleMaximize();

        // Assert
        assertCss(dialog, "ui-dialog-maximized");
        assertTrue(dialog.isVisible());
        assertTrue(dialog.isDisplayed());
        assertConfiguration(dialog.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Dialog Config = " + cfg);
        assertTrue(cfg.getBoolean("draggable"));
        assertTrue(cfg.getBoolean("cache"));
        assertTrue(cfg.getBoolean("resizable"));
        assertFalse(cfg.has("modal"));
        assertEquals("350", cfg.getString("width"));
        assertEquals("auto", cfg.getString("height"));
        assertEquals("center", cfg.getString("position"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:dlg")
        Dialog dialog;

        @FindBy(id = "form:dlg_clone")
        Dialog minimizedClone;

        @Override
        public String getLocation() {
            return "dialog/dialog004.xhtml";
        }
    }
}
