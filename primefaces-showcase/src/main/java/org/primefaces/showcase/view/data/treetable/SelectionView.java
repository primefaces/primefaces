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
package org.primefaces.showcase.view.data.treetable;

import org.primefaces.model.TreeNode;
import org.primefaces.showcase.domain.Document;
import org.primefaces.showcase.service.DocumentService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("ttSelectionView")
@ViewScoped
public class SelectionView implements Serializable {

    private TreeNode<Document> root1;
    private TreeNode<Document> root2;
    private TreeNode<Document> root3;
    private TreeNode<Document> selectedNode;
    private TreeNode<Document>[] selectedNodes1;
    private TreeNode<Document>[] selectedNodes2;

    @Inject
    private DocumentService service;

    @PostConstruct
    public void init() {
        root1 = service.createDocuments();
        root2 = service.createDocuments();
        root3 = service.createCheckboxDocuments();
    }

    public TreeNode<Document> getRoot1() {
        return root1;
    }

    public TreeNode<Document> getRoot2() {
        return root2;
    }

    public TreeNode<Document> getRoot3() {
        return root3;
    }

    public TreeNode<Document> getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode<Document> selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode<Document>[] getSelectedNodes1() {
        return selectedNodes1;
    }

    public void setSelectedNodes1(TreeNode<Document>[] selectedNodes1) {
        this.selectedNodes1 = selectedNodes1;
    }

    public TreeNode<Document>[] getSelectedNodes2() {
        return selectedNodes2;
    }

    public void setSelectedNodes2(TreeNode<Document>[] selectedNodes2) {
        this.selectedNodes2 = selectedNodes2;
    }

    public void setService(DocumentService service) {
        this.service = service;
    }

    public void displaySelectedSingle() {
        if (selectedNode != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void displaySelectedMultiple(TreeNode<Document>[] nodes) {
        if (nodes != null && nodes.length > 0) {
            StringBuilder builder = new StringBuilder();

            for (TreeNode node : nodes) {
                builder.append(node.getData().toString());
                builder.append("<br />");
            }

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", builder.toString());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
