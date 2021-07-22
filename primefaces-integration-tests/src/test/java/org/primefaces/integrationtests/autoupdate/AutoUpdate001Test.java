/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;

public class AutoUpdate001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoUpdate: Globally update all AutoUpdate components")
    public void testGlobal(Page page) {
        // Arrange
        assertInitialValues(page);

        // Act
        page.buttonGlobal.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("global", page.displayGlobal.getText());
        Assertions.assertEquals("", page.displayEvent1.getText());
        Assertions.assertEquals("", page.displayEvent2.getText());
    }

    @Test
    @Order(2)
    @DisplayName("AutoUpdate: Pub/Sub only update the events subscribed")
    public void testPubSubEvent1(Page page) {
        // Arrange
        assertInitialValues(page);

        // Act
        page.buttonEvent1.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("one", page.displayGlobal.getText());
        Assertions.assertEquals("one", page.displayEvent1.getText());
        Assertions.assertEquals("", page.displayEvent2.getText());
    }

    @Test
    @Order(3)
    @DisplayName("AutoUpdate: Pub/Sub only update the events subscribed")
    public void testPubSubEvent2(Page page) {
        // Arrange
        assertInitialValues(page);

        // Act
        page.buttonEvent2.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("two", page.displayGlobal.getText());
        Assertions.assertEquals("", page.displayEvent1.getText());
        Assertions.assertEquals("two", page.displayEvent2.getText());
    }

    private void assertInitialValues(Page page) {
        Assertions.assertEquals("", page.displayGlobal.getText());
        Assertions.assertEquals("", page.displayEvent1.getText());
        Assertions.assertEquals("", page.displayEvent2.getText());
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
