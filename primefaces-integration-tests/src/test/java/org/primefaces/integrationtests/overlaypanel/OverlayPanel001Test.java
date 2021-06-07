/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.OverlayPanel;

public class OverlayPanel001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("OverlayPanel: Show panel on button click")
    public void testShow(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        Assertions.assertFalse(overlayPanel.isDisplayed());

        // Act
        page.btnShow.click();
        overlayPanel.waitForDisplay();

        // Assert
        Assertions.assertTrue(overlayPanel.isDisplayed());
        assertConfiguration(overlayPanel.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("OverlayPanel: Show/Hide panel using API methods")
    public void testWidgetShowHide(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        Assertions.assertFalse(overlayPanel.isVisible());

        // Act
        overlayPanel.show();

        // Assert
        Assertions.assertTrue(overlayPanel.isDisplayed());

        // Act
        overlayPanel.hide();

        // Assert
        Assertions.assertFalse(overlayPanel.isDisplayed());
        assertConfiguration(overlayPanel.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("OverlayPanel: Update overlay")
    public void testUpdate(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        overlayPanel.show();
        Assertions.assertTrue(overlayPanel.isDisplayed());

        // Act
        page.btnUpdate.click();

        // Assert
        Assertions.assertFalse(overlayPanel.isDisplayed());
        assertConfiguration(overlayPanel.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("OverlayPanel: Destroy overlay")
    public void testDestroy(Page page) {
        // Arrange
        OverlayPanel overlayPanel = page.overlayPanel;
        overlayPanel.show();
        Assertions.assertTrue(overlayPanel.isDisplayed());

        // Act
        page.btnDestroy.click();

        // Assert
        try {
            overlayPanel.isDisplayed();
            Assertions.fail("OverlayPanel should have been destroyed.");
        }
        catch (NoSuchElementException ex) {
            // overlay panel should be destroyed
        }
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("OverlayPanel Config = " + cfg);
        Assertions.assertTrue(cfg.has("widgetVar"));
        Assertions.assertFalse(cfg.has("appendTo"));
        Assertions.assertTrue(cfg.getBoolean("dismissable"));
        Assertions.assertEquals("form:btnShow", cfg.getString("target"));
        Assertions.assertEquals(100, cfg.getInt("showDelay"));
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
