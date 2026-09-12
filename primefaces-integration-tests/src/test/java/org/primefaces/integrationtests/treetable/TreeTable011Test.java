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
package org.primefaces.integrationtests.treetable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TreeTable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTable011Test extends AbstractTreeTableTest {

    /** rowKey of "Primefaces", which has two children. */
    private static final String PARENT = "0_0";
    /** rowKey of "primefaces.app". */
    private static final String CHILD = "0_0_0";

    @Test
    @Order(1)
    @DisplayName("TreeTable: GitHub #9925 propagateSelectionDown=false must not select the child nodes")
    void childIsNotSelectedVisually(Page page) {
        // Act
        selectCheckbox(page, PARENT);

        // Assert
        assertTrue(isSelected(page, PARENT));
        assertFalse(isSelected(page, CHILD));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable: GitHub #9925 propagateSelectionDown=false must not submit the child nodes either")
    void childIsNotSubmitted(Page page) {
        // Arrange
        selectCheckbox(page, PARENT);

        // Act
        page.showSelected.click();

        // Assert - only the clicked node, the children must not be propagated behind the scenes
        assertEquals("Primefaces", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    private void selectCheckbox(Page page, String rowKey) {
        // checkbox selection always fires an AJAX request to resolve the descendants of the clicked node
        PrimeSelenium.guardAjax(row(page, rowKey).findElement(By.cssSelector("div.ui-chkbox-box"))).click();
    }

    private boolean isSelected(Page page, String rowKey) {
        return PrimeSelenium.hasCssClass(row(page, rowKey), "ui-state-highlight");
    }

    private WebElement row(Page page, String rowKey) {
        return page.getWebDriver().findElement(By.id("form:treeTable_node_" + rowKey));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:showSelected")
        CommandButton showSelected;

        @Override
        public String getLocation() {
            return "treetable/treeTable011.xhtml";
        }
    }
}
