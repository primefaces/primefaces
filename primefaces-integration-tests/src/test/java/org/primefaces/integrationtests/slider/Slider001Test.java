/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.integrationtests.slider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Slider;

class Slider001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Slider: int-value")
    void intValue(Page page) {
        // Arrange
        Slider slider = page.sliderInt;
        InputText inputText = page.inputInt;
        assertEquals(5, slider.getValue().intValue());

        // Act - add value
        slider.setValue(8);

        // Act - submit
        page.button.click();

        // Assert
        assertValue(slider, inputText, 8);
        assertIntConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Slider: float-value")
    void floatValue(Page page) {
        // Arrange
        Slider slider = page.sliderfloat;
        InputText inputText = page.inputFloat;
        assertEquals(3.14f, slider.getValue().floatValue());

        // Act - add value
        slider.setValue(9.9f);

        // Act - submit
        page.button.click();

        // Assert
        assertValue(slider, inputText, 9.9f);
        assertFloatConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Slider: Negative not accepted if min >= 0")
    void negativeRejected(Page page) {
        // Arrange
        Slider slider = page.sliderInt;
        InputText inputText = page.inputInt;
        assertEquals(5, slider.getValue().intValue());

        // Act - add value
        inputText.setValue("-7");

        // Act - submit
        page.button.click();

        // Assert
        assertValue(slider, inputText, 7);
        assertIntConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Slider: Decimal not accepted if integer only and max reached")
    void decimalRejected(Page page) {
        // Arrange
        Slider slider = page.sliderInt;
        InputText inputText = page.inputInt;
        assertEquals(5, slider.getValue().intValue());

        // Act - add value
        inputText.setValue("6.4");

        // Act - submit
        page.button.click();

        // Assert
        // Mojarra and MyFaces have slightly different behaviour - we check their common behaviour
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("Value is out of range."));
        assertIntConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Slider: Negative accepted if min < 0")
    void negativeAccepted(Page page) {
        // Arrange
        Slider slider = page.sliderfloat;
        InputText inputText = page.inputFloat;
        assertEquals(3.14f, slider.getValue().floatValue());

        // Act - add value
        inputText.setValue("-3.87");

        // Act - submit
        page.button.click();

        // Assert
        assertValue(slider, inputText, -3.87f);
        assertFloatConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("Slider: Only allow one negative symbol")
    void multipleNegativesRejected(Page page) {
        // Arrange
        Slider slider = page.sliderfloat;
        InputText inputText = page.inputFloat;
        assertEquals(3.14f, slider.getValue().floatValue());

        // Act - add value
        inputText.setValue("-4.5-6");

        // Act - submit
        page.button.click();

        // Assert
        assertValue(slider, inputText, -4.56f);
        assertFloatConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("Slider: Only allow one decimal symbol")
    void multipleDecimalsRejected(Page page) {
        // Arrange
        Slider slider = page.sliderfloat;
        InputText inputText = page.inputFloat;
        assertEquals(3.14f, slider.getValue().floatValue());

        // Act - add value
        inputText.setValue("2.9...8");

        // Act - submit
        page.button.click();

        // Assert
        assertValue(slider, inputText, 2.98f);
        assertFloatConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("Slider: Don't allow value past minimum")
    void minimum(Page page) {
        // Arrange
        Slider slider = page.sliderfloat;
        assertEquals(3.14f, slider.getValue().floatValue());

        // Act - add value
        slider.setValue(-11.0f);
        page.button.click();

        // Assert
        // Mojarra and MyFaces have slightly different behaviour - we check their common behaviour
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("Value is out of range."));
        assertFloatConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("Slider: Don't allow value past maximum")
    void maximum(Page page) {
        // Arrange
        Slider slider = page.sliderfloat;
        assertEquals(3.14f, slider.getValue().floatValue());

        // Act - add value
        slider.setValue(11.99f);
        page.button.click();

        // Assert
        // Mojarra and MyFaces have slightly different behaviour - we check their common behaviour
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("Value is out of range."));
        assertFloatConfiguration(slider.getWidgetConfiguration());
    }

    private void assertValue(Slider slider, InputText inputText, Number value) {
        if (value instanceof Integer) {
            assertEquals(value.intValue(), slider.getValue().intValue());
        }
        else {
            assertEquals(value.floatValue(), slider.getValue().floatValue());
        }
        assertEquals(value.toString(), inputText.getValue());
    }

    private void assertFloatConfiguration(JSONObject cfg) {
        assertConfiguration(cfg);
        assertEquals(-10, cfg.getInt("min"));
        assertEquals(10, cfg.getInt("max"));
        assertEquals("0.01", cfg.get("step").toString());
    }

    private void assertIntConfiguration(JSONObject cfg) {
        assertConfiguration(cfg);
        assertEquals(0, cfg.getInt("min"));
        assertEquals(50, cfg.getInt("max"));
        assertEquals(1, cfg.getInt("step"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Slider Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.getBoolean("animate"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:sliderInt")
        Slider sliderInt;

        @FindBy(id = "form:sliderFloat")
        Slider sliderfloat;

        @FindBy(id = "form:int")
        InputText inputInt;

        @FindBy(id = "form:float")
        InputText inputFloat;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "slider/slider001.xhtml";
        }
    }
}
