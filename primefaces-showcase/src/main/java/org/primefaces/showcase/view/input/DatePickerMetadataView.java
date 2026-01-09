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
package org.primefaces.showcase.view.input;

import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.model.datepicker.DateMetadataModel;
import org.primefaces.model.datepicker.DefaultDateMetadata;
import org.primefaces.model.datepicker.DefaultDateMetadataModel;
import org.primefaces.model.datepicker.LazyDateMetadataModel;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DatePickerMetadataView implements Serializable {

    private LocalDate date1;
    private LocalDate date2;
    private LocalDate date3;
    private LocalDate date4;
    private LocalDate date5;
    private LocalDate date6;
    private final DateMetadataModel model;
    private final DateMetadataModel modelLazy;
    private final DateMetadataModel modelEnabledDaysLazy;

    public DatePickerMetadataView() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        DefaultDateMetadata metadataDisabled = DefaultDateMetadata.builder().disabled(true).build();
        DefaultDateMetadata metadataStart = DefaultDateMetadata.builder().styleClass("start").build();
        DefaultDateMetadata metadataDeadline = DefaultDateMetadata.builder().styleClass("deadline").build();

        model = new DefaultDateMetadataModel();
        model.add(start.minusMonths(1), metadataDisabled);
        model.add(start.plusDays(start.getMonthValue() + 3), metadataStart);
        model.add(start.plusDays(start.getMonthValue() + 6), metadataDisabled);
        model.add(start.plusDays(start.getMonthValue() + 9), metadataDeadline);
        model.add(start.plusMonths(1), metadataDisabled);

        modelLazy = new LazyDateMetadataModel() {
            @Override
            public void loadDateMetadata(LocalDate start, LocalDate end) {
                add(start.plusDays(start.getMonthValue() + 2), metadataStart);
                add(start.plusDays(start.getMonthValue() + 5), metadataDisabled);
                add(start.plusDays(start.getMonthValue() + 8), metadataDeadline);
            }
        };

        // start and deadline have to be also enabled
        DefaultDateMetadata metadataStartEnabled = DefaultDateMetadata.builder().styleClass("start").enabled(true).build();
        DefaultDateMetadata metadataEnabled = DefaultDateMetadata.builder().enabled(true).build();
        DefaultDateMetadata metadataDeadlineEnabled = DefaultDateMetadata.builder().styleClass("deadline").enabled(true).build();
        modelEnabledDaysLazy = new LazyDateMetadataModel() {
            @Override
            public void loadDateMetadata(LocalDate start, LocalDate end) {
                add(start.plusDays(start.getMonthValue() + 2), metadataStartEnabled);
                // enable just five days since start
                for (int i = 1; i < 5; i++) {
                    add(start.plusDays(start.getMonthValue() + 2 + i), metadataEnabled);
                }

                add(start.plusDays(start.getMonthValue() + 6), metadataDeadlineEnabled);
            }
        };
    }

    public void onViewChange(DateViewChangeEvent event) {
        String summary = "Year: " + event.getYear() + ", month: " + event.getMonth();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public LocalDate getDate1() {
        return date1;
    }

    public void setDate1(LocalDate date1) {
        this.date1 = date1;
    }

    public LocalDate getDate2() {
        return date2;
    }

    public void setDate2(LocalDate date2) {
        this.date2 = date2;
    }

    public LocalDate getDate3() {
        return date3;
    }

    public void setDate3(LocalDate date3) {
        this.date3 = date3;
    }

    public LocalDate getDate4() {
        return date4;
    }

    public void setDate4(LocalDate date4) {
        this.date4 = date4;
    }

    public LocalDate getDate5() {
        return date5;
    }

    public void setDate5(LocalDate date5) {
        this.date5 = date5;
    }

    public LocalDate getDate6() {
        return date6;
    }

    public void setDate6(LocalDate date6) {
        this.date6 = date6;
    }

    public DateMetadataModel getModel() {
        return model;
    }

    public DateMetadataModel getModelLazy() {
        return modelLazy;
    }

    public DateMetadataModel getModelEnabledDaysLazy() {
        return modelEnabledDaysLazy;
    }
}
