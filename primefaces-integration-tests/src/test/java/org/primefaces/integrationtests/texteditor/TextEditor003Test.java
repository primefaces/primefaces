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
package org.primefaces.integrationtests.texteditor;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Button;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TextEditor;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextEditor003Test extends AbstractPrimePageTest {

    @Test
    void focusLostToButton(Page page) {
        // Arrange
        Messages messages = page.messages;
        TextEditor textEditor = page.editor;
        Button doNothing = page.doNothing;

        // Act
        textEditor.getEditor().click();
        textEditor.setValue("Hello world");
        PrimeSelenium.guardAjax(doNothing).click();

        // Assert
        assertEquals(1, messages.getAllMessages().size());
        assertEquals("<p>Hello world</p>", messages.getMessage(0).getSummary());
    }

    @Test
    void noBlurOnPaste(Page page) {
        // Arrange
        Messages messages = page.messages;
        WebElement quillEditor = page.editor.findElement(By.className("ql-editor"));
        Keys command = PrimeSelenium.isSafari() ? Keys.COMMAND : Keys.CONTROL;

        // Act
        quillEditor.sendKeys("Hello world");
        quillEditor.sendKeys(Keys.chord(command, "a"));
        quillEditor.sendKeys(Keys.chord(command, "c"));
        quillEditor.sendKeys(Keys.DELETE);
        quillEditor.sendKeys(Keys.chord(command, "v"));

        // Assert
        assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    void noBlurOnToolbar(Page page) {
        // Arrange
        Messages messages = page.messages;
        WebElement quillEditor = page.editor.findElement(By.className("ql-editor"));
        WebElement formatBold = page.editor.findElement(By.className("ql-bold"));

        // Act
        quillEditor.sendKeys("Hello world");
        quillEditor.sendKeys(Keys.chord(PrimeSelenium.isSafari() ? Keys.COMMAND : Keys.CONTROL, "a"));
        formatBold.click();

        // Assert
        assertEquals(0, messages.getAllMessages().size());
    }


    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:textEditor")
        TextEditor editor;

        @FindBy(id = "form:doNothing")
        Button doNothing;

        @Override
        public String getLocation() {
            return "texteditor/textEditor003.xhtml";
        }
    }
}
