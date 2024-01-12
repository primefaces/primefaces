/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.ConfirmPopup;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

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
        assertEquals("ui-confirm-popup-icon pi pi-exclamation-triangle", popup.getIcon().getAttribute("class"));
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

        // Act
        popup.getNoButton().click();

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

        // Act
        PrimeSelenium.guardAjax(popup.getYesButton()).click();

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

        // Act
        popup.getNoButton().click();

        // Assert
        assertFalse(popup.isVisible());
        assertTrue(page.messages.isEmpty());
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

        // Act
        PrimeSelenium.guardAjax(popup.getYesButton()).click();

        // Assert
        assertFalse(popup.isVisible());
        assertMessage(page, "Record deleted");
        assertConfiguration(popup.getWidgetConfiguration());
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

        @Override
        public String getLocation() {
            return "confirmpopup/confirmPopup001.xhtml";
        }
    }
}
