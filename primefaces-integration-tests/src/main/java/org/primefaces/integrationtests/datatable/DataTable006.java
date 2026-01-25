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

import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
public class DataTable006 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private ProgrammingLanguageLazyDataModel lazyDataModel;
    private List<ProgrammingLanguage> progLanguages;
    private List<ProgrammingLanguage> filteredProgLanguages;
    private List<ProgrammingLanguage> selectedProgLanguages;
    private boolean selectionPageOnly = true;
    private boolean lazy = false;
    private boolean paginator = true;

    @Inject
    private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        progLanguages = service.getLangs();
        lazyDataModel = new ProgrammingLanguageLazyDataModel();
    }

    public void submit() {
        if (selectedProgLanguages != null) {
            FacesMessage msg = new FacesMessage("Selected ProgrammingLanguage(s)", selectedProgLanguages.stream()
                        .sorted(Comparator.comparing(ProgrammingLanguage::getId))
                        .map(lang -> ((Integer) lang.getId()).toString())
                        .collect(Collectors.joining(",")));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void toggleSelectPageOnly() {
        setSelectionPageOnly(!isSelectionPageOnly());
    }

    public void toggleLazyMode() {
        setLazy(!isLazy());
    }

    public void togglePaginator() {
        setPaginator(!isPaginator());
    }

    public void unselectRows() {
        selectedProgLanguages.clear();
        TestUtils.addMessage("ProgrammingLanguages unselected via backing bean", "");
    }
}
