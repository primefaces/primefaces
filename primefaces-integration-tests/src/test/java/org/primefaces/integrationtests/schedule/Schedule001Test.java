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
package org.primefaces.integrationtests.schedule;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Schedule;
import org.primefaces.selenium.component.model.Msg;

public class Schedule001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Schedule: show and check for JS-errors")
    public void testBasic(Page page) {
        // Arrange
        Schedule schedule = page.schedule;

        // Act
        schedule.update(); // widget method

        // Assert
        assertConfiguration(schedule.getWidgetConfiguration(), "en");
    }

    @Test
    @Order(2)
    @DisplayName("Schedule: dateSelect")
    public void testDateSelect(Page page) {
        // Arrange
        Schedule schedule = page.schedule;

        // Act
        schedule.select("fc-daygrid-day-top");

        // Assert
        assertMessage(page, "Date selected");
        assertConfiguration(schedule.getWidgetConfiguration(), "en");
    }

    @Test
    @Order(3)
    @DisplayName("Schedule: eventSelect")
    public void testEventSelect(Page page) {
        // Arrange
        Schedule schedule = page.schedule;

        // Act
        schedule.select("fc-daygrid-event");

        // Assert
        assertMessage(page, "Event selected");
        assertConfiguration(schedule.getWidgetConfiguration(), "en");
    }

    @Test
    @Order(4)
    @DisplayName("Schedule: GitHub #6496 locale switching")
    public void testLocales(Page page) {
        // Arrange
        Schedule schedule = page.schedule;

        // Act
        page.buttonEnglish.click();

        // Assert
        assertButton(schedule.getTodayButton(), "Current Date");
        assertButton(schedule.getMonthButton(), "Month");
        assertButton(schedule.getWeekButton(), "Week");
        assertButton(schedule.getDayButton(), "Day");

        // Act
        page.buttonFrench.click();

        // Assert
        assertButton(schedule.getTodayButton(), "Aujourd'hui");
        assertButton(schedule.getMonthButton(), "Mois");
        assertButton(schedule.getWeekButton(), "Semaine");
        assertButton(schedule.getDayButton(), "Jour");
        assertConfiguration(schedule.getWidgetConfiguration(), "fr");
    }

    private void assertButton(WebElement button, String text) {
        Assertions.assertEquals(text, button.getText());
    }

    private void assertMessage(Page page, String message) {
        Msg msg = page.messages.getMessage(0);
        Assertions.assertNotNull(msg, "No messages found!");
        System.out.println("Message = " + msg);
        Assertions.assertTrue(msg.getSummary().contains(message));
    }

    private void assertConfiguration(JSONObject cfg, String locale) {
        assertNoJavascriptErrors();
        Assertions.assertEquals("form", cfg.getString("formId"));
        Assertions.assertEquals(locale, cfg.getString("locale"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:schedule")
        Schedule schedule;

        @FindBy(id = "form:btnEnglish")
        CommandButton buttonEnglish;

        @FindBy(id = "form:btnFrench")
        CommandButton buttonFrench;

        @Override
        public String getLocation() {
            return "schedule/schedule001.xhtml";
        }
    }
}
