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
package org.primefaces.integrationtests.sidebar;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Button;
import org.primefaces.selenium.component.Sidebar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Sidebar001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Sidebar: show")
    void show(Page page) throws InterruptedException {

        assertTrue(getWebDriver().getPageSource().contains("sidebar1 content"));
        assertFalse(page.sidebar1.isVisible());

        // Act
        page.sidebar1.show();

        // Assert
        assertTrue(getWebDriver().getPageSource().contains("sidebar1 content"));
        assertTrue(page.sidebar1.isVisible());

        assertDoesNotThrow(() -> {
            page.sidebar2.findElement(By.className("ui-sidebar-close")).isDisplayed();
        });
    }

    @Test
    @Order(2)
    @DisplayName("Sidebar: show dynamic")
    void dynamicShow(Page page) {

        assertFalse(getWebDriver().getPageSource().contains("sidebar2 content"));
        assertFalse(page.sidebar2.isVisible());

        // Act
        page.sidebar2.show();

        // Assert
        assertTrue(getWebDriver().getPageSource().contains("sidebar2 content"));
        assertTrue(page.sidebar2.isVisible());

        assertDoesNotThrow(() -> {
            page.sidebar2.findElement(By.className("ui-sidebar-close")).isDisplayed();
        });
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:sidebar1")
        Sidebar sidebar1;

        @FindBy(id = "form:button1")
        Button button1;

        @FindBy(id = "form:sidebar2")
        Sidebar sidebar2;

        @FindBy(id = "form:button2")
        Button button2;

        @Override
        public String getLocation() {
            return "sidebar/sidebar001.xhtml";
        }
    }
}
