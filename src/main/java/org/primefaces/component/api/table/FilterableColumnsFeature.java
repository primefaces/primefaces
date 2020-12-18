/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.api.table;

import java.lang.reflect.Array;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.model.FilterMeta;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;

public class FilterableColumnsFeature extends UITableFeature {

    public static final FilterableColumnsFeature INSTANCE = new FilterableColumnsFeature();

    @Override
    public boolean shouldDecode(FacesContext context, UITable table) {
        // we need to restore the column UIComponent instance, this is the only generic way probably
        if (table.isFilterByAsMapDefined()) {
            Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
            for (FilterMeta filterMeta : filterBy.values()) {
                if (filterMeta.getColumn() == null) {
                    filterMeta.setColumn(table.findColumn(filterMeta.getColumnKey()));
                }
            }
        }

        return isRequestParameterAvailable(context, table.getClientId(context) + "_filtering");
    }

    @Override
    public void decode(FacesContext context, UITable table) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        char separator = UINamingContainer.getSeparatorChar(context);

        Map<String, FilterMeta> filterBy = table.getFilterByAsMap();
        for (FilterMeta filterMeta : filterBy.values()) {
            Object filterValue;

            if (filterMeta.isGlobalFilter()) {
                filterValue = params.get(table.getClientId(context) + separator + FilterMeta.GLOBAL_FILTER_KEY);
            }
            else {
                UIColumn column = filterMeta.getColumn();
                UIComponent filterFacet = column.getFacet("filter");
                boolean hasCustomFilter = filterFacet != null;
                if (column instanceof DynamicColumn) {
                    if (hasCustomFilter) {
                        ((DynamicColumn) column).applyModel();
                        // UIColumn#rendered might change after restoring p:columns state at the right index
                        hasCustomFilter = ComponentUtils.shouldRenderFacet(filterFacet);
                    }
                    else {
                        ((DynamicColumn) column).applyStatelessModel();
                    }
                }

                if (hasCustomFilter) {
                    filterValue = ((ValueHolder) filterFacet).getLocalValue();
                }
                else {
                    String valueHolderClientId = column instanceof DynamicColumn
                            ? column.getContainerClientId(context) + separator + "filter"
                            : column.getClientId(context) + separator + "filter";
                    filterValue = params.get(valueHolderClientId);
                }
            }

            // returns null if empty string/array/object
            if (filterValue != null
                    && (filterValue instanceof String && LangUtils.isValueBlank((String) filterValue)
                    || filterValue.getClass().isArray() && Array.getLength(filterValue) == 0)) {
                filterValue = null;
            }

            filterMeta.setFilterValue(filterValue);
        }

        if (table.isMultiViewState()) {
            UITableState ts = (UITableState) table.getMultiViewState(true);
            ts.setFilterBy(filterBy);
        }
    }
}
