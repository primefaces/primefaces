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
import org.primefaces.model.datepicker.DefaultDateMetadata;
import org.primefaces.model.datepicker.LazyDateMetadataModel;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DatePicker012 implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date1;
    private LocalDate date2;
    private SimpleLazyDateMetadataModel lazyModel1;
    private SimpleLazyDateMetadataModel lazyModel2;

    @PostConstruct
    public void init() {
        lazyModel1 = new SimpleLazyDateMetadataModel();
        lazyModel2 = new SimpleLazyDateMetadataModel();
    }

    public void onDateSelect(SelectEvent event) {
        if ("datepicker1".equals(event.getComponent().getId())) {
            lazyModel2.setDisabledDate(date1);
        }
        else {
            lazyModel1.setDisabledDate(date2);
        }
    }

    public void onViewChange(DateViewChangeEvent event) {
        // nothing to do
    }

    private static final class SimpleLazyDateMetadataModel extends LazyDateMetadataModel {
        private static final long serialVersionUID = 1L;
        private static final DefaultDateMetadata DISABLED = DefaultDateMetadata.builder()
                .disabled(true).styleClass("tst-disabled").build();
        private LocalDate disabledDate;
        @Override
        public void loadDateMetadata(LocalDate start, LocalDate end) {
            if (disabledDate != null) {
                add(disabledDate, DISABLED);
            }
        }
        public void setDisabledDate(LocalDate disabledDate) {
            this.disabledDate = disabledDate;
        }
    }
}