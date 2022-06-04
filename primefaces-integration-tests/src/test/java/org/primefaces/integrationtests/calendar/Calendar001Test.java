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
package org.primefaces.integrationtests.calendar;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Calendar;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

public class Calendar001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Calendar: AJAX set date")
    public void testAjaxBasic(Page page) {
        // Arrange
        Calendar datePicker = page.datePicker;
        Assertions.assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());
        LocalDate value = LocalDate.of(1985, 7, 4);

        // Act
        datePicker.setValue(value);

        // Assert
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
        System.out.println("Calendar Config = " + cfg);
        Assertions.assertEquals("m/d/yy", cfg.getString("dateFormat"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        Calendar datePicker;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "calendar/calendar001.xhtml";
        }
    }
}
