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

import lombok.Data;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
@Data
public class TreeTable001 implements Serializable {

    private static final long serialVersionUID = 4336050122274677022L;

    private TreeNode<Document> root;
    private TreeNode<Document> selectedNode;

    private Document selectedDocument;

    @Inject
    private DocumentService service;

    @PostConstruct
    public void init() {
        root = service.createDocuments();
    }

    public void resetTable() {
        TreeTable treeTable = (TreeTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:treeTable");
        treeTable.reset();

        init();
    }

    public void showSelectedDocument() {
        TestUtils.addMessage("selected document", selectedDocument.getName());
    }

    public void showSelectedNode() {
        TestUtils.addMessage("selected node", selectedNode.getData().getName());
    }

    public void selectNode(NodeSelectEvent event) {
        TestUtils.addMessage("select-event", ((Document) event.getTreeNode().getData()).getName());
    }

    public void unselectNode(NodeUnselectEvent event) {
        TestUtils.addMessage("unselect-event", ((Document) event.getTreeNode().getData()).getName());
    }
}
