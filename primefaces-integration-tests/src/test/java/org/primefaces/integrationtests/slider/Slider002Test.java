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
package org.primefaces.integrationtests.slider;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Slider;
import org.primefaces.selenium.component.Spinner;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests GitHub #8619, #8630, #8631
 */
class Slider002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Slider: can not decrement below the minimum value")
    void decrementPastMin(Page page) {
        // Arrange
        Slider slider = page.slider;
        Spinner spinner = page.spinner;
        assertValue(slider, spinner, 800);

        // Act - spin down
        spinner.decrement();
        page.button.click();

        // Assert
        assertValue(slider, spinner, 800);
        assertConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Slider: increment should be rounded to step interval 500")
    void roundUp(Page page) {
        // Arrange
        Slider slider = page.slider;
        Spinner spinner = page.spinner;
        assertValue(slider, spinner, 800);

        // Act - spin up
        spinner.increment(); // increase to 1300
        page.button.click();

        // Assert
        assertValue(slider, spinner, 1000); // rounded to nearest step interval 1000
        assertConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Slider: decrement down should stop at minimum")
    void roundDown(Page page) {
        // Arrange
        Slider slider = page.slider;
        Spinner spinner = page.spinner;
        slider.setValue(1500);
        spinner.setValue(1500);
        assertValue(slider, spinner, 1500);

        // Act - spin up
        spinner.decrement(); // decrease to 1000
        spinner.decrement(); // decrease to 500
        page.button.click();

        // Assert
        assertValue(slider, spinner, 800); // should stop at min of 800 even though not on step
        assertConfiguration(slider.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Slider: can not decrement below the minimum value")
    void incrementPastMax(Page page) {
        // Arrange
        Slider slider = page.slider;
        Spinner spinner = page.spinner;
        slider.setValue(14500);
        spinner.setValue(14500);
        assertValue(slider, spinner, 14500);

        // Act - spin up
        spinner.increment();
        spinner.increment(); // increase to 15500
        page.button.click();

        // Assert
        assertValue(slider, spinner, 15000); // kept to max 15000
        assertConfiguration(slider.getWidgetConfiguration());
    }

    private void assertValue(Slider slider, Spinner spinner, Number value) {
        if (value instanceof Integer) {
            assertEquals(value.intValue(), slider.getValue().intValue());
        }
        else {
            assertEquals(value.floatValue(), slider.getValue().floatValue());
        }
        assertEquals(value, Integer.parseInt(StringUtils.remove(spinner.getValue(), ',')));
    }


    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Slider Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.getBoolean("animate"));
        assertEquals(800, cfg.getInt("min"));
        assertEquals(15000, cfg.getInt("max"));
        assertEquals(500, cfg.getInt("step"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:slider1")
        Slider slider;

        @FindBy(id = "form:spinner1")
        Spinner spinner;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "slider/slider002.xhtml";
        }
    }
}
