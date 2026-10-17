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

import org.primefaces.model.TreeNode;
import org.primefaces.showcase.domain.Document;
import org.primefaces.showcase.domain.Product;
import org.primefaces.showcase.service.DocumentService;
import org.primefaces.showcase.service.ProductService;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
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
 * Backing bean of the theming-next.xhtml demo page.
 */
@Named
@ViewScoped
@Getter
@Setter
public class ThemingNextView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient ProductService productService;

    @Inject
    private transient DocumentService documentService;

    private List<Product> products;
    private List<Product> selectedProducts = new ArrayList<>();
    private TreeNode<Document> documents;
    private List<String> cities = List.of("Berlin", "London", "Madrid", "Paris", "Rome", "Vienna");
    private String city;
    private List<String> selectedCities = new ArrayList<>(List.of("Paris"));
    private String option = "Option 2";
    private boolean checked = true;
    private boolean toggled = true;
    private String radio = "B";
    private LocalDate date = LocalDate.now();
    private Integer number = 42;
    private Integer rating = 3;
    private Integer slider = 40;
    private String password;
    private List<String> chips = new ArrayList<>(List.of("Faces", "Theming"));
    private String text;

    @PostConstruct
    public void init() {
        products = productService.getProducts(12);
        documents = documentService.createDocuments();
    }

    public void info() {
        addMessage(FacesMessage.SEVERITY_INFO, "Info", "This is an info message");
    }

    public void warn() {
        addMessage(FacesMessage.SEVERITY_WARN, "Warning", "This is a warning message");
    }

    public void error() {
        addMessage(FacesMessage.SEVERITY_ERROR, "Error", "This is an error message");
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
