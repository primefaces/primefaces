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
package org.primefaces.integrationtests.tree;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Tree010 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private TreeNode<String> horizontalRoot;
    private List<TreeNode<String>> horizontalSelection;
    private TreeNode<String> dynamicRoot;
    private List<TreeNode<String>> dynamicSelection;

    @PostConstruct
    public void init() {
        horizontalRoot = createNodes();
        dynamicRoot = createNodes();
    }

    private TreeNode<String> createNodes() {
        TreeNode<String> root = new CheckboxTreeNode<>("Root", null);
        root.setExpanded(true);

        TreeNode<String> documents = new CheckboxTreeNode<>("Documents", root);
        documents.setExpanded(true);
        new CheckboxTreeNode<>("work.doc", documents);

        // must never become selected, not even when its parent is selected
        TreeNode<String> privateDoc = new CheckboxTreeNode<>("private.doc", documents);
        privateDoc.setSelectable(false);

        new CheckboxTreeNode<>("resume.doc", documents);

        // all children are non-selectable, so its state must never be derived from its descendants
        TreeNode<String> archive = new CheckboxTreeNode<>("Archive", root);
        archive.setExpanded(true);

        TreeNode<String> locked = new CheckboxTreeNode<>("Locked", archive);
        locked.setExpanded(true);
        locked.setSelectable(false);

        new CheckboxTreeNode<>("old.doc", locked);

        // collapsed, so its preselected non-selectable child is not rendered by a dynamic tree when it is toggled
        TreeNode<String> backup = new CheckboxTreeNode<>("Backup", root);

        CheckboxTreeNode<String> snapshotDoc = new CheckboxTreeNode<>("snapshot.doc", backup);
        snapshotDoc.setSelectable(false);
        snapshotDoc.setSelected(true, false);

        new CheckboxTreeNode<>("daily.doc", backup);

        return root;
    }

    public void showSelectedNodes() {
        TestUtils.addMessage("horizontal", toString(horizontalSelection));
        TestUtils.addMessage("dynamic", toString(dynamicSelection));
    }

    private static String toString(List<TreeNode<String>> selection) {
        return selection == null
                ? ""
                : selection.stream().map(TreeNode::getData).sorted().collect(Collectors.joining(","));
    }

}
