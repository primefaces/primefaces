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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;

public class DatePicker004Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: date with time HH:mm:ss. See GitHub #6458 and #6459")
    public void testDateAndTimeWithSeconds(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerSeconds;
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 20, 22, 20, 19), datePicker.getValue());
        LocalDateTime value = LocalDateTime.of(1978, 2, 19, 11, 55, 19);

        // Act
        datePicker.setValue(value);
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel
        assertDate(panel, "February", "1978");
        assertTime(panel, "11", "55", "19");
        datePicker.hidePanel();

        // Assert Submit Value
        page.submitSeconds.click();
        LocalDateTime newValue = datePicker.getValue();
        Assertions.assertEquals(value, newValue);
        // #6459 showTime="true" automatically detected because of LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: date with time HH:mm:ss using widget setDate() API.")
    public void testSetDateWidgetApi(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerSeconds;
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 20, 22, 20, 19), datePicker.getValue());
        LocalDateTime value = LocalDateTime.of(1978, 2, 19, 11, 55, 19);

        // Act
        datePicker.setDate(value);
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel
        assertDate(panel, "February", "1978");
        assertTime(panel, "11", "55", "19");
        datePicker.hidePanel();

        // Assert Submit Value
        page.submitSeconds.click();
        LocalDateTime newValue = datePicker.getValue();
        Assertions.assertEquals(value, newValue);
        // #6459 showTime="true" automatically detected because of LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: date with time HH:mm")
    public void testDateAndTimeWithHours(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerHours;
        Assertions.assertEquals(LocalDateTime.of(2020, 10, 31, 13, 13), datePicker.getValue());
        LocalDateTime value = LocalDateTime.of(1978, 2, 19, 11, 55);

        // Act
        datePicker.setValue(value);
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel
        assertDate(panel, "February", "1978");
        assertTime(panel, "11", "55", null);
        datePicker.hidePanel();

        // Assert Submit Value
        page.submitHours.click();
        LocalDateTime newValue = datePicker.getValue();
        Assertions.assertEquals(value, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: GitHub #6810 showButtonBar='true' Clear button must clear everything.")
    public void testClearButton(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerSeconds;
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 20, 22, 20, 19), datePicker.getValue());

        // Act
        WebElement panel = datePicker.showPanel();

        // Assert Panel
        assertDate(panel, "August", "2020");
        assertTime(panel, "22", "20", "19");
        datePicker.hidePanel();

        // Act - click Clear button
        panel = datePicker.showPanel();
        LocalDateTime now = LocalDateTime.now();
        datePicker.getClearButton().click();

        // Assert - clear button reset to NOW
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
        assertDate(panel, now.getMonth().name(), Objects.toString(now.getYear()));
        assertTime(panel, Objects.toString(now.getHour()), null, null);
        Assertions.assertNull(datePicker.getValue());
    }

    @Test
    @Order(5)
    @DisplayName("DatePicker: GitHub #6810 showButtonBar='true' Today button must set to now.")
    public void testTodayButton(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerSeconds;
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 20, 22, 20, 19), datePicker.getValue());

        // Act
        WebElement panel = datePicker.showPanel();

        // Assert Panel
        assertDate(panel, "August", "2020");
        assertTime(panel, "22", "20", "19");
        datePicker.hidePanel();

        // Act - click Today button
        panel = datePicker.showPanel();
        datePicker.getTodayButton().click();

        // Assert - today button reset to NOW
        LocalDateTime now = LocalDateTime.now();
        assertDate(panel, now.getMonth().name(), Objects.toString(now.getYear()));
        assertTime(panel, "22", "20", "19");
        Assertions.assertNotNull(datePicker.getValue());
    }

    @Test
    @Order(6)
    @DisplayName("DatePicker: GitHub #7448 date with time HH:mm:ss.SSS.")
    public void testDateAndTimeWithMilliseconds(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerMilliseconds;
        Assertions.assertEquals(LocalDateTime.of(2021, 6, 25, 23, 22, 21, 19_000_000), datePicker.getValue());
        LocalDateTime value = LocalDateTime.of(1979, 3, 14, 13, 12, 11, 123_000_000);

        // Act
        datePicker.setValue(value);
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel
        assertDate(panel, "March", "1979");
        assertTime(panel, "13", "12", "11", "123");
        datePicker.hidePanel();

        // Assert Submit Value
        page.submitMilliseconds.click();
        LocalDateTime newValue = datePicker.getValue();
        Assertions.assertEquals(value, newValue);
        // #6459 showTime="true" automatically detected because of LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(7)
    @DisplayName("DatePicker: GitHub #7448 Clear button must clear everything.")
    public void testClearButtonMilliseconds(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerMilliseconds;
        Assertions.assertEquals(LocalDateTime.of(2021, 6, 25, 23, 22, 21, 19_000_000), datePicker.getValue());

        // Act
        WebElement panel = datePicker.showPanel();

        // Assert Panel
        assertDate(panel, "June", "2021");
        assertTime(panel, "23", "22", "21", "019");
        datePicker.hidePanel();

        // Act - click Clear button
        panel = datePicker.showPanel();
        LocalDateTime now = LocalDateTime.now();
        datePicker.getClearButton().click();

        // Assert - clear button reset to NOW
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
        assertDate(panel, now.getMonth().name(), Objects.toString(now.getYear()));
        assertTime(panel, Objects.toString(now.getHour()), null, null);
        Assertions.assertNull(datePicker.getValue());
    }

    @Test
    @Order(8)
    @DisplayName("DatePicker: GitHub #7448 Today button must set to now.")
    public void testTodayButtonMilliseconds(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerMilliseconds;
        Assertions.assertEquals(LocalDateTime.of(2021, 6, 25, 23, 22, 21, 19_000_000), datePicker.getValue());

        // Act
        WebElement panel = datePicker.showPanel();

        // Assert Panel
        assertDate(panel, "June", "2021");
        assertTime(panel, "23", "22", "21", "019");
        datePicker.hidePanel();

        // Act - click Today button
        panel = datePicker.showPanel();
        datePicker.getTodayButton().click();

        // Assert - today button reset to NOW
        LocalDateTime now = LocalDateTime.now();
        assertDate(panel, now.getMonth().name(), Objects.toString(now.getYear()));
        assertTime(panel, "23", "22", "21", "019");
        Assertions.assertNotNull(datePicker.getValue());
    }

    private void assertConfiguration(JSONObject cfg, String defaultDate) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        Assertions.assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
        Assertions.assertEquals(defaultDate, cfg.getString("defaultDate"));
        Assertions.assertEquals("single", cfg.getString("selectionMode"));
        Assertions.assertTrue(cfg.getBoolean("showTime"));
        Assertions.assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form1:dpHours")
        DatePicker datePickerHours;

        @FindBy(id = "form1:btnHours")
        CommandButton submitHours;

        @FindBy(id = "form2:dpSeconds")
        DatePicker datePickerSeconds;

        @FindBy(id = "form2:btnSeconds")
        CommandButton submitSeconds;

        @FindBy(id = "form3:dpMilliseconds")
        DatePicker datePickerMilliseconds;

        @FindBy(id = "form3:btnMilliseconds")
        CommandButton submitMilliseconds;

        @Override
        public String getLocation() {
            return "datepicker/datePicker004.xhtml";
        }
    }
}
