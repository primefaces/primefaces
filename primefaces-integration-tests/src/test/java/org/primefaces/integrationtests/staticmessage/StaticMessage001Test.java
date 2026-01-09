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
package org.primefaces.integrationtests.staticmessage;

import org.primefaces.component.growl.Growl;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.StaticMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaticMessage001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("StaticMessage: info message")
    void info(Page page) {
        // Arrange
        StaticMessage message = page.msgInfo;

        // Act

        // Assert
        assertDisplayed(message);
        assertNotPresent(message.getCloseButton());
        assertPresent(message.getIconInfo());
        assertEquals("INFO", message.getSummaryInfo().getText());
        assertEquals("Info Content", message.getDetailInfo().getText());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("StaticMessage: warn message")
    void warn(Page page) {
        // Arrange
        StaticMessage message = page.msgWarn;

        // Act

        // Assert
        assertDisplayed(message);
        assertNotPresent(message.getCloseButton());
        assertPresent(message.getIconWarn());
        assertEquals("WARN", message.getSummaryWarn().getText());
        assertEquals("Warn Content", message.getDetailWarn().getText());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("StaticMessage: error message")
    void error(Page page) {
        // Arrange
        StaticMessage message = page.msgError;

        // Act

        // Assert
        assertDisplayed(message);
        assertNotPresent(message.getCloseButton());
        assertPresent(message.getIconError());
        assertEquals("ERROR", message.getSummaryError().getText());
        assertEquals("Error Content", message.getDetailError().getText());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("StaticMessage: #10988 add closable button")
    void closable(Page page) {
        // Arrange
        StaticMessage message = page.msgClosable;
        assertPresent(message.getCloseButton());
        assertDisplayed(message);

        // Act
        message.getCloseButton().click();

        // Assert
        assertNotDisplayed(message);

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:growl")
        Growl growl;

        @FindBy(id = "form:msgInfo")
        StaticMessage msgInfo;

        @FindBy(id = "form:msgWarn")
        StaticMessage msgWarn;

        @FindBy(id = "form:msgError")
        StaticMessage msgError;

        @FindBy(id = "form:msgClosable")
        StaticMessage msgClosable;

        @Override
        public String getLocation() {
            return "staticmessage/staticMessage001.xhtml";
        }
    }
}
