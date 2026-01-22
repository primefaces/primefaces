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
package org.primefaces.integrationtests.colorpicker;

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.util.LangUtils;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractColorPickerTest extends AbstractPrimePageTest {

    private Duration defaultImplicitWaitTimeout;

    @BeforeEach
    public void beforeEach() {
        super.beforeEach();

        defaultImplicitWaitTimeout = getWebDriver().manage().timeouts().getImplicitWaitTimeout();
        getWebDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @AfterEach
    public void afterEach() {
        getWebDriver().manage().timeouts().implicitlyWait(defaultImplicitWaitTimeout);
    }

    protected void assertAriaLabel(WebElement panel, String id, String value) {
        WebElement ariaElement = panel.findElement(By.id(id));
        assertPresent(ariaElement);
        String ariaLabel = ariaElement.getDomAttribute("aria-label");
        if (LangUtils.isBlank(ariaLabel)) {
            ariaLabel = ariaElement.getAttribute("textContent");
        }
        assertEquals(value, ariaLabel);
    }

    protected void assertMessage(Messages messages, String summary, String detail) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(messages));
        Msg msg = messages.getMessage(0);
        assertEquals(summary, msg.getSummary());
        assertEquals(detail, msg.getDetail());
    }

}
