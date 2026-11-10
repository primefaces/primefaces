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
package org.primefaces.integrationtests.linkbutton;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.LinkButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class LinkButton001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("LinkButton: disabled state from the server and via widget")
    void disableEnable(Page page) {
        // Assert
        assertTrue(page.navigate.isEnabled());
        assertFalse(page.disabled.isEnabled());

        // Act
        page.navigate.disable();

        // Assert
        assertFalse(page.navigate.isEnabled());

        // Act
        page.navigate.enable();

        // Assert
        assertTrue(page.navigate.isEnabled());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("LinkButton: a disabled link button does not navigate by keyboard")
    void disabledDoesNotNavigate(Page page) {
        // the mouse cannot reach a disabled link button (pointer-events: none), but the link stays focusable

        // Act - disabled on the server
        page.disabled.getLink().sendKeys(Keys.ENTER);

        // Assert
        assertThrows(TimeoutException.class, () -> PrimeSelenium.waitGui().until(ExpectedConditions.urlContains("linkButton001Target")));

        // Act - disabled via widget
        page.navigate.disable();
        page.navigate.getLink().sendKeys(Keys.ENTER);

        // Assert
        assertThrows(TimeoutException.class, () -> PrimeSelenium.waitGui().until(ExpectedConditions.urlContains("linkButton001Target")));
        assertNoJavascriptErrors();

        // Act - enabled again, the keyboard navigates
        page.navigate.enable();
        page.navigate.getLink().sendKeys(Keys.ENTER);

        // Assert
        PrimeSelenium.waitDocumentLoad().until(ExpectedConditions.urlContains("linkButton001Target"));
    }

    @Test
    @Order(3)
    @DisplayName("LinkButton: click navigates to the outcome")
    void navigate(Page page) {
        // Arrange
        assertTrue(page.navigate.getHref().contains("linkButton001Target"));

        // Act
        page.navigate.click();

        // Assert
        assertEquals("LinkButton target", PrimeSelenium.getWebDriver().findElement(By.id("target")).getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:navigate")
        LinkButton navigate;

        @FindBy(id = "form:disabled")
        LinkButton disabled;

        @Override
        public String getLocation() {
            return "linkbutton/linkButton001.xhtml";
        }
    }
}
