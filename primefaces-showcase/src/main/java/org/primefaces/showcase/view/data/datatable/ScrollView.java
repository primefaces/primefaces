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
package org.primefaces.showcase.view.data.datatable;

import javax.faces.view.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.showcase.domain.Customer;
import org.primefaces.showcase.service.CustomerService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("dtScrollView")
@ViewScoped
public class ScrollView implements Serializable {

    private List<Customer> products1;
    private List<Customer> products2;
    private List<Customer> products3;
    private List<Customer> products4;
    private List<Customer> products5;
    private List<Customer> products6;
    private LazyDataModel<Customer> lazyModel;

    @Inject
    private CustomerService service;

    @PostConstruct
    public void init() {
        products1 = service.getCustomers(50);
        products2 = service.getCustomers(10);
        products3 = service.getCustomers(50);
        products4 = service.getCustomers(50);
        products5 = service.getCustomers(50);
        products6 = service.getCustomers(200);
        lazyModel = new LazyCustomerDataModel(service.getCustomers(20000));
    }

    public List<Customer> getCustomers1() {
        return products1;
    }

    public List<Customer> getCustomers2() {
        return products2;
    }

    public List<Customer> getCustomers3() {
        return products3;
    }

    public List<Customer> getCustomers4() {
        return products4;
    }

    public List<Customer> getCustomers5() {
        return products5;
    }

    public List<Customer> getCustomers6() {
        return products6;
    }

    public LazyDataModel<Customer> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Customer> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public void setService(CustomerService service) {
        this.service = service;
    }
}
