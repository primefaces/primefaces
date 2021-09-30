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

import java.time.LocalDate;

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
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

public class DatePicker008Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: AJAX set date")
    public void testAjaxBasic(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        Assertions.assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());
        LocalDate value = LocalDate.of(1985, 7, 4);

        // Act
        datePicker.setValue(value);
        WebElement panel = datePicker.showPanel();

        // Assert
        assertDate(panel, "July", "1985");
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(value, newValue);
        assertMessage(page, "1985-07-04");
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: AJAX close popup")
    public void testAjaxClosePopup(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        Assertions.assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());
        LocalDate value = LocalDate.of(1985, 7, 4);

        // Act
        datePicker.setValue(value);
        WebElement panel = datePicker.showPanel();
        datePicker.hidePanel();

        // Assert
        assertNotDisplayed(panel);
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: AJAX select date via click on day in the next month")
    public void testAjaxSelectDateNextMonth(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDate value = LocalDate.of(1985, 7, 4);

        // Act
        datePicker.setValue(value);
        datePicker.showPanel();
        datePicker.getNextMonthLink().click();
        datePicker.selectDay("31");

        // Assert
        LocalDate expectedDate = LocalDate.of(1985, 8, 31);
        Assertions.assertEquals(expectedDate, datePicker.getValueAsLocalDate());
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(expectedDate, newValue);
        assertMessage(page, "1985-08-31");
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: AJAX select date via click on day in the previous month")
    public void testAjaxSelectDatePreviousMonth(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDate value = LocalDate.of(1985, 7, 4);

        // Act
        datePicker.setValue(value);
        datePicker.showPanel();
        datePicker.getPreviousMonthLink().click();
        datePicker.selectDay("8");

        // Assert selected value
        LocalDate expectedDate = LocalDate.of(1985, 6, 8);
        Assertions.assertEquals(expectedDate, datePicker.getValueAsLocalDate());
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(expectedDate, newValue);
        assertMessage(page, "1985-06-08");
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DatePicker: AJAX date using widget setDate() API.")
    public void testAjaxSetDateWidgetApi(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        Assertions.assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());
        LocalDate value = LocalDate.of(1985, 7, 4);

        // Act
        datePicker.setDate(value.atStartOfDay());
        datePicker.hidePanel();
        datePicker.showPanel();

        // Assert
        assertDate(datePicker.getPanel(), "July", "1985");
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(value, newValue);
        assertMessage(page, "1985-07-04");
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    private void assertMessage(Page page, String message) {
        Messages messages = page.messages;
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(messages));
        Msg msg = messages.getMessage(0);
        Assertions.assertEquals(message, msg.getDetail());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        Assertions.assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
        Assertions.assertEquals("single", cfg.getString("selectionMode"));
        Assertions.assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "datepicker/datePicker008.xhtml";
        }
    }
}
