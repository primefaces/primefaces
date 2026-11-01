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
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class Dialog006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: #3210 minimize hides the dialog and docks an own bar instead of a clone")
    void minimizeRestore(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();

        // Act
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();

        // Assert
        assertTrue(dialog.isVisible());
        assertFalse(dialog.isDisplayed());
        assertDisplayed(page.docked);
        assertEquals("Minimizable", page.docked.getText());
        assertEquals(List.of("minimize"), page.messages.getAllSummaries());
        assertEquals(1L, countElements("[id='form:dlg']"));
        assertEquals(0L, countElements(".p-dialog-docking-zone .p-dialog"));

        // Act
        PrimeSelenium.guardAjax(page.dockedRestore).click();

        // Assert
        assertNotPresent(page.docked);
        assertTrue(dialog.isDisplayed());
        assertEquals(List.of("restoreMinimize"), page.messages.getAllSummaries());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Dialog: maximize and close from the docked bar")
    void dockedMaximizeAndClose(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();

        // Act
        PrimeSelenium.guardAjax(page.dockedMaximize).click();

        // Assert
        assertNotPresent(page.docked);
        assertCss(dialog, "p-dialog-maximized");

        // Act
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();
        PrimeSelenium.guardAjax(page.dockedClose).click();

        // Assert
        assertNotPresent(page.docked);
        assertFalse(dialog.isVisible());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Dialog: can still be dragged and resized after minimize and restore")
    void dragAndResizeAfterRestore(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();
        PrimeSelenium.guardAjax(page.dockedRestore).click();
        Point location = page.dialogBox.getLocation();
        Dimension size = page.dialogBox.getSize();

        // Act: drag the title bar to the left and up
        new Actions(getWebDriver())
                    .clickAndHold(page.dialogTitleBar)
                    .moveByOffset(-5, -5)
                    .moveByOffset(-50, -40)
                    .release()
                    .perform();

        // Assert
        Point moved = page.dialogBox.getLocation();
        assertTrue(Math.abs(moved.getX() - (location.getX() - 55)) <= 2, location + " -> " + moved);
        assertTrue(Math.abs(moved.getY() - (location.getY() - 45)) <= 2, location + " -> " + moved);

        // Act: resize with the south east handle
        new Actions(getWebDriver())
                    .clickAndHold(page.dialogResizeHandle)
                    .moveByOffset(5, 5)
                    .moveByOffset(30, 20)
                    .release()
                    .perform();

        // Assert
        Dimension resized = page.dialogBox.getSize();
        assertTrue(Math.abs(resized.getWidth() - (size.getWidth() + 35)) <= 2, size + " -> " + resized);
        assertTrue(resized.getHeight() > size.getHeight(), size + " -> " + resized);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Dialog: a relocated child overlay survives minimize and restore")
    void childOverlayAfterRestore(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        page.menu.show();
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();
        PrimeSelenium.guardAjax(page.dockedRestore).click();

        // Act
        page.menu.select("Beta");

        // Assert
        assertEquals("Beta", page.menu.getSelectedLabel());
        assertEquals(1L, countElements("body > [id='form:menu_panel']"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Dialog: AJAX update of a minimized dialog leaves nothing in the docking zone")
    void updateMinimized(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();
        assertDisplayed(page.docked);

        // Act
        page.btnUpdate.click();

        // Assert
        assertNotPresent(page.docked);
        assertEquals(0L, countElements(".p-dialog-docking-zone > *"));
        assertFalse(dialog.isVisible());

        // Act
        dialog.show();

        // Assert
        assertTrue(dialog.isDisplayed());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("Dialog: removing a minimized dialog leaves nothing in the docking zone")
    void destroyMinimized(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        PrimeSelenium.guardAjax(dialog.getMinimizeButton()).click();
        assertDisplayed(page.docked);

        // Act
        page.btnRemove.click();

        // Assert
        assertNotPresent(page.docked);
        assertEquals(0L, countElements(".p-dialog-docking-zone > *"));
        assertEquals(0L, countElements("[id='form:dlg']"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("Dialog: minimize and showing the minimized dialog again are animated")
    void animated(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();

        // Act
        PrimeSelenium.executeScript("PrimeFaces.utils.enableAnimations(); " + dialog.getWidgetByIdScript() + ".toggleMinimize();");

        // Assert: from the dialog to the docking zone
        assertEquals(1L, countElements(".p-effect-transfer.p-dialog-minimizing"));
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.docked));
        assertFalse(dialog.isDisplayed());

        // Act
        PrimeSelenium.executeScript("PrimeFaces.utils.enableAnimations(); " + dialog.getWidgetByIdScript() + ".show();");

        // Assert: from the docked bar to the dialog, which appears when the animation finished
        assertEquals(1L, countElements(".p-effect-transfer.p-dialog-minimizing"));
        assertFalse(dialog.isDisplayed());
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.dialogBox));
        assertNotPresent(page.docked);
        assertEquals(0L, countElements(".p-effect-transfer"));
        assertNoJavascriptErrors();
    }

    private long countElements(String selector) {
        return PrimeSelenium.executeScript("return document.querySelectorAll(\"" + selector + "\").length;");
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:dlg")
        Dialog dialog;

        /** The dialog box, the element with the client id is a container covering the viewport. */
        @FindBy(css = "[id='form:dlg'] > .p-dialog-box")
        WebElement dialogBox;

        @FindBy(css = "[id='form:dlg'] .p-dialog-titlebar")
        WebElement dialogTitleBar;

        @FindBy(css = "[id='form:dlg'] .p-resizable-se")
        WebElement dialogResizeHandle;

        @FindBy(id = "form:dlg_docked")
        WebElement docked;

        @FindBy(css = "[id='form:dlg_docked'] .p-dialog-docked-restore")
        WebElement dockedRestore;

        @FindBy(css = "[id='form:dlg_docked'] .p-dialog-docked-maximize")
        WebElement dockedMaximize;

        @FindBy(css = "[id='form:dlg_docked'] .p-dialog-docked-close")
        WebElement dockedClose;

        @FindBy(id = "form:menu")
        SelectOneMenu menu;

        @FindBy(id = "form:btnUpdate")
        CommandButton btnUpdate;

        @FindBy(id = "form:btnRemove")
        CommandButton btnRemove;

        @Override
        public String getLocation() {
            return "dialog/dialog006.xhtml";
        }
    }
}
