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

import org.primefaces.component.datatable.DataTable;
import org.primefaces.util.LangUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable021 implements Serializable {

    private static final long serialVersionUID = 3301604336239706575L;

    private List<ProgrammingLanguage> progLanguages;
    private List<ProgrammingLanguage> filteredProgLanguages;

    @Inject
    private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        progLanguages = service.getLangs();
    }

    public void resetTable() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:datatable");
        dataTable.reset();

        progLanguages = service.getLangs(); //progLanguages may have been sorted from DataTable
    }

    public void removeV1(ProgrammingLanguage programmingLanguage) {
        this.progLanguages.remove(programmingLanguage);
        if (this.filteredProgLanguages != null) {
            this.filteredProgLanguages.remove(programmingLanguage); // see https://github.com/primefaces/primefaces/issues/7336
        }

//        updateDataTableFilterWorkaround(); // work-around for PF 10.0.0
    }

    public void removeV2(ProgrammingLanguage programmingLanguage) {
        this.progLanguages.remove(programmingLanguage);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:datatable");
        dataTable.filterAndSort();

//        updateDataTableFilterWorkaround(); // work-around for PF 10.0.0
    }

    public void add() {
        ProgrammingLanguage programmingLanguage = new ProgrammingLanguage();
        programmingLanguage.setId(999);
        programmingLanguage.setName("Java " + LocalDateTime.now().toString());
        programmingLanguage.setFirstAppeared(2020);
        programmingLanguage.setType(ProgrammingLanguage.ProgrammingLanguageType.COMPILED);

        this.progLanguages.add(programmingLanguage);
        if (this.filteredProgLanguages != null) {
            this.filteredProgLanguages.add(programmingLanguage); // see https://github.com/primefaces/primefaces/issues/7336
        }

//        updateDataTableFilterWorkaround(); // work-around for PF 10.0.0
    }

//    private void updateDataTableFilterWorkaround() {
//        DataTable dataTable = (DataTable)FacesContext.getCurrentInstance().getViewRoot().findComponent("form:datatable");
//        FilterFeature filterFeature = (FilterFeature) dataTable.getFeature(DataTableFeatureKey.FILTER);
//        filterFeature.filter(FacesContext.getCurrentInstance(), dataTable);
//
//        SortFeature sortFeature = (SortFeature) dataTable.getFeature(DataTableFeatureKey.SORT);
//        sortFeature.sort(FacesContext.getCurrentInstance(), dataTable);
//    }

    public void deleteJavaProgrammingLanguage() {
        ProgrammingLanguage java = this.progLanguages.stream().filter(l -> l.getName().equals("Java")).findFirst().get();
        this.progLanguages.remove(java);

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:datatable");
        dataTable.filterAndSort();
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isBlank(filterText)) {
            return true;
        }

        ProgrammingLanguage programmingLanguage = (ProgrammingLanguage) value;

        return programmingLanguage.getName().toLowerCase().contains(filterText)
                || programmingLanguage.getType().toString().toLowerCase().contains(filterText);
    }
}
