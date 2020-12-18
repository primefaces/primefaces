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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

public class SortableColumnsFeature extends UITableFeature {

    public static final SortableColumnsFeature INSTANCE = new SortableColumnsFeature();

    @Override
    public boolean shouldDecode(FacesContext context, UITable table) {
        // we need to restore the column UIComponent instance, this is the only generic way probably
        if (table.isSortByAsMapDefined()) {
            Map<String, SortMeta> sortBy = table.getSortByAsMap();
            for (SortMeta sortMeta : sortBy.values()) {
                if (sortMeta.isForHeaderRow()) {
                    if (sortMeta.getHeaderRow() == null) {
                        sortMeta.setHeaderRow(
                                (HeaderRow) SearchExpressionFacade.resolveComponent(context, (UIComponent) table, sortMeta.getColumnKey()));
                    }
                }
                else if (sortMeta.getColumn() == null) {
                    sortMeta.setColumn(table.findColumn(sortMeta.getColumnKey()));
                }
            }
        }

        return isRequestParameterAvailable(context, table.getClientId(context) + "_sorting")
                && isRequestParameterAvailable(context, table.getClientId(context) + "_sortKey");
    }

    @Override
    public void decode(FacesContext context, UITable table) {
        String clientId = table.getClientId(context);
        String sortKey = getRequestParameter(context, clientId + "_sortKey");
        String sortDir = getRequestParameter(context, clientId + "_sortDir");

        String[] sortKeys = sortKey.split(",");
        String[] sortOrders = sortDir.split(",");

        if (sortKeys.length != sortOrders.length) {
            throw new FacesException("sortKeys != sortDirs");
        }

        Map<String, SortMeta> sortBy = table.getSortByAsMap();
        Map<String, Integer> sortKeysIndexes = IntStream.range(0, sortKeys.length).boxed()
                .collect(Collectors.toMap(i -> sortKeys[i], i -> i));

        for (Map.Entry<String, SortMeta> entry : sortBy.entrySet()) {
            SortMeta current = entry.getValue();
            if (!(current.getColumn() instanceof UIColumn)) {
                continue;
            }

            Integer index = sortKeysIndexes.get(entry.getKey());
            if (index != null) {
                current.setOrder(SortOrder.of(sortOrders[index]));
                current.setPriority(index);
            }
            else {
                current.setOrder(SortOrder.UNSORTED);
                current.setPriority(SortMeta.MIN_PRIORITY);
            }
        }

        if (table.isMultiViewState()) {
            UITableState ts = (UITableState) table.getMultiViewState(true);
            ts.setSortBy(sortBy);
        }
    }
}
