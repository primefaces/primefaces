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
package org.primefaces.integrationtests.core.widget;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardWidget;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

/**
 * Widgets using the interactions (draggable, droppable, sortable, resizable), updated and removed by AJAX.
 */
@Named
@ViewScoped
@Data
public class CoreWidget001 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private boolean rendered = true;
    private List<String> orderList;
    private DualListModel<String> pickList;
    private List<String> rows;
    private TreeNode<String> root;
    private DashboardModel dashboard;

    @PostConstruct
    public void init() {
        orderList = new ArrayList<>(Arrays.asList("Alpha", "Beta", "Gamma", "Delta"));
        pickList = new DualListModel<>(new ArrayList<>(Arrays.asList("One", "Two", "Three")), new ArrayList<>(Arrays.asList("Four")));
        rows = new ArrayList<>(Arrays.asList("Row 1", "Row 2", "Row 3"));

        root = new DefaultTreeNode<>("Root", null);
        TreeNode<String> folder = new DefaultTreeNode<>("Folder", root);
        new DefaultTreeNode<>("File 1", folder);
        new DefaultTreeNode<>("File 2", folder);
        new DefaultTreeNode<>("Other", root);
        folder.setExpanded(true);

        dashboard = new DefaultDashboardModel();
        dashboard.addWidget(new DefaultDashboardWidget(Arrays.asList("panel1", "panel2")));
        dashboard.addWidget(new DefaultDashboardWidget(Arrays.asList("panel3")));
    }

    public void toggle() {
        rendered = !rendered;
    }
}
