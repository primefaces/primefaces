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
package org.primefaces.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckboxTreeNodeTest {

    private TreeNode<?> root;

    @SuppressWarnings("unused")
    @BeforeEach
    void initModel() {
        root = new CheckboxTreeNode("Files", null);

        TreeNode documents = new CheckboxTreeNode("Documents", root);
        TreeNode pictures = new CheckboxTreeNode("Pictures", root);
        TreeNode movies = new CheckboxTreeNode("Movies", root);

        TreeNode work = new CheckboxTreeNode("Work", documents);
        TreeNode primefaces = new CheckboxTreeNode("PrimeFaces", documents);

        //Documents
        TreeNode expenses = new CheckboxTreeNode("document", "Expenses", work);
        TreeNode resume = new CheckboxTreeNode("document", "Resume", work);
        TreeNode refdoc = new CheckboxTreeNode("document", "Refdoc", primefaces);

        //Pictures
        TreeNode barca = new CheckboxTreeNode("picture", "Barcelona.jpg", pictures);
        TreeNode primelogo = new CheckboxTreeNode("picture", "Logo.jpg", pictures);
        TreeNode optimus = new CheckboxTreeNode("picture", "OptimusPrime.jpg", pictures);

        //Movies
        TreeNode pacino = new CheckboxTreeNode("Al Pacino", movies);
        TreeNode deniro = new CheckboxTreeNode("Robert De Niro", movies);

        TreeNode scarface = new CheckboxTreeNode("mp3", "Scarface", pacino);
        TreeNode carlitosWay = new CheckboxTreeNode("mp3", "Carlito's Way", pacino);

        TreeNode goodfellas = new CheckboxTreeNode("mp3", "Goodfellas", deniro);
        TreeNode untouchables = new CheckboxTreeNode("mp3", "Untouchables", deniro);
    }

    @Test
    void shouldPropagateDown() {
        TreeNode<?> work = root.getChildren().get(0).getChildren().get(0);
        assertEquals("Work", work.getData());

        work.setSelected(true);

        TreeNode expenses = work.getChildren().get(0);
        TreeNode resume = work.getChildren().get(1);

        assertTrue(expenses.isSelected());
        assertTrue(resume.isSelected());
    }

    @Test
    void shouldPropagateUpWhenOneChildOfANodeIsSelected() {
        TreeNode<?> document = root.getChildren().get(0);
        TreeNode<?> work = document.getChildren().get(0);
        TreeNode expenses = work.getChildren().get(0);

        expenses.setSelected(true);

        assertTrue(work.isPartialSelected());
        assertTrue(document.isPartialSelected());
        assertFalse(work.isSelected());
        assertFalse(document.isSelected());
    }

    @Test
    void shouldPropagateUpWhenAllChildrenOfANodeIsSelected() {
        TreeNode<?> document = root.getChildren().get(0);
        TreeNode<?> work = document.getChildren().get(0);
        TreeNode<?> expenses = work.getChildren().get(0);
        TreeNode resume = work.getChildren().get(1);

        expenses.setSelected(true);
        resume.setSelected(true);

        assertTrue(work.isSelected());
        assertTrue(document.isPartialSelected());
        assertFalse(document.isSelected());
    }

    @Test
    void shouldPropagateUpIgnoringNonSelectableChildren() {
        // #14798: a non-selectable child must not keep its parent from being fully selected
        TreeNode<?> work = root.getChildren().get(0).getChildren().get(0);
        TreeNode<?> expenses = work.getChildren().get(0);
        TreeNode resume = work.getChildren().get(1);
        expenses.setSelectable(false);

        resume.setSelected(true);

        assertTrue(work.isSelected());
        assertFalse(work.isPartialSelected());
        assertFalse(expenses.isSelected());
    }

    @Test
    void shouldKeepStateWithoutSelectableChildren() {
        // #14798: a node without any selectable child has nothing to derive its state from
        TreeNode<?> movies = root.getChildren().get(2);
        TreeNode<?> pacino = movies.getChildren().get(0);
        TreeNode<?> deniro = movies.getChildren().get(1);
        pacino.setSelectable(false);
        deniro.setSelectable(false);

        // selected by the model, but as all children of Movies are non-selectable, Movies must stay unselected
        ((CheckboxTreeNode<?>) pacino).setSelected(true, false, true);
        assertFalse(movies.isSelected());
        assertFalse(movies.isPartialSelected());

        // the same for unselecting
        movies.setSelected(true);
        ((CheckboxTreeNode<?>) pacino).setSelected(false, false, true);
        assertTrue(movies.isSelected());
        assertFalse(movies.isPartialSelected());
    }

    @Test
    void shouldDeleteNode() {
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        TreeNode expenses = root.getChildren().get(0).getChildren().get(0).getChildren().get(0);

        assertEquals("Expenses", expenses.getData());

        expenses.getChildren().clear();
        expenses.getParent().getChildren().remove(expenses);

        assertNull(expenses.getParent());
        assertEquals(1, work.getChildCount());
    }

    @Test
    void shouldUpdateSelectionStateWhenNodesAreAdded() {
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        assertEquals("Work", work.getData());
        assertEquals(2, work.getChildCount());

        TreeNode unselectedNode = new CheckboxTreeNode("Unselected");
        work.getChildren().add(unselectedNode);

        assertFalse(work.isSelected());
        assertFalse(work.isPartialSelected());

        TreeNode selectedNode1 = new CheckboxTreeNode("Selected1");
        selectedNode1.setSelected(true);
        work.getChildren().add(selectedNode1);

        assertFalse(work.isSelected());
        assertTrue(work.isPartialSelected());

        TreeNode unselectedNode2 = new CheckboxTreeNode("Unselected2");
        work.getChildren().add(unselectedNode2);

        assertFalse(work.isSelected());
        assertTrue(work.isPartialSelected());
    }

    @Test
    void shouldUpdateSelectionStateIgnoringNonSelectableChildWhenNodesAreAdded() {
        // #14798: like propagating the selection up, adding a node must not count non-selectable children
        TreeNode work = root.getChildren().get(0).getChildren().get(0);
        ((TreeNode<?>) work.getChildren().get(0)).setSelected(true);
        ((TreeNode<?>) work.getChildren().get(1)).setSelected(true);
        assertTrue(work.isSelected());

        TreeNode unselectable = new CheckboxTreeNode("Unselectable");
        unselectable.setSelectable(false);
        work.getChildren().add(unselectable);

        assertTrue(work.isSelected());
        assertFalse(work.isPartialSelected());
    }

    @Test
    void shouldKeepStateOfNonSelectableNodeWhenNodesAreAdded() {
        // #14798: a non-selectable node selected by the model must stay selected when children are added
        CheckboxTreeNode<?> shared = new CheckboxTreeNode<>("Shared", root);
        shared.setSelectable(false);
        shared.setSelected(true, false);

        new CheckboxTreeNode<>("Unselected", shared);

        assertTrue(shared.isSelected());
    }

    @Test
    void shouldKeepStateWithoutSelectableChildrenWhenNodesAreAdded() {
        // #14798: a node whose children are all non-selectable has nothing to derive its state from
        TreeNode<?> movies = root.getChildren().get(2);
        movies.getChildren().clear();

        TreeNode unselectable = new CheckboxTreeNode("Unselectable");
        unselectable.setSelectable(false);
        unselectable.setSelected(true);
        ((TreeNode) movies).getChildren().add(unselectable);
        ((CheckboxTreeNode<?>) movies).setSelected(false, false);

        TreeNode unselectable2 = new CheckboxTreeNode("Unselectable2");
        unselectable2.setSelectable(false);
        unselectable2.setSelected(true);
        ((TreeNode) movies).getChildren().add(unselectable2);

        assertFalse(movies.isSelected());
        assertFalse(movies.isPartialSelected());
    }

    @Test
    void shouldUpdateSelectionStateWhenNodesAreRemoved() {
        TreeNode<?> work = root.getChildren().get(0).getChildren().get(0);
        assertEquals("Work", work.getData());
        assertEquals(2, work.getChildCount());

        TreeNode resume = work.getChildren().get(1);
        assertEquals("Resume", resume.getData());

        work.getChildren().get(0).setSelected(true);

        assertFalse(work.isSelected());
        assertTrue(work.isPartialSelected());

        work.getChildren().remove(resume);

        assertTrue(work.isSelected());
        assertFalse(work.isPartialSelected());
    }

}
