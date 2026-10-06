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
package org.primefaces.integrationtests.autocomplete;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.AutoComplete;
import org.primefaces.selenium.component.CommandButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoComplete009Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #15304 forceSelection is not bypassed when submitting right after typing")
    void forceSelectionSubmitWhileTyping(Page page) {
        int[] delays = {0, 100, 200, 300, 400, 600, 900, 1200, 1400};
        for (int i = 0; i < delays.length * 2; i++) {
            // Act - type an invalid value and submit before/while the search is running
            for (char c : ("xyz" + i).toCharArray()) {
                getWebDriver().findElement(By.id("form:autocomplete_input")).sendKeys(String.valueOf(c));
                PrimeSelenium.wait(70);
            }
            PrimeSelenium.wait(delays[i % delays.length]);
            new Actions(getWebDriver()).moveToElement(page.button).click().perform();
            // second round: do not wait for the previous requests
            if (i < delays.length) {
                PrimeSelenium.wait(1500);
                PrimeSelenium.waitGui().until(PrimeExpectedConditions.ajaxQueueEmpty());
            }
            else {
                PrimeSelenium.wait(150);
            }
        }
        PrimeSelenium.wait(2500);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.ajaxQueueEmpty());

        // Assert - no invalid value reached the model
        assertEquals("[]", page.submittedValues.getText());
        assertEquals("", page.autoComplete.getInput().getAttribute("value"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:submittedValues")
        WebElement submittedValues;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete009.xhtml";
        }
    }
}
