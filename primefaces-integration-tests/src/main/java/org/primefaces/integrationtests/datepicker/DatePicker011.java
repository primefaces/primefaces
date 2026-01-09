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
package org.primefaces.integrationtests.datepicker;

import org.primefaces.event.DateViewChangeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.datepicker.DateMetadataModel;
import org.primefaces.model.datepicker.DefaultDateMetadata;
import org.primefaces.model.datepicker.DefaultDateMetadataModel;
import org.primefaces.model.datepicker.LazyDateMetadataModel;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DatePicker011 implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date0, date1, date2, date3;
    private LocalDate date4, date5, date6, date7;
    private DateMetadataModel model;
    private DateMetadataModel modelLazy;

    @PostConstruct
    public void init() {
        DefaultDateMetadata disabled = DefaultDateMetadata.builder().disabled(true).styleClass("tst-disabled").build();
        DefaultDateMetadata begin = DefaultDateMetadata.builder().styleClass("tst-begin").build();
        DefaultDateMetadata end = DefaultDateMetadata.builder().styleClass("tst-end").build();

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        model = new DefaultDateMetadataModel();
        model.add(start.minusMonths(1).plusDays(1), disabled); // 2nd of previous month
        model.add(start, disabled); // 1st of this month
        model.add(start.plusMonths(1).plusDays(2), disabled); // 3rd of next month
        model.add(start.plusDays(1), begin); // 2nd of this month
        model.add(start.plusDays(2), end); //3rd of this month

        modelLazy = new LazyDateMetadataModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public void loadDateMetadata(LocalDate d1, LocalDate d2) {
                LocalDate start = LocalDate.now().withDayOfMonth(1);
                add(start.minusMonths(1).plusDays(1), disabled); // 2nd of previous month
                add(start, disabled); // 1st of this month
                add(start.plusMonths(1).plusDays(2), disabled); // 3rd of next month
                add(start.plusDays(1), begin); // 2nd of this month
                add(start.plusDays(2), end); //3rd of this month
            }
        };
    }

    public void onDateSelect(SelectEvent event) {
        String summary = "dateSelect " + event.getObject();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onViewChange(DateViewChangeEvent event) {
        String summary = "viewChange Year: " + event.getYear() + ", month: " + (event.getMonth() + 1);
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onClose(AjaxBehaviorEvent event) {
        String summary = "close";
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    public void onSelect(AjaxBehaviorEvent event) {
        // do nothing
    }
}