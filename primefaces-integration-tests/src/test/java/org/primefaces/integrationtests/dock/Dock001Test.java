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
package org.primefaces.integrationtests.dock;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Dock;
import org.primefaces.selenium.component.Messages;

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

class Dock001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dock: renders the items with label, image and menu roles")
    void render(Page page) {
        // Arrange
        Dock dock = page.dock;

        // Act
        List<WebElement> items = dock.getItems();

        // Assert
        assertEquals(Arrays.asList("Home", "Music", "Video", "Email"), dock.getItemLabels());
        assertTrue(PrimeSelenium.hasCssClass(dock, "p-dock", "bottom"));
        WebElement container = dock.findElement(By.cssSelector("ul.p-dock-container"));
        assertTrue(PrimeSelenium.hasCssClass(container, "center", "bottom"));
        assertEquals("menu", container.getDomAttribute("role"));
        for (WebElement item : items) {
            assertEquals("none", item.getDomAttribute("role"));
            assertEquals("menuitem", item.findElement(By.tagName("a")).getDomAttribute("role"));
        }
        WebElement image = items.get(0).findElement(By.className("p-dock-image"));
        assertTrue(image.getDomAttribute("src").contains("/resources/dock/home.png"));
        assertTrue(image.isDisplayed());
        assertEquals("bottom", dock.getPosition());
        assertEquals("center", dock.getHalign());
        assertConfiguration(dock.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Dock: hovering an item magnifies it and marks its neighbours")
    void hover(Page page) {
        // Arrange
        Dock dock = page.dock;
        List<WebElement> items = dock.getItems();

        // Act
        dock.hoverItem("Music");

        // Assert
        assertTrue(dock.isItemActive("Music"));
        assertTrue(PrimeSelenium.hasCssClass(items.get(0), "prev"));
        assertTrue(PrimeSelenium.hasCssClass(items.get(2), "next"));
        assertTrue(PrimeSelenium.hasCssClass(items.get(3), "next-anchor"));
        assertTrue(items.get(1).findElement(By.tagName("em")).isDisplayed());
        assertFalse(items.get(0).findElement(By.tagName("em")).isDisplayed());

        // Act
        dock.unhoverItems();

        // Assert
        for (WebElement item : items) {
            assertFalse(PrimeSelenium.hasCssClass(item, "active"));
            assertFalse(PrimeSelenium.hasCssClass(item, "prev"));
            assertFalse(PrimeSelenium.hasCssClass(item, "next"));
            assertFalse(PrimeSelenium.hasCssClass(item, "prev-anchor"));
            assertFalse(PrimeSelenium.hasCssClass(item, "next-anchor"));
        }
        assertConfiguration(dock.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Dock: clicking an item invokes its action via AJAX")
    void click(Page page) {
        // Arrange
        Dock dock = page.dock;
        assertEquals("None", page.selected.getText());

        // Act
        dock.clickItem("Video");

        // Assert
        assertEquals("Video", page.selected.getText());
        assertEquals("Selected", page.messages.getMessage(0).getSummary());
        assertEquals("Video", page.messages.getMessage(0).getDetail());
        assertFalse(PrimeSelenium.hasCssClass(dock.findItemByValue("Video").findElement(By.tagName("a")), "p-dock-bounce"));

        // Act
        dock.clickItem("Home");

        // Assert
        assertEquals("Home", page.selected.getText());
        assertEquals("Home", page.messages.getMessage(0).getDetail());
        assertConfiguration(dock.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("bottom", cfg.getString("position"));
        assertEquals("center", cfg.getString("halign"));
        assertFalse(cfg.getBoolean("animate"));
        assertEquals(1600, cfg.getInt("animationDuration"));
        assertFalse(cfg.has("blockScroll"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:dock")
        Dock dock;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selected")
        WebElement selected;

        @Override
        public String getLocation() {
            return "dock/dock001.xhtml";
        }
    }
}
