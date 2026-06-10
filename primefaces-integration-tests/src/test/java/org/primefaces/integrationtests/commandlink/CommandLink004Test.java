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
package org.primefaces.integrationtests.commandlink;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandLink;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLink004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CommandLink: validateClient blocks submit when input is invalid")
    void invalidBlocksSubmit(Page page) {
        // Arrange - leave the required field empty
        assertNoJavascriptErrors();

        // Act - the request must NOT be sent because client validation fails, so don't guard AJAX
        page.linkValidate.clickUnguarded();

        // Assert - the action did not run and the field shows a client-side validation error
        assertTrue(page.messages.getAllSummaries().stream().noneMatch("Submitted"::equals));
        assertTrue(page.msgName.getText().contains("Value is required"), page.msgName.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("CommandLink: validateClient allows submit when input is valid")
    void validAllowsSubmit(Page page) {
        // Arrange
        page.name.setValue("PrimeFaces");

        // Act
        page.linkValidate.click();

        // Assert
        assertEquals("Submitted", page.messages.getMessage(0).getSummary());
        assertEquals("Form is valid: PrimeFaces", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:linkValidate")
        CommandLink linkValidate;

        @FindBy(id = "form:name")
        InputText name;

        @FindBy(id = "form:msgName")
        Messages msgName;

        @FindBy(id = "form:messages")
        Messages messages;

        @Override
        public String getLocation() {
            return "commandlink/commandLink004.xhtml";
        }
    }

}
