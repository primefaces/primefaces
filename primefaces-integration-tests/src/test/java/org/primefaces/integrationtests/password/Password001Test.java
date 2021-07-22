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
package org.primefaces.integrationtests.password;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Password;

public class Password001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Password: Value should not be redisplayed after submission")
    public void testRedisplayFalse(Page page) {
        // Arrange
        Password password = page.password;
        Assertions.assertEquals("", password.getValue());

        // Act
        password.setValue("encryptme!");
        page.button.click();

        // Assert
        Assertions.assertEquals("", password.getValue());
        assertConfiguration(password.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Password: Value should be redisplayed after submission")
    public void testRedisplayTrue(Page page) {
        // Arrange
        Password password = page.redisplay;
        Assertions.assertEquals("", password.getValue());

        // Act
        password.setValue("encryptme!");
        page.button.click();

        // Assert
        Assertions.assertEquals("encryptme!", password.getValue());
        assertConfiguration(password.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Password Config = " + cfg);
        Assertions.assertTrue(cfg.has("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:password")
        Password password;

        @FindBy(id = "form:redisplay")
        Password redisplay;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "password/password001.xhtml";
        }
    }
}
