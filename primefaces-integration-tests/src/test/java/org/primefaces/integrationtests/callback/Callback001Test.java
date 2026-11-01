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
package org.primefaces.integrationtests.callback;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Panel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Callback001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Callback: executes the JavaScript with source, event and the params of the widget")
    void widgetEvent(Page page) {
        // Act
        page.panel.toggle();

        // Assert
        assertEquals("form:panel|toggle|null", page.output.getText());

        // Act
        PrimeSelenium.executeScript("PF('panel').callBehavior('toggle', {params: [{name: 'a', value: 1}]});");

        // Assert
        assertEquals("form:panel|toggle|[{\"name\":\"a\",\"value\":1}]", page.output.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Callback: the following p:ajax is called, unless the callback returns false")
    void chained(Page page) {
        // Act
        page.chained.toggle();

        // Assert
        assertEquals("chained", page.output.getText());
        assertEquals("AJAX 1", page.msgs.getMessage(0).getSummary());

        // Act
        PrimeSelenium.executeScript("PF('stopped').toggle();");

        // Assert
        assertEquals("stopped", page.output.getText());
        assertEquals("AJAX 1", page.msgs.getMessage(0).getSummary());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Callback: a disabled callback is not executed")
    void disabled(Page page) {
        // Act
        PrimeSelenium.executeScript("PF('disabled').toggle();");

        // Assert
        assertEquals("-", page.output.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Callback: DOM event of a button, returning false prevents the AJAX request")
    void domEvent(Page page) {
        // Act
        page.button.click();

        // Assert
        assertEquals("form:button|click|null", page.output.getText());
        assertEquals("AJAX 1", page.msgs.getMessage(0).getSummary());

        // Act
        page.stoppedButton.getRoot().click();

        // Assert
        assertEquals("stoppedButton", page.output.getText());
        assertEquals("AJAX 1", page.msgs.getMessage(0).getSummary());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages msgs;

        @FindBy(id = "form:output")
        WebElement output;

        @FindBy(id = "form:panel")
        Panel panel;

        @FindBy(id = "form:chained")
        Panel chained;

        @FindBy(id = "form:disabled")
        Panel disabled;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:stoppedButton")
        CommandButton stoppedButton;

        @Override
        public String getLocation() {
            return "callback/callback001.xhtml";
        }
    }
}
