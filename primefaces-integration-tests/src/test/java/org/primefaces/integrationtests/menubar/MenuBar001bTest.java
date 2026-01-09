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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuBar001bTest extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("MenuBar: basic (hover)")
    void basic(Page page) {
        // Arrange
        Menubar menubar = page.menubar;

        // Act
        WebElement eltMenuMainC = menubar.findElement(By.id("form:mainC"));
        PrimeSelenium.guardAjax(eltMenuMainC).click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Main C"));

        // Act
        Actions actions = new Actions(page.getWebDriver());
        WebElement eltMenuMainA = menubar.findElement(By.id("form:mainA"));
        WebElement eltMenuSubA2 = eltMenuMainA.findElement(By.id("form:subA2"));
        actions.moveToElement(eltMenuMainA).build().perform();
        PrimeSelenium.guardAjax(eltMenuSubA2).click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Sub A-2"));

        // Act
        actions = new Actions(page.getWebDriver());
        WebElement eltMenuSubA1 = eltMenuMainA.findElement(By.id("form:subA1"));
        WebElement eltMenuDetailA1II = eltMenuMainA.findElement(By.id("form:detailA1II"));
        actions.moveToElement(menubar.getWrappedElement()).build().perform();
        actions.moveToElement(eltMenuMainA).build().perform();
        actions.moveToElement(eltMenuSubA1).build().perform();
        PrimeSelenium.guardAjax(eltMenuDetailA1II).click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Detail A-1-II"));

        assertConfiguration(menubar.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("MenuBar: basic (hover, selection via value)")
    void basicByValue(Page page) {
        // Arrange
        Menubar menubar = page.menubar;

        // Act
        menubar.selectMenuitemByValue("Main C");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Main C"));

        // Act
        page.messages.click();
        WebElement eltMenuMainA = menubar.selectMenuitemByValue("Main A");
        menubar.selectMenuitemByValue(eltMenuMainA, "Sub A-2");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Sub A-2"));

        // Act
        eltMenuMainA = menubar.selectMenuitemByValue("Main A");
        WebElement eltMenuSubA1 = menubar.selectMenuitemByValue(eltMenuMainA, "Sub A-1");
        menubar.selectMenuitemByValue(eltMenuSubA1, "Detail A-1-II");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Detail A-1-II"));

        assertConfiguration(menubar.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("MenuBar Config = " + cfg);
        assertTrue(cfg.has("toggleEvent"));
        assertEquals("hover", cfg.getString("toggleEvent"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:menubar")
        Menubar menubar;

        @Override
        public String getLocation() {
            return "menubar/menuBar001b.xhtml";
        }
    }
}
