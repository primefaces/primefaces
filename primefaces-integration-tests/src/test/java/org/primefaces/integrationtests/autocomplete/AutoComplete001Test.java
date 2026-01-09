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
package org.primefaces.integrationtests.autocomplete;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AutoComplete;
import org.primefaces.selenium.component.CommandButton;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class AutoComplete001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: usage like normal input; without suggestions")
    void basic(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        assertEquals("test", autoComplete.getValue());
        assertNotNull(autoComplete.getPanel());
        assertNotDisplayed(autoComplete.getPanel());

        // Act
        autoComplete.deactivate();
        autoComplete.setValue("hello");
        page.button.click();

        // Assert
        assertEquals("hello", autoComplete.getValue());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("AutoComplete: usage like normal input, with suggestions")
    void basic2(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Act
        autoComplete.activate();
        autoComplete.setValueWithoutTab("bye");
        autoComplete.wait4Panel();
        autoComplete.getInput().sendKeys(new CharSequence[] {Keys.TAB});
        page.button.click();

        // Assert
        assertEquals("bye0", autoComplete.getValue());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("AutoComplete: check suggested values")
    void suggestions(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Act
        autoComplete.activate();
        autoComplete.setValueWithoutTab("Prime");
        autoComplete.wait4Panel();

        // Assert - Part 1
        assertDisplayed(autoComplete.getPanel());
        assertNotNull(autoComplete.getItems());
        List<String> itemValues = autoComplete.getItemValues();
        assertEquals(10, itemValues.size());
        assertEquals("Prime0", itemValues.get(0));
        assertEquals("Prime9", itemValues.get(9));

        // Act
        page.button.click();

        // Assert - Part 2
        assertEquals("Prime", autoComplete.getValue());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("AutoComplete: client-side search-method")
    void search(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Act
        autoComplete.search("abc");

        // Assert
        assertDisplayed(autoComplete.getPanel());
        assertNotNull(autoComplete.getItems());
        List<String> itemValues = autoComplete.getItemValues();
        assertEquals(10, itemValues.size());
        assertEquals("abc0", itemValues.get(0));
        assertEquals("abc9", itemValues.get(9));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        assertTrue(cfg.has("appendTo"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete001.xhtml";
        }
    }
}
