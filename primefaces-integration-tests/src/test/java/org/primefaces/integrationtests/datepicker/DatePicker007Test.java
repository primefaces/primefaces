/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.time.LocalTime;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;

public class DatePicker007Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: GitHub #6636 TimeOnly at 12AM issue")
    public void testBasic(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        Assertions.assertEquals(LocalTime.of(0, 4), datePicker.getValue().toLocalTime());

        // Act
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel (12:04 AM)
        Assertions.assertNotNull(panel);
        assertTime(panel, "12", "04", null);
        Assertions.assertTrue(panel.getText().contains("AM"));

        // Act (go down by 1 hour)
        WebElement hourPicker = panel.findElement(By.className("ui-hour-picker"));
        hourPicker.findElement(By.className("ui-picker-down")).click();

        // Assert (new time should be 11:04 PM)
        assertTime(panel, "11", "04", null);
        Assertions.assertTrue(panel.getText().contains("PM"));
        assertConfiguration(datePicker.getWidgetConfiguration(), "12:04 AM");
    }

    private void assertConfiguration(JSONObject cfg, String defaultDate) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        Assertions.assertEquals(defaultDate, cfg.getString("defaultDate"));
        Assertions.assertEquals("single", cfg.getString("selectionMode"));
        Assertions.assertFalse(cfg.getBoolean("inline"));
        Assertions.assertTrue(cfg.getBoolean("timeOnly"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker007.xhtml";
        }
    }
}
