/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.organigram;

import org.primefaces.model.OrganigramNode;

/**
 * Some helpers methods for the organigram model/component.
 */
public final class OrganigramHelper {

    private OrganigramHelper() {
    }

    /**
     * Finds a {@link OrganigramNode} for the given rowKey.
     *
     * @param searchRoot The {@link OrganigramNode} to start the search.
     * @param rowKey     The rowKey.
     * @return The {@link OrganigramNode} for the rowKey or <code>null</code>.
     */
    public static OrganigramNode findTreeNode(OrganigramNode searchRoot, String rowKey) {

        if (rowKey == null || searchRoot == null) {
            return null;
        }

        if (searchRoot.getRowKey().equals(rowKey)) {
            return searchRoot;
        }

        if (searchRoot.getChildren() != null) {
            for (OrganigramNode child : searchRoot.getChildren()) {
                OrganigramNode childResult = findTreeNode(child, rowKey);
                if (childResult != null) {
                    return childResult;
                }
            }
        }

        return null;
    }

    /**
     * Finds a {@link OrganigramNode} for the given selection.
     *
     * @param searchRoot The {@link OrganigramNode} to start the search.
     * @param selection  The selection.
     * @return The {@link OrganigramNode} for the selection or <code>null</code>.
     */
    public static OrganigramNode findTreeNode(OrganigramNode searchRoot, OrganigramNode selection) {
        if (selection == null || searchRoot == null) {
            return null;
        }

        if (searchRoot.equals(selection)) {
            return searchRoot;
        }

        if (searchRoot.getChildren() != null) {
            for (OrganigramNode child : searchRoot.getChildren()) {
                OrganigramNode childResult = findTreeNode(child, selection);
                if (childResult != null) {
                    return childResult;
                }
            }
        }

        return null;
    }

    /**
     * Loops the {@link OrganigramNode}s and generates a rowKey for each node on the basis of the parent and child index.
     *
     * @param node The root node.
     */
    public static void buildRowKeys(OrganigramNode node) {
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (int i = 0; i < node.getChildren().size(); i++) {
                OrganigramNode childNode = node.getChildren().get(i);

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);

                buildRowKeys(childNode);
            }
        }
    }
}
