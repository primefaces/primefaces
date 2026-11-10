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

class Dashboard001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Dashboard: reorder a panel within its column with reorder AJAX event")
    void reorderInColumn(Page page) {
        // Arrange
        Dashboard dashboard = page.dashboard;
        assertFalse(dashboard.isResponsive());
        assertTrue(dashboard.isReordering());
        assertEquals(List.of(
                    List.of("form:sports", "form:finance"),
                    List.of("form:lifestyle", "form:weather"),
                    List.of("form:politics")), dashboard.getPanelIds());

        // Act
        dashboard.movePanel(dashboard.getPanel("form:finance"), dashboard.getPanel("form:sports"));

        // Assert
        assertEquals(List.of(
                    List.of("form:finance", "form:sports"),
                    List.of("form:lifestyle", "form:weather"),
                    List.of("form:politics")), dashboard.getPanelIds());
        assertEquals("Reordered: finance", page.messages.getMessage(0).getSummary());
        assertEquals("Sender:null Column:0 Item:0", page.messages.getMessage(0).getDetail());
        assertEquals("finance,sports|lifestyle,weather|politics", page.layout.getText());
        assertConfiguration(dashboard.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Dashboard: move panels to other columns with reorder AJAX event")
    void moveToOtherColumn(Page page) {
        // Arrange
        Dashboard dashboard = page.dashboard;

        // Act - before the first panel of another column
        dashboard.movePanel(dashboard.getPanel("form:politics"), dashboard.getPanel("form:lifestyle"));

        // Assert
        assertEquals(List.of(
                    List.of("form:sports", "form:finance"),
                    List.of("form:politics", "form:lifestyle", "form:weather"),
                    List.of()), dashboard.getPanelIds());
        assertEquals("Reordered: politics", page.messages.getMessage(0).getSummary());
        assertEquals("Sender:2 Column:1 Item:0", page.messages.getMessage(0).getDetail());
        assertEquals("sports,finance|politics,lifestyle,weather|", page.layout.getText());

        // Act - into the now empty column
        dashboard.movePanelToColumn(dashboard.getPanel("form:sports"), 2);

        // Assert
        assertEquals(List.of(
                    List.of("form:finance"),
                    List.of("form:politics", "form:lifestyle", "form:weather"),
                    List.of("form:sports")), dashboard.getPanelIds());
        assertEquals("Reordered: sports", page.messages.getMessage(0).getSummary());
        assertEquals("Sender:0 Column:2 Item:0", page.messages.getMessage(0).getDetail());
        assertEquals("finance|politics,lifestyle,weather|sports", page.layout.getText());
        assertConfiguration(dashboard.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Dashboard: reordered panels must not be lost after AJAX update")
    void reorderedAfterUpdate(Page page) {
        // Arrange
        Dashboard dashboard = page.dashboard;
        dashboard.movePanel(dashboard.getPanel("form:finance"), dashboard.getPanel("form:sports"));

        // Act
        page.update.click();

        // Assert
        assertEquals(List.of(
                    List.of("form:finance", "form:sports"),
                    List.of("form:lifestyle", "form:weather"),
                    List.of("form:politics")), dashboard.getPanelIds());

        // Act - the refreshed widget still reorders
        dashboard.movePanelToColumn(dashboard.getPanel("form:weather"), 2);

        // Assert
        assertEquals(List.of(
                    List.of("form:finance", "form:sports"),
                    List.of("form:lifestyle"),
                    List.of("form:politics", "form:weather")), dashboard.getPanelIds());
        assertEquals("Sender:1 Column:2 Item:1", page.messages.getMessage(0).getDetail());
        assertEquals("finance,sports|lifestyle|politics,weather", page.layout.getText());
        assertConfiguration(dashboard.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Dashboard: panels can not be dragged without reordering")
    void notReordering(Page page) {
        // Arrange
        Dashboard dashboard = page.locked;
        assertFalse(dashboard.isReordering());

        // Act
        dashboard.movePanel(dashboard.getPanel("form:music"), dashboard.getPanel("form:news"));

        // Assert
        assertEquals(List.of(List.of("form:news", "form:music")), dashboard.getPanelIds());
        assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertTrue(cfg.has("behaviors"));
        assertFalse(cfg.optBoolean("responsive", false));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:layout")
        WebElement layout;

        @FindBy(id = "form:dashboard")
        Dashboard dashboard;

        @FindBy(id = "form:locked")
        Dashboard locked;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "dashboard/dashboard001.xhtml";
        }
    }
}
