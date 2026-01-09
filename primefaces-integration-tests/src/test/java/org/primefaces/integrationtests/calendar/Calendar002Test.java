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
package org.primefaces.integrationtests.calendar;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Calendar;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Calendar002Test extends AbstractPrimePageTest {
    @Test
    @Order(1)
    @DisplayName("Calendar: No AJAX on leaving input")
    void leaveInputWithoutChange(Page page) {
        assertDoesNotThrow(() -> {
            // Arrange
            Calendar datePicker = page.datePicker;
            WebElement outside = page.outside;
            Messages messages = page.messages;

            // Act
            datePicker.getInput().click();
            outside.click(); // not guarded here as ideally no ajax should occur
            PrimeSelenium.wait(100);

            // Assert
            assertNotDisplayed(messages);
            assertConfiguration(datePicker.getWidgetConfiguration());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Calendar: AJAX on leaving input after changes")
    void leaveInputWithChange(Page page) {
        assertDoesNotThrow(() -> {
            // Arrange
            Calendar datePicker = page.datePicker;
            WebElement outside = page.outside;
            Messages messages = page.messages;

            // Act
            datePicker.getInput().click();
            datePicker.getInput().sendKeys(Keys.chord(PrimeSelenium.isSafari() ? Keys.COMMAND : Keys.CONTROL, "a"));
            datePicker.getInput().sendKeys("01/01/2023");
            PrimeSelenium.guardAjax(outside).click();

            // Assert
            assertDisplayed(messages);
            assertConfiguration(datePicker.getWidgetConfiguration());
        });
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Calendar Config = " + cfg);
        assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:outside")
        WebElement outside;

        @FindBy(id = "form:datepicker")
        Calendar datePicker;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "calendar/calendar002.xhtml";
        }
    }
}
