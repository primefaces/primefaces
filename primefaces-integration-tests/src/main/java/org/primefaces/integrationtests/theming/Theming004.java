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
package org.primefaces.integrationtests.theming;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Theming004 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private String text;
    private boolean checked;
    private List<String> selection;
    private LocalDate date;
    private Integer rating;
    private List<String> rows;
    private TreeNode<String> root;

    @PostConstruct
    public void init() {
        text = "One";
        checked = true;
        selection = new ArrayList<>(Arrays.asList("One"));
        rating = 3;
        rows = Arrays.asList("Row 1", "Row 2", "Row 3", "Row 4", "Row 5");

        root = new DefaultTreeNode<>("Root", null);
        TreeNode<String> documents = new DefaultTreeNode<>("Documents", root);
        documents.setExpanded(true);
        new DefaultTreeNode<>("Work", documents);
        TreeNode<String> home = new DefaultTreeNode<>("Home", documents);
        new DefaultTreeNode<>("Invoices.txt", home);
        TreeNode<String> events = new DefaultTreeNode<>("Events", root);
        new DefaultTreeNode<>("Meeting", events);
    }

    public void addMessages() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.isPostback()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error detail"));
            context.addMessage("form:messageInput", new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn", "Warn detail"));
        }
    }

    public List<String> complete(String query) {
        return Arrays.asList(query + "1", query + "2");
    }
}
