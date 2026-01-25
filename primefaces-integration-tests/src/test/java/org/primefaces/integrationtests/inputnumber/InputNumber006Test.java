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
package org.primefaces.integrationtests.inputnumber;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InputNumber006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: click on button after typing into InputNumber must trigger two ajax-calls (valueChange-Event)")
    void ajaxValueChangeEvent(final Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;

        // Act
        inputNumber.getInput().sendKeys("1234");
        page.button.click();
        /*
         Wait a bit because clicking the button triggers two ajax-calls and AjaxGuard as part of button.click() only
         guards the first AJAX-call.
         */
        PrimeSelenium.wait(500);

        // Assert
        assertEquals("value = 1234", page.outputpanel.getText());
        assertFalse(page.messages.isEmpty());
        assertEquals("some action; value: 1234", page.messages.getMessage(0).getSummary());

        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: click on button after typing into InputNumber must trigger two ajax-calls (blur-Event)")
    void ajaxBlurEvent(final Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber2;

        // Act
        inputNumber.getInput().sendKeys("5678");
        page.button2.click();
        /*
         Wait a bit because clicking the button triggers two ajax-calls and AjaxGuard as part of button.click() only
         guards the first AJAX-call.
         */
        PrimeSelenium.wait(500);

        // Assert
        assertEquals("value = 5678", page.outputpanel2.getText());
        assertFalse(page.messages.isEmpty());
        assertEquals("some second action; value: 5678", page.messages.getMessage(0).getSummary());

        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        assertEquals(0, cfg.get("decimalPlaces"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:inputnumber")
        InputNumber inputnumber;

        @FindBy(id = "form:inputnumber2")
        InputNumber inputnumber2;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:button2")
        CommandButton button2;

        @FindBy(id = "form:outputpanel")
        WebElement outputpanel;

        @FindBy(id = "form:outputpanel2")
        WebElement outputpanel2;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber006.xhtml";
        }
    }
}
