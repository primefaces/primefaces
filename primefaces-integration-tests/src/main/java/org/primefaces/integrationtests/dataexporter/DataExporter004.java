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
package org.primefaces.integrationtests.dataexporter;

import org.primefaces.component.export.DataExporters;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.integrationtests.treetable.Document;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


import jakarta.annotation.PostConstruct;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

/**
 * Exports the TreeTable through the programmatic {@link Exporter} API into a String instead of a download, so the
 * exported content can be asserted directly on the page.
 */
@Named
@ViewScoped
@Data
public class DataExporter004 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private TreeNode<Document> root;
    private TreeNode<Document>[] selectedNodes;
    private String output;

    @PostConstruct
    public void init() {
        root = new CheckboxTreeNode<>(new Document("Files", "-", "Folder"), null);

        TreeNode<Document> applications = new CheckboxTreeNode<>(new Document("Applications", "100kb", "Folder"), root);
        applications.setExpanded(true);

        // collapsed, so "primefaces.app" is not displayed
        TreeNode<Document> primefaces = new CheckboxTreeNode<>(new Document("Primefaces", "25kb", "Folder"), applications);
        new CheckboxTreeNode<>("app", new Document("primefaces.app", "10kb", "Application"), primefaces);

        new CheckboxTreeNode<>("app", new Document("editor.app", "25kb", "Application"), applications);

        TreeNode<Document> cloud = new CheckboxTreeNode<>(new Document("Cloud", "20kb", "Folder"), root);
        cloud.setExpanded(true);
        new CheckboxTreeNode<>("document", new Document("backup-1.zip", "10kb", "Zip"), cloud);

        // second page
        TreeNode<Document> desktop = new CheckboxTreeNode<>(new Document("Desktop", "150kb", "Folder"), root);
        new CheckboxTreeNode<>("document", new Document("note-todo.txt", "100kb", "Text"), desktop);
    }

    public void exportPageOnly() {
        export(ExportConfiguration.builder().pageOnly(true));
    }

    public void exportSelectionOnly() {
        export(ExportConfiguration.builder().selectionOnly(true));
    }

    public void exportAll() {
        export(ExportConfiguration.builder());
    }

    private void export(ExportConfiguration.Builder builder) {
        FacesContext context = FacesContext.getCurrentInstance();
        TreeTable table = (TreeTable) context.getViewRoot().findComponent("form:treeTable");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ExportConfiguration configuration = builder
                .encodingType(StandardCharsets.UTF_8.name())
                .exportHeader(true)
                .visibleOnly(true)
                .outputStream(out)
                .build();

        try {
            Exporter<TreeTable> exporter = DataExporters.get(TreeTable.class, "csv");
            exporter.export(context, Collections.singletonList(table), configuration);
        }
        catch (IOException e) {
            throw new FacesException(e);
        }

        // drop the byte order mark, it is not part of what this test is about
        output = out.toString(StandardCharsets.UTF_8).replace("\ufeff", "");
    }

}
