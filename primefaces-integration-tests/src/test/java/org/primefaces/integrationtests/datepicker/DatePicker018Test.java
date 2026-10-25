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
package org.primefaces.integrationtests.datepicker;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DatePicker;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatePicker018Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: GitHub #15324 commits time input when closing the picker")
    void timeInputCommit(Page page) {
        DatePicker datePicker = page.datepicker;
        assertEquals("2026-06-05 15:00:00", page.selectedValue.getText());
        WebElement panel = datePicker.showPanel();
        WebElement minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals("00", minuteInput.getAttribute("value"));
        assertEquals("0", page.dateSelectCount.getText());

        clearInput(minuteInput);
        minuteInput.sendKeys("05");
        assertEquals("0", page.dateSelectCount.getText());

        // The non-focusable outside target leaves the time input focused until the
        // popup closes, so hideOverlay must blur it to fire the normal change handler.
        awaitOutsideClickHandled(datePicker);
        PrimeSelenium.guardAjax(page.outside).click();
        assertEquals("2026-06-05 15:05:00", page.selectedValue.getText());
        assertEquals("1", page.dateSelectCount.getText());

        panel = datePicker.showPanel();
        minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals("05", minuteInput.getAttribute("value"));
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    // showOverlay ignores document clicks for 200ms after the panel opens (datepickerClick), so an
    // outside click sent sooner leaves the panel open and the typed minutes are never committed.
    private void awaitOutsideClickHandled(DatePicker datePicker) {
        PrimeSelenium.waitGui().until(d -> Boolean.FALSE.equals(PrimeSelenium.executeScript(
                "return " + datePicker.getWidgetByIdScript() + ".datepickerClick;")));
    }

    private void clearInput(WebElement input) {
        Keys modifier = PrimeSelenium.isSafari() ? Keys.COMMAND : Keys.CONTROL;
        input.sendKeys(modifier, "a", Keys.BACK_SPACE);
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertEquals("dd/mm/yy", cfg.getString("dateFormat"));
        assertTrue(cfg.getBoolean("timeInput"));
        assertTrue(cfg.getBoolean("showSeconds"));
        assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datepicker;

        @FindBy(id = "form:selectedValue")
        WebElement selectedValue;

        @FindBy(id = "form:dateSelectCount")
        WebElement dateSelectCount;

        @FindBy(id = "outside")
        WebElement outside;

        @Override
        public String getLocation() {
            return "datepicker/datePicker018.xhtml";
        }
    }
}
