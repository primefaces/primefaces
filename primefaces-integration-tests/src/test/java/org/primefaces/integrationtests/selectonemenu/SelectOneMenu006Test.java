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
import org.primefaces.selenium.component.model.Msg;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: editable=true choose selectItem value")
    void chooseSelectValue(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        selectOneMenu.toggleDropdown();

        // Assert
        List<WebElement> options = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        assertEquals(7, options.size());

        // Act
        selectOneMenu.select("Playstation 5");
        page.button.click();

        // Assert
        assertMessage(page, 0, "Console", "PS5");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: editable=true enter user supplied value")
    void enterUserSuppliedValue(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        WebElement editableInput = selectOneMenu.getEditableInput();

        // Act
        editableInput.clear();
        editableInput.sendKeys("Sega Genesis");
        page.button.click();

        // Assert
        assertMessage(page, 0, "Console", "Sega Genesis");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: getAssignedLabelText editable=true")
    void assignedLabelText(SelectOneMenu001Test.Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Test
        assertNotNull(selectOneMenu.getAssignedLabel());
        assertEquals("SelectOneMenu", selectOneMenu.getAssignedLabelText());
    }

    private void assertMessage(Page page, int index, String summary, String detail) {
        Msg message = page.messages.getMessage(index);
        assertEquals(summary, message.getSummary());
        assertEquals(detail, message.getDetail());
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

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu006.xhtml";
        }
    }
}
