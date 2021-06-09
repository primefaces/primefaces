/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectOneMenu;

public class SelectOneMenu002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: SelectItemGroup")
    public void testSelectItemGroup(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Select One", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Volkswagen");
        page.button.click();

        // Assert - part 1
        Assertions.assertEquals("Volkswagen", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.toggleDropdown();

        // Assert - part 2
        // items
        List<WebElement> optgroups = selectOneMenu.getInput().findElements(By.tagName("optgroup"));
        Assertions.assertEquals(2, optgroups.size());
        List<WebElement> options= selectOneMenu.getInput().findElements(By.tagName("option"));
        Assertions.assertEquals(8, options.size());
        // panel-content
        optgroups = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item-group"));
        Assertions.assertEquals(2, optgroups.size());
        options= selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        Assertions.assertEquals(8, options.size());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: escape=true/false")
    public void testEscape(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act
        selectOneMenu.toggleDropdown();

        // Assert
        List<WebElement> optgroups = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item-group"));
        Assertions.assertEquals("German Cars", optgroups.get(0).getText());
        Assertions.assertEquals("American <Cars>", optgroups.get(1).getText());

        List<WebElement> options= selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        Assertions.assertEquals("Mercedes", options.get(2).getText());
        Assertions.assertEquals("Chry<sler", options.get(4).getText());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        Assertions.assertTrue(cfg.has("appendTo"));
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
