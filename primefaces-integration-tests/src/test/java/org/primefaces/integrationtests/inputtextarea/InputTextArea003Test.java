/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.integrationtests.inputtextarea;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputTextarea;

public class InputTextArea003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputTextarea: MaxLength using less than max # of characters")
    public void testMaxLengthLessThan(Page page) {
        // Arrange
        InputTextarea inputText = page.inputtext;
        Assertions.assertEquals("", inputText.getValue());

        // Act
        inputText.setValue("four");
        page.button.click();

        // Assert
        Assertions.assertEquals("MaxLength Counter", inputText.getAssignedLabelText());
        Assertions.assertEquals("four", inputText.getValue());
        Assertions.assertEquals("6 characters remaining.", page.display.getText());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputTextarea: MaxLength using more than max # of characters")
    public void testMaxLengthMoreThan(Page page) {
        // Arrange
        InputTextarea inputText = page.inputtext;
        Assertions.assertEquals("", inputText.getValue());

        // Act
        inputText.setValue("12345678901234");
        page.button.click();

        // Assert
        Assertions.assertEquals("MaxLength Counter", inputText.getAssignedLabelText());
        Assertions.assertEquals("1234567890", inputText.getValue());
        Assertions.assertEquals("0 characters remaining.", page.display.getText());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputTextarea Config = " + cfg);
        Assertions.assertTrue(cfg.getBoolean("autoResize"));
        Assertions.assertEquals(10, cfg.getInt("maxlength"));
        Assertions.assertEquals("{0} characters remaining.", cfg.getString("counterTemplate"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputtext")
        InputTextarea inputtext;

        @FindBy(id = "form:display")
        WebElement display;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtextarea/inputTextArea003.xhtml";
        }
    }
}
