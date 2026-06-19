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
package org.primefaces.integrationtests.inputtext;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputText005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputText: counterTemplate using {0} remaining, {1} entered and {2} maxlength placeholders")
    void templatePlaceholders(Page page) {
        // Arrange
        InputText inputText = page.template;
        assertEquals("", inputText.getValue());

        // Act
        inputText.setValue("four");
        page.button.click();

        // Assert - 4 entered, 10 max, 6 remaining
        assertEquals("four", inputText.getValue());
        assertEquals("Entered 4 of 10, 6 left", page.displayTemplate.getText());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputText: countBytesAsChars counts an emoji as its UTF-8 byte length")
    void countBytesAsChars(Page page) {
        // Arrange
        InputText inputText = page.bytes;
        assertEquals("", inputText.getValue());

        // Act - ChromeDriver cannot sendKeys non-BMP chars, so set the value via script
        PrimeSelenium.executeScript("document.getElementById('form\\:bytes').value = 'four😀';");
        page.button.click();

        // Assert - "four" (4 bytes) + emoji (4 bytes) = 8 bytes, maxlength 10 -> 2 remaining
        assertEquals("four😀", inputText.getValue());
        assertEquals("2", page.displayBytes.getText());
        JSONObject cfg = inputText.getWidgetConfiguration();
        assertConfiguration(cfg);
        assertTrue(cfg.getBoolean("countBytesAsChars"));
    }

    @Test
    @Order(3)
    @DisplayName("InputText: counter without maxlength leaves the counter display empty")
    void counterWithoutMaxlength(Page page) {
        // Arrange
        InputText inputText = page.noMax;
        assertEquals("", inputText.getValue());

        // Act
        inputText.setValue("hello");
        page.button.click();

        // Assert - without a maxlength the widget never writes counter text
        assertEquals("hello", inputText.getValue());
        assertEquals("", page.displayNoMax.getText());
        assertConfiguration(inputText.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputText Config = " + cfg);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:template")
        InputText template;

        @FindBy(id = "form:displayTemplate")
        WebElement displayTemplate;

        @FindBy(id = "form:bytes")
        InputText bytes;

        @FindBy(id = "form:displayBytes")
        WebElement displayBytes;

        @FindBy(id = "form:nomax")
        InputText noMax;

        @FindBy(id = "form:displayNoMax")
        WebElement displayNoMax;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtext/inputText005.xhtml";
        }
    }
}
