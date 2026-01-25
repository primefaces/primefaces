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
package org.primefaces.integrationtests.core.ajax;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreAjax002Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("Core-AJAX: UICommand with literal oncomplete - https://github.com/primefaces/primefaces/issues/13700")
    void commandWithLiteralOncomplete(Page page) {
        assertEquals("", page.output1.getText());
        page.button1.click();
        assertEquals("literal", page.output1.getText());
    }

    @Test
    @DisplayName("Core-AJAX: UICommand with partial EL oncomplete - https://github.com/primefaces/primefaces/issues/13700")
    void commandWithPartialELOncomplete(Page page) {
        assertEquals("", page.output2.getText());
        page.button2.click();
        assertEquals("modified", page.output2.getText());
    }

    @Test
    @DisplayName("Core-AJAX: UICommand with full EL oncomplete - https://github.com/primefaces/primefaces/issues/13700")
    void commandWithFullELOncomplete(Page page) {
        assertEquals("", page.output3.getText());
        page.button3.click();
        assertEquals("modified", page.output3.getText());
    }

    @Test
    @DisplayName("Core-AJAX: AjaxBehavior with literal oncomplete - https://github.com/primefaces/primefaces/issues/13700")
    void behaviorWithLiteralOncomplete(Page page) {
        assertEquals("", page.output4.getText());
        page.button4.click();
        assertEquals("literal", page.output4.getText());
    }

    @Test
    @DisplayName("Core-AJAX: AjaxBehavior with partial EL oncomplete - https://github.com/primefaces/primefaces/issues/13700")
    void behaviorWithPartialELOncomplete(Page page) {
        assertEquals("", page.output5.getText());
        page.button5.click();
        assertEquals("modified", page.output5.getText());
    }

    @Test
    @DisplayName("Core-AJAX: AjaxBehavior with full EL oncomplete - https://github.com/primefaces/primefaces/issues/13700")
    void behaviorWithFullELOncomplete(Page page) {
        assertEquals("", page.output6.getText());
        page.button6.click();
        assertEquals("modified", page.output6.getText());
    }

    @Test
    @DisplayName("Core-AJAX: Assert literal oncomplete arguments and window context - https://github.com/primefaces/primefaces/issues/13700")
    void assertLiteralOncompleteArgumentsAndWindowContext(Page page) {
        assertEquals("", page.output7.getText());
        page.button7.click();
        assertEquals("literal 4 success ok true true true true", page.output7.getText());
    }

    @Test
    @DisplayName("Core-AJAX: Assert dynamic oncomplete arguments and window context - https://github.com/primefaces/primefaces/issues/13700")
    void assertDynamicOncompleteArgumentsAndWindowContext(Page page) {
        assertEquals("", page.output8.getText());
        page.button8.click();
        assertEquals("modified 4 success ok true true true true", page.output8.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form1:button")
        CommandButton button1;

        @FindBy(id = "output1")
        WebElement output1;

        @FindBy(id = "form2:button")
        CommandButton button2;

        @FindBy(id = "output2")
        WebElement output2;

        @FindBy(id = "form3:button")
        CommandButton button3;

        @FindBy(id = "output3")
        WebElement output3;

        @FindBy(id = "form4:button")
        CommandButton button4;

        @FindBy(id = "output4")
        WebElement output4;

        @FindBy(id = "form5:button")
        CommandButton button5;

        @FindBy(id = "output5")
        WebElement output5;

        @FindBy(id = "form6:button")
        CommandButton button6;

        @FindBy(id = "output6")
        WebElement output6;

        @FindBy(id = "form7:button")
        CommandButton button7;

        @FindBy(id = "output7")
        WebElement output7;

        @FindBy(id = "form8:button")
        CommandButton button8;

        @FindBy(id = "output8")
        WebElement output8;

        @Override
        public String getLocation() {
            return "core/ajax/coreAjax002.xhtml";
        }
    }
}
