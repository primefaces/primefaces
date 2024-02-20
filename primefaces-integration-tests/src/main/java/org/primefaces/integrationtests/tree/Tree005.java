/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Data;

import org.primefaces.model.TreeNode;
import org.primefaces.util.LangUtils;

@Named
@ViewScoped
@Data
public class Tree005 implements Serializable {

    @Inject
    private TreeNodeService treeNodeService;

    private TreeNode<String> root;

    @PostConstruct
    public void init() {
        root = treeNodeService.createNodes();
    }

    public boolean customFilter(TreeNode treeNode, Object filter, Locale locale) {
        if (treeNode.getData() == null || filter == null) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        if (LangUtils.isBlank(filterText)) {
            return true;
        }
        return ((String) treeNode.getData()).toLowerCase(locale).contains(filterText);
    }

}
