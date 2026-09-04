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

import org.primefaces.integrationtests.jpa.H2RegexJPALazyDataModel;
import org.primefaces.integrationtests.jpa.JpaEmployee;
import org.primefaces.integrationtests.jpa.JpaEmployeeRepository;
import org.primefaces.model.JPALazyDataModel;
import org.primefaces.model.LazyDataModel;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.convert.DateTimeConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;

/**
 * DataTable: the filterValueType match modes on a lazy, server side filtered table - every mode the end user
 * picks from the dropdown has to end up as a predicate in the criteria query JPALazyDataModel builds, rather
 * than being applied in memory as it is for a plain List.
 */
@Named
@ViewScoped
public class DataTable053 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager entityManager;

    @Inject
    private JpaEmployeeRepository repository;

    private LazyDataModel<JpaEmployee> lazyModel;

    @PostConstruct
    public void init() {
        // rewrite the fixture per view, so the relative date/time match modes stay assertable however long
        // the application has been running
        repository.reseed(entityManager);

        // H2RegexJPALazyDataModel rather than a plain JPALazyDataModel: "matches regex" is the one match mode
        // without a portable JPA translation, so the model has to bring the regex function of its database
        lazyModel = new JPALazyDataModel.Builder<JpaEmployee, H2RegexJPALazyDataModel<JpaEmployee>>(new H2RegexJPALazyDataModel<>())
                .entityClass(JpaEmployee.class)
                .entityManager(() -> entityManager)
                .build();
    }

    public LazyDataModel<JpaEmployee> getLazyModel() {
        return lazyModel;
    }

    public DateTimeConverter getReviewDateConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd");
        converter.setType("localDate");
        return converter;
    }

    public DateTimeConverter getStartTimeConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("HH:mm:ss");
        converter.setType("localTime");
        return converter;
    }

    public DateTimeConverter getLastLoginConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        converter.setType("localDateTime");
        return converter;
    }
}
