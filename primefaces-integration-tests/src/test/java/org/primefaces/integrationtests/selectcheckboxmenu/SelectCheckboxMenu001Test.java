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
package org.primefaces.integrationtests.selectcheckboxmenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectCheckboxMenu;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectCheckboxMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectCheckboxMenu: select value and submit")
    void submit(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        String value = "London";
        assertSelected(menu, 0);
        assertLabel(menu, "Cities");

        // Act
        menu.selectValue(value);
        page.button.click();

        // Assert
        assertSelected(menu, 1);
        assertLabel(menu, "Item(s) Selected");
        WebElement checkbox = menu.getSelectedCheckboxes().get(0);
        assertValue(checkbox, value);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectCheckboxMenu: disable the input")
    void disabled(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        String value = "Rome";
        assertSelected(menu, 0);

        // Act
        menu.disable();
        menu.selectValue(value);
        page.button.click();

        // Assert
        assertSelected(menu, 0);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectCheckboxMenu: enable the input")
    void enabled(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        String value = "Paris";
        assertSelected(menu, 0);
        menu.disable();

        // Act
        menu.enable();
        menu.selectValue(value);
        page.button.click();

        // Assert
        assertSelected(menu, 1);
        WebElement checkbox = menu.getSelectedCheckboxes().get(0);
        assertValue(checkbox, value);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectCheckboxMenu: check all the values")
    void checkAll(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        assertSelected(menu, 0);
        assertLabel(menu, "Cities");

        // Act
        menu.checkAll();
        page.button.click();

        // Assert
        assertSelected(menu, 9);
        assertLabel(menu, "Item(s) Selected");
        assertValue(menu.getSelectedCheckboxes().get(0), "Miami");
        assertValue(menu.getSelectedCheckboxes().get(1), "London");
        assertValue(menu.getSelectedCheckboxes().get(2), "Paris");
        assertValue(menu.getSelectedCheckboxes().get(3), "Istanbul");
        assertValue(menu.getSelectedCheckboxes().get(4), "Berlin");
        assertValue(menu.getSelectedCheckboxes().get(5), "Barcelona");
        assertValue(menu.getSelectedCheckboxes().get(6), "Rome");
        assertValue(menu.getSelectedCheckboxes().get(7), "Brasilia");
        assertValue(menu.getSelectedCheckboxes().get(8), "Amsterdam");
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectCheckboxMenu: uncheck all the values")
    void uncheckAll(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        menu.checkAll();
        assertSelected(menu, 9);
        assertLabel(menu, "Item(s) Selected");

        // Act
        menu.uncheckAll();
        page.button.click();

        // Assert
        assertSelected(menu, 0);
        assertLabel(menu, "Cities");
        assertConfiguration(menu.getWidgetConfiguration());
    }

    private void assertSelected(SelectCheckboxMenu menu, int count) {
        assertEquals(count, menu.getSelectedCheckboxes().size());
    }

    private void assertLabel(SelectCheckboxMenu menu, String label) {
        assertEquals(label, menu.getLabel().getText());
    }

    private void assertValue(WebElement checkbox, String value) {
        assertEquals(value, checkbox.getAttribute("value"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertFalse(cfg.getBoolean("dynamic"));
        assertTrue(cfg.getBoolean("showHeader"));
        assertEquals("@(body)", cfg.get("appendTo"));
        System.out.println("SelectCheckboxMenu Config = " + cfg);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:checkbox-menu")
        SelectCheckboxMenu selectCheckboxMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectcheckboxmenu/selectCheckboxMenu001.xhtml";
        }
    }

}
