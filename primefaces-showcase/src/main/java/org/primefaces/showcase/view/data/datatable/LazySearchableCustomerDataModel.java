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
package org.primefaces.showcase.view.data.datatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazySearchable;
import org.primefaces.model.MatchMode;
import org.primefaces.showcase.domain.Customer;

public class LazySearchableCustomerDataModel extends LazyCustomerDataModel implements LazySearchable {

    private static final long serialVersionUID = 1L;

    public LazySearchableCustomerDataModel(List<Customer> datasource) {
        super(datasource);
    }

    @Override
    public Map<String, FilterMeta> getSearchFilter(String query) {
        FilterMeta filterMeta = FilterMeta.builder()
                .field("name")
                .filterValue(query)
                .matchMode(MatchMode.CONTAINS)
                .build();
        Map<String, FilterMeta> searchFilter = new HashMap<>();
        searchFilter.put(filterMeta.getField(), filterMeta);
        return searchFilter;
    }

}
