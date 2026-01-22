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

import org.primefaces.event.RowEditEvent;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable007Lazy implements Serializable {

    private static final long serialVersionUID = 2990776186773208159L;

    protected ProgrammingLanguageLazyDataModel lazyDataModel;

    @Inject
    private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        lazyDataModel = new ProgrammingLanguageLazyDataModel();
    }

    public void onRowEdit(RowEditEvent<ProgrammingLanguage> event) {
        FacesMessage msg = new FacesMessage("ProgrammingLanguage Edited", Integer.toString(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent<ProgrammingLanguage> event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", Integer.toString(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onAddNew() {
        ProgrammingLanguage entity2Add = service.create(99, "Smalltalk");
        lazyDataModel.getLangs().add(entity2Add);
        FacesMessage msg = new FacesMessage("New Language added", entity2Add.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void submit() {

    }
}
