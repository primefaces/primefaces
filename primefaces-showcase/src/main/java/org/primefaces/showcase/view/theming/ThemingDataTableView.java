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

import org.primefaces.showcase.domain.Customer;
import org.primefaces.showcase.domain.CustomerStatus;
import org.primefaces.showcase.domain.Product;
import org.primefaces.showcase.service.CustomerService;
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
 * Backing bean of the theming DataTable section (theming/sections/datatable.xhtml): the data of its two tables,
 * with pre-selected rows to show the highlight state right away.
 */
@Named("themingDataTableView")
@ViewScoped
@Getter
@Setter
public class ThemingDataTableView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private transient ProductService productService;

    @Inject
    private transient CustomerService customerService;

    private List<Customer> customers;
    private List<Customer> filteredCustomers;
    private List<Customer> selectedCustomers;
    private List<Product> products;

    @PostConstruct
    public void init() {
        customers = customerService.getCustomers(25);
        selectedCustomers = new ArrayList<>(customers.subList(1, 3));
        products = productService.getClonedProducts(6);
    }

    public CustomerStatus[] getCustomerStatus() {
        return CustomerStatus.values();
    }
}
