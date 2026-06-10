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
package org.primefaces.integrationtests.inputtext;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.InputText;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputText006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputText: p:ajax 'blur' event fires its listener")
    void blurFiresAjax(Page page) {
        // Arrange
        InputText blurInput = page.blurInput;
        assertEquals("", page.eventlog.getText());

        // Act - type and then tab away to blur the field
        blurInput.getInput().sendKeys("hi");
        PrimeSelenium.guardAjax(blurInput.getInput()).sendKeys(Keys.TAB);

        // Assert
        assertEquals("blur", page.eventlog.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("InputText: p:ajax 'keyup' event fires its listener")
    void keyupFiresAjax(Page page) {
        // Arrange
        InputText keyupInput = page.keyupInput;
        assertEquals("", page.eventlog.getText());

        // Act - a single keystroke triggers keyup
        PrimeSelenium.guardAjax(keyupInput.getInput()).sendKeys("a");

        // Assert
        assertEquals("keyup", page.eventlog.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("InputText: p:ajax 'change' event fires its listener")
    void changeFiresAjax(Page page) {
        // Arrange
        InputText changeInput = page.changeInput;
        assertEquals("", page.eventlog.getText());

        // Act - setValue changes the value and tabs away, triggering change
        changeInput.setValue("changed");

        // Assert
        assertEquals("change", page.eventlog.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:blurInput")
        InputText blurInput;

        @FindBy(id = "form:keyupInput")
        InputText keyupInput;

        @FindBy(id = "form:changeInput")
        InputText changeInput;

        @FindBy(id = "form:eventlog")
        WebElement eventlog;

        @Override
        public String getLocation() {
            return "inputtext/inputText006.xhtml";
        }
    }
}
