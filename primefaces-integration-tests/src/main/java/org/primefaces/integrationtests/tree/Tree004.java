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

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.TreeNode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Tree004 implements Serializable {

    @Inject
    private TreeNodeService treeNodeService;

    private TreeNode<String> root;
    private TreeNode<String>[] selectedNodes;

    private String selection;

    @PostConstruct
    public void init() {
        root = treeNodeService.createNodes();
        selection = "multiple";
    }

    public void showSelectedNodes() {
        if (selectedNodes != null && selectedNodes.length > 0) {
            TestUtils.addMessage("Selected nodes", Arrays.stream(selectedNodes).map(n -> n.getData()).collect(Collectors.joining(",")));
        }
        else {
            TestUtils.addMessage("No node selected!", "");
        }
    }

    public void filter(jakarta.faces.event.AjaxBehaviorEvent event) {
        TestUtils.addMessage("filter-event");
    }

}
