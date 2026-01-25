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
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: dynamic - load items on demand")
    void dynamic(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Assert
        boolean contentPanelExists = false;
        try {
            selectOneMenu.getItems().getText();
            contentPanelExists = true;
        }
        catch (NoSuchElementException ex) {

        }
        assertFalse(contentPanelExists);

        // Act
        selectOneMenu.toggleDropdown();

        // Assert
        List<WebElement> options = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        assertEquals(6, options.size());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: dynamic - do NOT lose selection on submit")
    void dynamicAjaxSunmit(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu2;

        // Act
        page.button.click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("console2"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("PS4"));

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: dynamic with advanced rendering- load items on demand")
    void dynamicAdvancedRendering(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu3;

        // Assert
        boolean contentPanelExists = false;
        try {
            selectOneMenu.getItems().getText();
            contentPanelExists = true;
        }
        catch (NoSuchElementException ex) {

        }
        assertFalse(contentPanelExists);

        // Act
        selectOneMenu.toggleDropdown();

        // Assert
        List<WebElement> options = selectOneMenu.getTable().findElements(By.className("ui-selectonemenu-item"));
        assertEquals(5, options.size());

        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        assertTrue(cfg.has("dynamic"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:selectonemenu2")
        SelectOneMenu selectOneMenu2;

        @FindBy(id = "form:selectonemenu3")
        SelectOneMenu selectOneMenu3;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu004.xhtml";
        }
    }
}
