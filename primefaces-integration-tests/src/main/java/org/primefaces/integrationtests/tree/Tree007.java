/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class Tree007 implements Serializable {

    @Getter
    @Setter
    private TreeNode<String> root;
    @Getter
    @Setter
    private TreeNode<String>[] selectedNodes;

    public void init() {
        root = new CheckboxTreeNode<>("Files", null);
        TreeNode<String> node0 = new CheckboxTreeNode<>("Documents", root);
        TreeNode<String> node1 = new CheckboxTreeNode<>("Events", root);
        TreeNode<String> node2 = new CheckboxTreeNode<>("Movies", root);

        TreeNode<String> node00 = new CheckboxTreeNode<>("Work", node0);
        TreeNode<String> node01 = new CheckboxTreeNode<>("Home", node0);

        node00.getChildren().add(new CheckboxTreeNode<>("Expenses.doc"));
        node00.getChildren().add(new CheckboxTreeNode<>("Resume.doc"));
        node01.getChildren().add(new CheckboxTreeNode<>("Invoices.txt"));
        node01.setSelected(true);

        TreeNode<String> node10 = new CheckboxTreeNode<>("Meeting", node1);
        node10.setSelected(true);
        TreeNode<String> node11 = new CheckboxTreeNode<>("Product Launch", node1);
        node11.setSelected(true);
        TreeNode<String> node12 = new CheckboxTreeNode<>("Report Review", node1);
        node12.setSelected(true);

        TreeNode<String> node20 = new CheckboxTreeNode<>("Al Pacino", node2);
        TreeNode<String> node21 = new CheckboxTreeNode<>("Robert De Niro", node2);

        node20.getChildren().add(new CheckboxTreeNode<>("Scarface"));
        node20.getChildren().add(new CheckboxTreeNode<>("Serpico"));

        node21.getChildren().add(new CheckboxTreeNode<>("Goodfellas"));
        node21.getChildren().add(new CheckboxTreeNode<>("Untouchables"));
    }

    public void initWithoutPreselection() {
        root = new CheckboxTreeNode<>("Files", null);
        TreeNode<String> node0 = new CheckboxTreeNode<>("Documents", root);
        TreeNode<String> node1 = new CheckboxTreeNode<>("Events", root);
        TreeNode<String> node2 = new CheckboxTreeNode<>("Movies", root);

        TreeNode<String> node00 = new CheckboxTreeNode<>("Work", node0);
        TreeNode<String> node01 = new CheckboxTreeNode<>("Home", node0);

        node00.getChildren().add(new CheckboxTreeNode<>("Expenses.doc"));
        node00.getChildren().add(new CheckboxTreeNode<>("Resume.doc"));
        node01.getChildren().add(new CheckboxTreeNode<>("Invoices.txt"));

        TreeNode<String> node10 = new CheckboxTreeNode<>("Meeting", node1);
        TreeNode<String> node11 = new CheckboxTreeNode<>("Product Launch", node1);
        TreeNode<String> node12 = new CheckboxTreeNode<>("Report Review", node1);

        TreeNode<String> node20 = new CheckboxTreeNode<>("Al Pacino", node2);
        TreeNode<String> node21 = new CheckboxTreeNode<>("Robert De Niro", node2);

        node20.getChildren().add(new CheckboxTreeNode<>("Scarface"));
        node20.getChildren().add(new CheckboxTreeNode<>("Serpico"));

        node21.getChildren().add(new CheckboxTreeNode<>("Goodfellas"));
        node21.getChildren().add(new CheckboxTreeNode<>("Untouchables"));
    }
}
