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
package org.primefaces.integrationtests.inputotp;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputOtp;
import org.primefaces.selenium.component.Messages;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputOtp001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputOtp: typed characters fill the inputs and are submitted")
    void submit(Page page) {
        // Arrange
        InputOtp otp = page.otp;
        assertEquals("", otp.getValue());

        // Act
        otp.setValue("1234");
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("1234", page.code.getText());
        assertEquals("1234", otp.getValue());
        assertInputs(otp, "1", "2", "3", "4");
        assertConfiguration(otp.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputOtp: integerOnly rejects letters")
    void integerOnly(Page page) {
        // Arrange
        InputOtp otp = page.otp;

        // Act
        otp.setValue("1a2");

        // Assert
        assertEquals("12", otp.getValue());
        assertInputs(otp, "1", "2", "", "");
        assertConfiguration(otp.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputOtp: an incomplete code is invalid")
    void incomplete(Page page) {
        // Arrange
        InputOtp otp = page.otp;

        // Act
        otp.setValue("12");
        page.button.click();

        // Assert
        assertEquals(1, page.msgs.getAllMessages().size());
        String summary = page.msgs.getMessage(0).getSummary();
        assertTrue(summary.startsWith("OTP:"), summary);
        assertTrue(summary.contains("'4'"), summary);
        assertEquals("", page.code.getText());
        assertTrue(otp.getAttribute("class").contains("p-state-error"));
        assertConfiguration(otp.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("InputOtp: a masked value is not rendered unless redisplay is set")
    void mask(Page page) {
        // Assert
        assertEquals("", page.masked.getValue());
        assertInputs(page.masked, "", "", "", "");
        assertEquals("password", page.masked.getInput().getDomAttribute("type"));

        assertEquals("1234", page.redisplay.getValue());
        assertInputs(page.redisplay, "1", "2", "3", "4");
        assertConfiguration(page.masked.getWidgetConfiguration());
    }

    private void assertInputs(InputOtp otp, String... chars) {
        List<WebElement> inputs = otp.getInputs();
        assertEquals(chars.length, inputs.size());
        for (int i = 0; i < chars.length; i++) {
            assertEquals(chars[i], inputs.get(i).getDomProperty("value"), "input " + i);
        }
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputOtp Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages msgs;

        @FindBy(id = "form:otp")
        InputOtp otp;

        @FindBy(id = "form:code")
        WebElement code;

        @FindBy(id = "form:masked")
        InputOtp masked;

        @FindBy(id = "form:redisplay")
        InputOtp redisplay;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputotp/inputOtp001.xhtml";
        }
    }
}
