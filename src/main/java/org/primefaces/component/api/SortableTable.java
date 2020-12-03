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
package org.primefaces.component.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import static org.primefaces.component.datatable.DataTable.createValueExprFromVarField;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.model.SortMeta;

public interface SortableTable extends ColumnHolder {

    default Map<String, SortMeta> initSortBy(FacesContext context) {
        Map<String, SortMeta> sortMeta = new HashMap<>();
        AtomicBoolean sorted = new AtomicBoolean();

        HeaderRow headerRow = getHeaderRow();
        if (headerRow != null) {
            SortMeta s = SortMeta.of(context, getVar(), headerRow);
            sortMeta.put(s.getColumnKey(), s);
            sorted.set(true);
        }

        forEachColumn(c -> {
            SortMeta s = SortMeta.of(context, getVar(), c);
            if (s != null) {
                sorted.set(sorted.get() || s.isActive());
                sortMeta.put(s.getColumnKey(), s);
            }
        });

        // merge internal sortBy with user sortBy
        Object userSortBy = getSortBy();
        if (userSortBy != null) {
            updateSortByWithUserSortBy(context, sortMeta, userSortBy, sorted);
        }

        setDefaultSort(sorted.get());

        return sortMeta;
    }

    default void updateSortByWithTableState(Map<String, SortMeta> tsSortBy) {
        boolean defaultSort = isDefaultSort();
        for (Map.Entry<String, SortMeta> entry : tsSortBy.entrySet()) {
            SortMeta intlSortBy = getSortByAsMap().get(entry.getKey());
            if (intlSortBy != null) {
                SortMeta tsSortMeta = entry.getValue();
                intlSortBy.setPriority(tsSortMeta.getPriority());
                intlSortBy.setOrder(tsSortMeta.getOrder());
                defaultSort |= intlSortBy.isActive();
            }
        }

        setDefaultSort(defaultSort);
    }

    default void updateSortByWithUserSortBy(FacesContext context, Map<String, SortMeta> intlSortBy, Object usrSortBy, AtomicBoolean sorted) {
        Collection<SortMeta> sortBy;
        if (usrSortBy instanceof SortMeta) {
            sortBy = Collections.singletonList((SortMeta) usrSortBy);
        }
        else if (!(usrSortBy instanceof Collection)) {
            throw new FacesException("sortBy expects a single or a collection of SortMeta");
        }
        else {
            sortBy = (Collection<SortMeta>) usrSortBy;
        }

        for (SortMeta userSM : sortBy) {
            SortMeta intlSM = intlSortBy.values().stream()
                    .filter(o -> o.getField().equals(userSM.getField()))
                    .findAny()
                    .orElseThrow(() -> new FacesException("No column with field '" + userSM.getField() + "' has been found"));

            ValueExpression sortByVE = userSM.getSortBy();
            if (sortByVE == null) {
                sortByVE = createValueExprFromVarField(context, getVar(), userSM.getField());
            }

            intlSM.setPriority(userSM.getPriority());
            intlSM.setOrder(userSM.getOrder());
            intlSM.setSortBy(sortByVE);
            intlSM.setFunction(userSM.getFunction());
            sorted.set(sorted.get() || userSM.isActive());
        }
    }

    default SortMeta getHighestPriorityActiveSortMeta() {
        return getSortByAsMap().values().stream()
                .filter(SortMeta::isActive)
                .min(Comparator.comparingInt(SortMeta::getPriority))
                .orElse(null);
    }

    default boolean isSortingCurrentlyActive() {
        return getSortByAsMap().values().stream().anyMatch(SortMeta::isActive);
    }

    default boolean isColumnSortable(FacesContext context, UIColumn column) {
        Map<String, SortMeta> sortBy = getSortByAsMap();
        if (sortBy.containsKey(column.getColumnKey())) {
            return true;
        }

        SortMeta s = SortMeta.of(context, getVar(), column);
        if (s == null) {
            return false;
        }

        // unlikely to happen, in case columns change between two ajax requests
        sortBy.put(s.getColumnKey(), s);
        setSortByAsMap(sortBy);
        return true;
    }

    default String getSortMetaAsString() {
        return getSortByAsMap().keySet().stream()
                .collect(Collectors.joining("','", "['", "']"));
    }

    default HeaderRow getHeaderRow() {
        return null;
    }

    String getVar();

    Map<String, SortMeta> getSortByAsMap();

    void setSortByAsMap(Map<String, SortMeta> sortBy);

    Object getSortBy();

    void setSortBy(Object sortBy);

    boolean isDefaultSort();

    void setDefaultSort(boolean defaultSort);
}
