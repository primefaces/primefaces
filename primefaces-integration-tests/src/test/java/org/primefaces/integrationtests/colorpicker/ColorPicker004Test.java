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
import org.primefaces.selenium.component.ColorPicker;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ColorPicker004Test extends AbstractColorPickerTest {

    @Test
    @Order(2)
    @DisplayName("ColorPicker: Inline Hex setColor widget method")
    void setHexColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("#A427aE");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "#a427ae");
        WebElement panel = colorPicker.getPanel();
        assertDisplayed(panel);
        assertClickable(panel);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("ColorPicker: Inline Hex Alpha setColor widget method")
    void setHexAlphaColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("#0080009e");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "#0080009e");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("ColorPicker: Inline RGB setColor widget method")
    void setRgbColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("RGB(0,48,108)");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "rgb(0, 48, 108)");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("ColorPicker: Inline RGB Alpha setColor widget method")
    void setRgbAlphaColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("rgba(255, 0, 0, 0.68)");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "rgba(255, 0, 0, 0.68)");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("ColorPicker: Inline HSL setColor widget method")
    void setHslColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("HSL(171, 55%, 20%)");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "hsl(171, 55%, 20%)");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("ColorPicker: Inline HSL Alpha setColor widget method")
    void setHslAlphaColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("hsla(0, 100%, 50%, 0.68)");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "hsla(0, 100%, 50%, 0.68)");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("ColorPicker: Inline Invalid color format should be prevented server side")
    void setInvalidColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        ComponentUtils.sendKeys(colorPicker.findElement(By.id("clr-color-value")), "XXXXX");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "#000000");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("ColorPicker: Inline Use disable() widget method to disable component")
    void disable(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertTrue(colorPicker.isEnabled());

        // Act
        colorPicker.disable();

        // Assert
        assertFalse(colorPicker.isEnabled());
        assertNotClickable(colorPicker);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("ColorPicker: Inline Use enable() widget method to enable component")
    void enable(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        WebElement panel = colorPicker.getPanel();
        colorPicker.disable();
        assertFalse(colorPicker.isEnabled());

        // Act
        colorPicker.enable();

        // Assert
        assertTrue(colorPicker.isEnabled());
        assertClickable(panel);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("ColorPicker: Inline Destroy the widget and make sure its no longer a picker")
    void destroy(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertTrue(colorPicker.isEnabled());
        WebElement panel = colorPicker.getPanel();

        // Act
        colorPicker.destroy();

        // Assert
        assertNotPresent(panel);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(13)
    @DisplayName("ColorPicker: Inline Check all ARIA labels")
    void aria(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;

        // Act
        WebElement panel = colorPicker.showPanel();

        // Assert
        assertAriaLabel(panel, "clr-color-value", "Color value field");
        assertAriaLabel(panel, "clr-open-label", "Open color picker");
        assertAriaLabel(panel, "clr-close", "Close color picker");
        assertAriaLabel(panel, "clr-clear", "Clear the selected color");
        assertAriaLabel(panel, "clr-hue-slider", "Hue slider");
        assertAriaLabel(panel, "clr-alpha-slider", "Opacity slider");
        assertAriaLabel(panel, "clr-swatch-label", "Color swatch");
        assertAriaLabel(panel, "clr-color-area", "Saturation and brightness selector. Use up, down, left and right arrow keys to select.");
        assertAriaLabel(panel, "clr-color-marker", "Saturation: 73. Brightness: 62.");

        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ColorPicker Config = " + cfg);
        assertEquals("inline", cfg.getString("mode"));
        assertEquals("light", cfg.getString("themeMode"));
        assertEquals("large", cfg.getString("theme"));
        assertEquals("en", cfg.getString("locale"));
        assertTrue(cfg.getBoolean("formatToggle"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:inline")
        ColorPicker colorPicker;

        @FindBy(id = "form:button")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "colorpicker/colorPicker004.xhtml";
        }
    }
}
