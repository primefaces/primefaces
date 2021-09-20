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

import org.primefaces.model.FilterMeta;
import org.primefaces.model.SortMeta;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgrammingLanguageLazyDataModelRowCountOtherImpl extends ProgrammingLanguageLazyDataModel {

    private List<ProgrammingLanguage> langsFiltered;

    @Override
    public List<ProgrammingLanguage> load(int first, int pageSize, Map<String, SortMeta> sortMeta, Map<String, FilterMeta> filterMeta) {
        langsFiltered = sortAndFilterInternal(sortMeta, filterMeta);

        return langsFiltered.stream()
                .skip(first).limit(pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public int getRowCount() {
        if (langsFiltered == null) {
            return getLangs().size();
        }

        return langsFiltered.size();
    }

    @Override
    public void delete(ProgrammingLanguage language) {
        super.delete(language);
        langsFiltered.remove(language);
    }
}
