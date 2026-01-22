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
package org.primefaces.integrationtests.confirmdialog;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.ConfirmDialog;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ConfirmDialog001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ConfirmDialog: Button Show the dialog")
    void showDialog(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        assertFalse(dialog.isVisible());

        // Act
        page.confirm.click();

        // Assert
        assertDialog(page, true);
        assertEquals("Are you sure you want to proceed?", dialog.getMessage().getText());
        assertEquals("ui-icon ui-confirm-dialog-severity pi pi-exclamation-triangle", dialog.getIcon().getDomAttribute("class"));
    }

    @Test
    @Order(2)
    @DisplayName("ConfirmDialog: Button Show widget method")
    void showWidget(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;

        // Act
        dialog.show();

        // Assert
        assertDialog(page, true);
    }

    @Test
    @Order(3)
    @DisplayName("ConfirmDialog: Button Hide widget method")
    void hideWidget(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        dialog.show();
        assertTrue(dialog.isVisible());

        // Act
        dialog.hide();

        // Assert
        assertDialog(page, false);
    }

    @Test
    @Order(4)
    @DisplayName("ConfirmDialog: Button Confirm button pressing NO")
    void confirmNo(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        assertFalse(dialog.isVisible());
        page.confirm.click();
        CommandButton noButton = dialog.getNoButton();
        assertEquals("No", noButton.getText());
        assertCss(noButton, "ui-button ui-widget ui-state-default ui-button-text-icon-left ui-confirmdialog-no ui-button-flat");
        assertCss(noButton.findElement(By.className("ui-icon")), "ui-button-icon-left ui-icon ui-c pi pi-times");

        // Act
        noButton.click();

        // Assert
        assertTrue(page.message.isEmpty());
        assertDialog(page, false);
    }

    @Test
    @Order(5)
    @DisplayName("ConfirmDialog: Button Confirm button pressing YES")
    void confirmYes(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        page.confirm.click();
        assertTrue(dialog.isVisible());
        CommandButton yesButton = dialog.getYesButton();
        assertEquals("Yes", yesButton.getText());
        assertCss(yesButton, "ui-button ui-widget ui-state-default ui-button-text-icon-left ui-confirmdialog-yes");
        assertCss(yesButton.findElement(By.className("ui-icon")), "ui-button-icon-left ui-icon ui-c pi pi-check");

        // Act
        PrimeSelenium.guardAjax(yesButton).click();

        // Assert
        assertEquals("You have accepted", page.message.getMessage(0).getDetail());
        assertDialog(page, false);
    }

    @Test
    @Order(6)
    @DisplayName("ConfirmDialog: Button Delete button pressing NO")
    void deleteNo(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        assertFalse(dialog.isVisible());
        page.delete.click();
        CommandButton noButton = dialog.getNoButton();
        assertEquals("Keep this!", noButton.getText());
        assertCss(noButton,
                "ui-button ui-widget ui-state-default ui-button-text-icon-left ui-confirmdialog-no ui-button-flat bg-green-600 text-white");
        assertCss(noButton.findElement(By.className("ui-icon")), "ui-button-icon-left ui-icon ui-c pi pi-heart");

        // Act
        dialog.getNoButton().click();

        // Assert
        assertTrue(page.message.isEmpty());
        assertDialog(page, false);

        // assert the buttons are back to normal
        confirmNo(page);
    }

    @Test
    @Order(7)
    @DisplayName("ConfirmDialog: Button Delete button pressing YES")
    void deleteYes(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        assertFalse(dialog.isVisible());
        page.delete.click();
        CommandButton yesButton = dialog.getYesButton();
        assertEquals("Delete Me!", yesButton.getText());
        assertCss(yesButton, "ui-button ui-widget ui-state-default ui-button-text-icon-left ui-confirmdialog-yes bg-red-600 text-white");
        assertCss(yesButton.findElement(By.className("ui-icon")), "ui-button-icon-left ui-icon ui-c pi pi-trash");

        // Act
        PrimeSelenium.guardAjax(yesButton).click();

        // Assert
        assertEquals("Record deleted", page.message.getMessage(0).getDetail());
        assertDialog(page, false);
        // assert the buttons are back to normal
        confirmYes(page);
    }

    @Test
    @Order(7)
    @DisplayName("ConfirmDialog: Button Non AJAX button pressing NO")
    void nonAjaxNo(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        assertFalse(dialog.isVisible());
        page.nonAjax.click();

        // Act
        dialog.getNoButton().click();

        // Assert
        assertTrue(page.message.isEmpty());
        assertDialog(page, false);
    }

    @Test
    @Order(8)
    @DisplayName("ConfirmDialog: Button Non AJAX button pressing YES")
    void nonAjaxYes(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        assertFalse(dialog.isVisible());
        page.nonAjax.click();

        // Act
        dialog.getYesButton().click();

        // Assert
        assertEquals("Full page submitted", page.message.getMessage(0).getDetail());
        assertDialog(page, false);
    }

    private void assertDialog(Page page, boolean visible) {
        ConfirmDialog dialog = page.dialog;
        assertEquals(visible, dialog.isVisible());

        if (visible) {
            try {
                // modal dialog should block clickability of button
                page.confirm.click();
                fail("Button should not be clickable because modal mask is covering it!");
            }
            catch (ElementClickInterceptedException ex) {
                // element should be blocked by modal mask!
            }
            catch (WebDriverException ex) {
                // Safari: element should be blocked by modal mask!
            }
        }
        else {
            assertClickableOrLoading(page.confirm);
            assertClickableOrLoading(page.delete);
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(dialog));
        }

        assertConfiguration(dialog.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ConfirmDialog Config = " + cfg);
        assertTrue(cfg.getBoolean("global"));
        assertTrue(cfg.getBoolean("cache"));
        assertTrue(cfg.getBoolean("responsive"));
        assertTrue(cfg.getBoolean("modal"));
        assertEquals("auto", cfg.getString("height"));
        assertEquals("center", cfg.getString("position"));
        assertEquals("fade", cfg.getString("showEffect"));
        assertEquals("fade", cfg.getString("hideEffect"));
        assertEquals("@(body)", cfg.getString("appendTo"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:confirmdialog")
        ConfirmDialog dialog;

        @FindBy(id = "form:message")
        Messages message;

        @FindBy(id = "form:confirm")
        CommandButton confirm;

        @FindBy(id = "form:delete")
        CommandButton delete;

        @FindBy(id = "form:nonAjax")
        CommandButton nonAjax;

        @Override
        public String getLocation() {
            return "confirmdialog/confirmDialog001.xhtml";
        }
    }
}
