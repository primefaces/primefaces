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
package org.primefaces.integrationtests.autocomplete;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AutoComplete;
import org.primefaces.selenium.component.CommandButton;

public class AutoComplete003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: multiple mode with unique values")
    public void testMultiple(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Assert initial state
        List<String> values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("Ringo", values.get(0));

        // Act
        autoComplete.addItem("Ringo"); // duplicate should be ignored
        autoComplete.addItem("John");
        autoComplete.addItem("George");
        autoComplete.addItem("Paul"); // over the limit of 3 should not be added
        page.button.click();

        // Assert
        values = autoComplete.getValues();
        Assertions.assertEquals(3, values.size());
        Assertions.assertEquals("Ringo", values.get(0));
        Assertions.assertEquals("John", values.get(1));
        Assertions.assertEquals("George", values.get(2));
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("AutoComplete: multiple mode add item and remove item")
    public void testAddAndRemoveItem(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Assert initial state
        List<String> values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("Ringo", values.get(0));

        // Act
        autoComplete.addItem("Paul");
        autoComplete.removeItem("Ringo");
        page.button.click();

        // Assert
        values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("Paul", values.get(0));
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("AutoComplete: adding/removing an item while disabled should have no effect")
    public void testDisabled(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Assert initial state
        List<String> values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("Ringo", values.get(0));

        // Act
        autoComplete.disable();
        autoComplete.removeItem("Ringo");
        autoComplete.addItem("Paul");
        page.button.click();

        // Assert
        values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("Ringo", values.get(0));
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("AutoComplete: test enabling and disabling")
    public void testEnabled(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Assert initial state
        List<String> values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("Ringo", values.get(0));

        // Act
        autoComplete.disable();
        autoComplete.removeItem("Ringo");
        autoComplete.addItem("Paul");
        autoComplete.enable();
        autoComplete.removeItem("Ringo");
        autoComplete.addItem("George");
        page.button.click();

        // Assert
        values = autoComplete.getValues();
        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals("George", values.get(0));
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        Assertions.assertTrue(cfg.getBoolean("multiple"));
        Assertions.assertTrue(cfg.getBoolean("unique"));
        Assertions.assertEquals(3, cfg.getInt("selectLimit"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete003.xhtml";
        }
    }
}
