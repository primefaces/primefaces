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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

/**
 * Backs {@code dataTable052.xhtml} - the table-level {@code filterValueType} default. The fixture is
 * deliberately the plain, unmodified {@link EmployeeService} data: this scenario is about which columns
 * get a match-mode picker at all, not about exercising individual match modes (that is
 * {@link DataTable051}'s job), so no synthetic null/blank rows are needed.
 */
@Named
@ViewScoped
@Data
public class DataTable052 implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Employee> employees;
    private List<Employee> filteredEmployeesNone;
    private List<Employee> filteredEmployeesText;

    @Inject
    private EmployeeService service;

    @PostConstruct
    public void init() {
        // copy, so this view never mutates the shared, application-scoped list other DataTable tests use
        employees = new ArrayList<>(service.getEmployees());
    }
}
