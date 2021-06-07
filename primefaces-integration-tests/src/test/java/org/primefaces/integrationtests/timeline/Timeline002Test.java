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
package org.primefaces.integrationtests.timeline;

import org.json.JSONArray;
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
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Timeline;
import org.primefaces.selenium.component.model.Msg;

public class Timeline002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Timeline: GitHub #6721 B.C. Dates show and check for JS-errors")
    public void testBCDates_6721(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.select("vis-item-content");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.messages));

        // Assert
        Msg message = page.messages.getMessage(0);
        Assertions.assertEquals("Selected event:", message.getSummary());
        Assertions.assertEquals("164 B.C.", message.getDetail());
        assertConfiguration(timeline.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Timeline Config = " + cfg);
        Assertions.assertTrue(cfg.has("data"));
        JSONArray data = cfg.getJSONArray("data");
        JSONObject event = data.getJSONObject(0);
        Assertions.assertEquals("-000164-06", event.getString("start").substring(0, 10));
        Assertions.assertEquals("164 B.C.", event.get("content"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:timeline")
        Timeline timeline;

        @Override
        public String getLocation() {
            return "timeline/timeline002.xhtml";
        }
    }
}