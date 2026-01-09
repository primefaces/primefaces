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
package org.primefaces.integrationtests.autoupdate;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoUpdate001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoUpdate: Globally update all AutoUpdate components")
    void global(Page page) {
        // Arrange
        assertInitialValues(page);

        // Act
        page.buttonGlobal.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("global", page.displayGlobal.getText());
        assertEquals("", page.displayEvent1.getText());
        assertEquals("", page.displayEvent2.getText());
    }

    @Test
    @Order(2)
    @DisplayName("AutoUpdate: Pub/Sub only update the events subscribed")
    void pubSubEvent1(Page page) {
        // Arrange
        assertInitialValues(page);

        // Act
        page.buttonEvent1.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("one", page.displayGlobal.getText());
        assertEquals("one", page.displayEvent1.getText());
        assertEquals("", page.displayEvent2.getText());
    }

    @Test
    @Order(3)
    @DisplayName("AutoUpdate: Pub/Sub only update the events subscribed")
    void pubSubEvent2(Page page) {
        // Arrange
        assertInitialValues(page);

        // Act
        page.buttonEvent2.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("two", page.displayGlobal.getText());
        assertEquals("", page.displayEvent1.getText());
        assertEquals("two", page.displayEvent2.getText());
    }

    private void assertInitialValues(Page page) {
        assertEquals("", page.displayGlobal.getText());
        assertEquals("", page.displayEvent1.getText());
        assertEquals("", page.displayEvent2.getText());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:displayGlobal")
        WebElement displayGlobal;

        @FindBy(id = "form:displayEvent1")
        WebElement displayEvent1;

        @FindBy(id = "form:displayEvent2")
        WebElement displayEvent2;

        @FindBy(id = "form:btnGlobal")
        CommandButton buttonGlobal;

        @FindBy(id = "form:btnEvent1")
        CommandButton buttonEvent1;

        @FindBy(id = "form:btnEvent2")
        CommandButton buttonEvent2;

        @Override
        public String getLocation() {
            return "autoupdate/autoUpdate001.xhtml";
        }
    }
}
