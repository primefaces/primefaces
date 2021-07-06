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

import org.primefaces.model.TreeNode;

import java.util.Comparator;

public class DocumentSorterNameAscSizeDesc implements Comparator<TreeNode<Document>> {

    @Override
    public int compare(TreeNode<Document> treeNodeDoc1, TreeNode<Document> treeNodeDoc2) {
        Document doc1 = treeNodeDoc1.getData();
        Document doc2 = treeNodeDoc2.getData();

        try {
            if (doc1.getName().equalsIgnoreCase(doc2.getName())) {
                return doc2.getSize().compareToIgnoreCase(doc1.getSize());
            }
            else {
                return doc1.getName().compareToIgnoreCase(doc2.getName());
            }
        }
        catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
