/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.inputtext;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.model.Severity;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputText007Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputText: custom validator rejects the value and shows validatorMessage")
    void customValidatorFails(Page page) {
        // Arrange
        InputText validator = page.validator;
        Messages messages = page.messages;

        // Act
        validator.setValue("bad");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(1, messages.getAllMessages().size());
        Msg msg = messages.getAllMessages().get(0);
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertEquals("Custom validator failed!", msg.getSummary());
        assertCss(validator, "ui-state-error", "ui-inputtext");
    }

    @Test
    @Order(2)
    @DisplayName("InputText: custom validator accepts a valid value with no message")
    @Disabled("Fails on MyFaces 4.0 / 4.1 as of 06/2026")
    void customValidatorPasses(Page page) {
        // Arrange
        InputText validator = page.validator;
        Messages messages = page.messages;

        // Act
        validator.setValue("good");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("good", validator.getValue());
        assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    @Order(3)
    @DisplayName("InputText: conversion failure shows the custom converterMessage")
    @Disabled("Fails on MyFaces 4.0 / 4.1 as of 06/2026")
    void converterMessageOnBadInput(Page page) {
        // Arrange
        InputText converter = page.converter;
        Messages messages = page.messages;

        // Act - a non-numeric value cannot be converted to an Integer
        converter.setValue("abc");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(1, messages.getAllMessages().size());
        Msg msg = messages.getAllMessages().get(0);
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertEquals("Custom converter failed!", msg.getSummary());
        assertCss(converter, "ui-state-error", "ui-inputtext");
    }

    @Test
    @Order(4)
    @DisplayName("InputText: valueChangeListener is invoked with the new value")
    void valueChangeListenerInvoked(Page page) {
        // Arrange
        InputText vcl = page.vcl;
        Messages messages = page.messages;

        // Act
        vcl.setValue("newval");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(0, messages.getAllMessages().size());
        assertEquals("newval", page.lastChanged.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:validator")
        InputText validator;

        @FindBy(id = "form:converter")
        InputText converter;

        @FindBy(id = "form:vcl")
        InputText vcl;

        @FindBy(id = "form:lastChanged")
        WebElement lastChanged;

        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtext/inputText007.xhtml";
        }
    }
}
