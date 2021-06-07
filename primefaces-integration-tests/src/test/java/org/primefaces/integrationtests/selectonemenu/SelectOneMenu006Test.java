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
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.component.model.Msg;

public class SelectOneMenu006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: editable=true choose selectItem value")
    public void testChooseSelectValue(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        selectOneMenu.toggleDropdown();

        // Assert
        List<WebElement> options = selectOneMenu.getItems().findElements(By.className("ui-selectonemenu-item"));
        Assertions.assertEquals(7, options.size());

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
    public void testEnterUserSuppliedValue(Page page) {
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

    private void assertMessage(Page page, int index, String summary, String detail) {
        Msg message = page.messages.getMessage(index);
        Assertions.assertEquals(summary, message.getSummary());
        Assertions.assertEquals(detail, message.getDetail());
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

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu006.xhtml";
        }
    }
}
