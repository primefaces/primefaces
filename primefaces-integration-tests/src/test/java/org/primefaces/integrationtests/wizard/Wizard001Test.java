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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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

    private void click(String id) {
        PrimeSelenium.guardAjax(PrimeSelenium.getWebDriver().findElement(By.id(id))).click();
    }

    private void assertSteps(int current, boolean highlight) {
        List<WebElement> steps = PrimeSelenium.getWebDriver().findElements(By.cssSelector("#form\\:wizard .ui-wizard-step-title"));
        assertEquals(3, steps.size());
        for (int i = 0; i < steps.size(); i++) {
            String classes = steps.get(i).getDomAttribute("class");
            assertEquals(i == current, classes.contains("ui-wizard-step-current"));
            assertEquals(highlight && i < current, classes.contains("ui-wizard-step-completed"));
            assertEquals(i == current || (highlight && i < current), classes.contains("ui-state-highlight"));
        }
    }
}
