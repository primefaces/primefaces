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
package org.primefaces.integrationtests.organigram;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Organigram;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Organigram001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Organigram: nodes are drawn from the model")
    void render(Page page) {
        // Arrange
        Organigram organigram = page.organigram;

        // Assert
        assertEquals(List.of("Company", "Development", "Alice", "Bob", "QA", "Carol", "Marketing", "Dave"), organigram.getNodeTexts());
        for (WebElement node : organigram.getNodes()) {
            assertTrue(node.isDisplayed());
        }
        assertTrue(organigram.isExpanded("Company"));
        assertTrue(organigram.isExpanded("Development"));
        assertFalse(organigram.isExpanded("Alice"));
        assertNull(organigram.getSelectedNodeText());
        assertConfiguration(organigram.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Organigram: select a node with AJAX, selection must not be lost after AJAX update")
    void select(Page page) {
        // Arrange
        Organigram organigram = page.organigram;

        // Act
        organigram.select("QA");

        // Assert
        assertTrue(organigram.isSelected("QA"));
        assertEquals("QA", organigram.getSelectedNodeText());
        assertEquals("Selected", page.messages.getMessage(0).getSummary());
        assertEquals("QA", page.messages.getMessage(0).getDetail());
        assertEquals("QA", page.selection.getText());

        // Act
        organigram.select("Dave");

        // Assert - only one node is selected
        assertEquals("Dave", organigram.getSelectedNodeText());
        assertFalse(organigram.isSelected("QA"));
        assertEquals("Dave", page.selection.getText());

        // Act
        page.update.click();

        // Assert
        assertEquals("Dave", organigram.getSelectedNodeText());
        assertConfiguration(organigram.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Organigram: collapse and expand with AJAX, collapsed state must not be lost after AJAX update")
    void collapseAndExpand(Page page) {
        // Arrange
        Organigram organigram = page.organigram;
        assertTrue(organigram.getNode("Alice").isDisplayed());

        // Act
        organigram.collapse("Development");

        // Assert
        assertFalse(organigram.isExpanded("Development"));
        assertFalse(organigram.getNode("Alice").isDisplayed());
        assertFalse(organigram.getNode("Carol").isDisplayed());
        assertTrue(organigram.getNode("Dave").isDisplayed());
        assertEquals("Collapsed", page.messages.getMessage(0).getSummary());
        assertEquals("Development", page.messages.getMessage(0).getDetail());

        // Act
        page.update.click();

        // Assert
        assertFalse(organigram.isExpanded("Development"));
        assertFalse(organigram.getNode("Alice").isDisplayed());

        // Act
        organigram.expand("Development");

        // Assert
        assertTrue(organigram.isExpanded("Development"));
        assertTrue(organigram.getNode("Alice").isDisplayed());
        assertTrue(organigram.getNode("Carol").isDisplayed());
        assertEquals("Expanded", page.messages.getMessage(0).getSummary());
        assertEquals("Development", page.messages.getMessage(0).getDetail());
        assertConfiguration(organigram.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertFalse(cfg.getBoolean("zoom"));
        assertEquals(10, cfg.getInt("leafNodeConnectorHeight"));
        assertFalse(cfg.getBoolean("autoScrollToSelection"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:organigram")
        Organigram organigram;

        @FindBy(id = "form:selection")
        WebElement selection;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "organigram/organigram001.xhtml";
        }
    }
}
