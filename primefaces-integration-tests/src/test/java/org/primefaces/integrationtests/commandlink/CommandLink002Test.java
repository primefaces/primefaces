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
package org.primefaces.integrationtests.commandlink;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandLink;
import org.primefaces.selenium.component.ConfirmDialog;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLink002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CommandLink: confirmation dialog is shown on click")
    void showConfirm(Page page) {
        // Arrange
        assertFalse(page.dialog.isVisible());

        // Act
        page.linkConfirm.click();

        // Assert
        assertTrue(page.dialog.isVisible());
        assertEquals("Are you sure you want to proceed?", page.dialog.getMessage().getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("CommandLink: pressing NO dismisses the dialog without firing the action")
    void confirmNo(Page page) {
        // Arrange
        page.linkConfirm.click();
        assertTrue(page.dialog.isVisible());

        // Act
        page.dialog.getNoButton().click();

        // Assert
        assertFalse(page.dialog.isVisible());
        assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("CommandLink: pressing YES fires the action via AJAX")
    void confirmYes(Page page) {
        // Arrange
        page.linkConfirm.click();
        assertTrue(page.dialog.isVisible());

        // Act
        PrimeSelenium.guardAjax(page.dialog.getYesButton()).click();

        // Assert
        assertFalse(page.dialog.isVisible());
        assertEquals("Confirmed", page.messages.getMessage(0).getSummary());
        assertEquals("You have accepted", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:linkConfirm")
        CommandLink linkConfirm;

        @FindBy(id = "form:confirmdialog")
        ConfirmDialog dialog;

        @FindBy(id = "form:messages")
        Messages messages;

        @Override
        public String getLocation() {
            return "commandlink/commandLink002.xhtml";
        }
    }

}
