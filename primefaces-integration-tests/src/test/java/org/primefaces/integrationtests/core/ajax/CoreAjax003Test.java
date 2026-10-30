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
package org.primefaces.integrationtests.core.ajax;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreAjax003Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("Core-AJAX: Poll with EL oncomplete invokes listener once - https://github.com/primefaces/primefaces/issues/15440")
    void pollWithELOncomplete(Page page) {
        assertEquals("0", page.count1.getText());
        assertEquals("", page.output1.getText());

        // a single poll tick
        PrimeSelenium.executeScript(true, "PF('poll').cfg.fn()");

        assertEquals("1", page.count1.getText());
        assertEquals("1", page.output1.getText());
    }

    @Test
    @DisplayName("Core-AJAX: RemoteCommand with EL oncomplete invokes action once - https://github.com/primefaces/primefaces/issues/15440")
    void remoteCommandWithELOncomplete(Page page) {
        assertEquals("0", page.count2.getText());
        assertEquals("", page.output2.getText());

        PrimeSelenium.executeScript(true, "remoteCommand2()");

        assertEquals("1", page.count2.getText());
        assertEquals("1", page.output2.getText());
    }

    @Test
    @DisplayName("Core-AJAX: Hotkey with EL oncomplete invokes action once - https://github.com/primefaces/primefaces/issues/15440")
    void hotkeyWithELOncomplete(Page page) {
        assertEquals("0", page.count3.getText());
        assertEquals("", page.output3.getText());

        PrimeSelenium.guardAjax(new Actions(getWebDriver()).sendKeys("x").build()).perform();

        assertEquals("1", page.count3.getText());
        assertEquals("1", page.output3.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form1:count")
        WebElement count1;

        @FindBy(id = "output1")
        WebElement output1;

        @FindBy(id = "form2:count")
        WebElement count2;

        @FindBy(id = "output2")
        WebElement output2;

        @FindBy(id = "form3:count")
        WebElement count3;

        @FindBy(id = "output3")
        WebElement output3;

        @Override
        public String getLocation() {
            return "core/ajax/coreAjax003.xhtml";
        }
    }
}
