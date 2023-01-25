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
package org.primefaces.integrationtests.selectonemenu;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;

public class SelectOneMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: basic usecase")
    public void testBasic(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");
        page.button.click();

        // Assert - part 1
        Assertions.assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.select(3);
        page.button.click();

        // Assert - part 2
        Assertions.assertEquals("Charles", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: show panel")
    public void testShowPanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.show();

        // Assert
        assertDisplayed(selectOneMenu.getPanel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: hide panel")
    public void testHidePanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.show();
        selectOneMenu.hide();

        // Assert
        assertNotDisplayed(selectOneMenu.getPanel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: disable panel")
    public void testDisablePanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.disable();
        selectOneMenu.select("Max");

        // Assert - value should not be accepted
        assertNotClickable(selectOneMenu);
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: enable panel")
    public void testEnablePanel(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.disable();
        selectOneMenu.enable();
        selectOneMenu.select("Max");

        // Assert
        assertClickable(selectOneMenu);
        Assertions.assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: selectValue via JavaScript")
    public void testJsSelectValue(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.selectByValue("2");
        page.button.click();

        // Assert
        Assertions.assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: itemSelect - event")
    public void testItemSelect(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");

        // Assert - part 1
        Assertions.assertEquals("2", page.messages.getMessage(0).getDetail());
        Assertions.assertEquals("Driver-ID (itemSelect)", page.messages.getMessage(0).getSummary());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("SelectOneMenu: change - event")
    public void testChange(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu2;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");

        // Assert - part 1
        Assertions.assertEquals("2", page.messages.getMessage(0).getDetail());
        Assertions.assertEquals("Driver-ID (change)", page.messages.getMessage(0).getSummary());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        Assertions.assertTrue(cfg.has("appendTo"));
        Assertions.assertTrue(cfg.getBoolean("autoWidth"));
        Assertions.assertFalse(cfg.getBoolean("dynamic"));
        Assertions.assertEquals("fade", cfg.getString("effect"));
        Assertions.assertEquals("normal", cfg.getString("effectSpeed"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:selectonemenu2")
        SelectOneMenu selectOneMenu2;

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
