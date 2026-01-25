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
package org.primefaces.integrationtests.spinner;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.OutputLabel;
import org.primefaces.selenium.component.Spinner;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Spinner007Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Spinner: #12352: allow NULL initial value if required + min")
    void required(Page page) {
        // Arrange
        Spinner spinner = page.spinnerBv;
        assertEquals(null, spinner.getWidgetValue());
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "88");
        page.button1.click();

        // Assert
        assertEquals("", page.output1.getText());
        assertOutputLabel(page.output1, "");

        assertNoJavascriptErrors();
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(1)
    @DisplayName("Spinner: #12352: allow NULL to be submitted if not required")
    void notRequired(Page page) {
        // Arrange
        Spinner spinner = page.spinnerNotRequired;
        assertEquals(null, spinner.getWidgetValue());
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "77");
        page.button2.click();

        // Assert
        assertEquals("10", page.output2.getText());
        assertOutputLabel(page.output2, "10");

        assertNoJavascriptErrors();
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Spinner: GitHub #12346 respect min and max bean validations")
    void beanValidation(Page page) {
        // Arrange
        Spinner spinner = page.spinnerBv;
        assertEquals(null, spinner.getWidgetValue());

        // Act
        sendKeys(spinner, "99");
        page.button3.click();

        // Assert
        assertEquals("10", page.output3.getText());
        assertOutputLabel(page.output3, "10");

        assertNoJavascriptErrors();
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Spinner Config = " + cfg);
        assertEquals(1, cfg.get("min"));
        assertEquals(10, cfg.get("max"));
    }

    private void assertOutputLabel(OutputLabel outputLabel, String value) {
        assertEquals(value, outputLabel.getText());
    }

    public void sendKeys(Spinner spinner, CharSequence value) {
        WebElement input = spinner.getInput();
        ComponentUtils.sendKeys(input, value);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:required")
        Spinner spinnerRequired;

        @FindBy(id = "form:notRequired")
        Spinner spinnerNotRequired;

        @FindBy(id = "form:integerBv")
        Spinner spinnerBv;

        @FindBy(id = "form:output1")
        OutputLabel output1;

        @FindBy(id = "form:output2")
        OutputLabel output2;

        @FindBy(id = "form:output3")
        OutputLabel output3;

        @FindBy(id = "form:button1")
        CommandButton button1;

        @FindBy(id = "form:button2")
        CommandButton button2;

        @FindBy(id = "form:button3")
        CommandButton button3;

        @Override
        public String getLocation() {
            return "spinner/spinner007.xhtml";
        }
    }
}
