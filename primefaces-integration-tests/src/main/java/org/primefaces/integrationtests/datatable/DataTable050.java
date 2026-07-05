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

import org.primefaces.integrationtests.jpa.TrackingJPALazyDataModel;
import org.primefaces.integrationtests.jpa.entity.ProgrammingLanguageJpaEntity;
import org.primefaces.integrationtests.jpa.service.ProgrammingLanguageJpaService;

import java.io.Serializable;
import java.util.ArrayList;
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
public class DataTable050 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LazyDataModelCallTracker lazyDataModelCallTracker = new LazyDataModelCallTracker();
    private TrackingJPALazyDataModel lazyDataModel;
    private List<ProgrammingLanguageJpaEntity> selectedProgLanguages;
    private boolean selectionPageOnly = true;

    @Inject
    private ProgrammingLanguageJpaService jpaService;

    @PostConstruct
    public void init() {
        seedData();
        lazyDataModel = new TrackingJPALazyDataModel(jpaService.entityManagerSupplier(), "dataTable050");
    }

    private void seedData() {
        List<ProgrammingLanguageJpaEntity> languages = new ArrayList<>();
        languages.add(new ProgrammingLanguageJpaEntity("Java", 1995, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.COMPILED));
        languages.add(new ProgrammingLanguageJpaEntity("C#", 2000, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.COMPILED));
        languages.add(new ProgrammingLanguageJpaEntity("JavaScript", 1995, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.INTERPRETED));
        languages.add(new ProgrammingLanguageJpaEntity("TypeScript", 2012, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.INTERPRETED));
        languages.add(new ProgrammingLanguageJpaEntity("Python", 1990, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.INTERPRETED));
        languages.add(new ProgrammingLanguageJpaEntity("C++", 1985, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.COMPILED));
        languages.add(new ProgrammingLanguageJpaEntity("Go", 2009, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.COMPILED));
        languages.add(new ProgrammingLanguageJpaEntity("Rust", 2010, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.COMPILED));
        languages.add(new ProgrammingLanguageJpaEntity("Ruby", 1995, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.INTERPRETED));
        languages.add(new ProgrammingLanguageJpaEntity("PHP", 1995, ProgrammingLanguageJpaEntity.ProgrammingLanguageType.INTERPRETED));
        jpaService.saveAll(languages);
    }

    public void submit() {
        if (selectedProgLanguages != null) {
            FacesMessage msg = new FacesMessage("Selected ProgrammingLanguage(s)", selectedProgLanguages.stream()
                        .sorted(Comparator.comparing(ProgrammingLanguageJpaEntity::getId))
                        .map(lang -> lang.getId().toString())
                        .collect(Collectors.joining(",")));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void toggleSelectPageOnly() {
        setSelectionPageOnly(!isSelectionPageOnly());
    }

    public LazyDataModelCallTracker getLazyDataModelCallTracker() {
        return lazyDataModelCallTracker;
    }
}
