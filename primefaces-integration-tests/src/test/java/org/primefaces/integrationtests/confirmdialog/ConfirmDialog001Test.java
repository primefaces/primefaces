/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.ConfirmDialog;
import org.primefaces.selenium.component.Messages;

public class ConfirmDialog001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ConfirmDialog: Show the dialog")
    public void testShowDialog(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        Assertions.assertFalse(dialog.isVisible());

        // Act
        page.confirm.click();

        // Assert
        assertDialog(page, true);
        Assertions.assertEquals("Are you sure you want to proceed?", dialog.getMessage().getText());
        Assertions.assertEquals("ui-icon ui-confirm-dialog-severity pi pi-exclamation-triangle", dialog.getIcon().getAttribute("class"));
    }

    @Test
    @Order(2)
    @DisplayName("ConfirmDialog: Show widget method")
    public void testShowWidget(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;

        // Act
        dialog.show();

        // Assert
        assertDialog(page, true);
    }

    @Test
    @Order(3)
    @DisplayName("ConfirmDialog: Hide widget method")
    public void testHideWidget(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        dialog.show();
        Assertions.assertTrue(dialog.isVisible());

        // Act
        dialog.hide();

        // Assert
        assertDialog(page, false);
    }

    @Test
    @Order(4)
    @DisplayName("ConfirmDialog: Confirm button pressing NO")
    public void testConfirmNo(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        Assertions.assertFalse(dialog.isVisible());
        page.confirm.click();

        // Act
        dialog.getNoButton().click();

        // Assert
        Assertions.assertTrue(page.message.isEmpty());
        assertDialog(page, false);
    }

    @Test
    @Order(5)
    @DisplayName("ConfirmDialog: Confirm button pressing YES")
    public void testConfirmYes(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        page.confirm.click();
        Assertions.assertTrue(dialog.isVisible());

        // Act
        PrimeSelenium.guardAjax(dialog.getYesButton()).click();

        // Assert
        Assertions.assertEquals("You have accepted", page.message.getMessage(0).getDetail());
        assertDialog(page, false);
    }

    @Test
    @Order(6)
    @DisplayName("ConfirmDialog: Delete button pressing NO")
    public void testDeleteNo(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        Assertions.assertFalse(dialog.isVisible());
        page.delete.click();

        // Act
        dialog.getNoButton().click();

        // Assert
        Assertions.assertTrue(page.message.isEmpty());
        assertDialog(page, false);
    }

    @Test
    @Order(7)
    @DisplayName("ConfirmDialog: Delete button pressing YES")
    public void testDeleteYes(Page page) {
        // Arrange
        ConfirmDialog dialog = page.dialog;
        Assertions.assertFalse(dialog.isVisible());
        page.delete.click();

        // Act
        PrimeSelenium.guardAjax(dialog.getYesButton()).click();

        // Assert
        Assertions.assertEquals("Record deleted", page.message.getMessage(0).getDetail());
        assertDialog(page, false);
    }

    private void assertDialog(Page page, boolean visible) {
        ConfirmDialog dialog = page.dialog;
        Assertions.assertEquals(visible, dialog.isVisible());

        if (visible) {
            try {
                // modal dialog should block clickability of button
                page.confirm.click();
                Assertions.fail("Button should not be clickable because modal mask is covering it!");
            } catch (ElementClickInterceptedException ex) {
                // element should be blocked by modal mask!
            } catch (WebDriverException ex) {
                // Safari: element should be blocked by modal mask!
            }
        }
        else {
            assertClickable(page.confirm);
            assertClickable(page.delete);
        }

        assertConfiguration(dialog.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ConfirmDialog Config = " + cfg);
        Assertions.assertTrue(cfg.getBoolean("global"));
        Assertions.assertTrue(cfg.getBoolean("cache"));
        Assertions.assertTrue(cfg.getBoolean("responsive"));
        Assertions.assertTrue(cfg.getBoolean("modal"));
        Assertions.assertEquals("auto", cfg.getString("height"));
        Assertions.assertEquals("center", cfg.getString("position"));
        Assertions.assertEquals("fade", cfg.getString("showEffect"));
        Assertions.assertEquals("fade", cfg.getString("hideEffect"));
        Assertions.assertEquals("@(body)", cfg.getString("appendTo"));
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

        @Override
        public String getLocation() {
            return "confirmdialog/confirmDialog001.xhtml";
        }
    }
}
