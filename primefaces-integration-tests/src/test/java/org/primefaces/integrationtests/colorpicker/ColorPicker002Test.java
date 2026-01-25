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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColorPicker002Test extends AbstractColorPickerTest {

    @Test
    @Order(1)
    @DisplayName("ColorPicker: Check I8LN for Dutch language that all labels are translated")
    void aria(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;

        // Act
        WebElement panel = colorPicker.showPanel();

        // Assert
        assertConfiguration(colorPicker.getWidgetConfiguration());
        assertAriaLabel(panel, "clr-color-value", "Kleurwaardeveld");
        assertAriaLabel(panel, "clr-open-label", "Open kleurkiezer");
        assertAriaLabel(panel, "clr-close", "Sluit kleurkiezer");
        assertAriaLabel(panel, "clr-clear", "Wis de geselecteerde kleur");
        assertAriaLabel(panel, "clr-hue-slider", "Tintschuif");
        assertAriaLabel(panel, "clr-alpha-slider", "Dekkingschuif");
        assertAriaLabel(panel, "clr-swatch-label", "Kleurstaal");
        assertAriaLabel(panel, "clr-color-area", "Verzadiging- en helderheidkiezer. Gebruik pijltoetsen om te selecteren.");
        assertAriaLabel(panel, "clr-color-marker", "Verzadiging: 73. Helderheid: 62.");
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ColorPicker Config = " + cfg);
        assertEquals("popup", cfg.getString("mode"));
        assertEquals("light", cfg.getString("themeMode"));
        assertEquals("pill", cfg.getString("theme"));
        assertEquals("nl", cfg.getString("locale"));
        assertEquals("Wissen", cfg.getString("clearLabel"));
        assertEquals("Sluit", cfg.getString("closeLabel"));
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
            return "colorpicker/colorPicker002.xhtml";
        }
    }
}
