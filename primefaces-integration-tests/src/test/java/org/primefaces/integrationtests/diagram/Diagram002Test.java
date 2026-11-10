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
package org.primefaces.integrationtests.diagram;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Diagram;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Diagram002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Diagram: elements and the connection of the model are rendered")
    void initial(Page page) {
        // Arrange
        Diagram diagram = page.diagram;

        // Act
        int connections = diagram.getConnectionCount();

        // Assert
        assertEquals(3, diagram.getElements().size());
        assertEquals("A", diagram.getElement("A").getText());
        assertEquals("B", diagram.getElement("B").getText());
        assertEquals("C", diagram.getElement("C").getText());
        assertEquals(1, connections);
        assertTrue(diagram.isConnected("a-right", "b-left"));
        assertFalse(diagram.isConnected("a-bottom", "c-top"));
        assertEquals("1", page.connections.getText());
        assertConfiguration(diagram.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Diagram: connect fires the connect event and adds the connection to the model")
    void connect(Page page) {
        // Arrange
        Diagram diagram = page.diagram;

        // Act
        diagram.connect("a-bottom", "c-top");

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("Connected", page.messages.getMessage(0).getSummary());
        assertEquals("A-C", page.messages.getMessage(0).getDetail());
        assertEquals("2", page.connections.getText());
        assertEquals(2, diagram.getConnectionCount());
        assertTrue(diagram.isConnected("a-bottom", "c-top"));
        assertConfiguration(diagram.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Diagram: disconnect fires the disconnect event and removes the connection from the model")
    void disconnect(Page page) {
        // Arrange
        Diagram diagram = page.diagram;

        // Act
        diagram.disconnect("a-right", "b-left");

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("Disconnected", page.messages.getMessage(0).getSummary());
        assertEquals("A-B", page.messages.getMessage(0).getDetail());
        assertEquals("0", page.connections.getText());
        assertEquals(0, diagram.getConnectionCount());
        assertFalse(diagram.isConnected("a-right", "b-left"));
        assertConfiguration(diagram.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals(4, cfg.getJSONArray("endPoints").length());
        assertEquals(1, cfg.getJSONArray("connections").length());
        assertEquals(1, cfg.getInt("maxConnections"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:connections")
        WebElement connections;

        @FindBy(id = "form:diagram")
        Diagram diagram;

        @Override
        public String getLocation() {
            return "diagram/diagram002.xhtml";
        }
    }
}
