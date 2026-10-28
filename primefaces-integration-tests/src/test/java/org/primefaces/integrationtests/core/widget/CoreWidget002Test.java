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
package org.primefaces.integrationtests.core.widget;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DatePicker;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CoreWidget002Test extends AbstractWidgetLifecycleTest {

    private static final String[] WIDGETS = {
        "form:datePickerBody", "form:datePicker", "form:slider", "form:knob", "form:signature", "form:spinner"
    };

    private static final String UPDATE = "PrimeFaces.ab({s: 'form:btnUpdate', p: 'form:btnUpdate', u: 'form:wrapper'});";
    private static final String TOGGLE = "PrimeFaces.ab({s: 'form:btnToggle', p: 'form:btnToggle', u: 'form:wrapper'});";

    @Test
    @Order(1)
    @DisplayName("Widget lifecycle: AJAX updates of a DatePicker with an open overlay don't leak its global listeners")
    void updateOpenDatePicker(Page page) {
        for (DatePicker datePicker : new DatePicker[] {page.datePickerBody, page.datePicker}) {
            // Arrange: open and close once, jQuery keeps an internal focus handler on the document after the first one
            datePicker.showPanel();
            datePicker.hidePanel();
            JSONObject initialState = globalState();

            // Act: update while the overlay is open
            datePicker.showPanel();
            PrimeSelenium.executeScript(true, UPDATE);

            // Assert: one closed panel, the global listeners of the open overlay are gone
            assertEquals(1L, panelCount(datePicker.getId()), "panels of " + datePicker.getId());
            assertFalse(datePicker.getPanel().isDisplayed());
            assertSameState(initialState, globalState());

            // Assert: the date picker still works
            datePicker.showPanel();
            datePicker.selectDay("5");
            assertEquals(LocalDate.now().withDayOfMonth(5), datePicker.getValueAsLocalDate());
            datePicker.hidePanel();
        }
        assertWidgetCount(1, WIDGETS);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Widget lifecycle: removing a DatePicker with an open overlay removes its panel and global listeners")
    void removeOpenDatePicker(Page page) {
        // Arrange: the state without the widgets (after the internal focus handler of jQuery was added, see above)
        page.datePickerBody.showPanel();
        page.datePickerBody.hidePanel();
        PrimeSelenium.executeScript(true, TOGGLE);
        JSONObject removed = globalState();
        assertWidgetCount(0, WIDGETS);
        PrimeSelenium.executeScript(true, TOGGLE);
        assertWidgetCount(1, WIDGETS);

        // Act: remove while the overlay appended to the body is open
        page.datePickerBody.showPanel();
        PrimeSelenium.executeScript(true, TOGGLE);

        // Assert
        assertEquals(0L, panelCount("form:datePickerBody"));
        assertSameState(removed, globalState());
        assertWidgetCount(0, WIDGETS);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Widget lifecycle: an updated KeyFilter replaces its handlers on the input that stays")
    void updateKeyFilter(Page page) {
        // Act
        for (int i = 0; i < 3; i++) {
            PrimeSelenium.executeScript(true, UPDATE);
        }

        // Assert
        assertEquals(1L, (Long) PrimeSelenium.executeScript(
                    "return jQuery._data(document.getElementById('form:filtered'), 'events').beforeinput.length;"));
        page.filtered.sendKeys("a1b2c3");
        assertEquals("123", page.filtered.getAttribute("value"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Widget lifecycle: Knob, Slider, Signature and Spinner render once and still work after AJAX updates")
    void updateInputs(Page page) {
        // Arrange
        JSONObject initialState = globalState();

        // Act
        for (int i = 0; i < 3; i++) {
            PrimeSelenium.executeScript(true, UPDATE);
        }

        // Assert: rendered once
        assertSameState(initialState, globalState());
        assertEquals(1L, count("[id='form:knob'] svg"));
        assertEquals(1L, count("[id='form:slider'] .p-slider-handle"));
        assertEquals(1L, count("[id='form:signature'] canvas"));
        assertEquals(1L, (Long) PrimeSelenium.executeScript(
                    "return jQuery._data(document.getElementById('form:spinner_input'), 'events').wheel.length;"));

        // Assert: the knob and the slider still react to the keyboard
        page.knobSvg.sendKeys(Keys.ARROW_UP);
        assertEquals("41", page.knobSvg.getAttribute("aria-valuenow"));
        page.sliderHandle.sendKeys(Keys.ARROW_RIGHT);
        assertEquals("31", page.sliderHandle.getAttribute("aria-valuenow"));
        assertEquals("31", page.sliderInput.getAttribute("value"));
        assertWidgetCount(1, WIDGETS);
        assertNoJavascriptErrors();
    }

    private long panelCount(String clientId) {
        return (Long) PrimeSelenium.executeScript("return document.querySelectorAll('[id=' + JSON.stringify(arguments[0] + '_panel') + ']').length;",
                    clientId);
    }

    private long count(String selector) {
        return (Long) PrimeSelenium.executeScript("return document.querySelectorAll(arguments[0]).length;", selector);
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datePickerBody")
        DatePicker datePickerBody;

        @FindBy(id = "form:datePicker")
        DatePicker datePicker;

        @FindBy(id = "form:filtered")
        WebElement filtered;

        @FindBy(css = "[id='form:knob'] svg")
        WebElement knobSvg;

        @FindBy(css = "[id='form:slider'] .p-slider-handle")
        WebElement sliderHandle;

        @FindBy(id = "form:sliderInput")
        WebElement sliderInput;

        @Override
        public String getLocation() {
            return "core/widget/coreWidget002.xhtml";
        }
    }
}
