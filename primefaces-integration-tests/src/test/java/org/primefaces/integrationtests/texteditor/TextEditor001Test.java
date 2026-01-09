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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TextEditor;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextEditor001Test extends AbstractPrimePageTest {

    @Test
    void submit(Page page) {
        // Arrange
        TextEditor editor = page.textEditor;
        assertEquals("", editor.getValue());

        // Act
        editor.setValue("hello!");
        page.button.click();

        // Assert
        assertEquals("<p>hello!</p>", editor.getValue());
        assertConfiguration(editor.getWidgetConfiguration());
    }

    @Test
    void clear(Page page) {
        // Arrange
        TextEditor editor = page.textEditor;

        // Act
        editor.setValue("Some Text");
        editor.clear();

        // Assert
        assertEquals("", editor.getEditorValue());
        assertConfiguration(editor.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TextEditor Config = " + cfg);
        assertTrue(cfg.getBoolean("toolbarVisible"));
        assertEquals("snow", cfg.getString("theme"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:editor")
        TextEditor textEditor;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "texteditor/textEditor001.xhtml";
        }
    }
}
