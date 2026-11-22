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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneMenu018Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #13954 submitting without changes keeps the preselected disabled value")
    void submitUnchanged(Page page) {
        // Act
        page.submit.click();

        // Assert
        assertEquals("", page.countryValue.getText());
        assertEquals("CW", page.presetValue.getText());
        assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #13954 an enabled item is accepted")
    void selectEnabled(Page page) {
        // Act
        page.country.select("NL");
        page.submit.click();

        // Assert
        assertEquals("NL", page.countryValue.getText());
        assertEquals("CW", page.presetValue.getText());
        assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered disabled item is not accepted when there is no previous value")
    void tamperedDisabledWithoutPreviousValue(Page page) {
        // Act
        tamper("form:country_input", "CW");
        page.submit.click();

        // Assert
        assertEquals("", page.countryValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered disabled item is treated as no selection when there is a previous value")
    void tamperedDisabledWithPreviousValue(Page page) {
        // Arrange
        page.country.select("BE");
        page.submit.click();
        assertEquals("BE", page.countryValue.getText());

        // Act
        tamper("form:country_input", "AW");
        page.submit.click();

        // Assert - like h:selectOneMenu, the disabled value is replaced with an empty value
        assertEquals("", page.countryValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered other disabled item does not replace the preselected disabled value")
    void tamperedOtherDisabledWithPreselectedDisabledValue(Page page) {
        // Act
        tamper("form:preset_input", "AW");
        page.submit.click();

        // Assert
        assertEquals("CW", page.presetValue.getText());
        assertNoJavascriptErrors();
    }

    /**
     * Simulates a crafted request: re-enables a disabled option of the hidden select and selects it.
     */
    private void tamper(String selectId, String value) {
        PrimeSelenium.executeScript("var s = document.getElementById('" + selectId + "');"
                + "var o = s.querySelector('option[value=\"" + value + "\"]');"
                + "o.disabled = false; s.value = '" + value + "';");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:country")
        SelectOneMenu country;

        @FindBy(id = "form:preset")
        SelectOneMenu preset;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:countryValue")
        WebElement countryValue;

        @FindBy(id = "form:presetValue")
        WebElement presetValue;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu018.xhtml";
        }
    }
}
