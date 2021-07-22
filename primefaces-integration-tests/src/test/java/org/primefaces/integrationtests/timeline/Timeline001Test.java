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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Timeline;
import org.primefaces.selenium.component.model.Msg;

public class Timeline001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Timeline: show and check for JS-errors")
    public void testBasic(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.update();

        // Assert
        long eventCount = timeline.getNumberOfEvents();
        Assertions.assertEquals(15, eventCount);
        assertConfiguration(timeline.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Timeline: select-event")
    public void testSelect(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.select("vis-item-content");

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.messages));
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected event:"));
        assertConfiguration(timeline.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Timeline: Zoom In GitHub #6902/#5455 range changed event with and without CSP")
    public void testRangeChangedZoomIn(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.zoom(0.1);

        // Assert
        assertRangeChanged(page, "Range Changed:", "2014");
    }

    @Test
    @Order(4)
    @DisplayName("Timeline: Zoom Out range changed event with and without CSP")
    public void testRangeChangedZoomOut(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.zoom(-0.1);

        // Assert
        assertRangeChanged(page, "Range Changed:", "2014");
    }

    @Test
    @Order(5)
    @DisplayName("Timeline: Move Left range changed event with and without CSP")
    public void testRangeChangedMoveLeft(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.move(-0.1);

        // Assert
        assertRangeChanged(page, "Range Changed:", "2014");
    }

    @Test
    @Order(6)
    @DisplayName("Timeline: Move Right range changed event with and without CSP")
    public void testRangeChangedMoveRight(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        timeline.move(0.1);

        // Assert
        assertRangeChanged(page, "Range Changed:", "2014");
    }

    private void assertRangeChanged(Page page, String detail, String summary) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.messages));
        Msg message = page.messages.getMessage(0);
        Assertions.assertEquals(detail, message.getSummary());
        Assertions.assertTrue(message.getDetail().contains(summary));
        assertConfiguration(page.timeline.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Timeline Config = " + cfg);
        Assertions.assertTrue(cfg.has("data"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:timeline")
        Timeline timeline;

        @Override
        public String getLocation() {
            return "timeline/timeline001.xhtml";
        }
    }
}