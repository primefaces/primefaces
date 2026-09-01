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
package org.primefaces.integrationtests.menu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Menu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Menu: GitHub #15110 hover preserves input focus")
    void hoverPreservesInputFocus(Page page) {
        // Arrange
        WebElement first = page.getMenuLink("form:first");
        page.input.click();

        // Act
        new Actions(page.getWebDriver()).moveToElement(first).perform();

        // Assert
        assertEquals(page.input, page.getWebDriver().switchTo().activeElement());
        assertTrue(first.getAttribute("class").contains("ui-state-hover"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Menu: enabled item remains clickable")
    void clickEnabledItem(Page page) {
        // Arrange
        WebElement first = page.getMenuLink("form:first");
        page.input.click();

        // Act
        first.click();

        // Assert
        assertTrue(page.getWebDriver().getCurrentUrl().endsWith("#first"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Menu: keyboard entry and arrow navigation preserve roving tabindex")
    void keyboardNavigation(Page page) {
        // Arrange
        WebElement first = page.getMenuLink("form:first");
        WebElement second = page.getMenuLink("form:second");
        page.input.click();

        // Act
        page.input.sendKeys(Keys.TAB);

        // Assert
        assertEquals(first, page.getWebDriver().switchTo().activeElement());
        assertEquals("0", first.getAttribute("tabindex"));
        assertEquals("-1", second.getAttribute("tabindex"));

        // Act
        first.sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertEquals(second, page.getWebDriver().switchTo().activeElement());
        assertEquals("-1", first.getAttribute("tabindex"));
        assertEquals("0", second.getAttribute("tabindex"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Menu: disabled item hover preserves input focus")
    void disabledItemHoverPreservesInputFocus(Page page) {
        // Arrange
        WebElement disabled = page.getMenuLink("form:disabled");
        page.input.click();

        // Act
        new Actions(page.getWebDriver()).moveToElement(disabled).perform();

        // Assert
        assertEquals(page.input, page.getWebDriver().switchTo().activeElement());
        assertFalse(disabled.getAttribute("class").contains("ui-state-hover"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:input")
        WebElement input;

        @FindBy(id = "form:menu")
        WebElement menu;

        WebElement getMenuLink(String menuitemId) {
            return menu.findElement(By.id(menuitemId));
        }

        @Override
        public String getLocation() {
            return "menu/menu001.xhtml";
        }
    }
}
