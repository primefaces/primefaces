/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.integrationtests.tristatecheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TreeTable;
import org.primefaces.selenium.component.TriStateCheckbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TriStateCheckbox003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TriStateCheckbox: #15617 values inside a TreeTable are kept on expand/collapse")
    void keepValueOnTreeTableToggle(Page page) {
        // Arrange
        page.first.setValue(Boolean.TRUE);
        page.second.setValue(Boolean.FALSE);
        page.button.click();
        assertEquals("true,false", page.output.getText());

        // Act - expand and collapse the first node
        PrimeSelenium.guardAjax(page.treeTable.getRow(0).getToggler()).click();
        assertEquals(3, page.treeTable.getRows().size());
        PrimeSelenium.guardAjax(page.treeTable.getRow(0).getToggler()).click();
        page.button.click();

        // Assert
        assertEquals("true,false", page.output.getText());
        assertEquals(Boolean.TRUE, page.first.getValue());
        assertEquals(Boolean.FALSE, page.second.getValue());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TriStateCheckbox: #15617 explicitly submitted null value is still applied")
    void submitNullValue(Page page) {
        // Arrange
        page.first.setValue(Boolean.TRUE);
        page.button.click();
        assertEquals("true,", page.output.getText());

        // Act - true -> false -> null
        page.first.setValue(null);
        page.button.click();

        // Assert
        assertEquals(",", page.output.getText());
        assertEquals(null, page.first.getValue());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:treeTable:0:value")
        TriStateCheckbox first;

        @FindBy(id = "form:treeTable:1:value")
        TriStateCheckbox second;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:output")
        WebElement output;

        @Override
        public String getLocation() {
            return "tristatecheckbox/triStateCheckbox003.xhtml";
        }
    }
}
