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
import org.primefaces.selenium.component.TreeTable;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * With filterPrune="descendants" a match whose children are all hidden by the filter is displayed without a toggler.
 */
class TreeTable013Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: filterPrune=descendants hides the toggler of a match whose children are all hidden")
    void togglerOfPrunedMatch(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;

        // Act
        treeTable.filter("Name", "spanish");

        // Assert - the ancestor keeps its toggler, the match has no visible children
        assertEquals(List.of("Downloads", "Spanish"), names(treeTable));
        assertTrue(treeTable.getRow(0).getToggler().isDisplayed());
        assertFalse(treeTable.getRow(1).getToggler().isDisplayed());

        // Act
        treeTable.filter("Name", "");

        // Assert - the toggler is back once the filter is cleared
        assertEquals(List.of("Applications", "Cloud", "Desktop", "Documents", "Downloads", "Spanish", "Travel", "Main", "Other",
                "Pictures", "Videos"), names(treeTable));
        assertTrue(treeTable.getRow(5).getToggler().isDisplayed());
        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @Override
        public String getLocation() {
            return "treetable/treeTable013.xhtml";
        }
    }
}
