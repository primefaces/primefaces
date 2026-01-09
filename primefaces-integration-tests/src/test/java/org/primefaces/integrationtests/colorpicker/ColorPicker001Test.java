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

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ColorPicker001Test extends AbstractColorPickerTest {

    @Test
    @Order(1)
    @DisplayName("ColorPicker: validate popup is displayed on click")
    void popupOnClick(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.click();

        // Assert
        WebElement panel = colorPicker.getPanel();
        assertDisplayed(panel);
        assertClickable(panel);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("ColorPicker: Hex setColor widget method")
    void setHexColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.setColor("#A427aE");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", "#a427ae");
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("ColorPicker: Hex Alpha setColor widget method")
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
    @DisplayName("ColorPicker: RGB setColor widget method")
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
    @DisplayName("ColorPicker: RGB Alpha setColor widget method")
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
    @DisplayName("ColorPicker: HSL setColor widget method")
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
    @DisplayName("ColorPicker: HSL Alpha setColor widget method")
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
    @DisplayName("ColorPicker: Invalid color format should be prevented server side")
    void setInvalidColor(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertEquals("#2a9d8f", colorPicker.getColor());

        // Act
        colorPicker.getInput().sendKeys("XXXXX");
        page.submit.click();

        // Assert
        assertMessage(page.messages, "Color Saved", ""); // color ignored and set to NULL
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("ColorPicker: Use disable() widget method to disable component")
    void disable(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertTrue(colorPicker.isEnabled());

        // Act
        colorPicker.disable();
        WebElement panel = colorPicker.showPanel();

        // Assert
        assertFalse(colorPicker.isEnabled());
        assertNotDisplayed(panel);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("ColorPicker: Use enable() widget method to enable component")
    void enable(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        colorPicker.disable();
        assertFalse(colorPicker.isEnabled());

        // Act
        colorPicker.enable();
        WebElement panel = colorPicker.showPanel();

        // Assert
        assertTrue(colorPicker.isEnabled());
        assertDisplayed(panel);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("ColorPicker: Destroy the widget and make sure its no longer a picker")
    void destroy(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertTrue(colorPicker.isEnabled());
        WebElement triggerButton = colorPicker.getTriggerButton();
        assertClickable(triggerButton);

        // Act
        colorPicker.destroy();

        // Assert
        assertNotDisplayed(triggerButton);
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("ColorPicker: Trigger the popup wih keyboard")
    void keyboardAccessibility(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;
        assertTrue(colorPicker.isEnabled());
        WebElement triggerButton = colorPicker.getTriggerButton();
        assertClickable(triggerButton);

        // Act
        triggerButton.sendKeys(Keys.ENTER);

        // Assert
        assertDisplayed(colorPicker.getPanel());
        assertConfiguration(colorPicker.getWidgetConfiguration());
    }

    @Test
    @Order(13)
    @DisplayName("ColorPicker: Check all ARIA labels")
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
        assertEquals("popup", cfg.getString("mode"));
        assertEquals("light", cfg.getString("themeMode"));
        assertEquals("pill", cfg.getString("theme"));
        assertEquals("en", cfg.getString("locale"));
        assertEquals("Clear", cfg.getString("clearLabel"));
        assertEquals("Close", cfg.getString("closeLabel"));
        assertTrue(cfg.getBoolean("clearButton"));
        assertTrue(cfg.getBoolean("closeButton"));
        assertTrue(cfg.getBoolean("formatToggle"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:colorpicker")
        ColorPicker colorPicker;

        @FindBy(id = "form:button")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "colorpicker/colorPicker001.xhtml";
        }
    }
}
