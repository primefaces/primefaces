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
package org.primefaces.integrationtests.inputtext;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;

@Tag("SafariBasic") //example-tag used together with profile/properties/groups in pom.xml to run only tests with this tag
public class InputText001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    public void testInputTextWithAjax(Page page) {
        // Arrange
        InputText inputText = page.inputtext1;
        Assertions.assertEquals("byebye!", inputText.getValue());

        // Act
        inputText.setValue("hello!");
        page.button.click();

        // Assert
        Assertions.assertEquals("hello!", inputText.getValue());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    public void testInputTextWithoutAjax(Page page) {
        // Arrange
        InputText inputText = page.inputtext2;
        Assertions.assertEquals("safari", inputText.getValue());

        // Act
        inputText.setValue("hello safari!");
        page.button.click();

        // Assert
        Assertions.assertEquals("hello safari!", inputText.getValue());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputText: disabled")
    public void testDisable(Page page) {
        // Arrange
        InputText inputText = page.inputtext2;
        Assertions.assertEquals("safari", inputText.getValue());

        // Act
        inputText.disable();
        try {
            inputText.setValue("hello safari!");
            Assertions.fail("Element should be disabled!");
        }
        catch (InvalidElementStateException e) {
            assertNotClickable(inputText);
        }

        // Assert - value should not be accepted
        Assertions.assertEquals("safari", inputText.getValue());
        Assertions.assertFalse(inputText.isEnabled());
        assertCss(inputText, "ui-state-disabled");
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("InputText: enabled")
    public void testEnable(Page page) {
        // Arrange
        InputText inputText = page.inputtext2;
        Assertions.assertEquals("safari", inputText.getValue());

        // Act
        inputText.disable();
        inputText.enable();
        inputText.setValue("testing re-enabled");

        // Assert
        assertClickable(inputText);
        Assertions.assertTrue(inputText.isEnabled());
        Assertions.assertEquals("testing re-enabled", inputText.getValue());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputText Config = " + cfg);
        Assertions.assertFalse(cfg.has("maxlength"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputtext1")
        InputText inputtext1;

        @FindBy(id = "form:inputtext2")
        InputText inputtext2;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtext/inputText001.xhtml";
        }
    }
}
