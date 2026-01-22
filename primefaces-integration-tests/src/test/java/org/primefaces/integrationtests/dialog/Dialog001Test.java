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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Dialog;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Dialog001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: Show widget method")
    void showWidget(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        assertFalse(dialog.isVisible());

        // Act
        dialog.show();

        // Assert
        assertDialog(page, true);
        assertEquals("Modal Dialog", dialog.getTitle());
    }

    @Test
    @Order(2)
    @DisplayName("Dialog: Hide widget method")
    void hideWidget(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        assertTrue(dialog.isVisible());

        // Act
        dialog.hide();

        // Assert
        assertDialog(page, false);
    }

    @Test
    @Order(3)
    @DisplayName("Dialog: Open Dialog with click")
    void basicOk(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        assertFalse(dialog.isVisible());

        // Act
        page.buttonShowDialog.click();

        // Assert
        assertDialog(page, true);
    }

    @Test
    @Order(4)
    @DisplayName("Dialog: edit values within dialog, OK, close-event")
    void close(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        assertFalse(dialog.isVisible());
        page.buttonShowDialog.click();
        page.inputText2Dialog.setValue("test123");

        // Act
        page.buttonDlgOk.click();

        // Assert
        assertDialog(page, false);
        assertTrue(page.messages.getMessage(0).getSummary().contains("Dialog - close-event"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("text2: test123"));
        assertEquals("test123", page.inputText2Readonly.getValue());

        // Act
        page.buttonSubmit.click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Submit"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("text2: test123"));
        assertEquals("test123", page.inputText2Readonly.getValue());
        assertConfiguration(page.dialog.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Dialog: edit values within dialog, Cancel, close event")
    void basicCancel(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        page.buttonShowDialog.click();
        assertDisplayed(dialog);
        page.inputText2Dialog.setValue("testabc");

        // Act
        page.buttonDlgCancel.click();

        // Assert
        assertDialog(page, false);
        assertTrue(page.messages.getMessage(0).getSummary().contains("Dialog - close-event"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("text2: null"));
        assertEquals("", page.inputText2Readonly.getValue());

        // Act
        page.buttonSubmit.click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Submit"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("text2: null"));
        assertEquals("", page.inputText2Readonly.getValue());
        assertConfiguration(page.dialog.getWidgetConfiguration());
    }

    private void assertDialog(Page page, boolean visible) {
        Dialog dialog = page.dialog;
        assertEquals(visible, dialog.isVisible());
        assertEquals(visible, dialog.isDisplayed());
        assertConfiguration(dialog.getWidgetConfiguration());

        if (visible) {
            try {
                // modal dialog should block clickability of button
                page.buttonSubmit.click();
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
            assertClickable(page.buttonSubmit);
        }
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

        @FindBy(id = "form:text1")
        InputText inputText1;

        @FindBy(id = "form:date")
        DatePicker datePicker;

        @FindBy(id = "form:text2Readonly")
        InputText inputText2Readonly;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:buttonSubmit")
        CommandButton buttonSubmit;

        @FindBy(id = "form:buttonShowDialog")
        CommandButton buttonShowDialog;

        @FindBy(id = "form:text2Dialog")
        InputText inputText2Dialog;

        @FindBy(id = "form:buttonDlgOk")
        CommandButton buttonDlgOk;

        @FindBy(id = "form:buttonDlgCancel")
        CommandButton buttonDlgCancel;

        @FindBy(id = "form:dlg")
        Dialog dialog;

        @Override
        public String getLocation() {
            return "dialog/dialog001.xhtml";
        }
    }
}
