/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.integrationtests.cascadeselect;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CascadeSelect;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

class CascadeSelect001Test extends BaseCascadeSelectTest {

    @Test
    @Order(1)
    @DisplayName("CascadeSelect: select and submit item (SelectItems only with label)")
    void basic(Page page) {
        // Arrange
        CascadeSelect cascadeSelect = page.cascadeSelect;
        cascadeSelect.toggleDropdown();
        assertItems(cascadeSelect, 10);

        // Act
        cascadeSelect.select("nVidia");
        cascadeSelect.select("2000-Series");
        cascadeSelect.select("RTX 2080");

        // Assert
        assertMessage(page.messages, 0, "Selected GPU", "RTX 2080");
        assertConfiguration(cascadeSelect.getWidgetConfiguration());

        // Act
        page.button.click();

        // Assert
        assertMessage(page.messages, 0, "Selected GPU", "RTX 2080");
        assertEquals("RTX 2080", cascadeSelect.getSelectedLabel());
        assertConfiguration(cascadeSelect.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("CascadeSelect: show panel")
    void showPanel(Page page) {
        assertDoesNotThrow(() -> {
            // Arrange
            CascadeSelect cascadeSelect = page.cascadeSelect;

            // Act
            cascadeSelect.show();

            // Assert
            assertDisplayed(cascadeSelect.getPanel());
            assertConfiguration(cascadeSelect.getWidgetConfiguration());
        });
    }

    @Test
    @Order(3)
    @DisplayName("CascadeSelect: hide panel")
    void hidePanel(Page page) {
        assertDoesNotThrow(() -> {
            // Arrange
            CascadeSelect cascadeSelect = page.cascadeSelect;

            // Act
            cascadeSelect.show();
            cascadeSelect.hide();

            // Assert
            assertNotDisplayed(cascadeSelect.getPanel());
            assertConfiguration(cascadeSelect.getWidgetConfiguration());
        });
    }

    @Test
    @Order(4)
    @DisplayName("CascadeSelect: disable panel")
    void disablePanel(Page page) {
        // Arrange
        CascadeSelect cascadeSelect = page.cascadeSelect;

        // Act
        cascadeSelect.disable();
        cascadeSelect.select("nVidia");

        // Assert - value should not be accepted
        assertNotClickable(cascadeSelect);
        assertEquals("Select a GPU", cascadeSelect.getSelectedLabel());
        assertConfiguration(cascadeSelect.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("CascadeSelect: enable panel")
    void enablePanel(Page page) {
        // Arrange
        CascadeSelect cascadeSelect = page.cascadeSelect;

        // Act
        cascadeSelect.disable();
        cascadeSelect.enable();
        cascadeSelect.select("nVidia");
        cascadeSelect.select("2000-Series");
        cascadeSelect.select("RTX 2080");

        // Assert
        assertClickable(cascadeSelect);
        assertEquals("RTX 2080", cascadeSelect.getSelectedLabel());
        assertConfiguration(cascadeSelect.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:cascadeSelect")
        CascadeSelect cascadeSelect;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "cascadeselect/cascadeSelect001.xhtml";
        }
    }
}
