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
package org.primefaces.integrationtests.schedule;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Schedule;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.component.model.Msg;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

public class Schedule001Test extends AbstractPrimePageTest {

    public static final String ALTERNATIV_SERVER_TIMEZONE = "Europe/Vienna";
    public static final String ALTERNATIV_CLIENT_TIMEZONE = "Europe/Moscow";

    private Schedule001 schedule001;

    @BeforeEach
    void init() {
        schedule001 = new Schedule001();
        schedule001.init();
    }

    @Test
    @Order(1)
    @DisplayName("Schedule: show and check for JS-errors")
    void basic(Page page) {
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
    void dateSelect(Page page) {
        // Arrange
        Schedule schedule = page.schedule;
        page.buttonDeleteAllEvents.click();

        // Act
        schedule.select("fc-daygrid-day-top");

        // Assert
        assertMessage(page, "Date selected");
        assertConfiguration(schedule.getWidgetConfiguration(), "en");

        // Act
        selectSlot(page, "10:00:00");

        // Assert
        Msg msg = page.messages.getMessage(0);
        LocalDate startOfWeek = LocalDate.now();
        while (startOfWeek.getDayOfWeek() != DayOfWeek.SUNDAY) {
            startOfWeek = startOfWeek.minusDays(1);
        }

        String expectedMessage = "T10:00";
        assertTrue(msg.getDetail().endsWith(expectedMessage));

        // check with different clientTimeZone and (server)timeZone - settings ------------------------
        // Arrange
        setDifferingServerAndClientTimezone(page);

        // Act
        selectSlot(page, "10:00:00");

        // Assert
        msg = page.messages.getMessage(0);
        int hour = 10 - calcOffsetInHoursBetweenClientAndServerAndTimezone(startOfWeek.atStartOfDay(ZoneId.of(ALTERNATIV_SERVER_TIMEZONE)));
        // Message is created by server, so we see date selected transferred into server-timezone, what may be confusing from a user perspective

        // different ST/DST-scenarios
        String hourCurrent = String.format("T%02d:00", hour);
        String hourPlus1 = String.format("T%02d:00", hour + 1);
        String hourMinus1 = String.format("T%02d:00", hour - 1);
        boolean hourOK = msg.getDetail().endsWith(hourCurrent) || msg.getDetail().endsWith(hourPlus1) || msg.getDetail().endsWith(hourMinus1);
        assertTrue(hourOK, String.format("Expected: %s or %s or %s , Actual: %s", hourCurrent, hourPlus1, hourMinus1, msg.getDetail()));
    }

    private void selectSlot(Page page, String time) {
        Schedule schedule = page.schedule;
        schedule.findElement(By.className("fc-timeGridWeek-button")).click();
        List<WebElement> slotLaneElements = schedule.findElements(By.cssSelector(".fc-timegrid-slots table tr .fc-timegrid-slot-lane"));
        for (WebElement slotLaneElt : slotLaneElements) {
            if (slotLaneElt.getAttribute("data-time").equals(time)) {
                Actions actions = new Actions(page.getWebDriver());
                actions.moveToElement(slotLaneElt, 1, 1); //click on first day of this week (week starts with sunday)
                PrimeSelenium.guardAjax(actions.click().build()).perform();
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Schedule: eventSelect")
    void eventSelect(Page page) {
        // Arrange
        Schedule schedule = page.schedule;

        // Act
        schedule.select("fc-daygrid-event");

        // Assert
        assertMessage(page, "Event selected");
        assertEquals(schedule001.getEventModel().getEvents().get(0).getTitle(), page.selectedEventTitle.getValue());
        assertEquals(schedule001.getEventModel().getEvents().get(0).getStartDate(), page.selectedEventStartDate.getValue());
        assertEquals(schedule001.getEventModel().getEvents().get(0).getEndDate(), page.selectedEventEndDate.getValue());

        // check with different clientTimeZone and (server)timeZone - settings ------------------------
        // Arrange
        setDifferingServerAndClientTimezone(page);

        // Act
        schedule.select("fc-daygrid-event");

        // Assert
        assertMessage(page, "Event selected");
        assertEquals(schedule001.getEventModel().getEvents().get(0).getTitle(), page.selectedEventTitle.getValue());
        // DatePicker does not recognize clientTimeZone, so we see startDate and endDate transferred into server-timezone,
        // what may be confusing from a user perspective
        assertEquals(schedule001.getEventModel().getEvents().get(0).getStartDate(), page.selectedEventStartDate.getValue());
        assertEquals(schedule001.getEventModel().getEvents().get(0).getEndDate(), page.selectedEventEndDate.getValue());

        assertConfiguration(schedule.getWidgetConfiguration(), "en");
    }

    @Test
    @Order(4)
    @DisplayName("Schedule: GitHub #6496 locale switching")
    void locales(Page page) {
        // Arrange
        Schedule schedule = page.schedule;

        // Act
        page.buttonEnglish.click();

        // Assert
        assertButton(schedule.getTodayButton(), "Today");
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

    @Test
    @Order(5)
    @DisplayName("Schedule: event display - recognizing (server)timeZone and clientTimeZone")
    void eventDisplayRecognizingClientTimeZone(Page page) {
        // Arrange
        Schedule schedule = page.schedule;
        page.buttonGerman.click();

        // Assert
        List<WebElement> todaysEvents = schedule.findElements(By.cssSelector(".fc-day-today .fc-daygrid-event"));
        assertEquals(1, todaysEvents.size());
        String eventTime = todaysEvents.get(0).findElement(By.className("fc-event-time")).getText();
        String eventTitle = todaysEvents.get(0).findElement(By.className("fc-event-title")).getText();

        int eventid = LocalDateTime.now().getDayOfMonth() > 1 ? 1 : 0;
        ScheduleEvent referenceEvent = schedule001.getEventModel().getEvents().get(eventid);
        assertEquals(referenceEvent.getTitle(), eventTitle);
        assertEquals(referenceEvent.getStartDate().getHour() + " Uhr", eventTime);

        // check with different clientTimeZone and (server)timeZone - settings ------------------------
        // Arrange
        setDifferingServerAndClientTimezone(page);

        // Assert
        eventTime = "";
        // Don´t use .fc-day-today because this may already point to the day before or the next day in some time of day constellations
        todaysEvents = schedule.findElements(By.cssSelector(".fc-daygrid-event"));
        for (WebElement eventElt : todaysEvents) {
            if (eventElt.findElement(By.className("fc-event-title")).getText().equals(referenceEvent.getTitle())) {
                eventTime = eventElt.findElement(By.className("fc-event-time")).getText();
            }
        }

        assertEquals(
                (referenceEvent.getStartDate().getHour() + calcOffsetInHoursBetweenClientAndServerAndTimezone(ZonedDateTime.now())) + " Uhr",
                eventTime);
        assertNoJavascriptErrors();
    }

    private void setDifferingServerAndClientTimezone(Page page) {
        page.timeZone.select(ALTERNATIV_SERVER_TIMEZONE);
        page.clientTimeZone.select(ALTERNATIV_CLIENT_TIMEZONE);
    }

    private int calcOffsetInHoursBetweenClientAndServerAndTimezone(ZonedDateTime zonedDateTime) {
        ZonedDateTime zonedDateTimeClient = zonedDateTime.withZoneSameInstant(ZoneId.of(ALTERNATIV_CLIENT_TIMEZONE));
        ZonedDateTime zonedDateTimeServer = zonedDateTime.withZoneSameInstant(ZoneId.of(ALTERNATIV_SERVER_TIMEZONE));
        Duration offsetBetweenClientAndServer = Duration.between(zonedDateTimeServer.toLocalDateTime(), zonedDateTimeClient.toLocalDateTime());
        return (int) offsetBetweenClientAndServer.toHours();
    }

    private void assertButton(WebElement button, String text) {
        assertEquals(text, button.getText());
    }

    private void assertMessage(Page page, String message) {
        Msg msg = page.messages.getMessage(0);
        assertNotNull(msg, "No messages found!");
        System.out.println("Message = " + msg);
        assertTrue(msg.getSummary().contains(message));
    }

    private void assertConfiguration(JSONObject cfg, String locale) {
        assertNoJavascriptErrors();
        assertEquals("form", cfg.getString("formId"));
        assertEquals(locale, cfg.getString("locale"));
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

        @FindBy(id = "form:btnGerman")
        CommandButton buttonGerman;

        @FindBy(id = "form:btnDeleteAllEvents")
        CommandButton buttonDeleteAllEvents;

        @FindBy(id = "form:timeZone")
        SelectOneMenu timeZone;

        @FindBy(id = "form:clientTimeZone")
        SelectOneMenu clientTimeZone;

        @FindBy(id = "form:selectedEventTitle")
        InputText selectedEventTitle;

        @FindBy(id = "form:selectedEventStartDate")
        DatePicker selectedEventStartDate;

        @FindBy(id = "form:selectedEventEndDate")
        DatePicker selectedEventEndDate;

        @Override
        public String getLocation() {
            return "schedule/schedule001.xhtml";
        }
    }
}
