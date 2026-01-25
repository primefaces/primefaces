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

import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgrammingLanguageLazyDataModelViewBeanReference extends ProgrammingLanguageLazyDataModel {

    // see https://github.com/primefaces/primefaces/issues/9349

    private DataTable039 dataTable039;

    public ProgrammingLanguageLazyDataModelViewBeanReference(DataTable039 dataTable039) {
        this.dataTable039 = dataTable039;
    }

    @Override
    public List<ProgrammingLanguage> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Stream<ProgrammingLanguage> langsStream = langs.stream();

        if (dataTable039.getSelectedProgrammingLanguageTypes().size() > 0) {
            Set<ProgrammingLanguage.ProgrammingLanguageType> filterValuesSet = dataTable039.getSelectedProgrammingLanguageTypes().stream()
                    .collect(Collectors.toSet());

            langsStream = langsStream.filter(lang -> {
                return filterValuesSet.contains(lang.getType());
            });
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            for (SortMeta meta : sortBy.values()) {
                langsStream = langsStream.sorted(new ProgrammingLanguageLazySorter(meta));
            }
        }

        return langsStream.collect(Collectors.toList());
    }
}
