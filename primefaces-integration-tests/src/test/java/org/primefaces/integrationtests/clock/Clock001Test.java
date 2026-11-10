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
package org.primefaces.integrationtests.clock;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Clock;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Clock001Test extends AbstractPrimePageTest {

    private static final String TIME = "\\d{2}:\\d{2}:\\d{2}";
    private static final String DATE_TIME = "\\d{2}\\.\\d{2}\\.\\d{4} " + TIME;

    @Test
    @Order(1)
    @DisplayName("Clock: client mode ticks with the pattern")
    void client(Page page) {
        // Arrange
        Clock clock = page.client;
        assertTrue(clock.isClient());
        assertFalse(clock.isAnalog());

        // Act
        clock.waitForTick();
        String first = clock.getDateTime();
        clock.waitForTick();
        String second = clock.getDateTime();

        // Assert
        assertTrue(first.matches(TIME), first);
        assertTrue(second.matches(TIME), second);
        assertNotEquals(first, second);

        // Act - restart
        clock.stop();
        clock.start();
        clock.waitForTick();

        // Assert
        assertTrue(clock.getDateTime().matches(TIME), clock.getDateTime());
        assertConfiguration(clock.getWidgetConfiguration());
        assertEquals("HH:mm:ss", clock.getWidgetConfiguration().getString("pattern"));
    }

    @Test
    @Order(2)
    @DisplayName("Clock: server mode starts with the value of the server and syncs with the server time")
    void server(Page page) {
        // Arrange
        Clock clock = page.server;
        assertFalse(clock.isClient());

        // Act
        clock.waitForTick();

        // Assert - started at the fixed server value 10:30:00
        String dateTime = clock.getDateTime();
        assertTrue(dateTime.matches("15\\.01\\.2020 10:30:0[1-9]"), dateTime);

        // Act
        clock.sync();

        // Assert - current server time
        dateTime = clock.getDateTime();
        assertTrue(dateTime.matches(DATE_TIME), dateTime);
        assertFalse(dateTime.startsWith("15.01.2020"), dateTime);

        // Act - still ticking after the sync
        clock.waitForTick();

        // Assert
        assertTrue(clock.getDateTime().matches(DATE_TIME), clock.getDateTime());
        JSONObject cfg = clock.getWidgetConfiguration();
        assertConfiguration(cfg);
        assertEquals("2020/01/15 10:30:00", cfg.getString("value"));
    }

    @Test
    @Order(3)
    @DisplayName("Clock: analog clock is drawn and the second hand moves")
    void analog(Page page) {
        // Arrange
        Clock clock = page.analog;
        assertTrue(clock.isAnalog());

        // Assert - face, 12 hour marks, 3 hands, pin
        assertEquals(17, clock.getAnalogElements().size());
        assertEquals("circle", clock.getAnalogElements().get(0).getTagName());
        assertEquals("circle", clock.getAnalogElements().get(16).getTagName());

        // Act
        clock.waitForTick();

        // Assert
        assertConfiguration(clock.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Clock: W pattern is the week in month")
    void weekInMonth(Page page) {
        // Arrange
        Clock clock = page.week;

        // Act
        clock.waitForTick();

        // Assert - 2020-01-15 is in the 3rd week of January 2020 (1-4, 5-11, 12-18)
        String dateTime = clock.getDateTime();
        assertTrue(dateTime.matches("3 10:30:0[1-9]"), dateTime);
        assertConfiguration(clock.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.has("mode"));
        assertTrue(cfg.has("displayMode"));
        assertTrue(cfg.has("locale"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:client")
        Clock client;

        @FindBy(id = "form:server")
        Clock server;

        @FindBy(id = "form:week")
        Clock week;

        @FindBy(id = "form:analog")
        Clock analog;

        @Override
        public String getLocation() {
            return "clock/clock001.xhtml";
        }
    }
}
