/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.junit.jupiter.api.Assertions;
import org.primefaces.integrationtests.AbstractTableTest;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeList;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.TreeTable;
import org.primefaces.selenium.component.model.treetable.Row;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractTreeTableTest extends AbstractTableTest {

    protected TreeNode<Document> root = new DocumentService().createDocuments();

    protected void assertRows(TreeTable treeTable, TreeNode root) {
        List<Row> rows = treeTable.getRows();
        assertRows(rows, root);
    }

    protected void assertRows(List<Row> rows, TreeNode<Document> root) {
        int expectedSize = countRowsRecursive(root);
        Assertions.assertNotNull(rows);
        if (expectedSize == 0) {
            Assertions.assertEquals(1, rows.size()); // No records found.
            return;
        }
        Assertions.assertEquals(expectedSize, rows.size());

        int row = 0;
        List<TreeNode<Document>> treeNodes = flattenTreeNodes(root);
        for (TreeNode<Document> treeNode: treeNodes) {
            Assertions.assertEquals(treeNode.getData().getName(), rows.get(row).getCell(0).getText());
            Assertions.assertEquals(treeNode.getData().getSize(), rows.get(row).getCell(1).getText());
            Assertions.assertEquals(treeNode.getData().getType(), rows.get(row).getCell(2).getText());
            row++;
        }
    }

    private int countRowsRecursive(TreeNode<Document> root) {
        int cnt = root.getChildCount();
        for (TreeNode<Document> child : root.getChildren()) {
            if (child.isExpanded()) {
                cnt += countRowsRecursive(child);
            }
        }
        return cnt;
    }

    private List<TreeNode<Document>> flattenTreeNodes(TreeNode<Document> root) {
        List<TreeNode<Document>> treeNodes = new TreeNodeList<>();
        for (TreeNode<Document> child : root.getChildren()) {
            treeNodes.add(child);
            if (child.isExpanded()) {
                treeNodes.addAll(flattenTreeNodes(child));
            }
        }
        return treeNodes;
    }

    public TreeNode<Document> sortBy(TreeNode<Document> root, final Comparator comparator) {
        List<TreeNode<Document>> listSorted = (List<TreeNode<Document>>) root.getChildren().stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        List<TreeNode<Document>> listSortedRecursive = new ArrayList<>();
        for (TreeNode<Document> t: listSorted) {
            if (t.getChildCount() > 0) {
                listSortedRecursive.add(sortBy(t, comparator));
            }
            else {
                listSortedRecursive.add(t);
            }
        }

        DefaultTreeNode<Document> treeSorted = new DefaultTreeNode<>(root.getData(), null);
        treeSorted.setChildren(listSortedRecursive);
        treeSorted.setExpanded(root.isExpanded());
        return treeSorted;
    }

    protected void assertMessage(Messages messages, int index, String summary, String detail) {
        Msg message = messages.getMessage(index);
        Assertions.assertEquals(summary, message.getSummary());
        Assertions.assertEquals(detail, message.getDetail());
    }
}
