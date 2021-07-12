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

public class SelectOneMenu008Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: basic usecase with converter")
    public void testBasicWithConverter(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Charles", selectOneMenu.getSelectedLabel());

        // Act
        selectOneMenu.select("Max");
        page.button.click();

        // Assert - part 1
        Assertions.assertEquals("Max", selectOneMenu.getSelectedLabel());
        Assertions.assertEquals(1, page.messages.getAllMessages().size());
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("selected driver"));
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains("Max"));
        assertConfiguration(selectOneMenu.getWidgetConfiguration());

        // Act
        selectOneMenu.select(4);
        page.button.click();

        // Assert - part 2
        Assertions.assertEquals("Lando", selectOneMenu.getSelectedLabel());
        Assertions.assertEquals(1, page.messages.getAllMessages().size());
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("selected driver"));
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains("Lando"));
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
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu008.xhtml";
        }
    }
}
