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
package org.primefaces.integrationtests.core.csp;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoreCsp001Test  extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Core-CSP: open in new page don't break NONCE in current page - https://github.com/primefaces/primefaces/issues/12399")
    void cspInNewPage(Page page) {
        // Arrange
        assertEquals("", page.firstname.getValue());
        assertEquals("", page.lastname.getValue());

        // Act
        page.firstname.setValue("John");
        page.lastname.setValue("Doe");
        page.lnkNewPage.click();

        // Assert
        assertEquals("John", page.firstname.getValue());
        assertEquals("Doe", page.lastname.getValue());
        assertNoJavascriptErrors();

        // Act
        page.firstname.setValue("");
        page.lastname.setValue(""); // AJAX update triggers CSP nonce mismatch

        // Assert
        assertPresent(page.firstname);
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:firstname")
        InputText firstname;

        @FindBy(id = "form:lastname")
        InputText lastname;

        @FindBy(id = "form:btnSave")
        CommandButton btnSave;

        @FindBy(id = "form:lnkNewPage")
        WebElement lnkNewPage;

        @Override
        public String getLocation() {
            return "core/csp/coreCsp001.xhtml";
        }
    }
}
