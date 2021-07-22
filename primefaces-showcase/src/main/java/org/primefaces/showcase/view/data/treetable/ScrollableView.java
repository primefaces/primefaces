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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named("ttScrollableView")
@ViewScoped
public class ScrollableView implements Serializable {

    private TreeNode<Document> root1;
    private TreeNode<Document> root2;
    private TreeNode<Document> root3;

    @Inject
    private DocumentService service;

    @PostConstruct
    public void init() {
        root1 = service.createDocuments();
        root2 = service.createDocuments();
        root3 = service.createDocuments();

        root1.getChildren().get(0).setExpanded(true);
        root1.getChildren().get(1).setExpanded(true);
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

    public void setService(DocumentService service) {
        this.service = service;
    }
}
