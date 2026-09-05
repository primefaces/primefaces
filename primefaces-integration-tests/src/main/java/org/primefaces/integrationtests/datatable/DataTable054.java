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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.convert.DateTimeConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;
import lombok.Value;

/**
 * Backs dataTable054.xhtml: the two ways to clear a filter match-mode picker again (the per-column "Clear"
 * action inside the picker's own menu and the table-wide {@code clearFiltersButton}), plus {@code p:columns},
 * where {@code filterValueType} has to work off a dynamic column model instead of a literal attribute.
 */
@Named
@ViewScoped
@Data
public class DataTable054 implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Employee> employees;
    private List<Employee> filteredEmployees;
    private List<Employee> filteredDynamicEmployees;

    private List<ColumnModel> columns = new ArrayList<>();

    @Inject
    private EmployeeService service;

    @PostConstruct
    public void init() {
        // the unmodified fixture is enough here - unlike DataTable051, this view is about clearing a filter
        // and about the dynamic-column plumbing, not about the individual match modes, so no row needs a
        // synthetic null/blank/relative-date value
        employees = new ArrayList<>(service.getEmployees());

        // the date column needs a couple of known values so a picked date actually filters something -
        // computed against today so they never go stale
        LocalDate today = LocalDate.now();
        employees.get(0).setReviewDate(today);              // id 1, Mike Master
        employees.get(1).setReviewDate(today.minusDays(1));  // id 2, Susan Pepper

        // p:columns takes a single converter for every generated column, which cannot serve a numeric and a
        // textual column at once - so the two columns the test filters by value are Strings (no conversion
        // needed), and the numeric one is asserted through its picker instead. The last column opts out
        // entirely, to prove filterValueType="none" is honored on p:columns too.
        columns.add(new ColumnModel("ID", "id", "gt", "numeric"));
        columns.add(new ColumnModel("last name", "lastName", "contains", "text"));
        columns.add(new ColumnModel("first name", "firstName", "contains", "none"));
    }

    /**
     * Explicit fixed-pattern converter (not the locale-dependent default) so a date typed or picked as text
     * parses unambiguously - same rationale as DataTable051.
     */
    public DateTimeConverter getReviewDateConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd");
        converter.setType("localDate");
        return converter;
    }

    @Value
    public static class ColumnModel implements Serializable {
        private static final long serialVersionUID = 1L;
        private String header;
        private String property;
        private String filterMatchMode;
        private String filterValueType;
    }
}
