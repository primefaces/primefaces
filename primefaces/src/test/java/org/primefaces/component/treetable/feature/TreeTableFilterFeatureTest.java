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
package org.primefaces.component.treetable.feature;

import org.primefaces.component.treetable.TreeTable;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.TreeNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TreeTableFilterFeatureTest {

    /**
     * Matches the nodes by their data instead of evaluating the column filters.
     */
    private static FilterFeature matching(String data) {
        return new FilterFeature() {
            @Override
            protected boolean isMatching(FacesContext context, TreeTable tt, TreeNode<?> root, TreeNode<?> node, Map<String, FilterMeta> filterBy,
                    Locale filterLocale) {
                return data.equals(node.getData());
            }
        };
    }

    private static Set<TreeNode<?>> collect(FilterFeature filterFeature, TreeNode<?> root, boolean expandMatches, boolean pruneDescendants) {
        Set<TreeNode<?>> filteredNodes = Collections.newSetFromMap(new IdentityHashMap<>());
        filterFeature.collectFilteredNodes(null, null, root, root, Collections.emptyMap(), Locale.ENGLISH, false, expandMatches,
                pruneDescendants, filteredNodes);
        return filteredNodes;
    }

    @Test
    void keepsAncestorsAndDescendantsOfMatchesOnDefaultPrune() {
        DefaultTreeNode<String> root = new DefaultTreeNode<>();
        DefaultTreeNode<String> parent = new DefaultTreeNode<>("Type", "Parent", root);
        DefaultTreeNode<String> match = new DefaultTreeNode<>("Type", "Match", parent);
        DefaultTreeNode<String> childOfMatch = new DefaultTreeNode<>("Type", "Child", match);
        DefaultTreeNode<String> sibling = new DefaultTreeNode<>("Type", "Sibling", parent);
        DefaultTreeNode<String> other = new DefaultTreeNode<>("Type", "Other", root);

        Set<TreeNode<?>> filteredNodes = collect(matching("Match"), root, false, false);

        assertEquals(3, filteredNodes.size());
        assertTrue(filteredNodes.contains(parent));
        assertTrue(filteredNodes.contains(match));
        assertTrue(filteredNodes.contains(childOfMatch));
        assertFalse(filteredNodes.contains(sibling));
        assertFalse(filteredNodes.contains(other));
    }

    @Test
    void prunesDescendantsOfMatchesWhenRequested() {
        DefaultTreeNode<String> root = new DefaultTreeNode<>();
        DefaultTreeNode<String> match = new DefaultTreeNode<>("Type", "Match", root);
        DefaultTreeNode<String> childOfMatch = new DefaultTreeNode<>("Type", "Child", match);
        DefaultTreeNode<String> matchingChild = new DefaultTreeNode<>("Type", "Match", childOfMatch);

        Set<TreeNode<?>> filteredNodes = collect(matching("Match"), root, false, true);

        assertTrue(filteredNodes.contains(match));
        assertTrue(filteredNodes.contains(childOfMatch), "ancestor of a match");
        assertTrue(filteredNodes.contains(matchingChild));

        DefaultTreeNode<String> root2 = new DefaultTreeNode<>();
        DefaultTreeNode<String> match2 = new DefaultTreeNode<>("Type", "Match", root2);
        DefaultTreeNode<String> childOfMatch2 = new DefaultTreeNode<>("Type", "Child", match2);

        Set<TreeNode<?>> filteredNodes2 = collect(matching("Match"), root2, false, true);

        assertTrue(filteredNodes2.contains(match2));
        assertFalse(filteredNodes2.contains(childOfMatch2));
    }

    @Test
    void expandsAncestorsOfMatchesOnlyWhenRequested() {
        DefaultTreeNode<String> root = new DefaultTreeNode<>();
        DefaultTreeNode<String> parent = new DefaultTreeNode<>("Type", "Parent", root);
        DefaultTreeNode<String> match = new DefaultTreeNode<>("Type", "Match", parent);
        new DefaultTreeNode<>("Type", "Child", match);

        collect(matching("Match"), root, false, false);
        assertFalse(parent.isExpanded());

        collect(matching("Match"), root, true, false);
        assertTrue(parent.isExpanded());
        assertFalse(match.isExpanded(), "a match itself is not expanded");
    }

    @Test
    void filterKeepsTheModel() {
        DefaultTreeNode<String> root = new DefaultTreeNode<>();
        DefaultTreeNode<String> match = new DefaultTreeNode<>("Type", "Match", root);
        DefaultTreeNode<String> other = new DefaultTreeNode<>("Type", "Other", root);

        collect(matching("Match"), root, true, true);

        assertEquals(2, root.getChildCount());
        assertEquals(match, root.getChildren().get(0));
        assertEquals(other, root.getChildren().get(1));
    }

    @Test
    void filterWithoutActiveFiltersDisplaysAllNodes() {
        FacesContext context = mock(FacesContext.class);

        FilterMeta inactiveFilter = mock(FilterMeta.class);
        when(inactiveFilter.isActive()).thenReturn(false);
        Map<String, FilterMeta> filterBy = new HashMap<>();
        filterBy.put("type", inactiveFilter);

        TreeTable treeTable = mock(TreeTable.class);
        when(treeTable.getFilterByAsMap()).thenReturn(filterBy);

        new FilterFeature().filter(context, treeTable, new DefaultTreeNode<>(), true);

        verify(treeTable).setFilteredNodes(null);
    }
}
