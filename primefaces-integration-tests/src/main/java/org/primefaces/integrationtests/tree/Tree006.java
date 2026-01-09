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

import org.primefaces.model.LazyDefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;

@Named
@ViewScoped
public class Tree006 implements Serializable {

    private TreeNode<FileInfo> root;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        root = new LazyDefaultTreeNode<>(new FileInfo(context.getExternalContext().getRealPath("/"), true),
                (fileInfo) -> listFiles(fileInfo),
                (fileInfo) -> !fileInfo.isDirectory());
    }

    public static List<FileInfo> listFiles(FileInfo parentFolder) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Lazy load", parentFolder.getPath()));

        List<FileInfo> result = new ArrayList<>();

        File[] files = new File(parentFolder.getPath()).listFiles();
        if (files != null) {
            for (File file : files) {
                result.add(new FileInfo(file.getAbsolutePath(), file.isDirectory()));
            }
        }

        return result;
    }


    public TreeNode getRoot() {
        return root;
    }

    @Getter
    public static class FileInfo implements Serializable {

        private String path;
        private String name;
        private boolean directory;

        public FileInfo(String path, boolean directory) {
            this.path = path;
            if (this.path.equals(File.separator)) {
                this.name = this.path;
            }
            else {
                String[] parts = path.split(File.separator.equals("\\") ? "\\\\" : File.separator);
                this.name = parts[parts.length - 1];
            }
            this.directory = directory;
        }
    }
}
