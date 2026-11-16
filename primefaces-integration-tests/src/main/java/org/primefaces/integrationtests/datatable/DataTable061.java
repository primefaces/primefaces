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
package org.primefaces.integrationtests.datatable;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable061 implements Serializable {

    private static final long serialVersionUID = 1L;

    private FieldKeyedLazyDataModel lazyDataModel = new FieldKeyedLazyDataModel();
    private List<ProgrammingLanguage> selectedProgLanguages;

    public void submit() {
        if (selectedProgLanguages != null) {
            FacesMessage msg = new FacesMessage("Selected ProgrammingLanguage(s)", selectedProgLanguages.stream()
                        .sorted(Comparator.comparing(ProgrammingLanguage::getId))
                        .map(lang -> lang.getId().toString())
                        .collect(Collectors.joining(",")));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /**
     * Looks the filter up by field, like the showcase does ({@code filterBy.get("name")}).
     */
    public static class FieldKeyedLazyDataModel extends ProgrammingLanguageLazyDataModel {

        private static final long serialVersionUID = 1L;

        @Override
        protected List<ProgrammingLanguage> sortAndFilterInternal(Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
            FilterMeta name = filterBy == null ? null : filterBy.get("name");
            return super.sortAndFilterInternal(sortBy, name == null ? Collections.emptyMap() : Map.of("name", name));
        }
    }
}
