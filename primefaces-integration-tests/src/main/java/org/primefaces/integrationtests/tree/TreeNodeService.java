/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.integrationtests.tree;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class TreeNodeService {

    public TreeNode<String> createNodes() {
        TreeNode<String> root = new DefaultTreeNode<>("Files", null);
        TreeNode<String> node0 = new DefaultTreeNode<>("Documents", root);
        TreeNode<String> node1 = new DefaultTreeNode<>("Events", root);
        TreeNode<String> node2 = new DefaultTreeNode<>("Movies", root);

        TreeNode<String> node00 = new DefaultTreeNode<>("Work", node0);
        TreeNode<String> node01 = new DefaultTreeNode<>("Home", node0);

        node00.getChildren().add(new DefaultTreeNode<>("Expenses.doc"));
        node00.getChildren().add(new DefaultTreeNode<>("Resume.doc"));
        node01.getChildren().add(new DefaultTreeNode<>("Invoices.txt"));

        TreeNode<String> node10 = new DefaultTreeNode<>("Meeting", node1);
        TreeNode<String> node11 = new DefaultTreeNode<>("Product Launch", node1);
        TreeNode<String> node12 = new DefaultTreeNode<>("Report Review", node1);

        TreeNode<String> node20 = new DefaultTreeNode<>("Al Pacino", node2);
        TreeNode<String> node21 = new DefaultTreeNode<>("Robert De Niro", node2);

        node20.getChildren().add(new DefaultTreeNode<>("Scarface"));
        node20.getChildren().add(new DefaultTreeNode<>("Serpico"));

        node21.getChildren().add(new DefaultTreeNode<>("Goodfellas"));
        node21.getChildren().add(new DefaultTreeNode<>("Untouchables"));

        return root;
    }

}
