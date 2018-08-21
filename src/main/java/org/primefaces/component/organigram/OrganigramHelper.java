/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.organigram;

import org.primefaces.model.OrganigramNode;

/**
 * Some helpers methods for the organigram model/component.
 */
public final class OrganigramHelper {

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
