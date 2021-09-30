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
package org.primefaces.integrationtests.texteditor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AccordionPanel;
import org.primefaces.selenium.component.CommandButton;

public class TextEditor002Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("TextEditor: Prevent regression of GitHub #2802 when upgrading QuillJS: https://github.com/primefaces/primefaces/issues/2802")
    public void testSubmit(Page page) {
        // Arrange
        assertClickable(page.updateForm);
        assertDisplayed(page.accordionPanel);

        // Act
        page.updateForm.click(); // update form
        page.accordionPanel.toggleTab(1); // expand tab 2

        // Assert
        // NOTE: if we get Javascript errors someone upgraded QuillJS but did not reapply the patch
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:accordion")
        AccordionPanel accordionPanel;

        @FindBy(id = "form:updateForm")
        CommandButton updateForm;

        @Override
        public String getLocation() {
            return "texteditor/textEditor002.xhtml";
        }
    }
}
