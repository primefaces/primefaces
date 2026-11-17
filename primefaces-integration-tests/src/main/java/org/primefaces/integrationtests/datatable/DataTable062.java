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

import org.primefaces.PrimeFaces;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable062 implements Serializable {

    @Serial private static final long serialVersionUID = 7712603538612473013L;

    private List<ProgrammingLanguage> progLanguages;

    /**
     * Own lists, as the in-memory sort of each table reorders its list.
     */
    private List<ProgrammingLanguage> elFieldLanguages;

    private List<ProgrammingLanguage> repeatedLanguages;

    private List<ProgrammingLanguage> otherRepeatedLanguages;

    private List<ProgrammingLanguage> userSortByLanguages;

    private List<ProgrammingLanguage> toggledUserSortByLanguages;

    /**
     * Which headerRow is rendered: <code>none</code>, <code>year</code> or <code>type</code>.
     */
    private String mode = "none";

    /**
     * The field of the headerRow which is only rendered once it is set.
     */
    private String groupField;

    private SortRecordingLazyDataModel lazyModel = new SortRecordingLazyDataModel();

    /**
     * The number of header row {@link SortMeta} in the last {@link SortEvent}.
     */
    private long sortEventHeaderRows = -1;

    @Inject
    private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        progLanguages = service.getLangs();
        elFieldLanguages = service.getLangs();
        repeatedLanguages = service.getLangs();
        otherRepeatedLanguages = service.getLangs();
        userSortByLanguages = service.getLangs();
        toggledUserSortByLanguages = service.getLangs();
    }

    public void onSort(SortEvent event) {
        sortEventHeaderRows = event.getSortBy().values().stream().filter(SortMeta::isHeaderRow).count();
    }

    /**
     * The field of a headerRow which is never rendered, so it must never be evaluated.
     */
    public String getFailingField() {
        throw new IllegalStateException("a headerRow which is not rendered must not be evaluated");
    }

    /**
     * A user sortBy for the field of a headerRow.
     */
    public SortMeta getYearDescending() {
        // with the priority of a headerRow, otherwise a column sort would take precedence over the grouping
        return SortMeta.builder().field("firstAppeared").order(SortOrder.DESCENDING).priority(SortMeta.MAX_PRIORITY).build();
    }

    public void clearTableState() {
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        PrimeFaces.current().multiViewState().clearAll(viewId, true);
    }

    /**
     * Records the sort fields of the last load, to check which {@link SortMeta} the table passes to a lazy model.
     */
    public static class SortRecordingLazyDataModel extends ProgrammingLanguageLazyDataModel {

        @Serial private static final long serialVersionUID = 1L;

        private String lastSortFields = "";

        @Override
        public List<ProgrammingLanguage> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
            lastSortFields = String.join(",", sortBy.keySet());
            return super.load(first, pageSize, sortBy, filterBy);
        }

        public String getLastSortFields() {
            return lastSortFields;
        }
    }
}
