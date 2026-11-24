/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneMenu019Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #13954 a disabled placeholder is not a disabled item, only required fails")
    void placeholderUnchanged(Page page) {
        // Act
        page.submit.click();

        // Assert
        assertPlaceholderRequired(page);
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #13954 a disabled placeholder is not a disabled item with partialSubmit")
    void placeholderUnchangedPartialSubmit(Page page) {
        // Act
        page.submitPartial.click();

        // Assert
        assertPlaceholderRequired(page);
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #13954 enabled items are accepted next to a disabled placeholder")
    void placeholderSelectEnabled(Page page) {
        // Act
        page.optional.select("1");
        page.required.select("2");
        page.submit.click();

        // Assert
        assertTrue(page.messages.isEmpty());
        assertEquals("1", page.optionalValue.getText());
        assertEquals("2", page.requiredValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: GitHub #13954 preselected disabled Integer and enum values are kept")
    void presetNonStringModels(Page page) {
        // Act
        page.presetSubmit.click();

        // Assert
        assertTrue(page.presetMessages.isEmpty());
        assertEquals("3", page.intPresetValue.getText());
        assertEquals("LARGE", page.enumPresetValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered disabled Integer item fails validation")
    void tamperedDisabledInteger(Page page) {
        // Act
        page.required.select("1");
        SelectOneMenu018Test.tamper("placeholder:optional_input", "3");
        page.submitPartial.click();

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        String summary = page.messages.getMessage(0).getSummary();
        assertTrue(summary.contains("Optional") && summary.contains(SelectOneMenu018Test.INVALID), summary);
        assertEquals("", page.optionalValue.getText());
        assertEquals("", page.requiredValue.getText());
        assertNoJavascriptErrors();
    }

    private void assertPlaceholderRequired(Page page) {
        assertEquals(1, page.messages.getAllMessages().size());
        String summary = page.messages.getMessage(0).getSummary();
        assertTrue(summary.contains("Required"), summary);
        assertFalse(summary.contains(SelectOneMenu018Test.INVALID), summary);
        assertEquals("", page.optionalValue.getText());
        assertEquals("", page.requiredValue.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "placeholder:msgs")
        Messages messages;

        @FindBy(id = "placeholder:optional")
        SelectOneMenu optional;

        @FindBy(id = "placeholder:required")
        SelectOneMenu required;

        @FindBy(id = "placeholder:submit")
        CommandButton submit;

        @FindBy(id = "placeholder:submitPartial")
        CommandButton submitPartial;

        @FindBy(id = "placeholder:optionalValue")
        WebElement optionalValue;

        @FindBy(id = "placeholder:requiredValue")
        WebElement requiredValue;

        @FindBy(id = "preset:msgs")
        Messages presetMessages;

        @FindBy(id = "preset:submit")
        CommandButton presetSubmit;

        @FindBy(id = "preset:intPresetValue")
        WebElement intPresetValue;

        @FindBy(id = "preset:enumPresetValue")
        WebElement enumPresetValue;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu019.xhtml";
        }
    }
}
