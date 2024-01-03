/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Data;
import org.primefaces.event.SelectEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.model.DefaultLazyDataModel;

@Named
@ViewScoped
@Data
public class DataTable002 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    protected List<ProgrammingLanguage> programmingLanguages;
    protected ProgrammingLanguageLazyDataModel lazyDataModel;
    protected DefaultLazyDataModel<ProgrammingLanguage> reflectionLazyDataModel;
    protected ProgrammingLanguage selectedProgrammingLanguage;

    @PostConstruct
    public void init() {
        programmingLanguages = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            programmingLanguages.add(new ProgrammingLanguage(i, "Language " + i, 1990 + (i % 10),
                    ((i % 2) == 0) ? ProgrammingLanguage.ProgrammingLanguageType.COMPILED : ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));
        }
        lazyDataModel = new ProgrammingLanguageLazyDataModel();
        reflectionLazyDataModel = DefaultLazyDataModel.<ProgrammingLanguage>builder()
                .valueSupplier(() -> programmingLanguages)
                .rowKeyProvider(ProgrammingLanguage::getId)
                .build();
    }

    public void onRowSelect(SelectEvent<ProgrammingLanguage> event) {
        TestUtils.addMessage("ProgrammingLanguage Selected", event.getObject().getId() + " - " + event.getObject().getName());
    }

    public void delete(ProgrammingLanguage language) {
        lazyDataModel.delete(language);
        if (programmingLanguages != null) {
            programmingLanguages.remove(language);
        }
        TestUtils.addMessage("ProgrammingLanguage Deleted", language.getId() + " - " + language.getName());
    }

    public void submit() {
        if (selectedProgrammingLanguage != null) {
            TestUtils.addMessage("Selected ProgrammingLanguage", selectedProgrammingLanguage.getId().toString());
        }
    }

    public ProgrammingLanguage.ProgrammingLanguageType[] getTypes() {
        return ProgrammingLanguage.ProgrammingLanguageType.values();
    }
}
