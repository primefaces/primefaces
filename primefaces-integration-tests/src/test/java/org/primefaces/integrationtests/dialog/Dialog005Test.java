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
import org.primefaces.selenium.component.Dialog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Dialog005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: #15384 boxStyle/boxStyleClass and contentStyle/contentStyleClass")
    void boxAndContentStyle(Page page) {
        // Arrange
        Dialog dialog = page.dialog;

        // Act
        dialog.show();

        // Assert
        assertTrue(dialog.isDisplayed());
        assertCss(dialog, "ui-dialog", "container-class");
        assertTrue(dialog.getAttribute("style").contains("opacity"));

        WebElement box = getBox(dialog);
        assertCss(box, "ui-dialog-box", "box-wide");
        assertTrue(box.getAttribute("style").contains("min-height"));
        assertEquals("600px", box.getCssValue("width")); // CSS width is not overridden by an inline width

        WebElement content = dialog.getContent();
        assertCss(content, "ui-dialog-content", "content-class");
        assertEquals("7px", content.getCssValue("padding-top"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Dialog: #15384 boxStyleClass width is kept after maximize restore")
    void boxStyleClassAfterMaximize(Page page) {
        // Arrange
        Dialog dialog = page.dialog;
        dialog.show();
        dialog.toggleMaximize();
        assertCss(dialog, "ui-dialog-maximized");

        // Act
        dialog.toggleMaximize();

        // Assert
        assertFalse(dialog.getAttribute("class").contains("ui-dialog-maximized"));
        assertEquals("600px", getBox(dialog).getCssValue("width"));
        assertNoJavascriptErrors();
    }

    private WebElement getBox(Dialog dialog) {
        return dialog.findElement(By.className("ui-dialog-box"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:dlg")
        Dialog dialog;

        @Override
        public String getLocation() {
            return "dialog/dialog005.xhtml";
        }
    }
}
