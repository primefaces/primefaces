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

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.showcase.domain.Document;
import org.primefaces.showcase.domain.Product;
import org.primefaces.showcase.service.ProductService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming-next/datatable.xhtml demo page: data the existing showcase beans do not offer
 * (pre-selected rows to show the highlight state right away, empty models for the empty message).
 */
@Named("themingNextDataTableView")
@ViewScoped
@Getter
@Setter
public class ThemingNextDataTableView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient ProductService productService;

    private List<Product> products;
    private Product selectedProduct;
    private List<Product> selectedProducts;
    private List<Product> emptyProducts = new ArrayList<>();
    private TreeNode<Document> emptyRoot = new DefaultTreeNode<>(new Document("Files", "-", "Folder"), null);

    @PostConstruct
    public void init() {
        products = productService.getClonedProducts(8);
        selectedProduct = products.get(1);
        selectedProducts = new ArrayList<>(products.subList(2, 4));
    }
}
