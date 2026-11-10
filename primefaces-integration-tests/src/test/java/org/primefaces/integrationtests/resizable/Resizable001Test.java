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
package org.primefaces.integrationtests.resizable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Resizable;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Resizable001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Resizable: resize with the corner handle and resize AJAX event")
    void resize(Page page) {
        // Arrange
        Resizable resizable = page.box;
        assertEquals(new Dimension(200, 100), resizable.getSize());
        assertEquals(List.of("e", "s", "se"), resizable.getHandleDirections());
        assertTrue(resizable.isResizeAjaxified());

        // Act
        resizable.resizeBy(50, 30);

        // Assert
        assertEquals(new Dimension(250, 130), resizable.getSize());
        assertEquals("Resized", page.messages.getMessage(0).getSummary());
        assertEquals("Width:250 Height:130", page.messages.getMessage(0).getDetail());

        // Act
        resizable.resizeBy(-80, -50);

        // Assert
        assertEquals(new Dimension(170, 80), resizable.getSize());
        assertEquals("Width:170 Height:80", page.messages.getMessage(0).getDetail());
        assertConfiguration(resizable.getWidgetConfiguration(), "form:box");
    }

    @Test
    @Order(2)
    @DisplayName("Resizable: resizing respects the minimum and maximum size")
    void minMax(Page page) {
        // Arrange
        Resizable resizable = page.limited;
        assertFalse(resizable.isResizeAjaxified());

        // Act
        resizable.resizeBy(200, 200);

        // Assert
        assertEquals(new Dimension(250, 150), resizable.getSize());

        // Act
        resizable.resizeBy(-200, -120);

        // Assert
        assertEquals(new Dimension(100, 60), resizable.getSize());
        assertTrue(page.messages.isEmpty());

        JSONObject cfg = resizable.getWidgetConfiguration();
        assertConfiguration(cfg, "form:limited");
        assertEquals(100, cfg.getInt("minWidth"));
        assertEquals(250, cfg.getInt("maxWidth"));
    }

    @Test
    @Order(3)
    @DisplayName("Resizable: the east handle only resizes the width")
    void eastHandle(Page page) {
        // Arrange
        Resizable resizable = page.horizontal;
        assertEquals(List.of("e"), resizable.getHandleDirections());

        // Act
        resizable.resizeBy("e", 60, 40);

        // Assert
        assertEquals(new Dimension(260, 100), resizable.getSize());
        assertTrue(page.messages.isEmpty());
        assertConfiguration(resizable.getWidgetConfiguration(), "form:horizontal");
    }

    private void assertConfiguration(JSONObject cfg, String target) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals(target, cfg.getString("target"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:box")
        Resizable box;

        @FindBy(id = "form:limited")
        Resizable limited;

        @FindBy(id = "form:horizontal")
        Resizable horizontal;

        @Override
        public String getLocation() {
            return "resizable/resizable001.xhtml";
        }
    }
}
