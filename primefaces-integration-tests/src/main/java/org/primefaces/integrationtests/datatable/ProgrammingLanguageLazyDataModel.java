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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.MatchMode;
import org.primefaces.model.SortMeta;

public class ProgrammingLanguageLazyDataModel extends LazyDataModel<ProgrammingLanguage> {

    private static final long serialVersionUID = -3415081263308946252L;

    protected List<ProgrammingLanguage> langs;

    public ProgrammingLanguageLazyDataModel() {
        langs = new ArrayList<>();
        for (int i = 1; i <= 75; i++) {
            langs.add(new ProgrammingLanguage(i, "Language " + i, 1990 + (i % 10),
                    ((i % 2) == 0) ? ProgrammingLanguage.ProgrammingLanguageType.COMPILED : ProgrammingLanguage.ProgrammingLanguageType.INTERPRETED));
        }
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        List<ProgrammingLanguage> langsFiltered = sortAndFilterInternal(null, filterBy);
        return (int) langsFiltered.size();
    }

    @Override
    public List<ProgrammingLanguage> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        List<ProgrammingLanguage> langsFiltered = sortAndFilterInternal(sortBy, filterBy);
        setRowCount(langsFiltered.size());

        return langsFiltered.stream()
                    .skip(first).limit(pageSize)
                    .collect(Collectors.toList());
    }

    protected List<ProgrammingLanguage> sortAndFilterInternal(Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Stream<ProgrammingLanguage> langsStream = langs.stream();

        if (filterBy != null && !filterBy.isEmpty()) {
            for (FilterMeta meta : filterBy.values()) {
                if (meta.getFilterValue() != null) {
                    langsStream = langsStream.filter(lang -> {
                        if (meta.getField().equals("firstAppeared") && meta.getMatchMode() == MatchMode.GREATER_THAN_EQUALS) {
                            int filterValueInt = Integer.parseInt((String) meta.getFilterValue());
                            return lang.getFirstAppeared() >= filterValueInt;
                        }
                        if (meta.getField().equals("name")) {
                            return lang.getName().contains((String) meta.getFilterValue());
                        }
                        if (meta.getField().equals("type")) {
                            Collection<ProgrammingLanguage.ProgrammingLanguageType> filterValues = null;

                            if (meta.getFilterValue() instanceof String[]) {  // Mojarra
                                filterValues = Arrays.stream((String[]) meta.getFilterValue())
                                        .map(ProgrammingLanguage.ProgrammingLanguageType::valueOf)
                                        .collect(Collectors.toSet());
                            }
                            else if (meta.getFilterValue() instanceof Object[]) { //MyFaces
                                filterValues = Arrays.stream((Object[]) meta.getFilterValue())
                                        .map(f -> ProgrammingLanguage.ProgrammingLanguageType.valueOf(f.toString()))
                                        .collect(Collectors.toSet());
                            }
                            else {
                                filterValues = (Collection<ProgrammingLanguage.ProgrammingLanguageType>) meta.getFilterValue();
                            }
                            return filterValues.contains(lang.getType());
                        }
                        return true; //TODO: add additional implementation when required
                    });
                }
            }
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            for (SortMeta meta : sortBy.values()) {
                langsStream = langsStream.sorted(new ProgrammingLanguageLazySorter(meta));
            }
        }

        return langsStream.collect(Collectors.toList());
    }

    @Override
    public ProgrammingLanguage getRowData(String rowKey) {
        int rowKeyNumeric = Integer.parseInt(rowKey);
        return langs.stream().filter(lang -> lang.getId() == rowKeyNumeric).findFirst().get();
    }

    @Override
    public String getRowKey(ProgrammingLanguage object) {
        return object.getId().toString();
    }

    public List<ProgrammingLanguage> getLangs() {
        return langs;
    }

    public void delete(ProgrammingLanguage language) {
        langs.remove(language);
    }
}
