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
package org.primefaces.integrationtests.dashboard;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Dashboard;
import org.primefaces.selenium.component.Messages;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Dashboard002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dashboard: responsive dashboard swaps two panels with reorder AJAX event")
    void swap(Page page) {
        // Arrange
        Dashboard dashboard = page.dashboard;
        assertTrue(dashboard.isResponsive());
        assertEquals(List.of(List.of("form:bar"), List.of("form:pie"), List.of("form:line")), dashboard.getPanelIds());

        // Act
        dashboard.movePanel(dashboard.getPanel("form:bar"), dashboard.getPanel("form:line"));

        // Assert
        assertEquals(List.of(List.of("form:line"), List.of("form:pie"), List.of("form:bar")), dashboard.getPanelIds());
        assertEquals("Reordered: bar", page.messages.getMessage(0).getSummary());
        assertEquals("Sender:0 Column:2 Item:0", page.messages.getMessage(0).getDetail());
        assertEquals("line|pie|bar", page.layout.getText());
        assertConfiguration(dashboard.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Dashboard: swapped panels must not be lost after AJAX update")
    void swappedAfterUpdate(Page page) {
        // Arrange
        Dashboard dashboard = page.dashboard;
        dashboard.movePanel(dashboard.getPanel("form:bar"), dashboard.getPanel("form:line"));

        // Act
        page.update.click();

        // Assert
        assertEquals(List.of(List.of("form:line"), List.of("form:pie"), List.of("form:bar")), dashboard.getPanelIds());

        // Act - the refreshed widget still swaps
        dashboard.movePanel(dashboard.getPanel("form:pie"), dashboard.getPanel("form:line"));

        // Assert
        assertEquals(List.of(List.of("form:pie"), List.of("form:line"), List.of("form:bar")), dashboard.getPanelIds());
        assertEquals("Reordered: pie", page.messages.getMessage(0).getSummary());
        assertEquals("Sender:1 Column:0 Item:0", page.messages.getMessage(0).getDetail());
        assertEquals("pie|line|bar", page.layout.getText());
        assertConfiguration(dashboard.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.getBoolean("responsive"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:layout")
        WebElement layout;

        @FindBy(id = "form:dashboard")
        Dashboard dashboard;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "dashboard/dashboard002.xhtml";
        }
    }
}
