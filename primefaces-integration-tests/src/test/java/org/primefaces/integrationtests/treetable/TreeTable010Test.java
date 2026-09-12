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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTable010Test extends AbstractTreeTableTest {

    /** rowKey of "Primefaces", the parent of the non-selectable node. */
    private static final String PARENT = "0_0";
    /** rowKey of "primefaces.app". */
    private static final String SELECTABLE_CHILD = "0_0_0";
    /** rowKey of "native.app", which is {@code setSelectable(false)}. */
    private static final String UNSELECTABLE_CHILD = "0_0_1";

    @Test
    @Order(1)
    @DisplayName("TreeTable: GitHub #14798 checkbox selection must not propagate to a non-selectable child node")
    void checkboxSelectionDoesNotPropagateToUnselectableNode(Page page) {
        // Arrange
        assertFalse(isSelected(page, UNSELECTABLE_CHILD));

        // Act - select the parent of the non-selectable node
        selectCheckbox(page, PARENT);

        // Assert - visually the non-selectable node must stay unselected, its selectable sibling must not
        assertTrue(isSelected(page, PARENT));
        assertTrue(isSelected(page, SELECTABLE_CHILD));
        assertFalse(isSelected(page, UNSELECTABLE_CHILD));
        assertFalse(isChecked(page, UNSELECTABLE_CHILD));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable: GitHub #14798 a non-selectable child node must not be submitted as selected")
    void unselectableNodeIsNotSubmitted(Page page) {
        // Arrange
        selectCheckbox(page, PARENT);

        // Act
        page.showSelected.click();

        // Assert
        String selected = page.messages.getMessage(0).getDetail();
        assertFalse(selected.contains("native.app"), () -> "non-selectable node was submitted: " + selected);
        assertTrue(selected.contains("primefaces.app"), () -> "selectable node is missing: " + selected);
        assertTrue(selected.contains("mobile.app"), () -> "selectable node is missing: " + selected);
        assertNoJavascriptErrors();
    }

    private void selectCheckbox(Page page, String rowKey) {
        // checkbox selection always fires an AJAX request to resolve the descendants of the clicked node
        PrimeSelenium.guardAjax(row(page, rowKey).findElement(By.cssSelector("div.ui-chkbox-box"))).click();
    }

    private boolean isSelected(Page page, String rowKey) {
        return PrimeSelenium.hasCssClass(row(page, rowKey), "ui-state-highlight");
    }

    private boolean isChecked(Page page, String rowKey) {
        return !row(page, rowKey).findElements(By.cssSelector("span.ui-chkbox-icon.ui-icon-check")).isEmpty();
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
            return "treetable/treeTable010.xhtml";
        }
    }
}
