/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.util.CalendarUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class EmployeeService {

    public List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(Employee.builder().id(1).firstName("Mike").lastName("Master").birthDate(LocalDate.of(1970, 1, 1))
                .salary(5000).role(Employee.Role.MANAGER).lastLoginDateTime(LocalDateTime.of(2021, 1, 10, 12, 23)).build());
        employees.add(Employee.builder().id(2).firstName("Susan").lastName("Pepper").birthDate(LocalDate.of(1980, 5, 1))
                .salary(2500).role(Employee.Role.HR).lastLoginDateTime(LocalDateTime.of(2021, 1, 10, 0, 0)).build());
        employees.add(Employee.builder().id(3).firstName("Alfred").lastName("Paul").birthDate(LocalDate.of(1990, 7, 31))
                .salary(null).role(Employee.Role.DEVELOPER).lastLoginDateTime(LocalDateTime.of(2021, 1, 10, 14, 47)).build());
        employees.add(Employee.builder().id(4).firstName("Chris").lastName("Clark").birthDate(LocalDate.of(1982, 8, 14))
                .salary(3000).role(Employee.Role.DEVELOPER).lastLoginDateTime(LocalDateTime.of(2021, 1, 10, 23, 1)).build());
        employees.add(Employee.builder().id(5).firstName("James").lastName("Bush").birthDate(LocalDate.of(2001, 3, 5))
                .salary(2200).role(Employee.Role.DEVELOPER).lastLoginDateTime(LocalDateTime.of(2021, 1, 10, 2, 13)).build());
        employees.add(Employee.builder().id(6).firstName("Trish").lastName("Mayer").birthDate(LocalDate.of(1970, 1, 5))
                .salary(2300).role(Employee.Role.QS).lastLoginDateTime(null).build());
        employees.add(Employee.builder().id(11).firstName("Margret").lastName("Johnson").birthDate(LocalDate.of(1969, 12, 31))
                .salary(2600).role(Employee.Role.FINANCE).lastLoginDateTime(LocalDateTime.of(2021, 3, 28, 23, 59)).build());
        employees.add(Employee.builder().id(533).firstName("Mary").lastName("March").birthDate(LocalDate.of(1980, 5, 10))
                .salary(2800).role(Employee.Role.SALES).lastLoginDateTime(LocalDateTime.of(2021, 2, 10, 16, 31)).build());

        employees.forEach(e -> {
            if (e.getLastLoginDateTime() != null) {
                e.setLastLoginDate(CalendarUtils.convertLocalDateTime2Date(e.getLastLoginDateTime()));
            }
        });

        return employees;
    }
}
