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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Filtering hides nodes instead of working on a copy of the tree, so that selecting, expanding and the paginator work
 * on the nodes of the model - https://github.com/primefaces/primefaces/issues/12795
 */
class TreeTable012Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: selecting a filtered node selects the node of the model")
    void selectWhileFiltered(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        treeTable.filter("Name", "down");
        assertEquals(List.of("Downloads"), names(treeTable));

        // Act
        PrimeSelenium.guardAjax(treeTable.getRow(0).getCell(1).getWebElement()).click();
        page.buttonShowSelectedNode.click();

        // Assert
        assertMessage(page.messages, 0, "selected node", "Downloads in model");
        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable: nodes expanded while filtering stay expanded after clearing the filter")
    void expandWhileFiltered(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;

        // Act - the filter expands the ancestors of the match
        treeTable.filter("Name", "mobi");

        // Assert
        assertEquals(List.of("Applications", "Primefaces", "mobile.app"), names(treeTable));

        // Act
        treeTable.filter("Name", "");

        // Assert - the ancestors were expanded in the model
        assertEquals(List.of("Applications", "Primefaces", "primefaces.app", "native.app", "mobile.app", "editor.app", "settings.app",
                "Cloud"), names(treeTable));

        // Act - expand a match by the user
        treeTable.filter("Name", "down");
        treeTable.getRow(0).toggle();

        // Assert - children of a match are displayed
        assertEquals(List.of("Downloads", "Spanish", "Travel"), names(treeTable));

        // Act
        treeTable.filter("Name", "");
        treeTable.selectPage(3);

        // Assert - the node was expanded in the model
        assertEquals(List.of("Downloads", "Spanish", "Travel", "Main"), names(treeTable));
        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("TreeTable: paginator pages the filtered root nodes and processes the rows of the page")
    void paginatorWhileFiltered(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;

        // Act
        treeTable.filter("Name", "txt");

        // Assert - Desktop, Downloads and Other match, two root nodes per page
        assertEquals(List.of("Desktop", "note-meeting.txt", "note-todo.txt", "Downloads", "Spanish", "tutorial-a1.txt", "tutorial-a2.txt"),
                names(treeTable));
        assertEquals(2, treeTable.getPaginator().getPages().size());

        // Act
        treeTable.selectPage(2);

        // Assert
        assertEquals(List.of("Other", "todo.txt"), names(treeTable));

        // Act - a command of a row of the filtered page
        PrimeSelenium.guardAjax(treeTable.getRow(1).getCell(3).getWebElement().findElement(By.cssSelector("button"))).click();

        // Assert
        assertMessage(page.messages, 0, "selected document", "todo.txt");

        // Act - clearing the filter updates the paginator: 9 root nodes, two per page
        treeTable.filter("Name", "");

        // Assert
        assertEquals(5, treeTable.getPaginator().getPages().size());

        // Act - no match
        treeTable.filter("Name", "nomatch");

        // Assert
        assertTrue(treeTable.isEmpty());
        assertEquals(1, treeTable.getPaginator().getPages().size());
        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:buttonShowSelectedNode")
        CommandButton buttonShowSelectedNode;

        @Override
        public String getLocation() {
            return "treetable/treeTable012.xhtml";
        }
    }
}
