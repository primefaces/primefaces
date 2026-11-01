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
package org.primefaces.integrationtests.wizard;

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

class Wizard001Test extends AbstractPrimePageTest {

    @ParameterizedTest
    @CsvSource({"false,first,0", "true,first,0", "false,second,1", "true,second,1"})
    @Order(1)
    @DisplayName("Wizard: #15031 distinguish current and completed steps across navigation and refresh")
    void stepStyles(boolean highlight, String start, int initial) {
        // Arrange
        goTo("wizard/wizard001.xhtml?highlight=" + highlight + "&start=" + start);
        assertSteps(initial, highlight);

        // Act
        click("form:wizard_next");

        // Assert
        assertSteps(initial + 1, highlight);
        click("form:refresh");
        assertSteps(initial + 1, highlight);
        click("form:wizard_back");
        assertSteps(initial, highlight);
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @CsvSource({"missing", "hidden"})
    @Order(2)
    void invalidInitialStep(String start) {
        // Arrange / Act
        goTo("wizard/wizard001.xhtml?highlight=true&start=" + start);
        // Assert
        assertSteps(-1, false);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    void preservesCustomHighlight() {
        // Arrange
        goTo("wizard/wizard001.xhtml?custom=true&highlight=true");
        // Act / Assert
        for (String id : new String[] {"form:wizard_next", "form:wizard_back", "form:refresh"}) {
            click(id);
            String classes = PrimeSelenium.getWebDriver().findElements(By.cssSelector(".p-wizard-step-title")).get(2).getDomAttribute("class");
            assertTrue(classes.contains("p-state-highlight"));
            assertTrue(classes.contains("custom"));
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    void validationFailureKeepsStep() {
        // Arrange
        goTo("wizard/wizard001.xhtml?required=true&highlight=true");
        // Act
        click("form:wizard_next");
        // Assert
        assertSteps(0, true);
        PrimeSelenium.getWebDriver().findElement(By.id("form:value")).sendKeys("valid");
        click("form:wizard_next");
        assertSteps(1, true);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    void effectsAndHiddenStatus() {
        // Arrange
        goTo("wizard/wizard001.xhtml?highlight=true&effect=fade");
        // Act
        click("form:wizard_next");
        // Assert
        assertSteps(1, true);
        click("form:wizard_back");
        assertSteps(0, true);
        goTo("wizard/wizard001.xhtml?status=false");
        click("form:wizard_next");
        assertTrue(PrimeSelenium.getWebDriver().findElements(By.cssSelector(".p-wizard-step-title")).isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    void incompleteCallbackKeepsCurrentStep() {
        // Arrange
        goTo("wizard/wizard001.xhtml?highlight=true");
        // Act: exercise callback responses without making a network request.
        PrimeSelenium.executeScript("const original = PrimeFaces.ajax.Request.handle;"
                + "try { PrimeFaces.ajax.Request.handle = o => {"
                + "o.oncomplete(null, null, {}, null);"
                + "o.oncomplete(null, null, {currentStep:'missing'}, null);"
                + "o.oncomplete(null, null, {validationFailed:true,currentStep:'third'}, null);"
                + "}; PF('wizard').loadStep('second', 'next'); }"
                + "finally { PrimeFaces.ajax.Request.handle = original; }");
        // Assert
        assertEquals("first", PrimeSelenium.executeScript("return PF('wizard').currentStep;"));
        assertSteps(0, true);
        PrimeSelenium.executeScript("PF('wizard').currentStep = undefined; PF('wizard').updateStepStatus();");
        assertSteps(0, true);
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @CsvSource({"first,blockNext,next,0", "second,blockBack,back,1"})
    @Order(7)
    void callbackCanCancelNavigation(String start, String parameter, String direction, int index) {
        // Arrange
        goTo("wizard/wizard001.xhtml?highlight=true&start=" + start + "&" + parameter + "=true");
        // Act: a vetoed callback intentionally sends no AJAX request.
        PrimeSelenium.getWebDriver().findElement(By.id("form:wizard_" + direction)).click();
        // Assert
        assertSteps(index, true);
        assertNoJavascriptErrors();
    }

    private void click(String id) {
        PrimeSelenium.guardAjax(PrimeSelenium.getWebDriver().findElement(By.id(id))).click();
        new WebDriverWait(PrimeSelenium.getWebDriver(), Duration.ofSeconds(5)).until(driver ->
                Boolean.TRUE.equals(PrimeSelenium.executeScript("return !PF('wizard').content.is(':animated');")));
    }

    private void assertSteps(int current, boolean highlight) {
        List<WebElement> steps = PrimeSelenium.getWebDriver().findElements(By.cssSelector("#form\\:wizard .p-wizard-step-title"));
        assertEquals(3, steps.size());
        for (int i = 0; i < steps.size(); i++) {
            String classes = steps.get(i).getDomAttribute("class");
            assertEquals(i == current, classes.contains("p-wizard-step-current"));
            assertEquals(highlight && i < current, classes.contains("p-wizard-step-completed"));
            assertEquals(i == current || (highlight && i < current), classes.contains("p-state-highlight"));
        }
    }
}
