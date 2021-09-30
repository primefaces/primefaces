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

public class InputMask004Test extends AbstractInputMaskTest {

    @Test
    @Order(1)
    @DisplayName("InputMask: GitHub #6469 Optional test with server side validation.")
    public void testOptionalAreaCode1(final Page page) {
        // Arrange
        final InputMask inputMask = page.optional;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("1");
        page.button.click();

        // Assert
        Assertions.assertEquals("1", inputMask.getValue());
        Assertions.assertEquals("1", inputMask.getWidgetValueUnmasked());
        assertConfiguration(inputMask.getWidgetConfiguration(), "9[999]");
    }

    @Test
    @Order(2)
    @DisplayName("InputMask: GitHub #6469 Optional test with server side validation.")
    public void testOptionalAreaCode2(final Page page) {
        // Arrange
        final InputMask inputMask = page.optional;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("12");
        page.button.click();

        // Assert
        Assertions.assertEquals("12__", inputMask.getValue());
        Assertions.assertEquals("12", inputMask.getWidgetValueUnmasked());
        assertConfiguration(inputMask.getWidgetConfiguration(), "9[999]");
    }

    @Test
    @Order(2)
    @DisplayName("InputMask: GitHub #6469 Optional test with server side validation.")
    public void testOptionalAreaCode3(final Page page) {
        // Arrange
        final InputMask inputMask = page.optional;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("123");
        page.button.click();

        // Assert
        Assertions.assertEquals("123_", inputMask.getValue());
        Assertions.assertEquals("123", inputMask.getWidgetValueUnmasked());
        assertConfiguration(inputMask.getWidgetConfiguration(), "9[999]");
    }

    @Test
    @Order(4)
    @DisplayName("InputMask: GitHub #6469 Optional test with server side validation.")
    public void testOptionalAreaCode4(final Page page) {
        // Arrange
        final InputMask inputMask = page.optional;
        Assertions.assertEquals("", inputMask.getValue());

        // Act
        inputMask.setValue("1234");
        page.button.click();

        // Assert
        Assertions.assertEquals("1234", inputMask.getValue());
        Assertions.assertEquals("1234", inputMask.getWidgetValueUnmasked());
        assertConfiguration(inputMask.getWidgetConfiguration(), "9[999]");
    }

    private void assertConfiguration(JSONObject cfg, String mask) {
        assertNoJavascriptErrors();
        System.out.println("InputMask Config = " + cfg);
        Assertions.assertEquals(mask, cfg.getString("mask"));
        Assertions.assertFalse(cfg.has(AbstractInputMaskTest.AUTO_CLEAR));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:optional")
        InputMask optional;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputmask/inputMask004.xhtml";
        }
    }
}
