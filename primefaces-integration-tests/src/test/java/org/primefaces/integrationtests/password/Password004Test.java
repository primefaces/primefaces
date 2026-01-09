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
package org.primefaces.integrationtests.password;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Password;
import org.primefaces.selenium.component.model.Msg;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Password004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Password: Passwords are required for matching")
    void required(Page page) {
        // Arrange
        Password pwd1 = page.pwd1;
        Password pwd2 = page.pwd2;
        assertEquals("", pwd1.getValue());
        assertEquals("", pwd2.getValue());

        // Act
        page.button.click();

        // Assert
        Msg message1 = page.messages.getMessage(0);
        assertNotNull(message1);
        assertEquals("Password 1: Validation Error: Value is required.", message1.getSummary());
        Msg message2 = page.messages.getMessage(1);
        assertNotNull(message2);
        assertEquals("Password 2: Validation Error: Value is required.", message2.getSummary());
        assertEquals("", pwd1.getValue());
        assertEquals("", pwd2.getValue());
        assertCss(pwd1, "ui-state-error");
        assertCss(pwd2, "ui-state-error");
        assertConfiguration(pwd1.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Password: Passwords do not match and should be rejected")
    void doNotMatch(Page page) {
        // Arrange
        Password pwd1 = page.pwd1;
        Password pwd2 = page.pwd2;
        assertEquals("", pwd1.getValue());
        assertEquals("", pwd2.getValue());

        // Act
        pwd1.setValue("apple");
        pwd2.setValue("orange");
        page.button.click();

        // Assert
        Msg message1 = page.messages.getMessage(0);
        assertNotNull(message1);
        assertEquals("Password 1: Validation Error.", message1.getSummary());
        assertEquals("'Password 1' should match with 'Password 2'.", message1.getDetail());
        assertEquals("", pwd1.getValue());
        assertEquals("", pwd2.getValue());
        assertCss(pwd1, "ui-state-error");
        assertCss(pwd2, "ui-state-error");
        assertConfiguration(pwd1.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Password: Passwords match and should be accepted")
    void match(Page page) {
        // Arrange
        Password pwd1 = page.pwd1;
        Password pwd2 = page.pwd2;
        assertEquals("", pwd1.getValue());
        assertEquals("", pwd2.getValue());

        // Act
        pwd1.setValue("banana");
        pwd2.setValue("banana");
        page.button.click();

        // Assert
        Msg message1 = page.messages.getMessage(0);
        assertNull(message1);
        assertEquals("", pwd1.getValue());
        assertEquals("", pwd2.getValue());
        assertConfiguration(pwd1.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Password Config = " + cfg);
        assertTrue(cfg.has("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:pwd1")
        Password pwd1;

        @FindBy(id = "form:pwd2")
        Password pwd2;

        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "password/password004.xhtml";
        }
    }
}
