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
package org.primefaces.integrationtests.confirmpopup;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.ConfirmPopup;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfirmPopup001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ConfirmPopup: Show the popup")
    void showWidget(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        assertFalse(popup.isVisible());

        // Act
        page.confirm.click();

        // Assert
        assertTrue(popup.isVisible());
        assertEquals("Are you sure you want to proceed?", popup.getMessage().getText());
        assertEquals("p-confirm-popup-icon pi pi-exclamation-triangle", popup.getIcon().getAttribute("class"));
        assertConfiguration(popup.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("ConfirmPopup: Show widget method")
    void hideWidget(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        page.confirm.click();
        assertTrue(popup.isVisible());

        // Act
        popup.hidePopup();

        // Assert
        assertFalse(popup.isVisible());
        assertConfiguration(popup.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("ConfirmPopup: Confirm button pressing NO")
    void confirmNo(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        assertFalse(popup.isVisible());
        page.confirm.click();
        CommandButton noButton = popup.getNoButton();
        assertEquals("No", noButton.getText());
        assertCss(noButton,
                "p-button p-widget p-state-default p-button-text-icon-left p-confirm-popup-no p-button-flat");
        assertCss(noButton.findElement(By.className("p-button-icon-left")), "p-button-icon-left p-c pi pi-times");

        // Act
        noButton.click();

        // Assert
        assertFalse(popup.isVisible());
        assertTrue(page.messages.isEmpty());
        assertConfiguration(popup.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("ConfirmPopup: Confirm button pressing YES")
    void confirmYes(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        assertFalse(popup.isVisible());
        page.confirm.click();
        CommandButton yesButton = popup.getYesButton();
        assertEquals("Yes", yesButton.getText());
        assertCss(yesButton, "p-button p-widget p-state-default p-button-text-icon-left p-confirm-popup-yes p-state-focus");
        assertCss(yesButton.findElement(By.className("p-button-icon-left")), "p-button-icon-left p-c pi pi-check");

        // Act
        PrimeSelenium.guardAjax(yesButton).click();

        // Assert
        assertFalse(popup.isVisible());
        assertMessage(page, "You have accepted");
        assertConfiguration(popup.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("ConfirmPopup: Delete button pressing NO")
    void deleteNo(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        assertFalse(popup.isVisible());
        page.delete.click();
        CommandButton noButton = popup.getNoButton();
        assertEquals("Keep this!", noButton.getText());
        assertCss(noButton,
                "p-button p-widget p-state-default p-button-text-icon-left p-confirm-popup-no p-button-flat bg-green-600 text-white");
        assertCss(noButton.findElement(By.className("p-button-icon-left")), "p-button-icon-left p-c pi pi-heart");

        // Act
        noButton.click();

        // Assert
        assertFalse(popup.isVisible());
        assertTrue(page.messages.isEmpty());
        assertConfiguration(popup.getWidgetConfiguration());

        // assert the buttons are back to normal
        confirmNo(page);
    }

    @Test
    @Order(6)
    @DisplayName("ConfirmPopup: Default icon to the one set on the global")
    void defaultIcon(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        assertFalse(popup.isVisible());


        // Act
        page.question.click();

        // Assert
        assertTrue(popup.isVisible());
        assertTrue(page.messages.isEmpty());
        assertCss(popup.getIcon(), "p-confirm-popup-icon pi pi-question-circle");
        assertConfiguration(popup.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("ConfirmPopup: Delete button pressing YES")
    void deleteYes(Page page) {
        // Arrange
        ConfirmPopup popup = page.popup;
        assertFalse(popup.isVisible());
        page.delete.click();
        CommandButton yesButton = popup.getYesButton();
        assertEquals("Delete Me!", yesButton.getText());
        assertCss(yesButton,
                "p-button p-widget p-state-default p-button-text-icon-left p-confirm-popup-yes bg-red-600 text-white p-state-focus");
        assertCss(yesButton.findElement(By.className("p-button-icon-left")), "p-button-icon-left p-c pi pi-trash");

        // Act
        PrimeSelenium.guardAjax(yesButton).click();

        // Assert
        assertFalse(popup.isVisible());
        assertMessage(page, "Record deleted");
        assertConfiguration(popup.getWidgetConfiguration());
        // assert the buttons are back to normal
        confirmYes(page);
    }

    private void assertMessage(Page page, String message) {
        Messages messages = page.messages;
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(messages));
        Msg msg = messages.getMessage(0);
        assertEquals(message, msg.getDetail());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ConfirmPopup Config = " + cfg);
        assertTrue(cfg.getBoolean("global"));
        assertTrue(cfg.getBoolean("dismissable"));
        assertEquals("@(body)", cfg.getString("appendTo"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:confirmpopup")
        ConfirmPopup popup;

        @FindBy(id = "form:message")
        Messages messages;

        @FindBy(id = "form:confirm")
        CommandButton confirm;

        @FindBy(id = "form:delete")
        CommandButton delete;

        @FindBy(id = "form:question")
        CommandButton question;

        @Override
        public String getLocation() {
            return "confirmpopup/confirmPopup001.xhtml";
        }
    }
}
