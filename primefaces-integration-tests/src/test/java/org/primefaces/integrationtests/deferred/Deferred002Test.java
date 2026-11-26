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
package org.primefaces.integrationtests.deferred;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Deferred;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Deferred002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Deferred: mode=manual without loading facet shows nothing until the content is loaded")
    void defaultIndicator(Page page) {
        // Assert
        Deferred deferred = page.indicator;
        assertFalse(deferred.isLoaded());
        assertTrue(PrimeSelenium.hasCssClass(deferred, "p-deferred-manual"));
        assertFalse(deferred.isDisplayed());
        List<WebElement> indicators = deferred.findElements(By.cssSelector("i.p-deferred-loading"));
        assertEquals(1, indicators.size());
        assertEquals("true", indicators.get(0).getDomAttribute("aria-hidden"));

        // Act
        deferred.load();

        // Assert
        assertTrue(deferred.isLoaded());
        assertTrue(deferred.isDisplayed());
        assertEquals("Indicator content", deferred.getText());
        assertTrue(deferred.findElements(By.cssSelector(".p-deferred-loading")).isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Deferred: mode=manual shows the loading facet with the trigger, not hidden from assistive technology")
    void triggerInFacet(Page page) {
        // Assert
        Deferred deferred = page.trigger;
        assertFalse(deferred.isLoaded());
        assertTrue(deferred.findElements(By.cssSelector("[aria-hidden]")).isEmpty());
        WebElement button = deferred.findElement(By.id("form:triggerButton"));
        assertTrue(button.isDisplayed());

        // Act
        button.click();
        deferred.waitUntilLoaded();

        // Assert
        assertEquals("Trigger content", deferred.getText());
        assertTrue(deferred.findElements(By.id("form:triggerButton")).isEmpty());

        // Act
        page.ajax.click();

        // Assert
        assertTrue(page.trigger.isLoaded());
        assertEquals("Trigger content", page.trigger.getText());
        assertEquals("1", page.triggerLoadCount.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Deferred: loaded=true renders the content directly, without load request and listener")
    void loadedAttribute(Page page) {
        // Assert
        assertTrue(page.loaded.isLoaded());
        assertEquals("Loaded content", page.loaded.getText());

        // Act
        page.ajax.click();

        // Assert
        assertTrue(page.loaded.isLoaded());
        assertEquals("Loaded content", page.loaded.getText());
        assertEquals("0", page.loadedLoadCount.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Deferred: layout=inline renders a span with style and styleClass")
    void inline(Page page) {
        // Assert
        Deferred deferred = page.inline;
        assertEquals("span", deferred.getTagName());
        assertTrue(PrimeSelenium.hasCssClass(deferred, "p-deferred"));
        assertTrue(PrimeSelenium.hasCssClass(deferred, "custom"));
        assertTrue(deferred.getDomAttribute("style").contains("color"));
        assertEquals("Inline placeholder", deferred.getText());

        // Act
        deferred.load();

        // Assert
        assertEquals("span", page.inline.getTagName());
        assertTrue(PrimeSelenium.hasCssClass(page.inline, "custom"));
        assertEquals("Inline content", page.inline.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Deferred: a nested deferred loads on its own once the outer content is loaded")
    void nested(Page page) {
        // Assert
        assertFalse(page.outer.isLoaded());
        assertTrue(page.getWebDriver().findElements(By.id("form:inner")).isEmpty());

        // Act
        page.outer.load();
        page.inner.waitUntilLoaded();

        // Assert
        assertTrue(page.outer.isLoaded());
        assertTrue(page.inner.isLoaded());
        assertEquals("Inner content", page.inner.getText());
        assertTrue(page.outer.getText().contains("Outer content"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("Deferred: a non-AJAX postback renders loaded content directly and keeps the rest deferred")
    void nonAjaxPostback(Page page) {
        // Arrange
        page.trigger.findElement(By.id("form:triggerButton")).click();
        page.trigger.waitUntilLoaded();

        // Act
        page.postback.click();

        // Assert
        assertTrue(page.trigger.isLoaded());
        assertEquals("Trigger content", page.trigger.getText());
        assertEquals("1", page.triggerLoadCount.getText());
        assertFalse(page.indicator.isLoaded());
        assertFalse(page.outer.isLoaded());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("Deferred: a skeleton in the loading facet is replaced by the content, hidden from assistive technology")
    void skeleton(Page page) {
        // Assert
        Deferred deferred = page.skeleton;
        assertFalse(deferred.isLoaded());
        WebElement placeholder = deferred.findElement(By.cssSelector(":scope > .p-deferred-placeholder"));
        assertEquals("true", placeholder.getDomAttribute("aria-hidden"));
        assertEquals(1, placeholder.findElements(By.cssSelector(".p-skeleton")).size());
        assertNull(deferred.getDomAttribute("aria-busy"));

        // Act
        deferred.scrollIntoViewAndLoad();

        // Assert
        assertTrue(page.skeleton.isLoaded());
        assertEquals("Skeleton content", page.skeleton.getText());
        assertTrue(page.skeleton.findElements(By.cssSelector(".p-skeleton, .p-deferred-placeholder")).isEmpty());
        assertNull(page.skeleton.getDomAttribute("aria-busy"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:indicator")
        Deferred indicator;

        @FindBy(id = "form:trigger")
        Deferred trigger;

        @FindBy(id = "form:triggerLoadCount")
        WebElement triggerLoadCount;

        @FindBy(id = "form:loaded")
        Deferred loaded;

        @FindBy(id = "form:loadedLoadCount")
        WebElement loadedLoadCount;

        @FindBy(id = "form:inline")
        Deferred inline;

        @FindBy(id = "form:outer")
        Deferred outer;

        @FindBy(id = "form:inner")
        Deferred inner;

        @FindBy(id = "form:skeleton")
        Deferred skeleton;

        @FindBy(id = "form:ajax")
        CommandButton ajax;

        @FindBy(id = "form:postback")
        CommandButton postback;

        @Override
        public String getLocation() {
            return "deferred/deferred002.xhtml";
        }
    }
}
