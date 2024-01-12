/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;

//example-tag used together with profile/properties/groups in pom.xml to run only tests with this tag
@Tag("SafariBasic")
class InputText001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    void inputTextWithAjax(Page page) {
        // Arrange
        InputText inputText = page.inputtext1;
        assertEquals("byebye!", inputText.getValue());

        // Act
        inputText.setValue("hello!");
        page.button.click();

        // Assert
        assertEquals("hello!", inputText.getValue());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    void inputTextWithoutAjax(Page page) {
        // Arrange
        InputText inputText = page.inputtext2;
        assertEquals("safari", inputText.getValue());

        // Act
        inputText.setValue("hello safari!");
        page.button.click();

        // Assert
        assertEquals("hello safari!", inputText.getValue());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputText: disabled")
    void disable(Page page) {
        // Arrange
        InputText inputText = page.inputtext2;
        assertEquals("safari", inputText.getValue());

        // Act
        inputText.disable();
        try {
            inputText.setValue("hello safari!");
            fail("Element should be disabled!");
        }
        catch (InvalidElementStateException e) {
            assertNotClickable(inputText);
        }

        // Assert - value should not be accepted
        assertEquals("safari", inputText.getValue());
        assertFalse(inputText.isEnabled());
        assertCss(inputText, "ui-state-disabled");
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("InputText: enabled")
    void enable(Page page) {
        // Arrange
        InputText inputText = page.inputtext2;
        assertEquals("safari", inputText.getValue());

        // Act
        inputText.disable();
        inputText.enable();
        inputText.setValue("testing re-enabled");

        // Assert
        assertClickable(inputText);
        assertTrue(inputText.isEnabled());
        assertEquals("testing re-enabled", inputText.getValue());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputText Config = " + cfg);
        assertFalse(cfg.has("maxlength"));
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
