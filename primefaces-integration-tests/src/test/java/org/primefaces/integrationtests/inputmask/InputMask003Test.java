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
package org.primefaces.integrationtests.inputmask;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputMask;

public class InputMask003Test extends AbstractInputMaskTest {

    @Test
    @Order(1)
    @DisplayName("InputMask: Alphanumeric mask with invalid value")
    public void testAlphanumericInvalid(final Page page) {
        // Arrange
        final InputMask inputMask = page.alphanumeric;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("22-222-2222");
        page.button.click();

        // Assert
        Assertions.assertEquals("", inputMask.getValue());
        assertConfiguration(inputMask.getWidgetConfiguration(), "aa-999-A999");
    }

    @Test
    @Order(2)
    @DisplayName("InputMask: Alphanumeric mask with valid value")
    public void testAlphanumericValid(final Page page) {
        // Arrange
        final InputMask inputMask = page.alphanumeric;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("ab-123-c456");
        page.button.click();

        // Assert
        Assertions.assertEquals("ab-123-C456", inputMask.getValue());
        assertConfiguration(inputMask.getWidgetConfiguration(), "aa-999-A999");
    }

    @Test
    @Order(3)
    @DisplayName("InputMask: Optional value is omitted and value is OK")
    public void testOptionalWithoutExtension(final Page page) {
        // Arrange
        final InputMask inputMask = page.optional;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("(123) 456-7890");
        page.button.click();

        // Assert
        Assertions.assertEquals("(123) 456-7890", inputMask.getValue());
        assertConfiguration(inputMask.getWidgetConfiguration(), AbstractInputMaskTest.OPTIONAL_MASK);
    }

    @Test
    @Order(4)
    @DisplayName("InputMask: Optional value is included and value is OK")
    public void testOptionalWithExtension(final Page page) {
        // Arrange
        final InputMask inputMask = page.optional;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("(123) 456-7890 x12345");
        page.button.click();

        // Assert
        Assertions.assertEquals("(123) 456-7890 x12345", inputMask.getValue());
        assertConfiguration(inputMask.getWidgetConfiguration(), AbstractInputMaskTest.OPTIONAL_MASK);
    }

    private void assertConfiguration(JSONObject cfg, String mask) {
        assertNoJavascriptErrors();
        System.out.println("InputMask Config = " + cfg);
        Assertions.assertEquals(mask, cfg.getString("mask"));
        Assertions.assertFalse(cfg.has(AbstractInputMaskTest.AUTO_CLEAR));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:alphanumeric")
        InputMask alphanumeric;

        @FindBy(id = "form:optional")
        InputMask optional;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputmask/inputMask003.xhtml";
        }
    }
}
