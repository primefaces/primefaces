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
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable051 implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Employee> employees;
    private List<Employee> filteredEmployees;

    @Inject
    private EmployeeService service;

    @PostConstruct
    public void init() {
        // copy (don't mutate the shared, application-scoped EmployeeService list used by other DataTable tests) -
        // each element is still a fresh object from this specific getEmployees() call, so setting fields below
        // (e.g. #active) is safe and doesn't leak into any other test that independently calls getEmployees()
        employees = new ArrayList<>(service.getEmployees());

        // #7427 "true"/"false"/"is (not) null" boolean modes need a mix of true, false, and untouched (null)
        // employees to be distinguishable: ids 1, 4, 11 -> true; ids 2, 5, 533 -> false; ids 3, 6 -> left null
        employees.get(0).setActive(true);   // id 1, Mike Master
        employees.get(1).setActive(false);  // id 2, Susan Pepper
        employees.get(3).setActive(true);   // id 4, Chris Clark
        employees.get(4).setActive(false);  // id 5, James Bush
        employees.get(6).setActive(true);   // id 11, Margret Johnson
        employees.get(7).setActive(false);  // id 533, Mary March

        // #7427 "is (not) empty" / "is (not) null" need a null and a blank lastName to be distinguishable
        employees.add(Employee.builder().id(900).firstName("Nolan").lastName(null)
                .birthDate(LocalDate.of(1975, 6, 15)).build());
        employees.add(Employee.builder().id(901).firstName("Blanche").lastName("")
                .birthDate(LocalDate.of(1985, 9, 20)).build());
    }
}
