/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.integrationtests.texteditor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TextEditor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextEditor004Test extends AbstractPrimePageTest {
    @Test
    @DisplayName("TextEditor: change event not fired if editor is not blurred")
    void noChange(Page page) {
        // Arrange
        Messages messages = page.messages;
        WebElement quillEditor = page.editor.findElement(By.className("ql-editor"));

        // Act
        quillEditor.sendKeys("Hello world");

        // Assert
        assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    @DisplayName("TextEditor: change event fired if editor is blurred")
    void changeEvent(Page page) {
        // Arrange
        Messages messages = page.messages;
        WebElement outside = page.outside;
        WebElement quillEditor = page.editor.findElement(By.className("ql-editor"));

        // Act
        quillEditor.click();
        quillEditor.sendKeys("Hello world");
        PrimeSelenium.guardAjax(outside).click();

        // Assert
        assertEquals(1, messages.getAllMessages().size());
        assertEquals("<p>Hello world</p>", messages.getMessage(0).getSummary());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:outside")
        WebElement outside;

        @FindBy(id = "form:textEditor")
        TextEditor editor;

        @Override
        public String getLocation() {
            return "texteditor/textEditor004.xhtml";
        }
    }
}
