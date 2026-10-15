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
package org.primefaces.integrationtests.dialog;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Dialog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Dialog005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: #14287 remove relocated dialog with its original container")
    void remove(Page page) {
        // Arrange
        assertEquals("@(body)", page.dialog.getWidgetConfiguration().getString("appendTo"));
        page.dialog.show();
        assertTrue(page.dialog.isVisible());
        page.dialog.hide();

        // Act
        page.remove.click();

        // Assert
        assertNotPresent(page.dialog);
        assertEquals(Boolean.TRUE, PrimeSelenium.executeScript("return !PrimeFaces.widgets.relocatedDialog"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Dialog: #14287 refresh container and then remove relocated dialog")
    void refreshContainer(Page page) {
        // Arrange
        page.refresh.click();
        page.dialog.show();
        assertTrue(page.dialog.isVisible());
        page.dialog.hide();

        // Act
        page.remove.click();

        // Assert
        assertNotPresent(page.dialog);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Dialog: #14287 direct update preserves original container tracking")
    void refreshDialog(Page page) {
        // Arrange
        page.refreshDialog.click();
        page.dialog.show();
        assertTrue(page.dialog.isVisible());
        page.dialog.hide();

        // Act
        page.remove.click();

        // Assert
        assertNotPresent(page.dialog);
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:dlg")
        Dialog dialog;
        @FindBy(id = "form:remove")
        CommandButton remove;
        @FindBy(id = "form:refresh")
        CommandButton refresh;
        @FindBy(id = "form:refreshDialog")
        CommandButton refreshDialog;

        @Override
        public String getLocation() {
            return "dialog/dialog005.xhtml";
        }
    }
}
