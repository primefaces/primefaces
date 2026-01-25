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
package org.primefaces.integrationtests.splitbutton;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.SplitButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SplitButton001Test extends AbstractPrimePageTest {

    private static final String CLASS_NAME_MENUBUTTON = "ui-splitbutton-menubutton";

    @Test
    @Order(1)
    @DisplayName("SplitButton: dropdown visibility #2690 #10086")
    void basic(Page page) {
        assertNotNull(page.splitButtonChildren.findElement(By.className(CLASS_NAME_MENUBUTTON)));
        assertThrows(NoSuchElementException.class,
                () -> page.splitButtonNoChildren.findElement(By.className(CLASS_NAME_MENUBUTTON)));
        assertThrows(NoSuchElementException.class,
                () -> page.splitButtonNoRenderedChildren.findElement(By.className(CLASS_NAME_MENUBUTTON)));
        assertNotNull(page.splitButtonOverlayPanel.findElement(By.className(CLASS_NAME_MENUBUTTON)));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:children")
        SplitButton splitButtonChildren;

        @FindBy(id = "form:noChildren")
        SplitButton splitButtonNoChildren;

        @FindBy(id = "form:noRenderedChildren")
        SplitButton splitButtonNoRenderedChildren;

        @FindBy(id = "form:overlayPanel")
        SplitButton splitButtonOverlayPanel;

        @Override
        public String getLocation() {
            return "splitbutton/splitbutton001.xhtml";
        }
    }
}
