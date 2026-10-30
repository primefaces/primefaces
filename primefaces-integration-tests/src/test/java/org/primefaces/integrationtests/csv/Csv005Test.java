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
package org.primefaces.integrationtests.csv;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;
import org.primefaces.selenium.component.InputOtp;
import org.primefaces.selenium.component.InputPhone;
import org.primefaces.selenium.component.Message;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Client side validation of components that consist of several inputs (p:inputOtp, p:inputPhone, p:inputNumber): the
 * hidden input with the value is validated once, the component and its inputs are highlighted.
 */
class Csv005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CSV: InputOtp - p:clientValidator")
    void inputOtp(Page page) {
        // Act
        page.otp.setValue("12");

        // Assert
        assertMessage(page.msgOtp, "OTP: Validation Error: Length is less than allowable minimum of '4'.");
        assertHighlighted(page.otp, page.otp.getInput(), true);

        // Act
        page.otp.setValue("1234");

        // Assert
        assertMessage(page.msgOtp, "");
        assertHighlighted(page.otp, page.otp.getInput(), false);
    }

    @Test
    @Order(2)
    @DisplayName("CSV: InputPhone - p:clientValidator")
    void inputPhone(Page page) {
        // Act
        page.phone.setValue("123");

        // Assert
        assertMessage(page.msgPhone, "Phone: Validation Error: Value is not a valid phone number.");
        assertHighlighted(page.phone, page.phone.getInput(), true);

        // Act
        page.phone.setValue("030 123456");

        // Assert
        assertMessage(page.msgPhone, "");
        assertHighlighted(page.phone, page.phone.getInput(), false);
    }

    @Test
    @Order(3)
    @DisplayName("CSV: validateClient validates each component once and focuses the first input")
    void validateClient(Page page) {
        // Act
        page.btnAjax.getRoot().click(); // do not guard as it fails validation and never fires AJAX

        // Assert
        assertMessage(page.msgOtp, "OTP: Validation Error: Value is required.");
        assertMessage(page.msgPhone, "Phone: Validation Error: Value is required.");
        // without label: the client id of the component, not of its hidden input
        assertMessage(page.msgNumber, "form:number: Validation Error: Value is required.");
        // one message per component, although p:inputPhone and p:inputNumber submit several inputs
        Assertions.assertEquals(3, page.messages.getAllMessages().size());
        Assertions.assertEquals("form:otp_0", getWebDriver().switchTo().activeElement().getDomAttribute("id"));
        assertHighlighted(page.otp, page.otp.getInput(), true);
        assertHighlighted(page.phone, page.phone.getInput(), true);
    }

    @Test
    @Order(4)
    @DisplayName("CSV: valid composite components are submitted")
    void submit(Page page) {
        // Arrange
        page.otp.setValue("1234");
        page.phone.setValue("030 123456");
        page.number.setValue(5);

        // Act
        page.btnAjax.click();

        // Assert
        Assertions.assertTrue(page.messages.isEmpty());
        Assertions.assertEquals("1234", page.otpValue.getText());
        Assertions.assertEquals("+4930123456", page.phoneValue.getText());
        Assertions.assertEquals("5", page.numberValue.getText());
        assertNoJavascriptErrors();
    }

    private void assertMessage(Message message, String expected) {
        // an empty p:message has no size (it is invisible), only a message is shown
        if (!expected.isEmpty()) {
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(message));
        }
        Assertions.assertEquals(expected, message.getText());
        assertNoJavascriptErrors();
    }

    private void assertHighlighted(WebElement component, WebElement input, boolean invalid) {
        Assertions.assertEquals(invalid, PrimeSelenium.hasCssClass(component, "p-state-error"));
        Assertions.assertEquals(invalid ? "true" : null, input.getDomAttribute("aria-invalid"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:otp")
        InputOtp otp;

        @FindBy(id = "form:msgOtp")
        Message msgOtp;

        @FindBy(id = "form:otpValue")
        WebElement otpValue;

        @FindBy(id = "form:phone")
        InputPhone phone;

        @FindBy(id = "form:msgPhone")
        Message msgPhone;

        @FindBy(id = "form:phoneValue")
        WebElement phoneValue;

        @FindBy(id = "form:number")
        InputNumber number;

        @FindBy(id = "form:msgNumber")
        Message msgNumber;

        @FindBy(id = "form:numberValue")
        WebElement numberValue;

        @FindBy(id = "form:btnAjax")
        CommandButton btnAjax;

        @Override
        public String getLocation() {
            return "csv/csv005.xhtml";
        }
    }
}
