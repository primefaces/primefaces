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
package org.primefaces.integrationtests.datepicker;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DatePicker019 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private LocalDate minDate;
    private LocalDate maxDate;
    private LocalDateTime minDateTime;
    private List<LocalDate> disabledDates;
    private List<LocalDate> enabledDates;
    private List<Integer> disabledDays;

    private LocalDate date;
    private LocalDate validatedDate;
    private LocalDate enabledDate;
    private List<LocalDate> range;
    private LocalDateTime dateTime;
    private LocalDate requiredDate;
    private List<LocalDate> week;
    private YearMonth month;

    @PostConstruct
    public void init() {
        minDate = LocalDate.of(2020, 8, 10);
        maxDate = LocalDate.of(2020, 8, 25);
        minDateTime = LocalDateTime.of(2020, 8, 20, 10, 0);
        disabledDates = List.of(LocalDate.of(2020, 8, 20));
        enabledDates = List.of(LocalDate.of(2020, 8, 20));
        disabledDays = List.of(0, 3); // Sunday, Wednesday

        date = LocalDate.of(2020, 8, 12);
        validatedDate = date;
        enabledDate = LocalDate.of(2020, 8, 20);
        dateTime = LocalDateTime.of(2020, 8, 20, 12, 0);
        requiredDate = date;
        week = List.of(LocalDate.of(2020, 8, 2), LocalDate.of(2020, 8, 8));
        month = YearMonth.of(2020, 8);
    }
}
