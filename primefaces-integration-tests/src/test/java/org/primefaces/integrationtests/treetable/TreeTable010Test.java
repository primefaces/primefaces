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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    /** rowKey of "Archive", whose only child is non-selectable. */
    private static final String ARCHIVE = "1";
    /** rowKey of "Locked", which is {@code setSelectable(false)}. */
    private static final String ARCHIVE_UNSELECTABLE_CHILD = "1_0";
    /** rowKey of "old.app", a selectable child of "Locked". */
    private static final String ARCHIVE_GRANDCHILD = "1_0_0";
    /** rowKey of the collapsed "Backup", whose non-rendered "snapshot.app" is non-selectable but selected. */
    private static final String BACKUP = "2";
    /** rowKey of "Shared", which is {@code setSelectable(false)} but selected by the model. */
    private static final String SHARED = "3";
    /** rowKey of "shared.app", a selectable child of "Shared", also selected by the model. */
    private static final String SHARED_CHILD = "3_0";

    @Test
    @Order(1)
    @DisplayName("TreeTable: GitHub #14798 checkbox selection must not propagate to a non-selectable child node")
    void checkboxSelectionDoesNotPropagateToUnselectableNode(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertFalse(treeTable.getRow(UNSELECTABLE_CHILD).isSelected());

        // Act - select the parent of the non-selectable node
        treeTable.getRow(PARENT).toggleCheckbox();

        // Assert - visually the non-selectable node must stay unselected, its selectable siblings must not
        assertTrue(treeTable.getRow(PARENT).isSelected());
        assertTrue(treeTable.getRow(SELECTABLE_CHILD).isSelected());
        assertTrue(treeTable.getRow(OTHER_SELECTABLE_CHILD).isSelected());
        assertFalse(treeTable.getRow(UNSELECTABLE_CHILD).isSelected());
        assertFalse(treeTable.getRow(UNSELECTABLE_CHILD).isChecked());
        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable: GitHub #14798 a non-selectable child node must not be submitted as selected")
    void unselectableNodeIsNotSubmitted(Page page) {
        // Arrange
        page.treeTable.getRow(PARENT).toggleCheckbox();

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
        treeTable.getRow(PARENT).toggleCheckbox();

        // Act - uncheck and re-check a selectable child
        treeTable.getRow(OTHER_SELECTABLE_CHILD).toggleCheckbox();
        assertTrue(treeTable.getRow(PARENT).isPartialSelected());
        treeTable.getRow(OTHER_SELECTABLE_CHILD).toggleCheckbox();

        // Assert - all selectable children are selected again, so the parent must be fully selected
        assertTrue(treeTable.getRow(PARENT).isSelected());
        assertFalse(treeTable.getRow(PARENT).isPartialSelected());
        assertFalse(treeTable.getRow(UNSELECTABLE_CHILD).isSelected());

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
        treeTable.getRow(PARENT).toggleCheckbox();

        // Act
        treeTable.getRow(PARENT).toggleCheckbox();

        // Assert
        assertFalse(treeTable.getRow(PARENT).isSelected());
        assertFalse(treeTable.getRow(SELECTABLE_CHILD).isSelected());
        assertFalse(treeTable.getRow(OTHER_SELECTABLE_CHILD).isSelected());
        assertFalse(treeTable.getRow(UNSELECTABLE_CHILD).isSelected());

        page.showSelected.click();
        // only the preselected non-selectable nodes of the other folders are left
        assertSubmittedSelection(page, "legacy.app", "snapshot.app", "Shared", "shared.app");
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("TreeTable: GitHub #14798 toggling the parent must keep a preselected non-selectable node selected")
    void preselectedUnselectableNodeKeepsSelection(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertTrue(treeTable.getRow(PRESELECTED_UNSELECTABLE_CHILD).isSelected());

        // Act - check and uncheck its parent
        treeTable.getRow(LEGACY_PARENT).toggleCheckbox();
        assertTrue(treeTable.getRow(LEGACY_SELECTABLE_CHILD).isSelected());
        treeTable.getRow(LEGACY_PARENT).toggleCheckbox();

        // Assert - screen and submitted selection must agree: the non-selectable node is untouched
        assertFalse(treeTable.getRow(LEGACY_PARENT).isSelected());
        assertFalse(treeTable.getRow(LEGACY_SELECTABLE_CHILD).isSelected());
        assertTrue(treeTable.getRow(PRESELECTED_UNSELECTABLE_CHILD).isSelected());
        assertTrue(treeTable.getRow(PRESELECTED_UNSELECTABLE_CHILD).isChecked());

        page.showSelected.click();
        assertSubmittedSelection(page, "legacy.app", "snapshot.app", "Shared", "shared.app");
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("TreeTable: GitHub #14798 a parent without selectable children must not be selected by a grandchild")
    void parentWithoutSelectableChildrenKeepsState(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;

        // Act & Assert - "Archive" only has the non-selectable "Locked", so toggling "old.app" must not change it
        treeTable.getRow(ARCHIVE_GRANDCHILD).toggleCheckbox();
        assertTrue(treeTable.getRow(ARCHIVE_GRANDCHILD).isSelected());
        assertFalse(treeTable.getRow(ARCHIVE).isSelected());
        assertFalse(treeTable.getRow(ARCHIVE).isPartialSelected());
        assertFalse(treeTable.getRow(ARCHIVE_UNSELECTABLE_CHILD).isSelected());

        treeTable.getRow(ARCHIVE_GRANDCHILD).toggleCheckbox();
        assertFalse(treeTable.getRow(ARCHIVE_GRANDCHILD).isSelected());
        assertFalse(treeTable.getRow(ARCHIVE).isSelected());
        assertFalse(treeTable.getRow(ARCHIVE).isPartialSelected());

        treeTable.getRow(ARCHIVE_GRANDCHILD).toggleCheckbox();
        page.showSelected.click();
        assertSubmittedSelection(page, "legacy.app", "snapshot.app", "Shared", "shared.app", "old.app");
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("TreeTable: GitHub #14798 unchecking a collapsed parent must keep its preselected non-selectable child")
    void collapsedPreselectedUnselectableNodeKeepsSelection(Page page) {
        // Arrange - the children of "Backup" are not rendered
        TreeTable treeTable = page.treeTable;
        assertFalse(treeTable.hasRow(BACKUP + "_0"));

        // Act
        treeTable.getRow(BACKUP).toggleCheckbox();
        treeTable.getRow(BACKUP).toggleCheckbox();

        // Assert
        page.showSelected.click();
        assertSubmittedSelection(page, "legacy.app", "snapshot.app", "Shared", "shared.app");
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("TreeTable: GitHub #14798 the server must reject a submitted non-selectable node")
    void serverRejectsUnselectableNode(Page page) {
        // Arrange - bypass the widget's propagation rules
        PrimeSelenium.executeScript("var tt = PF('ttWidget'); tt.selections.push('" + UNSELECTABLE_CHILD + "'); tt.writeSelections();");

        // Act
        page.showSelected.click();

        // Assert
        assertSubmittedSelection(page, "legacy.app", "snapshot.app", "Shared", "shared.app");
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("TreeTable: GitHub #14798 an unchecked child of a preselected non-selectable node must not be reselected")
    void uncheckedChildOfPreselectedUnselectableNodeStaysUnselected(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertTrue(treeTable.getRow(SHARED).isSelected());
        assertTrue(treeTable.getRow(SHARED_CHILD).isSelected());

        // Act - uncheck the child, then re-render the tree from the model
        treeTable.getRow(SHARED_CHILD).toggleCheckbox();
        page.showSelected.click();

        // Assert - the server must not propagate the kept selection of "Shared" down into its child again
        assertSubmittedSelection(page, "legacy.app", "snapshot.app", "Shared");
        assertTrue(page.treeTable.getRow(SHARED).isSelected());
        assertFalse(page.treeTable.getRow(SHARED_CHILD).isSelected());
        assertFalse(page.treeTable.getRow(SHARED_CHILD).isChecked());
        assertConfiguration(page.treeTable.getWidgetConfiguration());
    }

    private void assertSubmittedSelection(Page page, String... expected) {
        String detail = page.messages.getMessage(0).getDetail();
        Set<String> selected = detail.isEmpty() ? Set.of() : new HashSet<>(Arrays.asList(detail.split(",")));
        assertEquals(Set.of(expected), selected);
    }

    @Override
    protected void assertConfiguration(JSONObject cfg) {
        assertConfiguration(cfg, "ttWidget");
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
