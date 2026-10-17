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
    @DisplayName("DatePicker: GitHub #15324 commits time input once when leaving the panel")
    void timeInputCommit(Page page) {
        // Arrange
        DatePicker datePicker = page.datepicker;
        assertEquals("2026-06-05 15:00:00", page.selectedValue.getText());
        WebElement panel = datePicker.showPanel();
        WebElement hourInput = panel.findElement(By.cssSelector(".ui-hour-picker input"));
        WebElement minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals("03", hourInput.getAttribute("value"));
        assertEquals("00", minuteInput.getAttribute("value"));
        assertEquals("1", hourInput.getAttribute("min"));
        assertEquals("12", hourInput.getAttribute("max"));
        assertEquals("0", page.dateSelectCount.getText());

        // Clearing a field must leave it editable until the replacement is complete.
        clearInput(hourInput);
        assertEquals("", hourInput.getAttribute("value"));
        assertEquals("2026-06-05 15:00:00", page.selectedValue.getText());

        // Time edits stay local while focus moves between fields, so an AJAX update
        // cannot interrupt typing or steal focus between the hour and minute.
        hourInput.sendKeys("04");
        assertEquals("0", page.dateSelectCount.getText());
        clearInput(minuteInput);
        minuteInput.sendKeys("05");
        assertEquals("0", page.dateSelectCount.getText());

        // Leaving the panel commits both fields with one dateSelect request.
        PrimeSelenium.guardAjax(page.outside).click();
        assertEquals("2026-06-05 16:05:00", page.selectedValue.getText());
        assertEquals("1", page.dateSelectCount.getText());

        // A single digit is a valid value when Enter explicitly commits the field.
        panel = datePicker.showPanel();
        minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        clearInput(minuteInput);
        minuteInput.sendKeys("6");
        assertEquals("1", page.dateSelectCount.getText());
        PrimeSelenium.guardAjax(minuteInput).sendKeys(Keys.ENTER);
        assertEquals("2026-06-05 16:06:00", page.selectedValue.getText());
        assertEquals("2", page.dateSelectCount.getText());

        // A context-menu paste has no keyup event; leaving the panel must still
        // read and commit the pasted value.
        panel = datePicker.showPanel();
        minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        PrimeSelenium.executeScript(
                "arguments[0].focus(); arguments[0].value = '07'; "
                + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                minuteInput);
        PrimeSelenium.guardAjax(page.outside).click();
        assertEquals("2026-06-05 16:07:00", page.selectedValue.getText());
        assertEquals("3", page.dateSelectCount.getText());

        datePicker.showPanel();

        // Assert
        WebElement reopenedMinuteInput = datePicker.getPanel().findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals("04", datePicker.getPanel().findElement(By.cssSelector(".ui-hour-picker input")).getAttribute("value"));
        assertEquals("07", reopenedMinuteInput.getAttribute("value"));
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: range time input does not display an unapplied value")
    void rangeTimeInput(Page page) {
        WebElement panel = page.rangeDatepicker.showPanel();
        WebElement minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        String originalMinute = minuteInput.getAttribute("value");

        clearInput(minuteInput);
        minuteInput.sendKeys("30");
        page.outside.click();

        panel = page.rangeDatepicker.showPanel();
        minuteInput = panel.findElement(By.cssSelector(".ui-minute-picker input"));
        assertEquals(originalMinute, minuteInput.getAttribute("value"));
        assertNoJavascriptErrors();
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

        @FindBy(id = "form:rangeDatepicker")
        DatePicker rangeDatepicker;

        @FindBy(id = "outside")
        WebElement outside;

        @Override
        public String getLocation() {
            return "datepicker/datePicker018.xhtml";
        }
    }
}
