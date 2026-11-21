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
package org.primefaces.integrationtests.datatable;

import org.primefaces.integrationtests.jpa.TrackingJPALazyDataModel;
import org.primefaces.integrationtests.jpa.entity.CountryJpaEntity;
import org.primefaces.integrationtests.jpa.service.CountryJpaService;
import org.primefaces.model.DefaultLazyDataModel;
import org.primefaces.model.LazyDataModel;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable061Lazy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final LazyDataModelCallTracker lazyDataModelCallTracker = new LazyDataModelCallTracker();
    private LazyDataModel<Employee> employees;
    private TrackingJPALazyDataModel<CountryJpaEntity> countries;

    @Inject
    private EmployeeService service;

    @Inject
    private CountryJpaService countryJpaService;

    @PostConstruct
    public void init() {
        employees = DefaultLazyDataModel.<Employee>builder()
                .valueSupplier(filterBy -> service.getEmployees())
                .rowKeyProvider(Employee::getId)
                .build();

        DataTable051.seedData(countryJpaService);
        countries = new TrackingJPALazyDataModel<>(countryJpaService.entityManagerSupplier(), "dataTable061Lazy",
                CountryJpaEntity.class, CountryJpaEntity::getId);
    }
}
