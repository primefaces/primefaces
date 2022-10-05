/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.integrationtests.selectoneradio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.OutputLabel;
import org.primefaces.selenium.component.RadioButton;
import org.primefaces.selenium.component.SelectOneRadio;

public class SelectOneRadio003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneRadio: Custom: Referenced")
    public void testCustomReferenced(Page page) {
        // Arrange
        Assertions.assertTrue(page.opt2.isSelected());

        // Act
        page.opt3.select();
        page.submit.click();

        // Assert
        Assertions.assertTrue(page.opt3.isSelected());

        // Act
        page.label4.click();
        page.submit.click();

        // Assert
        Assertions.assertTrue(page.opt4.isSelected());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectoneradio")
        SelectOneRadio selectOneRadio;

        @FindBy(id = "form:opt1")
        RadioButton opt1;
        @FindBy(id = "form:opt2")
        RadioButton opt2;
        @FindBy(id = "form:opt3")
        RadioButton opt3;
        @FindBy(id = "form:opt4")
        RadioButton opt4;

        @FindBy(id = "form:label1")
        OutputLabel label1;
        @FindBy(id = "form:label2")
        OutputLabel label2;
        @FindBy(id = "form:label3")
        OutputLabel label3;
        @FindBy(id = "form:label4")
        OutputLabel label4;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "selectoneradio/selectOneRadio003.xhtml";
        }
    }
}
