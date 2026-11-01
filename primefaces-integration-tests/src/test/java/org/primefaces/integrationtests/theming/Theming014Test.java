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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Invalid form fields in every preset: the invalid border color (--p-form-field-invalid-border-color, all component invalid
 * tokens reference it) stays on hover and focus, in light and dark mode, after server side and client side validation.
 */
class Theming014Test extends AbstractThemingTest {

    private static final String INVALID = "--p-form-field-invalid-border-color";

    private static final List<String> FIELDS = List.of("text", "password", "number", "textarea", "mask", "autocomplete", "date",
            "spinner", "chips", "select", "multiselect", "radio", "checkboxes");

    /** the element of the field that is marked invalid and draws the border (the input, the select box, the checkbox box, ...) */
    private static final String INVALID_ELEMENT_SCRIPT = ""
            + "var root = document.getElementById(arguments[0]);"
            + "var all = [root].concat(Array.prototype.slice.call(root.querySelectorAll('.p-state-error')));"
            + "return all.filter(function (e) {"
            + "  return e.classList.contains('p-state-error') && parseFloat(getComputedStyle(e).borderTopWidth) > 0;"
            + "})[0] || null;";

    /** focuses the input behind the element (the element itself, the hidden accessible input of a checkbox, the label of a select, ...) */
    private static final String FOCUS_SCRIPT = ""
            + "var e = arguments[0];"
            + "var f = e.matches('input, textarea') ? e : e.closest('.field').querySelector('input:not([type=hidden]), textarea, [tabindex=\"0\"]');"
            + "if (!f) { return false; }"
            + "f.focus();"
            + "return document.activeElement === f;";

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(1)
    @DisplayName("Theming: server side validation marks every field invalid, also on hover and focus, in light and dark mode")
    void serverValidation(String preset) {
        // Arrange
        PrimeSelenium.goTo("theming/theming014.xhtml?theme=" + preset);
        setColorScheme("light");
        WebElement text = PrimeSelenium.getWebDriver().findElement(By.id("form:text"));
        assertFalse(text.getDomAttribute("class").contains("p-state-error"));

        // Act
        PrimeSelenium.guardAjax(PrimeSelenium.getWebDriver().findElement(By.id("form:submit"))).click();
        setColorScheme("light");

        // Assert
        for (String scheme : List.of("light", "dark")) {
            setColorScheme(scheme);
            for (String field : FIELDS) {
                assertTokenValue(preset + " " + scheme + " " + field, invalidElement(field), "border-top-color", INVALID);
            }
        }
        setColorScheme("light");
        for (String field : FIELDS) {
            WebElement element = invalidElement(field);
            new Actions(PrimeSelenium.getWebDriver()).moveToElement(element).perform();
            assertTokenValue(preset + " hover " + field, element, "border-top-color", INVALID);
            Boolean focused = PrimeSelenium.executeScript(FOCUS_SCRIPT, element);
            assertTrue(focused, preset + " " + field + " could not be focused");
            assertTokenValue(preset + " focus " + field, invalidElement(field), "border-top-color", INVALID);
        }
        assertTokenWired(preset + " label", PrimeSelenium.getWebDriver().findElement(By.id("form:textLabel")), "color",
                "--p-label-invalid-color");
        WebElement message = PrimeSelenium.getWebDriver().findElement(By.cssSelector("[id='form:textMessage'].p-message-error"));
        assertTokenWired(preset + " message", message, "background-color", "--p-message-error-background");
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(2)
    @DisplayName("Theming: client side validation marks the fields invalid (also the spinner, marked on its wrapper)")
    void clientValidation(String preset) {
        // Arrange
        PrimeSelenium.goTo("theming/theming014.xhtml?theme=" + preset);
        setColorScheme("light");

        // Act: no request, the client side validation fails
        PrimeSelenium.getWebDriver().findElement(By.id("form:submitClient")).click();

        // Assert
        for (String field : List.of("text", "password", "textarea")) {
            WebElement element = invalidElement(field);
            assertTokenValue(preset + " " + field, element, "border-top-color", INVALID);
            new Actions(PrimeSelenium.getWebDriver()).moveToElement(element).perform();
            assertTokenValue(preset + " hover " + field, element, "border-top-color", INVALID);
        }
        WebElement spinner = PrimeSelenium.getWebDriver().findElement(By.id("form:spinner"));
        assertTrue(spinner.getDomAttribute("class").contains("p-state-error"), "the client side validation marks the spinner wrapper");
        WebElement spinnerInput = PrimeSelenium.getWebDriver().findElement(By.id("form:spinner_input"));
        assertTokenValue(preset + " spinner input", spinnerInput, "border-top-color", INVALID);
        new Actions(PrimeSelenium.getWebDriver()).moveToElement(spinnerInput).perform();
        assertTokenValue(preset + " hover spinner input", spinnerInput, "border-top-color", INVALID);
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(3)
    @DisplayName("Theming: the tooltip of an invalid field (p:message display=tooltip) and an invalid readonly selectOneRadio")
    void tooltipAndReadonly(String preset) {
        // Arrange
        PrimeSelenium.goTo("theming/theming014.xhtml?theme=" + preset);
        setColorScheme("light");
        PrimeSelenium.guardAjax(PrimeSelenium.getWebDriver().findElement(By.id("form:submit"))).click();
        setColorScheme("light");

        // Act: hover the invalid field
        WebElement field = PrimeSelenium.getWebDriver().findElement(By.id("form:tooltipText"));
        new Actions(PrimeSelenium.getWebDriver()).moveToElement(field).perform();
        WebElement text = PrimeSelenium.getWebDriver().findElement(By.cssSelector(".p-tooltip .p-tooltip-text"));
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(text));

        // Assert
        assertTrue(text.getDomAttribute("class").contains("p-state-error"), preset + ": the tooltip is not marked invalid");
        assertTokenWired(preset + " invalid tooltip", text, "background-color", "--p-tooltip-invalid-background");
        assertTokenWired(preset + " invalid tooltip", text, "color", "--p-tooltip-invalid-color");

        // Act: a readonly radio marked invalid (like the renderer marks an invalid component), hovered
        WebElement box = PrimeSelenium.getWebDriver().findElement(By.cssSelector("[id='form:readonlyRadio'] .p-radiobutton-box"));
        PrimeSelenium.executeScript("arguments[0].classList.add('p-state-error');", box);
        new Actions(PrimeSelenium.getWebDriver()).moveToElement(box).perform();

        // Assert
        assertTokenValue(preset + " readonly radio hover", box, "border-top-color", INVALID);
        assertNoJavascriptErrors();
    }

    private static WebElement invalidElement(String field) {
        WebElement element = PrimeSelenium.executeScript(INVALID_ELEMENT_SCRIPT, "form:" + field);
        assertNotNull(element, field + ": no element marked p-state-error with a border");
        return element;
    }
}
