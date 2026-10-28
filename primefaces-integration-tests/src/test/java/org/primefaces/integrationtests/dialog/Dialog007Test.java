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
import org.primefaces.selenium.component.SelectOneMenu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Dialog007Test extends AbstractPrimePageTest {

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
        assertSingleDialogAndMarker();
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
        assertSingleDialogAndMarker();
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
    @Order(4)
    @DisplayName("Dialog: #14287 remove minimized clone with original container")
    void removeMinimized(Page page) {
        page.dialog.show();
        page.dialog.toggleMinimize();
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg_clone\"]').length"));
        page.remove.click();
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg_clone\"]').length"));
        assertEquals(Boolean.TRUE, PrimeSelenium.executeScript("return !PrimeFaces.widgets.relocatedDialog"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Dialog: #14287 marker cleanup does not trigger ancestor removal")
    void markerRemovalDoesNotBubble(Page page) {
        PrimeSelenium.executeScript("window.containerRemovals = 0;"
                + "$(document.getElementById('form:container')).on('remove.test', () => window.containerRemovals++);");
        PrimeSelenium.executeScript("PF('relocatedDialog').destroy();");
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return window.containerRemovals"));
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:container\"] > span[hidden]').length"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("Dialog: #14287 refresh minimized dialog without leaving its old clone")
    void refreshMinimized(Page page) {
        page.dialog.show();
        page.dialog.toggleMinimize();
        page.refreshDialog.click();
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg_clone\"]').length"));
        assertSingleDialogAndMarker();
        page.dialog.show();
        assertTrue(page.dialog.isVisible());
        page.dialog.hide();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("Dialog: minimizing and restoring preserves relocated child overlays")
    void minimizeWithChildOverlay(Page page) {
        // Arrange
        page.dialog.show();
        page.choice.select("Two");

        // Act
        page.dialog.toggleMinimize();
        PrimeSelenium.waitGui().until(driver -> Boolean.TRUE.equals(PrimeSelenium.executeScript("return PF('relocatedDialog').minimized")));
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:choice_panel\"]').length"));
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg_clone\"] .p-dialog-content').length"));
        page.dialog.toggleMinimize();
        PrimeSelenium.waitGui().until(driver -> Boolean.FALSE.equals(PrimeSelenium.executeScript("return PF('relocatedDialog').minimized")));

        // Assert
        page.choice.select("One");
        assertEquals("One", page.choice.getSelectedLabel());
        page.dialog.hide();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("Dialog: disabled dynamic overlay refresh removes its previous source marker")
    void disabledOverlayRefresh(Page page) {
        // Arrange
        assertSingleDialogAndMarker();

        // Act: exercise the disabled DynamicOverlayWidget refresh path.
        PrimeSelenium.executeScript("const widget = PF('relocatedDialog'); widget.refresh({...widget.cfg, disabled: true});");

        // Assert
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:container\"] > span[hidden]').length"));
        assertNoJavascriptErrors();
    }

    private void assertSingleDialogAndMarker() {
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg\"]').length"));
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:container\"] > span[hidden]').length"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:dlg")
        Dialog dialog;
        @FindBy(id = "form:choice")
        SelectOneMenu choice;
        @FindBy(id = "form:remove")
        CommandButton remove;
        @FindBy(id = "form:refresh")
        CommandButton refresh;
        @FindBy(id = "form:refreshDialog")
        CommandButton refreshDialog;

        @Override
        public String getLocation() {
            return "dialog/dialog007.xhtml";
        }
    }
}
