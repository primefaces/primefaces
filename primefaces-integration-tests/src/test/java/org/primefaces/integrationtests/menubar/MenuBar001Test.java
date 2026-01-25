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
package org.primefaces.integrationtests.menubar;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Menubar;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuBar001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("MenuBar: basic (click)")
    void basic(Page page) {
        // Arrange
        Menubar menubar = page.menubar;

        // Act
        WebElement eltMenuMainC = menubar.findElement(By.id("form:mainC"));
        PrimeSelenium.guardAjax(eltMenuMainC).click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Main C"));

        // Act
        WebElement eltMenuMainA = menubar.findElement(By.id("form:mainA"));
        eltMenuMainA.click();
        WebElement eltMenuSubA2 = eltMenuMainA.findElement(By.id("form:subA2"));
        PrimeSelenium.guardAjax(eltMenuSubA2).click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Sub A-2"));

        assertConfiguration(menubar.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("MenuBar: basic (click, selection via value)")
    void basicByValue(Page page) {
        // Arrange
        Menubar menubar = page.menubar;

        // Act
        menubar.selectMenuitemByValue("Main C");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Main C"));

        // Act
        WebElement eltMenuMainA = menubar.selectMenuitemByValue("Main A");
        menubar.selectMenuitemByValue(eltMenuMainA, "Sub A-2");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Sub A-2"));

        assertConfiguration(menubar.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("MenuBar Config = " + cfg);
        assertTrue(cfg.has("toggleEvent")); // click or hover
        assertEquals("click", cfg.getString("toggleEvent"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:menubar")
        Menubar menubar;

        @Override
        public String getLocation() {
            return "menubar/menuBar001.xhtml";
        }
    }
}
