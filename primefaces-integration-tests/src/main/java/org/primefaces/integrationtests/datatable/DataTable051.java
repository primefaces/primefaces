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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.convert.DateTimeConverter;
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

        // #7427 relative-date match modes ("today", "this week", "last N days", ...) are computed against
        // LocalDate.now() at filter time, so the fixture dates are computed the same way here rather than
        // hardcoded - hardcoded literals would silently go stale (and desync from DataTable051Test's mirrored
        // copy) the day after this branch was written. +/-1 week/month/year is always safely within the
        // corresponding "last/next" bucket regardless of where "today" falls within its own week/month/year.
        LocalDate today = LocalDate.now();
        employees.get(0).setReviewDate(today);                  // id 1, Mike Master - today
        employees.get(1).setReviewDate(today.minusDays(1));      // id 2, Susan Pepper - yesterday
        employees.get(3).setReviewDate(today.plusDays(1));       // id 4, Chris Clark - tomorrow
        employees.get(4).setReviewDate(today.minusWeeks(1));     // id 5, James Bush - last week
        employees.get(5).setReviewDate(today.plusWeeks(1));      // id 6, Trish Mayer - next week
        employees.get(6).setReviewDate(today.minusMonths(1));    // id 11, Margret Johnson - last month
        employees.get(7).setReviewDate(today.plusMonths(1));     // id 533, Mary March - next month

        // #7427 "is (not) empty" / "is (not) null" need a null and a blank lastName to be distinguishable;
        // Nolan's reviewDate is also left null for the same reason on the date column
        employees.add(Employee.builder().id(900).firstName("Nolan").lastName(null)
                .birthDate(LocalDate.of(1975, 6, 15)).build());
        employees.add(Employee.builder().id(901).firstName("Blanche").lastName("")
                .birthDate(LocalDate.of(1985, 9, 20)).reviewDate(today.minusDays(100)).build());

        // #7427 "last/next year" - a full year offset is always safely within the corresponding bucket,
        // unlike a quarter offset (which can land 2 quarters back/forward depending on where "today" falls
        // within its own quarter), so these get their own dedicated rows rather than reusing an existing one
        employees.add(Employee.builder().id(902).firstName("Yolanda").lastName("Young")
                .reviewDate(today.minusYears(1)).build());
        employees.add(Employee.builder().id(903).firstName("Zack").lastName("Zimmer")
                .reviewDate(today.plusYears(1)).build());

        // #7427 "last/next N minutes/hours" need checkInTime (bare LocalTime) and lastLoginDateTime (full
        // LocalDateTime) values at known offsets from "now", computed the same way as reviewDate above so
        // they never go stale. lastLoginDateTime here overrides EmployeeService's hardcoded 2021 fixture
        // dates on THIS instance's own copy only - the shared, application-scoped service list is untouched.
        // Truncated to whole seconds so an "equals" filter round-trips losslessly through the "HH:mm:ss" /
        // "yyyy-MM-dd HH:mm:ss" converter pattern below, which has no sub-second precision to parse back.
        LocalTime now = LocalTime.now().withNano(0);
        LocalDateTime nowDateTime = LocalDateTime.now().withNano(0);
        employees.get(0).setCheckInTime(now.minusMinutes(10));           // id 1 - within last 30min / last 2h
        employees.get(0).setLastLoginDateTime(nowDateTime.minusMinutes(10));
        employees.get(1).setCheckInTime(now.minusHours(1));              // id 2 - outside last 30min, within last 2h
        employees.get(1).setLastLoginDateTime(nowDateTime.minusHours(1));
        employees.get(3).setCheckInTime(now.plusMinutes(10));            // id 4 - within next 30min / next 2h
        employees.get(3).setLastLoginDateTime(nowDateTime.plusMinutes(10));
        employees.get(4).setCheckInTime(now.plusHours(3));               // id 5 - outside next 2h
        employees.get(4).setLastLoginDateTime(nowDateTime.plusHours(3));
    }

    public DateTimeConverter getReviewDateConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd");
        converter.setType("localDate");
        return converter;
    }

    public DateTimeConverter getCheckInTimeConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("HH:mm:ss");
        converter.setType("localTime");
        return converter;
    }

    public DateTimeConverter getLastLoginDateTimeConverter() {
        DateTimeConverter converter = new DateTimeConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        converter.setType("localDateTime");
        return converter;
    }
}
