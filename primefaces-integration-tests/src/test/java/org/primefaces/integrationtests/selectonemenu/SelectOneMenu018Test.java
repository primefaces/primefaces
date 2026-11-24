/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;

public class SelectOneMenu018Test extends AbstractPrimePageTest {

    static final String INVALID = "Value is not valid";

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #13954 submitting without changes keeps the preselected disabled value")
    public void submitUnchanged(Page page) {
        // Act
        page.submit.click();

        // Assert
        Assertions.assertEquals("", page.countryValue.getText());
        Assertions.assertEquals("CW", page.presetValue.getText());
        Assertions.assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #13954 an enabled item is accepted")
    public void selectEnabled(Page page) {
        // Act
        page.country.select("NL");
        page.submit.click();

        // Assert
        Assertions.assertEquals("NL", page.countryValue.getText());
        Assertions.assertEquals("CW", page.presetValue.getText());
        Assertions.assertTrue(page.messages.isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered disabled item fails validation when there is no previous value")
    public void tamperedDisabledWithoutPreviousValue(Page page) {
        // Act
        tamper("form:country_input", "CW");
        page.submit.click();

        // Assert
        assertInvalid(page, "Country");
        Assertions.assertEquals("", page.countryValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered disabled item fails validation and keeps the previous value")
    public void tamperedDisabledWithPreviousValue(Page page) {
        // Arrange
        page.country.select("BE");
        page.submit.click();
        Assertions.assertEquals("BE", page.countryValue.getText());

        // Act
        tamper("form:country_input", "AW");
        page.submit.click();

        // Assert
        assertInvalid(page, "Country");
        Assertions.assertEquals("BE", page.countryValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: GitHub #13954 a tampered other disabled item fails validation with a preselected disabled value")
    public void tamperedOtherDisabledWithPreselectedDisabledValue(Page page) {
        // Act
        tamper("form:preset_input", "AW");
        page.submit.click();

        // Assert
        assertInvalid(page, "Preset");
        Assertions.assertEquals("CW", page.presetValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("SelectOneMenu: GitHub #13954 an unknown value fails validation with a preselected disabled value")
    public void unknownValueWithPreselectedDisabledValue(Page page) {
        // Act - an unknown value was silently replaced with the preselected disabled value before
        PrimeSelenium.executeScript("var s = document.getElementById('form:preset_input');"
                + "var o = document.createElement('option'); o.value = 'XX'; s.appendChild(o); s.value = 'XX';");
        page.submit.click();

        // Assert
        assertInvalid(page, "Preset");
        Assertions.assertEquals("CW", page.presetValue.getText());
        assertNoJavascriptErrors();
    }

    private static void assertInvalid(Page page, String label) {
        Assertions.assertEquals(1, page.messages.getAllMessages().size());
        String summary = page.messages.getMessage(0).getSummary();
        Assertions.assertTrue(summary.contains(label) && summary.contains(INVALID), summary);
    }

    /**
     * Simulates a crafted request: re-enables a disabled option of the hidden select and selects it.
     */
    static void tamper(String selectId, String value) {
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
