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
package org.primefaces.integrationtests.confirmdialog;

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmDialog003Test extends AbstractPrimePageTest {

    @ParameterizedTest
    @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    @Order(1)
    @DisplayName("Confirm: #9983 switch value changes only after confirmation")
    void toggle(boolean popup, boolean keyboard) {
        // Arrange
        goTo("confirmdialog/confirmDialog003.xhtml?popup=" + popup);
        WebElement input = element("form:toggle_input");
        assertFalse(input.isSelected());

        // Act
        clickToggle(keyboard);
        answer(popup, false);

        // Assert
        assertFalse(input.isSelected());
        assertEquals("false,A,0", element("form:values").getText());
        clickToggle(keyboard);
        answer(popup, true);
        assertTrue(input.isSelected());
        assertEquals("true,A,1", element("form:values").getText());
        clickToggle(keyboard);
        answer(popup, false);
        assertTrue(input.isSelected());
        assertEquals("true,A,1", element("form:values").getText());
        clickToggle(keyboard);
        answer(popup, true);
        assertFalse(input.isSelected());
        assertEquals("false,A,2", element("form:values").getText());
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @CsvSource({"false,false", "false,true", "true,false", "true,true"})
    @Order(2)
    @DisplayName("Confirm: #15314 selection is retained on cancel and updated once on confirm")
    void select(boolean popup, boolean keyboard) {
        // Arrange
        goTo("confirmdialog/confirmDialog003.xhtml?popup=" + popup);
        assertTrue(element("form:select:0").isSelected());

        // Act
        chooseB(keyboard);
        answer(popup, false);

        // Assert
        assertTrue(element("form:select:0").isSelected());
        assertFalse(element("form:select:1").isSelected());
        assertEquals("false,A,0", element("form:values").getText());
        if (keyboard) {
            awaitFocusReturn();
        }
        chooseB(keyboard);
        answer(popup, true);
        assertTrue(element("form:select:1").isSelected());
        assertEquals("false,B,1", element("form:values").getText());
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    @Order(3)
    @DisplayName("Confirm: a dynamically disabled switch cannot request a change")
    void disabledToggle(boolean popup) {
        // Arrange
        goTo("confirmdialog/confirmDialog003.xhtml?popup=" + popup);
        element("form:disable").click();
        assertFalse(element("form:toggle_input").isEnabled());

        // Act: disabled widgets use pointer-events:none, so click their screen position.
        new Actions(PrimeSelenium.getWebDriver())
                .moveToElement(element("form:toggle")).click().perform();

        // Assert
        assertFalse(element("form:dialog").isDisplayed());
        assertFalse(element("form:popup").isDisplayed());
        assertEquals("false,A,0", element("form:values").getText());
        element("form:enable").click();
        clickToggle(false);
        answer(popup, true);
        assertEquals("true,A,1", element("form:values").getText());
        assertNoJavascriptErrors();
    }

    private void clickToggle(boolean keyboard) {
        if (keyboard) {
            element("form:toggle_input").sendKeys(Keys.SPACE);
        }
        else {
            element("form:toggle").findElement(By.className("ui-toggleswitch-slider")).click();
        }
    }

    private void chooseB(boolean keyboard) {
        if (keyboard) {
            element("form:select:0").sendKeys(Keys.ARROW_RIGHT);
        }
        else {
            element("form:select").findElement(By.cssSelector("label[for='form:select:1']")).click();
        }
    }

    // Closing the dialog or popup refocuses option B about 100ms later (returnFocus). If that lands between
    // sendKeys focusing option A and the key press, ARROW_RIGHT wraps from B back to the selected A and nothing changes.
    private void awaitFocusReturn() {
        new WebDriverWait(PrimeSelenium.getWebDriver(), Duration.ofSeconds(5))
                .until(d -> "form:select".equals(d.switchTo().activeElement().getDomAttribute("name")));
    }

    private void answer(boolean popup, boolean yes) {
        String prefix = popup ? "ui-confirm-popup-" : "ui-confirmdialog-";
        WebElement button = PrimeSelenium.getWebDriver().findElement(By.className(prefix + (yes ? "yes" : "no")));
        new WebDriverWait(PrimeSelenium.getWebDriver(), Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOf(button));
        if (yes) {
            PrimeSelenium.guardAjax(button).click();
        }
        else {
            button.click();
        }
    }

    private WebElement element(String id) {
        return PrimeSelenium.getWebDriver().findElement(By.id(id));
    }
}
