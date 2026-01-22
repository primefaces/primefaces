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
package org.primefaces.integrationtests.colorpicker;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.ColorPicker;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ColorPicker003Test extends AbstractColorPickerTest {

    @Test
    @Order(1)
    @DisplayName("ColorPicker: AJAX open event")
    void ajaxOpen(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPickerOpenClose;

        // Act
        WebElement panel = colorPicker.showPanel();

        // Assert
        assertConfiguration(colorPicker.getWidgetConfiguration());
        assertDisplayed(panel);
        assertMessage(page.messages, "Popup Opened", "#d62828");
    }

    @Test
    @Order(2)
    @DisplayName("ColorPicker: AJAX close event")
    void ajaxClose(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPickerOpenClose;

        // Act
        WebElement panel = colorPicker.showPanel();
        colorPicker.hidePanel(false);

        // Assert
        assertConfiguration(colorPicker.getWidgetConfiguration());
        assertNotDisplayed(panel);
        assertMessage(page.messages, "Popup Closed", "#d62828");
    }

    @Test
    @Order(3)
    @DisplayName("ColorPicker: AJAX change event same format")
    void ajaxChangeSameFormat(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPickerAjax;
        assertEquals("#e9c46a", colorPicker.getColor());

        // Act
        colorPicker.setColor("#ff00ff");

        // Assert
        assertConfiguration(colorPicker.getWidgetConfiguration());
        assertMessage(page.messages, "Color Changed", "#ff00ff");
    }

    @Test
    @Order(3)
    @DisplayName("ColorPicker: AJAX change event new format")
    void ajaxChangeNewFormat(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPickerAjax;
        assertEquals("#e9c46a", colorPicker.getColor());

        // Act
        colorPicker.setColor("rgb(0, 183, 255)");
        PrimeSelenium.wait(300);

        // Assert
        assertConfiguration(colorPicker.getWidgetConfiguration());
        assertMessage(page.messages, "Color Changed", "rgb(0, 183, 255)");
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ColorPicker Config = " + cfg);
        assertEquals("popup", cfg.getString("mode"));
        if (cfg.has("themeMode")) {
            assertEquals("light", cfg.getString("themeMode"));
        }
        assertFalse(cfg.has("theme"));
        assertEquals("en", cfg.getString("locale"));
        assertTrue(cfg.getBoolean("clearButton"));
        assertTrue(cfg.getBoolean("closeButton"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:openclose")
        ColorPicker colorPickerOpenClose;

        @FindBy(id = "form:ajax")
        ColorPicker colorPickerAjax;

        @Override
        public String getLocation() {
            return "colorpicker/colorPicker003.xhtml";
        }
    }
}
