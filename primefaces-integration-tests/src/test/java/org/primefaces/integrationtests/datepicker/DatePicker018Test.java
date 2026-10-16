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
    @DisplayName("DatePicker: completed time input updates once and survives closing")
    void timeInputKeyUp(Page page) {
        // Arrange
        DatePicker datePicker = page.datepicker;
        assertEquals("2026-06-05 15:00:00", page.selectedValue.getText());
        WebElement panel = datePicker.showPanel();
        WebElement hourInput = panel.findElement(By.cssSelector(".ui-hour-picker input"));
        WebElement minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals("03", hourInput.getAttribute("value"));
        assertEquals("00", minuteInput.getAttribute("value"));

        // Clearing a field must leave it editable until the replacement is complete.
        hourInput.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
        assertEquals("", hourInput.getAttribute("value"));
        assertEquals("2026-06-05 15:00:00", page.selectedValue.getText());

        // Act: typing 04 in a 12-hour field must preserve the current PM half-day.
        PrimeSelenium.guardAjax(hourInput).sendKeys("04");

        // Assert the AJAX behavior has received the typed value before the panel closes.
        assertEquals("2026-06-05 16:00:00", page.selectedValue.getText());

        minuteInput.sendKeys(Keys.CONTROL, "a", Keys.BACK_SPACE);
        assertEquals("", minuteInput.getAttribute("value"));
        assertEquals("2026-06-05 16:00:00", page.selectedValue.getText());
        PrimeSelenium.guardAjax(minuteInput).sendKeys("05");
        assertEquals("2026-06-05 16:05:00", page.selectedValue.getText());

        // Act
        page.outside.click();
        datePicker.showPanel();

        // Assert
        WebElement reopenedMinuteInput = datePicker.getPanel().findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals("04", datePicker.getPanel().findElement(By.cssSelector(".ui-hour-picker input")).getAttribute("value"));
        assertEquals("05", reopenedMinuteInput.getAttribute("value"));
        assertConfiguration(datePicker.getWidgetConfiguration());
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

        @FindBy(id = "outside")
        WebElement outside;

        @Override
        public String getLocation() {
            return "datepicker/datePicker018.xhtml";
        }
    }
}
