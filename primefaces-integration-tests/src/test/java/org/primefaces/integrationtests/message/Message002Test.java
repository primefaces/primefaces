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
package org.primefaces.integrationtests.message;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Message002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Message: #3796 validation errors with composite components")
    void basic(Page page) {
        // Arrange
        assertNotPresent(page.msgCustomInputTextTooltip.getSummaryError());
        assertNotPresent(page.msgCustomInputTextTooltipHolder.getSummaryError());
        assertNotPresent(page.msgCustomInputTextTooltipHolderWithComposite.getSummaryError());
        assertNotPresent(page.msgStandardInputText.getSummaryError());

        // Act
        page.buttonSubmit.click();

        // Assert
        assertPresent(page.msgStandardInputText.getSummaryError());
        assertPresent(page.msgCustomInputTextTooltip.getSummaryError());
        assertPresent(page.msgCustomInputTextTooltipHolder.getSummaryError());
        assertPresent(page.msgCustomInputTextTooltipHolderWithComposite.getSummaryError());
        assertEquals("Label for standard InputText: Validation Error: Value is required.", page.msgStandardInputText.getSummaryError().getText());
        assertEquals("Label for custom InputTextTooltip: Validation Error: Value is required.", page.msgCustomInputTextTooltip.getSummaryError().getText());
        assertEquals("Label for custom InputTextTooltipHolder: Validation Error: Value is required.",
                page.msgCustomInputTextTooltipHolder.getSummaryError().getText());
        assertEquals("Label for custom InputTextTooltipHolderWithComposite: Validation Error: Value is required.",
                page.msgCustomInputTextTooltipHolderWithComposite.getSummaryError().getText());


        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgStandardInputText")
        Message msgStandardInputText;

        @FindBy(id = "form:msgCustomInputTextTooltip")
        Message msgCustomInputTextTooltip;

        @FindBy(id = "form:msgCustomInputTextTooltipHolder")
        Message msgCustomInputTextTooltipHolder;

        @FindBy(id = "form:msgCustomInputTextTooltipHolderWithComposite")
        Message msgCustomInputTextTooltipHolderWithComposite;

        @FindBy(id = "form:buttonSubmit")
        CommandButton buttonSubmit;

        @FindBy(id = "form:buttonReset")
        CommandButton buttonReset;

        @Override
        public String getLocation() {
            return "message/message002.xhtml";
        }
    }
}
