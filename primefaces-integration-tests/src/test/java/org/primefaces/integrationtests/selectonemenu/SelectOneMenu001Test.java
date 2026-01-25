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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanCheckbox;
import org.primefaces.selenium.component.SelectOneMenu;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: basic usecase")
    void basic(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");
        page.button.click();

        // Assert - part 1
        assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.select(3);
        page.button.click();

        // Assert - part 2
        assertEquals("Charles", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: Selecting a record by typing some letters #4682")
    void selectingByTypingSomeLetters(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.getLabel().sendKeys("La");
//        selectOneMenu.getLabel().sendKeys(Keys.TAB);
        page.button.click();

        // Assert - part 1
        assertEquals("Lando", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.getLabel().sendKeys("Le");
//        selectOneMenu.getLabel().sendKeys(Keys.TAB);
        page.button.click();

        // Assert - part 2
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: show panel")
    void showPanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.show();

        // Assert
        assertDisplayed(selectOneMenu.getPanel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: hide panel")
    void hidePanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.show();
        selectOneMenu.hide();

        // Assert
        assertNotDisplayed(selectOneMenu.getPanel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: disable panel")
    void disablePanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.disable();
        selectOneMenu.select("Max");

        // Assert - value should not be accepted
        assertNotClickable(selectOneMenu);
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("SelectOneMenu: enable panel")
    void enablePanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.disable();
        selectOneMenu.enable();
        selectOneMenu.select("Max");

        // Assert
        assertClickable(selectOneMenu);
        assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("SelectOneMenu: selectValue via JavaScript")
    void jsSelectValue(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.selectByValue("2");
        page.button.click();

        // Assert
        assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("SelectOneMenu: itemSelect - event")
    void itemSelect(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");

        // Assert - part 1
        assertEquals("2", page.messages.getMessage(0).getDetail());
        assertEquals("Driver-ID (itemSelect)", page.messages.getMessage(0).getSummary());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("SelectOneMenu: change - event")
    void change(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu2;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");

        // Assert - part 1
        assertEquals("2", page.messages.getMessage(0).getDetail());
        assertEquals("Driver-ID (change)", page.messages.getMessage(0).getSummary());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("SelectOneMenu: alwaysDisplayLabel")
    void alwaysDisplayLabel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu2;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        page.alwaysDisplayLabel.check();
        assertEquals("Select a driver", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");

        // Assert
        assertEquals("2", page.messages.getMessage(0).getDetail());
        assertEquals("Driver-ID (change)", page.messages.getMessage(0).getSummary());
        assertEquals("Select a driver", selectOneMenu.getSelectedLabel());

        // Act
        page.alwaysDisplayLabel.uncheck();
        assertEquals("Max", selectOneMenu.getSelectedLabel());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("SelectOneMenu: getAssignedLabelText")
    void assignedLabelText(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Test
        assertNotNull(selectOneMenu.getAssignedLabel());
        assertEquals("SelectOneMenu", selectOneMenu.getAssignedLabelText());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        assertTrue(cfg.has("appendTo"));
        assertEquals("auto", cfg.getString("autoWidth"));
        assertFalse(cfg.getBoolean("dynamic"));
        assertEquals("fade", cfg.getString("effect"));
        assertEquals("normal", cfg.getString("effectSpeed"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:selectonemenu2")
        SelectOneMenu selectOneMenu2;

        @FindBy(id = "form:alwaysDisplayLabel")
        SelectBooleanCheckbox alwaysDisplayLabel;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu001.xhtml";
        }
    }
}
