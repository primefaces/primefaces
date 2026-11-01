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
package org.primefaces.integrationtests.inputphone;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputPhone;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputPhone001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputPhone: a value selects its country and is formatted")
    void preset(Page page) {
        // Arrange
        InputPhone preset = page.preset;

        // Assert
        assertEquals("US", preset.getCountry());
        assertEquals("(201) 555-0123", preset.getValue());
        assertEquals("+12015550123", preset.getWidgetValue());
        assertTrue(preset.isValidNumber());
        assertConfiguration(preset.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputPhone: a national number is submitted in the E.164 format")
    void submit(Page page) {
        // Arrange
        InputPhone phone = page.phone;
        assertEquals("DE", phone.getCountry());

        // Act
        phone.setValue("030 123456");
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("+4930123456", page.phoneValue.getText());
        assertEquals("+4930123456", phone.getWidgetValue());
        assertEquals("030 123456", phone.getValue());
        assertConfiguration(phone.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputPhone: an invalid number is rejected by the server")
    void invalid(Page page) {
        // Arrange
        InputPhone phone = page.phone;

        // Act
        phone.setValue("123");
        assertFalse(phone.isValidNumber());
        page.button.click();

        // Assert
        assertEquals(1, page.msgs.getAllMessages().size());
        assertEquals("Phone: Validation Error: Value is not a valid phone number.", page.msgs.getMessage(0).getSummary());
        assertEquals("", page.phoneValue.getText());
        assertEquals("123", phone.getValue());
        assertTrue(phone.getAttribute("class").contains("p-state-error"));
        assertConfiguration(phone.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("InputPhone: selecting a country calls the countrySelect event")
    void countrySelect(Page page) {
        // Arrange
        InputPhone country = page.country;
        assertEquals("DE", country.getCountry());

        // Act
        country.selectCountry("AT");

        // Assert
        assertEquals("AT", country.getCountry());
        assertEquals("AT", page.selectedCountry.getText());
        assertEquals("+43", country.getCountryButton().findElement(By.className("p-inputphone-dial-code")).getText());
        assertConfiguration(country.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputPhone Config = " + cfg);
        assertTrue(cfg.has("initialCountry"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages msgs;

        @FindBy(id = "form:phone")
        InputPhone phone;

        @FindBy(id = "form:phoneValue")
        WebElement phoneValue;

        @FindBy(id = "form:preset")
        InputPhone preset;

        @FindBy(id = "form:country")
        InputPhone country;

        @FindBy(id = "form:selectedCountry")
        WebElement selectedCountry;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputphone/inputPhone001.xhtml";
        }
    }
}
