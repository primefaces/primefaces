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
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneMenu002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: SelectItemGroup")
    void selectItemGroup(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Select One", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Volkswagen");
        page.button.click();

        // Assert - part 1
        assertEquals("Volkswagen", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.toggleDropdown();

        // Assert - part 2
        // items
        List<WebElement> optgroups = selectOneMenu.getInput().findElements(By.tagName("optgroup"));
        assertEquals(2, optgroups.size());
        List<WebElement> options = selectOneMenu.getInput().findElements(By.tagName("option"));
        assertEquals(11, options.size());
        // panel-content
        optgroups = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item-group"));
        assertEquals(2, optgroups.size());
        options = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        assertEquals(11, options.size());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: escape=true/false")
    void escape(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act
        selectOneMenu.toggleDropdown();

        // Assert - part 1
        List<WebElement> optgroups = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item-group"));
        assertEquals("German Cars", optgroups.get(0).getText());
        assertEquals("American <Cars>", optgroups.get(1).getText());

        List<WebElement> options = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        assertEquals("Mercedes", options.get(2).getText());
        assertEquals("Chry<sler", options.get(4).getText());
        assertEquals("GitHub \"9336\" Quoted", options.get(8).getText());
        assertEquals("< GitHub <i>9336</i>", options.get(9).getText());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.select("Ford & Lincoln");

        // Assert - part 2
        assertEquals("Ford & Lincoln", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act - part 3
        page.button.click();

        // Assert - part 3
        assertEquals("Ford & Lincoln", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act - part 4
        selectOneMenu.select("< GitHub <i>9336</i>");

        // Assert - part 4
        assertEquals("< GitHub <i>9336</i>", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act - part 5
        selectOneMenu.select("GitHub \"9336\" Quoted");

        // Assert - part 5
        assertEquals("GitHub \"9336\" Quoted", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        assertTrue(cfg.has("appendTo"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu002.xhtml";
        }
    }
}
