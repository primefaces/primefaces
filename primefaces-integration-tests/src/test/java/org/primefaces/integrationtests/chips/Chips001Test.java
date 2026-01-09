/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.integrationtests.chips;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Chips;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Chips001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Chips: add and remove value using AJAX")
    void basic(Page page) {
        // Arrange
        Chips chips = page.chips;
        assertDisplayed(chips);

        // Assert initial state
        List<String> values = chips.getValues();
        assertEquals(2, values.size());
        assertEquals("Defect", values.get(0));
        assertEquals("Feature", values.get(1));

        // Act - add value
        chips.addValue("Question");

        // Assert - itemSelect-event
        assertEquals("itemSelect", page.messages.getMessage(0).getSummary());
        assertEquals("Question", page.messages.getMessage(0).getDetail());

        // Act - submit
        page.button.click();

        // Assert
        assertEquals("Defect, Feature, Question", page.messages.getMessage(0).getSummary());

        // Act - remove value
        chips.removeValue("Defect");

        // Assert - itemSelect-event
        assertEquals("itemUnselect", page.messages.getMessage(0).getSummary());
        assertEquals("Defect", page.messages.getMessage(0).getDetail());

        // Act - submit
        page.button.click();

        // Assert
        assertEquals("Feature, Question", page.messages.getMessage(0).getSummary());
        values = chips.getValues();
        assertEquals(2, values.size());
        assertEquals("Feature", values.get(0));
        assertEquals("Question", values.get(1));

        assertConfiguration(chips.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Chips Config = " + cfg);
        if (cfg.has("behaviors")) {
            JSONObject behaviors = cfg.getJSONObject("behaviors");
            System.out.println("Behaviors = " + behaviors);
            assertTrue(behaviors.has("itemSelect"));
            assertTrue(behaviors.has("itemUnselect"));
        }

        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:chips")
        Chips chips;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "chips/chips001.xhtml";
        }
    }
}
