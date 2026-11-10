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
package org.primefaces.integrationtests.mindmap;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Mindmap;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Mindmap001Test extends AbstractPrimePageTest {

    private static final String SPECIAL_LABEL = "\"\\</script>";

    @Test
    @Order(1)
    @DisplayName("Mindmap: root and its child nodes are drawn")
    void render(Page page) {
        // Arrange
        Mindmap mindmap = page.mindmap;

        // Assert
        assertEquals("Root", mindmap.getCenterNodeLabel());
        assertEquals(List.of("Root", "A", "B", "C", SPECIAL_LABEL), mindmap.getNodeLabels());
        assertEquals("text", mindmap.getNode("A").getTagName());
        assertTrue(mindmap.isSelectAjaxified());
        assertTrue(mindmap.isDblselectAjaxified());
        assertConfiguration(mindmap.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Mindmap: select loads the node with AJAX and draws it in the center")
    void select(Page page) {
        // Arrange
        Mindmap mindmap = page.mindmap;

        // Act
        mindmap.selectNode("A");

        // Assert - child nodes loaded lazily by the listener, parent node to navigate back
        assertEquals("Selected", page.messages.getMessage(0).getSummary());
        assertEquals("A", page.messages.getMessage(0).getDetail());
        assertEquals("A", mindmap.getCenterNodeLabel());
        assertEquals(List.of("A", "A1", "A2", "Root"), mindmap.getNodeLabels());

        // Act - back to the parent
        mindmap.selectNode("Root");

        // Assert
        assertEquals("Root", page.messages.getMessage(0).getDetail());
        assertEquals("Root", mindmap.getCenterNodeLabel());
        assertEquals(List.of("Root", "A", "B", "C", SPECIAL_LABEL), mindmap.getNodeLabels());
        assertConfiguration(mindmap.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Mindmap: double click fires dblselect instead of select")
    void dblselect(Page page) {
        // Arrange
        Mindmap mindmap = page.mindmap;

        // Act
        mindmap.doubleClickNode("B");

        // Assert
        assertEquals("Double selected", page.messages.getMessage(0).getSummary());
        assertEquals("B", page.messages.getMessage(0).getDetail());
        assertEquals("Root", mindmap.getCenterNodeLabel());
        assertEquals(List.of("Root", "A", "B", "C", SPECIAL_LABEL), mindmap.getNodeLabels());
        assertConfiguration(mindmap.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Mindmap: label with quote, backslash and </script> is escaped and rendered as text")
    void escapedLabel(Page page) {
        // Arrange
        Mindmap mindmap = page.mindmap;

        // Assert - drawn as text
        assertEquals("text", mindmap.getNode(SPECIAL_LABEL).getTagName());

        // Act
        mindmap.selectNode(SPECIAL_LABEL);

        // Assert - select response is parsed
        assertEquals(SPECIAL_LABEL, page.messages.getMessage(0).getDetail());
        assertEquals(SPECIAL_LABEL, mindmap.getCenterNodeLabel());
        assertEquals(List.of(SPECIAL_LABEL, "Root"), mindmap.getNodeLabels());
        assertConfiguration(mindmap.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals(0, cfg.getInt("effectSpeed"));
        assertEquals("Root", cfg.getJSONObject("model").getString("label"));
        assertEquals(4, cfg.getJSONObject("model").getJSONArray("children").length());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:mindmap")
        Mindmap mindmap;

        @Override
        public String getLocation() {
            return "mindmap/mindmap001.xhtml";
        }
    }
}
