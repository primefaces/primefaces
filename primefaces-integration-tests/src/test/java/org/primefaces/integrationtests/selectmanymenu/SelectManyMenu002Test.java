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
package org.primefaces.integrationtests.selectmanymenu;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectManyMenu;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectManyMenu002Test extends AbstractSelectManyMenuTest {

    @Test
    @Order(1)
    @DisplayName("SelectManyMenu: basic usecase with converter + showCheckbox")
    void basicWithConverter(Page page) {
        // Arrange
        SelectManyMenu selectManyMenu = page.selectManyMenu;
        assertSelected(selectManyMenu, Arrays.asList("Max", "Charles"));

        // Act
        selectManyMenu.toggleSelection("Lando", false);
        page.button.click();

        // Assert - part 1
        assertSelected(selectManyMenu, Arrays.asList("Max", "Charles", "Lando"));
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("selected drivers"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Max,Charles,Lando"));
        assertConfiguration(selectManyMenu.getWidgetConfiguration());

        // Act
        selectManyMenu.toggleSelection( "Charles", false);
        page.button.click();

        // Assert - part 2
        assertSelected(selectManyMenu, Arrays.asList("Max", "Lando"));
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("selected drivers"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Max,Lando"));
        assertConfiguration(selectManyMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectManyMenu Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectmanymenu")
        SelectManyMenu selectManyMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectmanymenu/selectManyMenu002.xhtml";
        }
    }
}
