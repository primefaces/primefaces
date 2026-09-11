/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import org.primefaces.selenium.component.TextEditor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextEditor005Test extends AbstractPrimePageTest {

    /**
     * Fires a real `copy` event at the editor and returns what the widget put onto the clipboard, so the
     * whole Quill copy path (onCaptureCopy -> onCopy -> setData) is exercised without OS clipboard access.
     */
    private String copyToClipboard(String mimeType) {
        return PrimeSelenium.executeScript(
                    "var widget = PF('wgtEditor');"
                                + "widget.editor.setSelection(0, widget.editor.getLength());"
                                + "var data = new DataTransfer();"
                                + "widget.editor.root.dispatchEvent("
                                + "  new ClipboardEvent('copy', {clipboardData: data, bubbles: true, cancelable: true}));"
                                + "return data.getData('" + mimeType + "');");
    }

    @Test
    @Order(1)
    @DisplayName("TextEditor: GitHub #15217 copying must keep normal spaces normal in text/html")
    void copyKeepsNormalSpaces(Page page) {
        // Arrange
        page.textEditor.setValue("Lorem ipsum dolor sit amet");

        // Act
        String html = copyToClipboard("text/html");

        // Assert
        assertEquals("Lorem ipsum dolor sit amet", html);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TextEditor: GitHub #15217 copying keeps text/plain and the formatting intact")
    void copyKeepsPlainTextAndFormatting(Page page) {
        // Arrange - "ipsum" in bold
        PrimeSelenium.executeScript("var q = PF('wgtEditor').editor;"
                    + "q.setText('Lorem ipsum dolor');"
                    + "q.formatText(6, 5, 'bold', true);");

        // Act
        String html = copyToClipboard("text/html");
        String text = copyToClipboard("text/plain");

        // Assert - spaces normalized, but the bold run survives
        assertEquals("Lorem <strong>ipsum</strong> dolor", html);
        assertEquals("Lorem ipsum dolor", text.strip());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("TextEditor: GitHub #15217 a deliberate non-breaking space must survive copying")
    void copyKeepsDeliberateNonBreakingSpace(Page page) {
        // Arrange - a real U+00A0 typed by the user, between "10" and "km"
        PrimeSelenium.executeScript("PF('wgtEditor').editor.setText('10\u00A0km per hour');");

        // Act
        String html = copyToClipboard("text/html");

        // Assert - only the spaces Quill invented are normalized, the deliberate one is kept
        assertEquals("10\u00A0km per hour", html);
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:editor")
        TextEditor textEditor;

        @Override
        public String getLocation() {
            return "texteditor/textEditor005.xhtml";
        }
    }
}
