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

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable004 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private List<ProgrammingLanguage> progLanguages;
    private ProgrammingLanguage selectedProgLanguage;

    @Inject
    private ProgrammingLanguageService service;

    private boolean partialDisabledSelection = false;

    @PostConstruct
    public void init() {
        progLanguages = service.getLangs();
    }

    public void onRowSelect(SelectEvent<ProgrammingLanguage> event) {
        TestUtils.addMessage("ProgrammingLanguage Selected", event.getObject().getId() + " - " + event.getObject().getName());
    }

    public void onRowUnselect(UnselectEvent<ProgrammingLanguage> event) {
        TestUtils.addMessage("ProgrammingLanguage Unselected", event.getObject().getId() + " - " + event.getObject().getName());
    }

    public void unselectRow() {
        selectedProgLanguage = null;
        TestUtils.addMessage("ProgrammingLanguage unselected via backing bean", "");
    }

    public void submit() {
        if (selectedProgLanguage != null) {
            TestUtils.addMessage("Selected ProgrammingLanguage", selectedProgLanguage.getId() + " - " + selectedProgLanguage.getName());
        }
        else {
            TestUtils.addMessage("NO ProgrammingLanguage selected", "");
        }
    }

    public boolean isSelectionDisabled(ProgrammingLanguage lang) {
        if (isPartialDisabledSelection() && (lang.getId() % 2) == 0) {
            return true;
        }
        return false;
    }
}
