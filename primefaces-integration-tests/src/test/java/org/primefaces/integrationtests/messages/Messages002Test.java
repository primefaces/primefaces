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
package org.primefaces.integrationtests.messages;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Messages002Test extends AbstractPrimePageTest {
    @Test
    @DisplayName("client-side appendMessage: close icon not rendered")
    public void testNotClosable(Page page) {
        PrimeSelenium.executeScript("PF('messages').appendMessage({severity:'info', summary:'Info'});");

        assertThrows(NoSuchElementException.class, () -> page.messages.getCloseButton("info"));
    }

    @Test
    @DisplayName("client-side appendMessage: close icon rendered")
    public void testClosable(Page page) {
        PrimeSelenium.executeScript("PF('messagesClosable').appendMessage({severity:'info', summary:'Info'});");

        assertDisplayed(page.messagesClosable.getCloseButton("info"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:messagesClosable")
        Messages messagesClosable;

        @Override
        public String getLocation() {
            return "messages/messages002.xhtml";
        }
    }
}
