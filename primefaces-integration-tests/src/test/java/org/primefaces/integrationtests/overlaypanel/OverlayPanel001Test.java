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
package org.primefaces.integrationtests.overlaypanel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.OverlayPanel;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class OverlayPanel001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("OverlayPanel: Show panel on button click")
    void show(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        assertFalse(overlayPanel.isDisplayed());

        // Act
        page.btnShow.click();
        overlayPanel.waitForDisplay();

        // Assert
        assertTrue(overlayPanel.isDisplayed());
        assertConfiguration(overlayPanel.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("OverlayPanel: Show/Hide panel using API methods")
    void widgetShowHide(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        assertFalse(overlayPanel.isVisible());

        // Act
        overlayPanel.show();

        // Assert
        assertTrue(overlayPanel.isDisplayed());

        // Act
        overlayPanel.hide();

        // Assert
        assertFalse(overlayPanel.isDisplayed());
        assertConfiguration(overlayPanel.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("OverlayPanel: Update overlay")
    void update(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        overlayPanel.show();
        assertTrue(overlayPanel.isDisplayed());

        // Act
        page.btnUpdate.click();

        // Assert
        assertFalse(overlayPanel.isDisplayed());
        assertConfiguration(overlayPanel.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("OverlayPanel: Unrender overlay")
    void unrender(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        overlayPanel.show();
        assertTrue(overlayPanel.isDisplayed());

        // Act
        page.btnDestroy.click();

        // Assert
        try {
            overlayPanel.isDisplayed();
            fail("OverlayPanel should have been unrendered.");
        }
        catch (NoSuchElementException ex) {
            // overlay panel should be destroyed
        }
    }

    @Test
    @Order(5)
    @DisplayName("OverlayPanel: Destroy widget and make sure DOM elements removed")
    void destroy(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        overlayPanel.show();
        assertTrue(overlayPanel.isVisible());
        assertEquals(1, getOverlays().size());

        // Act
        overlayPanel.destroy();

        // Assert
        assertEquals(0, getOverlays().size());
        assertNoJavascriptErrors();
    }

    private List<WebElement> getOverlays() {
        return getWebDriver().findElements(By.className("ui-overlaypanel"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("OverlayPanel Config = " + cfg);
        assertTrue(cfg.has("widgetVar"));
        assertFalse(cfg.has("appendTo"));
        assertTrue(cfg.getBoolean("dismissable"));
        assertEquals("form:btnShow", cfg.getString("target"));
        assertEquals(100, cfg.getInt("showDelay"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:overlaypanel")
        OverlayPanel overlayPanel;

        @FindBy(id = "form:btnShow")
        CommandButton btnShow;

        @FindBy(id = "form:btnUpdate")
        CommandButton btnUpdate;

        @FindBy(id = "form:btnDestroy")
        CommandButton btnDestroy;

        @Override
        public String getLocation() {
            return "overlaypanel/overlayPanel001.xhtml";
        }
    }
}
