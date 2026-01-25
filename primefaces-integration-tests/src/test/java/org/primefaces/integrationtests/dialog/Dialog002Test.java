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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Dialog;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Dialog002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: Close button should receive focus if no other focusable elements")
    void showWidget(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        assertTrue(dialog.isVisible());
        PrimeSelenium.wait(400);

        // Act (ENTER key should close the dialog because the Close button is focused)
        Actions actions = new Actions(page.getWebDriver());
        Action enterKey = actions.sendKeys(Keys.ENTER).build();
        PrimeSelenium.guardAjax(enterKey).perform();

        // Assert
        assertDialog(page, false);
    }

    private void assertDialog(Page page, boolean visible) {
        Dialog dialog = page.dialog;
        assertEquals(visible, dialog.isVisible());
        assertEquals(visible, dialog.isDisplayed());
        assertConfiguration(dialog.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Dialog Config = " + cfg);
        assertTrue(cfg.getBoolean("draggable"));
        assertTrue(cfg.getBoolean("cache"));
        assertTrue(cfg.getBoolean("resizable"));
        assertTrue(cfg.getBoolean("modal"));
        assertEquals("auto", cfg.getString("width"));
        assertEquals("100", cfg.getString("height"));
        assertEquals("center", cfg.getString("position"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:buttonShowDialog")
        CommandButton buttonShowDialog;


        @FindBy(id = "form:dlg")
        Dialog dialog;

        @Override
        public String getLocation() {
            return "dialog/dialog002.xhtml";
        }
    }
}
