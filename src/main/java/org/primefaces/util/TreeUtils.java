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
package org.primefaces.util;

import java.util.Arrays;
import java.util.Comparator;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeList;

/**
 * Utilities for Tree components.
 */
public class TreeUtils {

    private TreeUtils() {
    }

    /**
     * Sorts children of a node using a comparator
     *
     * @param node Node instance whose children to be sorted
     * @param comparator Comparator to use in sorting
     */
    public static void sortNode(TreeNode node, Comparator comparator) {
        TreeNodeList children = (TreeNodeList) node.getChildren();

        if (children != null && !children.isEmpty()) {
            Object[] childrenArray = children.toArray();
            Arrays.sort(childrenArray, comparator);
            for (int i = 0; i < childrenArray.length; i++) {
                children.setSibling(i, (TreeNode) childrenArray[i]);
            }

            for (int i = 0; i < children.size(); i++) {
                sortNode(children.get(i), comparator);
            }
        }
    }
}
