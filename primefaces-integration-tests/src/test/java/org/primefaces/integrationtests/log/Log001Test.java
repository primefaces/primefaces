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
package org.primefaces.integrationtests.log;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Log;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Log001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Log: messages of the client side API are shown with their severity")
    void log(Page page) {
        // Arrange
        Log log = page.log;
        log.clearItems();
        assertTrue(log.getItems().isEmpty());

        // Act
        log.log("info", "Info message");
        log.log("warn", "Warn message");
        log.log("debug", "Debug message");
        log.log("error", "Error message");

        // Assert
        List<WebElement> items = log.getItems();
        assertEquals(4, items.size());
        assertEquals(Arrays.asList("Info message", "Warn message", "Debug message", "Error message"), log.getVisibleMessages());
        assertEquals("info", log.getSeverity(items.get(0)));
        assertEquals("warn", log.getSeverity(items.get(1)));
        assertEquals("debug", log.getSeverity(items.get(2)));
        assertEquals("error", log.getSeverity(items.get(3)));
        assertTrue(PrimeSelenium.hasCssClass(items.get(0).findElement(By.tagName("span")), "p-log-info-icon"));

        // Act - messages are escaped
        log.log("info", "<b>bold</b>");

        // Assert
        assertEquals("<b>bold</b>", log.getMessage(log.getItems().get(4)));
        assertTrue(log.findElements(By.cssSelector(".p-log-items b")).isEmpty());
        assertConfiguration(log.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Log: filter by severity and clear")
    void filter(Page page) {
        // Arrange
        Log log = page.log;
        log.clearItems();
        log.log("info", "Info 1");
        log.log("debug", "Debug 1");
        log.log("info", "Info 2");

        // Act
        log.filter("info");

        // Assert
        assertTrue(log.isFilterActive("info"));
        assertFalse(log.isFilterActive("all"));
        assertEquals(Arrays.asList("Info 1", "Info 2"), log.getVisibleMessages());

        // Act - a new message of another severity is hidden by the filter
        log.log("warn", "Warn 1");

        // Assert
        assertEquals(4, log.getItems().size());
        assertEquals(Arrays.asList("Info 1", "Info 2"), log.getVisibleMessages());

        // Act
        log.filter("all");

        // Assert
        assertTrue(log.isFilterActive("all"));
        assertFalse(log.isFilterActive("info"));
        assertEquals(Arrays.asList("Info 1", "Debug 1", "Info 2", "Warn 1"), log.getVisibleMessages());

        // Act
        log.filter("debug");
        log.clearItems();

        // Assert
        assertTrue(log.getItems().isEmpty());
        assertTrue(log.isFilterActive("all"));
        assertFalse(log.isFilterActive("debug"));
        assertConfiguration(log.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Log: AJAX requests are logged at debug level")
    void ajax(Page page) {
        // Arrange
        Log log = page.log;
        log.clearItems();
        page.name.setValue("PrimeFaces");

        // Act
        page.submit.click();

        // Assert
        assertEquals("PrimeFaces", page.output.getText());
        List<WebElement> items = log.getItems();
        assertFalse(items.isEmpty());
        assertEquals("debug", log.getSeverity(items.get(0)));
        assertEquals("Initiating ajax request.", log.getMessage(items.get(0)));
        assertTrue(log.getVisibleMessages().stream().anyMatch(m -> m.startsWith("Post Data:") && m.contains("PrimeFaces")));
        assertConfiguration(log.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Log: hide and show the log window")
    void showHide(Page page) {
        // Arrange
        Log log = page.log;
        assertTrue(log.isDisplayed());

        // Act
        log.hide();

        // Assert
        assertFalse(log.isDisplayed());

        // Act
        log.show();

        // Assert
        assertTrue(log.isDisplayed());
        assertConfiguration(log.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "log")
        Log log;

        @FindBy(id = "form:name")
        InputText name;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:output")
        WebElement output;

        @Override
        public String getLocation() {
            return "log/log001.xhtml";
        }
    }
}
