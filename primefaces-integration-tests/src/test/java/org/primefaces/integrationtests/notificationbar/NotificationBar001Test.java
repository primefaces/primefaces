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
package org.primefaces.integrationtests.notificationbar;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.NotificationBar;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class NotificationBar001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("NotificationBar: show and hide with buttons, close with the close element")
    void showHideClose(Page page) {
        // Arrange
        NotificationBar bar = page.top;
        assertFalse(bar.isVisible());
        assertEquals("body", bar.findElement(By.xpath("..")).getTagName());

        // Act
        page.show.click();
        bar.waitUntilVisible();

        // Assert
        assertTrue(bar.isVisible());
        assertEquals("0px", PrimeSelenium.getComputedStyle(bar, "top"));
        assertTrue(bar.getText().contains("PrimeFaces Rocks!"));

        // Act
        page.hide.click();
        bar.waitUntilHidden();

        // Assert
        assertFalse(bar.isVisible());

        // Act
        page.show.click();
        bar.waitUntilVisible();
        bar.close();

        // Assert
        assertFalse(bar.isVisible());
        assertConfiguration(bar.getWidgetConfiguration(), "top", "slide");
    }

    @Test
    @Order(2)
    @DisplayName("NotificationBar: auto display at the bottom, hide and show via widget API")
    void autoDisplayBottom(Page page) {
        // Arrange
        NotificationBar bar = page.bottom;
        assertTrue(bar.isVisible());
        assertEquals("bottom", bar.getPosition());
        assertEquals("0px", PrimeSelenium.getComputedStyle(bar, "bottom"));
        assertEquals("Bottom bar", bar.getText());

        // Act
        bar.hide();

        // Assert
        assertFalse(bar.isVisible());

        // Act
        bar.show();

        // Assert
        assertTrue(bar.isVisible());
        assertFalse(page.top.isVisible());
        JSONObject cfg = bar.getWidgetConfiguration();
        assertConfiguration(cfg, "bottom", "none");
        assertTrue(cfg.getBoolean("autoDisplay"));
    }

    @Test
    @Order(3)
    @DisplayName("NotificationBar: content updated by AJAX")
    void ajaxUpdate(Page page) {
        // Arrange
        NotificationBar bar = page.top;

        // Act
        page.update.click();

        // Assert - the bar moved to the body was replaced
        assertEquals(1, getWebDriver().findElements(By.id("form:top")).size());
        assertEquals("body", bar.findElement(By.xpath("..")).getTagName());

        // Act
        bar.show();

        // Assert
        assertTrue(bar.isVisible());
        assertTrue(bar.getText().contains("Updated text"));

        // Act - the refreshed widget still closes
        bar.close();

        // Assert
        assertFalse(bar.isVisible());
        assertConfiguration(bar.getWidgetConfiguration(), "top", "slide");
    }

    @Test
    @Order(4)
    @DisplayName("NotificationBar: show and hide animate with effectSpeed")
    void effectSpeed(Page page) {
        // Arrange
        NotificationBar bar = page.top;

        // Act - record the durations passed to the jQuery animations
        String durations = PrimeSelenium.executeScript("var w = PF('topBar'), d = [];"
                    + "w.jq.slideDown = function(s) { d.push(s); return $.fn.slideDown.apply(this, arguments); };"
                    + "w.jq.slideUp = function(s) { d.push(s); return $.fn.slideUp.apply(this, arguments); };"
                    + "w.show(); w.hide(); return d.join();");
        bar.waitUntilHidden();

        // Assert
        assertEquals("fast,fast", durations);
        assertFalse(bar.isVisible());
        JSONObject cfg = bar.getWidgetConfiguration();
        assertConfiguration(cfg, "top", "slide");
        assertEquals("fast", cfg.getString("effectSpeed"));
    }

    private void assertConfiguration(JSONObject cfg, String position, String effect) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals(position, cfg.getString("position"));
        assertEquals(effect, cfg.getString("effect"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:top")
        NotificationBar top;

        @FindBy(id = "form:bottom")
        NotificationBar bottom;

        @FindBy(id = "form:show")
        CommandButton show;

        @FindBy(id = "form:hide")
        CommandButton hide;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "notificationbar/notificationBar001.xhtml";
        }
    }
}
