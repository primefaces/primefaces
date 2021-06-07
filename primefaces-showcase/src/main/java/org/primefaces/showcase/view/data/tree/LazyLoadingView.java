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
package org.primefaces.showcase.view.data.tree;

import java.io.File;
import javax.faces.view.ViewScoped;
import org.primefaces.model.TreeNode;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

@Named("treeLazyLoadingView")
@ViewScoped
public class LazyLoadingView implements Serializable {

    private TreeNode root;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        FileInfo path = new FileInfo(context.getExternalContext().getRealPath("/"));
        root = new LazyLoadingTreeNode(path, (folder) -> listFiles(folder));
    }

    public static List<FileInfo> listFiles(String parentFolder) {
        File[] files = new File(parentFolder).listFiles();
        if (files == null) {
            return new ArrayList<>();
        }

        List<FileInfo> result = new ArrayList<>();
        for (File file : files) {
            result.add(new FileInfo(file.getAbsolutePath()));
        }
        return result;
    }

    public TreeNode getRoot() {
        return root;
    }
}
