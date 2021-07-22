/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.integrationtests.general.utilities.TestUtils;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable009 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private List<ProgrammingLanguage> progLanguages;
    private List<ProgrammingLanguage> filteredProgLanguages;
    private List<Integer> allYearsSince1990;

    @Inject
    private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        progLanguages = service.getLangs();

        allYearsSince1990 = new ArrayList<>();
        for (int year = 1990; year <= LocalDate.now().getYear(); year++) {
            allYearsSince1990.add(year);
        }
    }

    public void filterListener(FilterEvent filterEvent) {
        // show actual filter as message
        filterEvent.getFilterBy().values().stream()
                    .filter(filterMeta -> filterMeta.getFilterValue() != null)
                    .forEach(filterMeta ->
                            TestUtils.addMessage("FilterValue for " + filterMeta.getField(), filterMeta.getFilterValue().toString()));

        // show actual filtered values
        DataTable dataTable = (DataTable) filterEvent.getSource();
        String filterValuesFlat = "null";
        if (dataTable.getFilteredValue() != null) {
            List<ProgrammingLanguage> filteredProgLanguagesAtEvent = (List<ProgrammingLanguage>) dataTable.getFilteredValue();

            filterValuesFlat = filteredProgLanguagesAtEvent.stream().map(ProgrammingLanguage::getName).collect(Collectors.joining(","));
        }

        TestUtils.addMessage("FilteredValue(s)", filterValuesFlat);
    }

    public void reportFilteredProgLanguages() {
        String filterValuesFlat = "null";
        if (filteredProgLanguages != null) {
            filterValuesFlat = filteredProgLanguages.stream().map(ProgrammingLanguage::getName).collect(Collectors.joining(","));
        }

        TestUtils.addMessage("FilteredValue(s)", filterValuesFlat);
    }

    public List<Integer> firstAppearedYearsWithProgLanguages() {
        return progLanguages.stream().map(p -> p.getFirstAppeared()).distinct().sorted().collect(Collectors.toList());
    }

    public void addJsfImplMessage()  {
        TestUtils.addJsfImplMessage();
    }
}
