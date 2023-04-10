/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.integrationtests.panel;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Panel;

public class Panel001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Panel: Close panel")
    public void testClose(Page page) {
        // Arrange
        Panel panel = page.panel;
        Assertions.assertTrue(panel.isDisplayed());

        // Act
        panel.close();

        // Assert
        Assertions.assertFalse(panel.isDisplayed());
        Assertions.assertFalse(page.messages.isEmpty());
        Assertions.assertEquals("Panel Closed", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals("Closed panel id:'panel'", page.messages.getMessage(0).getDetail());
        assertConfiguration(panel.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Panel: Collapse toggle panel")
    public void testCollapse(Page page) {
        // Arrange
        Panel panel = page.panel;
        Assertions.assertTrue(Boolean.valueOf(panel.getHeader().getAttribute("aria-expanded")));

        // Act
        panel.collapse();

        // Assert
        Assertions.assertFalse(Boolean.valueOf(panel.getHeader().getAttribute("aria-expanded")));
        Assertions.assertFalse(page.messages.isEmpty());
        Assertions.assertEquals("panel toggled", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals("Status:HIDDEN", page.messages.getMessage(0).getDetail());
        assertConfiguration(panel.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Panel: Expand toggle panel")
    public void testExpand(Page page) {
        // Arrange
        Panel panel = page.panel;
        panel.collapse();
        Assertions.assertFalse(Boolean.valueOf(panel.getHeader().getAttribute("aria-expanded")));

        // Act
        panel.expand();

        // Assert
        Assertions.assertTrue(Boolean.valueOf(panel.getHeader().getAttribute("aria-expanded")));
        Assertions.assertFalse(page.messages.isEmpty());
        Assertions.assertEquals("panel toggled", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals("Status:VISIBLE", page.messages.getMessage(0).getDetail());
        assertConfiguration(panel.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Panel: Toggle Click")
    public void testToggle(Page page) {
        // Arrange
        Panel panel = page.panel;
        Assertions.assertTrue(Boolean.valueOf(panel.getHeader().getAttribute("aria-expanded")));

        // Act
        panel.toggle();

        // Assert
        Assertions.assertFalse(Boolean.valueOf(panel.getHeader().getAttribute("aria-expanded")));
        Assertions.assertFalse(page.messages.isEmpty());
        Assertions.assertEquals("panel toggled", page.messages.getMessage(0).getSummary());
        Assertions.assertEquals("Status:HIDDEN", page.messages.getMessage(0).getDetail());
        assertConfiguration(panel.getWidgetConfiguration());
    }


    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Panel Config = " + cfg);
        Assertions.assertTrue(cfg.has("widgetVar"));
        Assertions.assertTrue(cfg.getBoolean("closable"));
        Assertions.assertTrue(cfg.getBoolean("toggleable"));
        Assertions.assertEquals(500, cfg.getInt("closeSpeed"));
        Assertions.assertEquals(500, cfg.getInt("toggleSpeed"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:panel")
        Panel panel;

        @Override
        public String getLocation() {
            return "panel/panel001.xhtml";
        }
    }
}
