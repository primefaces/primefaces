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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TreeTable;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTable010Test extends AbstractTreeTableTest {

    /** rowKey of "Primefaces", the parent of the non-selectable node. */
    private static final String PARENT = "0_0";
    /** rowKey of "primefaces.app". */
    private static final String SELECTABLE_CHILD = "0_0_0";
    /** rowKey of "native.app", which is {@code setSelectable(false)}. */
    private static final String UNSELECTABLE_CHILD = "0_0_1";
    /** rowKey of "mobile.app". */
    private static final String OTHER_SELECTABLE_CHILD = "0_0_2";
    /** rowKey of "Legacy", the parent of the preselected non-selectable node. */
    private static final String LEGACY_PARENT = "0_1";
    /** rowKey of "legacy.app", which is {@code setSelectable(false)} but selected by the model. */
    private static final String PRESELECTED_UNSELECTABLE_CHILD = "0_1_0";
    /** rowKey of "modern.app". */
    private static final String LEGACY_SELECTABLE_CHILD = "0_1_1";

    @Test
    @Order(1)
    @DisplayName("TreeTable: GitHub #14798 checkbox selection must not propagate to a non-selectable child node")
    void checkboxSelectionDoesNotPropagateToUnselectableNode(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertFalse(isSelected(treeTable, UNSELECTABLE_CHILD));

        // Act - select the parent of the non-selectable node
        selectCheckbox(treeTable, PARENT);

        // Assert - visually the non-selectable node must stay unselected, its selectable siblings must not
        assertTrue(isSelected(treeTable, PARENT));
        assertTrue(isSelected(treeTable, SELECTABLE_CHILD));
        assertTrue(isSelected(treeTable, OTHER_SELECTABLE_CHILD));
        assertFalse(isSelected(treeTable, UNSELECTABLE_CHILD));
        assertFalse(isChecked(treeTable, UNSELECTABLE_CHILD));
        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable: GitHub #14798 a non-selectable child node must not be submitted as selected")
    void unselectableNodeIsNotSubmitted(Page page) {
        // Arrange
        selectCheckbox(page.treeTable, PARENT);

        // Act
        page.showSelected.click();

        // Assert
        String selected = page.messages.getMessage(0).getDetail();
        assertFalse(selected.contains("native.app"), () -> "non-selectable node was submitted: " + selected);
        assertTrue(selected.contains("primefaces.app"), () -> "selectable node is missing: " + selected);
        assertTrue(selected.contains("mobile.app"), () -> "selectable node is missing: " + selected);
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("TreeTable: GitHub #14798 re-checking a child must select its parent despite a non-selectable sibling")
    void recheckingChildSelectsParent(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        selectCheckbox(treeTable, PARENT);

        // Act - uncheck and re-check a selectable child
        unselectCheckbox(treeTable, OTHER_SELECTABLE_CHILD);
        assertTrue(isPartialSelected(treeTable, PARENT));
        selectCheckbox(treeTable, OTHER_SELECTABLE_CHILD);

        // Assert - all selectable children are selected again, so the parent must be fully selected
        assertTrue(isSelected(treeTable, PARENT));
        assertFalse(isPartialSelected(treeTable, PARENT));
        assertFalse(isSelected(treeTable, UNSELECTABLE_CHILD));

        page.showSelected.click();
        String selected = page.messages.getMessage(0).getDetail();
        assertTrue(selected.contains("Primefaces"), () -> "parent node is missing: " + selected);
        assertFalse(selected.contains("native.app"), () -> "non-selectable node was submitted: " + selected);
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("TreeTable: GitHub #14798 unchecking the parent unselects its selectable children")
    void uncheckingParentUnselectsChildren(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        selectCheckbox(treeTable, PARENT);

        // Act
        unselectCheckbox(treeTable, PARENT);

        // Assert
        assertFalse(isSelected(treeTable, PARENT));
        assertFalse(isSelected(treeTable, SELECTABLE_CHILD));
        assertFalse(isSelected(treeTable, OTHER_SELECTABLE_CHILD));
        assertFalse(isSelected(treeTable, UNSELECTABLE_CHILD));

        page.showSelected.click();
        // only the preselected legacy.app of the other folder is left
        assertEquals("legacy.app", page.messages.getMessage(0).getDetail());
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("TreeTable: GitHub #14798 toggling the parent must keep a preselected non-selectable node selected")
    void preselectedUnselectableNodeKeepsSelection(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertTrue(isSelected(treeTable, PRESELECTED_UNSELECTABLE_CHILD));

        // Act - check and uncheck its parent
        selectCheckbox(treeTable, LEGACY_PARENT);
        assertTrue(isSelected(treeTable, LEGACY_SELECTABLE_CHILD));
        unselectCheckbox(treeTable, LEGACY_PARENT);

        // Assert - screen and submitted selection must agree: the non-selectable node is untouched
        assertFalse(isSelected(treeTable, LEGACY_PARENT));
        assertFalse(isSelected(treeTable, LEGACY_SELECTABLE_CHILD));
        assertTrue(isSelected(treeTable, PRESELECTED_UNSELECTABLE_CHILD));
        assertTrue(isChecked(treeTable, PRESELECTED_UNSELECTABLE_CHILD));

        page.showSelected.click();
        assertEquals("legacy.app", page.messages.getMessage(0).getDetail());
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TreeTable Config = " + cfg);
        assertEquals("ttWidget", cfg.getString("widgetVar"));
        assertEquals("checkbox", cfg.getString("selectionMode"));
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
