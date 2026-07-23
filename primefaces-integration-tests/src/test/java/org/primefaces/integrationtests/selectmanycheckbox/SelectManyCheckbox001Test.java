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
package org.primefaces.integrationtests.selectmanycheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectManyCheckbox;

import java.util.Arrays;
import java.util.Collections;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectManyCheckbox001Test extends AbstractSelectManyCheckboxTest {

    @Test
    @Order(1)
    @DisplayName("SelectManyCheckbox: basic usecase with converter - select and deselect")
    void basic(Page page) {
        // Arrange
        SelectManyCheckbox selectManyCheckbox = page.selectManyCheckbox;
        assertEquals(4, selectManyCheckbox.getItemsSize());
        assertEquals(Arrays.asList("Lewis", "Max", "Charles", "Lando"), selectManyCheckbox.getLabels());
        assertSelected(selectManyCheckbox, Arrays.asList("Max", "Charles"));

        // Act - select additional item
        selectManyCheckbox.toggle(3);
        page.button.click();

        // Assert - part 1
        assertSelected(selectManyCheckbox, Arrays.asList("Max", "Charles", "Lando"));
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("selected drivers"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Max,Charles,Lando"));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());

        // Act - deselect item
        selectManyCheckbox.deselect(2);
        page.button.click();

        // Assert - part 2
        assertSelected(selectManyCheckbox, Arrays.asList("Max", "Lando"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Max,Lando"));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectManyCheckbox: selectAll and deselectAll")
    void selectAndDeselectAll(Page page) {
        // Arrange
        SelectManyCheckbox selectManyCheckbox = page.selectManyCheckbox;
        assertSelected(selectManyCheckbox, Arrays.asList("Max", "Charles"));

        // Act - select all
        selectManyCheckbox.selectAll();
        page.button.click();

        // Assert - all selected
        assertSelected(selectManyCheckbox, Arrays.asList("Lewis", "Max", "Charles", "Lando"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Lewis,Max,Charles,Lando"));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());

        // Act - deselect all
        selectManyCheckbox.deselectAll();
        page.button.click();

        // Assert - none selected
        assertSelected(selectManyCheckbox, Collections.emptyList());
        assertTrue(page.messages.getMessage(0).getDetail().contains("no driver selected"));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectManyCheckbox: toggle a single checkbox on and off")
    void toggle(Page page) {
        // Arrange
        SelectManyCheckbox selectManyCheckbox = page.selectManyCheckbox;
        assertEquals(false, selectManyCheckbox.isSelected(0));

        // Act - toggle on
        selectManyCheckbox.toggle(0);

        // Assert
        assertEquals(true, selectManyCheckbox.isSelected(0));

        // Act - toggle off
        selectManyCheckbox.toggle(0);

        // Assert
        assertEquals(false, selectManyCheckbox.isSelected(0));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectManyCheckbox Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectmanycheckbox")
        SelectManyCheckbox selectManyCheckbox;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectmanycheckbox/selectManyCheckbox001.xhtml";
        }
    }
}
