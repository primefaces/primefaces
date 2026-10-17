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
package org.primefaces.showcase.view.theming;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.showcase.domain.Document;
import org.primefaces.showcase.domain.Product;
import org.primefaces.showcase.service.DocumentService;
import org.primefaces.showcase.service.ProductService;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming-next/data.xhtml demo page.
 */
@Named("themingNextDataView")
@ViewScoped
@Getter
@Setter
public class ThemingNextDataView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient ProductService productService;

    @Inject
    private transient DocumentService documentService;

    private List<Product> products;
    private List<Product> manyProducts;
    private List<Product> emptyProducts = Collections.emptyList();

    private TreeNode<Document> singleTree;
    private TreeNode<Document> multipleTree;
    private TreeNode<Document> checkboxTree;
    private TreeNode<Document> dragDropTree;
    private TreeNode<Document> filterTree;
    private TreeNode<String> horizontalTree;
    private TreeNode<String> horizontalCheckboxTree;
    private TreeNode<Document> selectedNode;
    private TreeNode<Document>[] selectedNodes;
    private TreeNode<Document>[] checkedNodes;
    private TreeNode<String> selectedHorizontalNode;
    private TreeNode<String>[] checkedHorizontalNodes;

    private List<String> cities = new ArrayList<>(List.of("Berlin", "London", "Madrid", "Paris", "Rome", "Vienna"));
    private DualListModel<String> pickCities;
    private DualListModel<String> pickCitiesFiltered;

    private TimelineModel<String, String> timeline;
    private TimelineModel<String, String> groupedTimeline;

    @PostConstruct
    public void init() {
        products = productService.getProducts(6);
        manyProducts = productService.getClonedProducts(30);

        singleTree = documentService.createDocuments();
        multipleTree = documentService.createDocuments();
        checkboxTree = documentService.createCheckboxDocuments();
        dragDropTree = documentService.createDocuments();
        filterTree = documentService.createDocuments();
        expandFirst(singleTree);
        expandFirst(multipleTree);
        expandFirst(checkboxTree);
        expandFirst(dragDropTree);

        horizontalTree = createHorizontalTree();
        horizontalCheckboxTree = createHorizontalTree();

        pickCities = new DualListModel<>(new ArrayList<>(List.of("Berlin", "London", "Madrid", "Paris")),
                new ArrayList<>(List.of("Rome", "Vienna")));
        pickCitiesFiltered = new DualListModel<>(new ArrayList<>(List.of("Amsterdam", "Barcelona", "Berlin", "Istanbul", "London")),
                new ArrayList<>(List.of("Madrid", "Paris")));

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        timeline = new TimelineModel<>();
        timeline.add(TimelineEvent.<String>builder().data("Kick-off").startDate(start.plusDays(1)).build());
        timeline.add(TimelineEvent.<String>builder().data("Design").startDate(start.plusDays(3)).endDate(start.plusDays(10)).build());
        timeline.add(TimelineEvent.<String>builder().data("Review").startDate(start.plusDays(12)).type("point").build());
        timeline.add(TimelineEvent.<String>builder().data("Implementation").startDate(start.plusDays(11)).endDate(start.plusDays(24)).build());
        timeline.add(TimelineEvent.<String>builder().data("Freeze").startDate(start.plusDays(20)).endDate(start.plusDays(23))
                .type("background").build());
        timeline.add(TimelineEvent.<String>builder().data("Release").startDate(start.plusDays(26)).build());

        groupedTimeline = new TimelineModel<>();
        groupedTimeline.addGroup(new TimelineGroup<>("frontend", "Frontend"));
        groupedTimeline.addGroup(new TimelineGroup<>("backend", "Backend"));
        groupedTimeline.addGroup(new TimelineGroup<>("qa", "QA"));
        groupedTimeline.add(TimelineEvent.<String>builder().data("Theming").startDate(start.plusDays(2)).endDate(start.plusDays(12))
                .group("frontend").build());
        groupedTimeline.add(TimelineEvent.<String>builder().data("Showcase").startDate(start.plusDays(10)).endDate(start.plusDays(20))
                .group("frontend").build());
        groupedTimeline.add(TimelineEvent.<String>builder().data("Renderer").startDate(start.plusDays(4)).endDate(start.plusDays(15))
                .group("backend").build());
        groupedTimeline.add(TimelineEvent.<String>builder().data("Tests").startDate(start.plusDays(14)).endDate(start.plusDays(25))
                .group("qa").build());
        groupedTimeline.add(TimelineEvent.<String>builder().data("Sign-off").startDate(start.plusDays(26)).group("qa").build());
    }

    public void onFileUpload(FileUploadEvent event) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Uploaded", event.getFile().getFileName()));
    }

    private static void expandFirst(TreeNode<?> root) {
        if (root.getChildCount() > 0) {
            root.getChildren().get(0).setExpanded(true);
        }
    }

    private static TreeNode<String> createHorizontalTree() {
        TreeNode<String> root = new DefaultTreeNode<>("Root", null);
        TreeNode<String> company = new DefaultTreeNode<>("Company", root);
        company.setExpanded(true);

        TreeNode<String> development = new DefaultTreeNode<>("Development", company);
        development.setExpanded(true);
        new DefaultTreeNode<>("Faces", development);
        new DefaultTreeNode<>("Backend", development);

        TreeNode<String> marketing = new DefaultTreeNode<>("Marketing", company);
        marketing.setExpanded(true);
        new DefaultTreeNode<>("Social Media", marketing);

        TreeNode<String> sales = new DefaultTreeNode<>("Sales", company);
        new DefaultTreeNode<>("Europe", sales);
        new DefaultTreeNode<>("America", sales);
        return root;
    }
}
