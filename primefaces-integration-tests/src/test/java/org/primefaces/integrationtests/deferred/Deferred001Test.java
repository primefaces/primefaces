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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Deferred;
import org.primefaces.selenium.component.SelectBooleanCheckbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Deferred001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Deferred: mode=load loads the content after page load and calls the load listener")
    void load(Page page) {
        // Act
        page.load.waitUntilLoaded();

        // Assert
        assertEquals("Deferred content", page.load.getText());
        assertEquals("load", page.load.getClientConfiguration().getString("mode"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Deferred: content that is not loaded stays deferred in an AJAX update and is not processed")
    void notLoaded(Page page) {
        // Arrange
        page.load.waitUntilLoaded();
        assertFalse(page.manual.isLoaded());
        assertEquals("Placeholder", page.manual.getText());

        // Act
        page.submit.click();

        // Assert
        assertFalse(page.manual.isLoaded());
        assertEquals("Placeholder", page.manual.getText());
        assertEquals("true", page.checked.getText());
        assertEquals("0", page.manualLoadCount.getText());
        assertTrue(page.load.isLoaded());
        assertEquals("Deferred content", page.load.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Deferred: mode=manual loads the content with load(), afterwards it is rendered directly and processed")
    void manual(Page page) {
        // Arrange
        page.load.waitUntilLoaded();
        assertFalse(page.manual.isLoaded());
        assertNull(page.manual.getDomAttribute("aria-busy"));

        // Act
        page.manual.load();

        // Assert
        assertTrue(page.manual.isLoaded());
        assertNull(page.manual.getDomAttribute("aria-busy"));
        assertTrue(page.manual.findElements(By.id("form:placeholder")).isEmpty());
        assertTrue(page.checkbox.getValue());

        // Act
        page.checkbox.uncheck();
        page.submit.click();

        // Assert
        assertTrue(page.manual.isLoaded());
        assertFalse(page.checkbox.getValue());
        assertEquals("false", page.checked.getText());
        assertEquals("1", page.manualLoadCount.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Deferred: mode=visible loads the content once scrolled into view")
    void visible(Page page) {
        // Arrange
        page.load.waitUntilLoaded();
        assertFalse(page.visible.isLoaded());

        // Act
        page.visible.scrollIntoViewAndLoad();

        // Assert
        assertTrue(page.visible.isLoaded());
        assertEquals("Visible content", page.visible.getText());
        assertEquals("visible", page.visible.getClientConfiguration().getString("mode"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Deferred: mode=visible is still observed after an AJAX update of the placeholder")
    void visibleAfterUpdate(Page page) {
        // Arrange
        page.load.waitUntilLoaded();
        page.submit.click();
        assertFalse(page.visible.isLoaded());

        // Act
        page.visible.scrollIntoViewAndLoad();

        // Assert
        assertEquals("Visible content", page.visible.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("Deferred: a child of content that is not loaded is not processed when it is the process target")
    void processChildNotLoaded(Page page) {
        // Arrange
        page.load.waitUntilLoaded();
        assertFalse(page.manual.isLoaded());

        // Act
        page.processChild.click();

        // Assert
        assertFalse(page.manual.isLoaded());
        assertEquals("true", page.checked.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:load")
        Deferred load;

        @FindBy(id = "form:manual")
        Deferred manual;

        @FindBy(id = "form:visible")
        Deferred visible;

        @FindBy(id = "form:checkbox")
        SelectBooleanCheckbox checkbox;

        @FindBy(id = "form:checked")
        WebElement checked;

        @FindBy(id = "form:manualLoadCount")
        WebElement manualLoadCount;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:processChild")
        CommandButton processChild;

        @Override
        public String getLocation() {
            return "deferred/deferred001.xhtml";
        }
    }
}
