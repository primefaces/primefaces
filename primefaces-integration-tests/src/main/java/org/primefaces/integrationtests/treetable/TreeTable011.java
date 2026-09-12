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
package org.primefaces.integrationtests.treetable;

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
public class TreeTable011 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private TreeNode<Document> root;
    private List<TreeNode<Document>> selectedNodes;

    @PostConstruct
    public void init() {
        root = new CheckboxTreeNode<>(new Document("Files", "-", "Folder"), null);

        TreeNode<Document> applications = new CheckboxTreeNode<>(new Document("Applications", "100kb", "Folder"), root);
        applications.setExpanded(true);

        TreeNode<Document> primefaces = new CheckboxTreeNode<>(new Document("Primefaces", "25kb", "Folder"), applications);
        primefaces.setExpanded(true);

        new CheckboxTreeNode<>("app", new Document("primefaces.app", "10kb", "Application"), primefaces);
        new CheckboxTreeNode<>("app", new Document("mobile.app", "5kb", "Application"), primefaces);

        // a second child of "Applications", so selecting "Primefaces" leaves "Applications" partially selected
        // and propagateSelectionUp does not add it to the selection - this test is only about downwards propagation
        new CheckboxTreeNode<>("app", new Document("editor.app", "25kb", "Application"), applications);
    }

    public void showSelectedNodes() {
        String names = selectedNodes == null
                ? ""
                : selectedNodes.stream().map(n -> n.getData().getName()).collect(Collectors.joining(","));
        TestUtils.addMessage("selected nodes", names);
    }

}
