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

class Dialog006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dialog: #14287 refresh nested relocated overlays without deleting the new dialog")
    void refreshNestedOverlays(Page page) {
        page.refresh.click();
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg\"]').length"));
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:sidebar\"]').length"));
        assertEquals(1L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:sidebar\"] span[hidden]').length"));
        page.dialog.show();
        assertTrue(page.dialog.isVisible());
        page.dialog.hide();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Dialog: #14287 remove nested relocated overlays with their source container")
    void removeNestedOverlays(Page page) {
        page.remove.click();
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:dlg\"], [id=\"form:sidebar\"]').length"));
        assertEquals(Boolean.TRUE, PrimeSelenium.executeScript("return !PrimeFaces.widgets.nestedDialog && !PrimeFaces.widgets.relocatedSidebar"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Dialog: #14287 no marker when appendTo is already the parent")
    void unchangedParent(Page page) {
        assertEquals(0L, PrimeSelenium.<Long>executeScript("return document.querySelectorAll('[id=\"form:localContainer\"] > span[hidden]').length"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:dlg")
        Dialog dialog;
        @FindBy(id = "form:refresh")
        CommandButton refresh;
        @FindBy(id = "form:remove")
        CommandButton remove;

        @Override
        public String getLocation() {
            return "dialog/dialog006.xhtml";
        }
    }
}
