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
package org.primefaces.component.datatable.feature;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * "Select all" (@all) on a lazy DataTable must load the rows with the same sort/filter maps as
 * {@link DataTable#loadLazyScrollData(int, int)}: only the active ones, keyed by field.
 */
class SelectionFeatureTest {

    private static final List<String> ROWS = List.of("apple", "avocado", "banana", "apricot", "cherry");

    private FacesContext context;
    private DataTable table;
    private ValueExpression selectionVE;
    private TestLazyDataModel model;
    private Map<String, FilterMeta> filterByAsMap;
    private Map<String, SortMeta> sortByAsMap;

    @BeforeEach
    void setUp() {
        context = mock(FacesContext.class);
        ExternalContext externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getRequestMap()).thenReturn(new HashMap<>());
        when(context.getELContext()).thenReturn(mock(ELContext.class));

        selectionVE = mock(ValueExpression.class);
        when(selectionVE.getType(any())).thenAnswer(i -> List.class);

        model = new TestLazyDataModel();
        filterByAsMap = new LinkedHashMap<>();
        sortByAsMap = new LinkedHashMap<>();

        table = mock(DataTable.class);
        when(table.isLazy()).thenReturn(true);
        when(table.getLazyDataModel()).thenAnswer(i -> model);
        when(table.getVar()).thenReturn("row");
        when(table.isSelectionEnabled()).thenReturn(true);
        when(table.getValueExpression(DataTable.PropertyKeys.selection.name())).thenReturn(selectionVE);
        when(table.getRowKey(any())).thenAnswer(i -> i.getArgument(0));
        when(table.getFilterByAsMap()).thenReturn(filterByAsMap);
        when(table.getSortByAsMap()).thenReturn(sortByAsMap);
        when(table.getActiveFilterMeta()).thenCallRealMethod();
        when(table.getActiveSortMeta()).thenCallRealMethod();
    }

    @Test
    void selectAllLoadsWithActiveFiltersKeyedByField() {
        filterByAsMap.put("tbl:nameCol", FilterMeta.builder().field("name").filterValue("ap").build());
        filterByAsMap.put("tbl:otherCol", FilterMeta.builder().field("other").build()); // inactive

        List<Object> selection = selectAll("@all");

        assertEquals(Set.of("name"), model.loadedFilterBy.keySet());
        assertEquals(List.of("apple", "apricot"), selection);
    }

    @Test
    void selectAllCountsWithCurrentFiltersInsteadOfStaleRowCount() {
        // filter widened in the same request: the row count of the previous request is out of date
        filterByAsMap.put("tbl:nameCol", FilterMeta.builder().field("name").filterValue("a").build());
        model.setRowCount(2);

        List<Object> selection = selectAll("@all");

        assertEquals(Set.of("name"), model.countedFilterBy.keySet());
        assertEquals(List.of("apple", "avocado", "apricot"), selection);
    }

    @Test
    void selectAllFallsBackToRowCountWhenCountIsNotImplemented() {
        // documented pattern: count() returns 0 and load() calls setRowCount()
        filterByAsMap.put("tbl:nameCol", FilterMeta.builder().field("name").filterValue("ap").build());
        model.countNotImplemented = true;
        model.setRowCount(2); // set by load() in the previous request

        List<Object> selection = selectAll("@all");

        assertEquals(List.of("apple", "apricot"), selection);
    }

    @Test
    void selectAllReloadsWhenLoadSetsBiggerRowCount() {
        // count() not implemented and filter widened in the same request: load() reports the new row count
        filterByAsMap.put("tbl:nameCol", FilterMeta.builder().field("name").filterValue("a").build());
        model.countNotImplemented = true;
        model.setRowCount(2); // set by load() in the previous request

        List<Object> selection = selectAll("@all");

        assertEquals(List.of("apple", "avocado", "apricot"), selection);
    }

    @Test
    void selectAllAfterLoadUsesRowCountWithoutCounting() {
        // decodeSelectionRowKeys runs after the model has been loaded with the current filters
        filterByAsMap.put("tbl:nameCol", FilterMeta.builder().field("name").filterValue("ap").build());
        model.setRowCount(2);
        when(table.getSelectedRowKeys()).thenReturn(Set.of("@all"));

        new SelectionFeature().decodeSelectionRowKeys(context, table);

        assertNull(model.countedFilterBy);
        assertEquals(List.of("apple", "apricot"), capturedSelection());
    }

    @Test
    void selectAllAfterLoadSkipsLoadWhenNothingMatches() {
        model.setRowCount(0);
        when(table.getSelectedRowKeys()).thenReturn(Set.of("@all"));

        new SelectionFeature().decodeSelectionRowKeys(context, table);

        assertNull(model.countedFilterBy);
        assertNull(model.loadedFilterBy);
    }

    @Test
    void selectAllLoadsWithActiveSortMeta() {
        sortByAsMap.put("tbl:nameCol", SortMeta.builder().field("name").order(SortOrder.DESCENDING).build());
        sortByAsMap.put("tbl:otherCol", SortMeta.builder().field("other").order(SortOrder.UNSORTED).build());

        selectAll("@all");

        assertEquals(table.getActiveSortMeta(), model.loadedSortBy);
        assertEquals(Set.of("name"), model.loadedSortBy.keySet());
    }

    @Test
    void selectAllExcludesDeselectedRows() {
        List<Object> selection = selectAll("@all", "!banana", "!cherry");

        assertEquals(List.of("apple", "avocado", "apricot"), selection);
    }

    @SuppressWarnings("unchecked")
    private List<Object> selectAll(String... rowKeys) {
        new SelectionFeature().decodeSelection(context, table, Set.of(rowKeys));
        return capturedSelection();
    }

    @SuppressWarnings("unchecked")
    private List<Object> capturedSelection() {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(selectionVE).setValue(any(), captor.capture());
        List<Object> selection = new ArrayList<>((List<Object>) captor.getValue());
        selection.sort((a, b) -> Integer.compare(ROWS.indexOf(a), ROWS.indexOf(b)));
        return selection;
    }

    /**
     * Reads the filter by field, like the showcase models do, and records what it receives.
     */
    private static class TestLazyDataModel extends LazyDataModel<String> {

        private static final long serialVersionUID = 1L;

        private Map<String, SortMeta> loadedSortBy;
        private Map<String, FilterMeta> loadedFilterBy;
        private Map<String, FilterMeta> countedFilterBy;
        private boolean countNotImplemented;

        @Override
        public int count(Map<String, FilterMeta> filterBy) {
            countedFilterBy = filterBy;
            return countNotImplemented ? 0 : filter(filterBy).size();
        }

        @Override
        public List<String> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
            loadedSortBy = sortBy;
            loadedFilterBy = filterBy;
            List<String> rows = filter(filterBy);
            if (countNotImplemented) {
                setRowCount(rows.size());
            }
            return rows.subList(first, Math.min(first + pageSize, rows.size()));
        }

        @Override
        public String getRowKey(String object) {
            return object;
        }

        @Override
        public String getRowData(String rowKey) {
            return rowKey;
        }

        private static List<String> filter(Map<String, FilterMeta> filterBy) {
            FilterMeta name = filterBy.get("name");
            if (name == null) {
                return ROWS;
            }
            String value = name.getFilterValue().toString();
            return ROWS.stream().filter(r -> r.startsWith(value)).collect(Collectors.toList());
        }
    }
}
