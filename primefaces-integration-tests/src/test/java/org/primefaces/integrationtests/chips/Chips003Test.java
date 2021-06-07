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
package org.primefaces.integrationtests.chips;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Chips;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

public class Chips003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Chips: GitHub #6643 Test unique attribute only allowing distinct items")
    public void testUnique(Page page) {
        // Arrange
        Chips chips = page.chips;

        // Assert initial state
        List<String> values = chips.getValues();
        Assertions.assertEquals(2, values.size());
        Assertions.assertEquals("One", values.get(0));
        Assertions.assertEquals("Two", values.get(1));

        // Act - add values
        chips.addValue("One"); //duplicate should be ignored
        chips.addValue("Three"); //new item should be added
        chips.addValue("Two"); //duplicate should be ignored

        // Act - submit
        page.button.click();

        // Assert
        Assertions.assertEquals("One, Two, Three", page.messages.getMessage(0).getSummary());
        values = chips.getValues();
        Assertions.assertEquals(3, values.size());
        Assertions.assertEquals("One", values.get(0));
        Assertions.assertEquals("Two", values.get(1));
        Assertions.assertEquals("Three", values.get(2));
        assertConfiguration(chips.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Chips Config = " + cfg);
        Assertions.assertTrue(cfg.has("id"));
        Assertions.assertTrue(cfg.getBoolean("unique"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:chips")
        Chips chips;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "chips/chips003.xhtml";
        }
    }
}
