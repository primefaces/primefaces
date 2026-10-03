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
package org.primefaces.component.datatable.export;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.CSVOptions;
import org.primefaces.component.export.ColumnValue;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataTableExporterTest {

    /**
     * rowCount (3) is larger than the data actually returned by load() (1 row), e.g. because the count is stale.
     * Without a bufferSize the export used to call load(1, 3) forever.
     */
    @Test
    void exportAllStopsWhenRowCountExceedsData() {
        TestLazyDataModel model = new TestLazyDataModel(rows(1), 3);

        List<Object> exported = exportAll(model, null);

        assertEquals(rows(1), exported);
        assertEquals(List.of(0, 1), model.loadOffsets);
    }

    @Test
    void exportAllStopsWhenLoadReturnsNull() {
        TestLazyDataModel model = new TestLazyDataModel(null, 3);

        List<Object> exported = exportAll(model, null);

        assertEquals(Collections.emptyList(), exported);
        assertEquals(List.of(0), model.loadOffsets);
    }

    @Test
    void exportAllWithAccurateRowCountLoadsOnce() {
        TestLazyDataModel model = new TestLazyDataModel(rows(5), 5);

        List<Object> exported = exportAll(model, null);

        assertEquals(rows(5), exported);
        assertEquals(List.of(0), model.loadOffsets);
    }

    @Test
    void exportAllBufferizedStopsOnEmptyPortion() {
        TestLazyDataModel model = new TestLazyDataModel(rows(5), 999);

        List<Object> exported = exportAll(model, 2);

        assertEquals(rows(5), exported);
        assertEquals(List.of(0, 2, 4, 5), model.loadOffsets);
    }

    private static List<Object> exportAll(TestLazyDataModel model, Integer bufferSize) {
        FacesContext context = mock(FacesContext.class);
        DataTable table = mock(DataTable.class);
        when(table.isLazy()).thenReturn(true);
        when(table.getValue()).thenReturn(model);
        when(table.getActiveSortMeta()).thenReturn(Collections.emptyMap());
        when(table.getActiveFilterMeta()).thenReturn(Collections.emptyMap());
        when(table.getClientId(any())).thenReturn("tbl");

        TestExporter exporter = new TestExporter(ExportConfiguration.builder().bufferSize(bufferSize).build());
        exporter.exportAll(context, table);
        return exporter.exported;
    }

    private static List<Object> rows(int count) {
        return IntStream.range(0, count).mapToObj(i -> "row" + i).collect(Collectors.toList());
    }

    /**
     * Returns slices of a fixed list while reporting a (possibly wrong) row count.
     * Fails fast instead of hanging if the exporter keeps calling load().
     */
    private static class TestLazyDataModel extends LazyDataModel<Object> {

        private static final long serialVersionUID = 1L;
        private static final int MAX_LOADS = 50;

        private final List<Object> data;
        private final List<Integer> loadOffsets = new ArrayList<>();

        TestLazyDataModel(List<Object> data, int reportedRowCount) {
            this.data = data;
            setRowCount(reportedRowCount);
        }

        @Override
        public int count(Map<String, FilterMeta> filterBy) {
            return getRowCount();
        }

        @Override
        public List<Object> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
            loadOffsets.add(first);
            if (loadOffsets.size() > MAX_LOADS) {
                throw new IllegalStateException("load() called " + loadOffsets.size() + " times, offsets " + loadOffsets);
            }
            if (data == null) {
                return null;
            }
            int from = Math.min(first, data.size());
            int to = Math.min(first + pageSize, data.size());
            return new ArrayList<>(data.subList(from, to));
        }
    }

    /**
     * Records exported rows instead of rendering cells, so no columns or document are needed.
     */
    private static class TestExporter extends DataTableExporter<Object, CSVOptions> {

        private final List<Object> exported = new ArrayList<>();

        TestExporter(ExportConfiguration config) {
            super(CSVOptions.STANDARD);
            this.exportConfiguration = config;
        }

        @Override
        protected void exportRowsPortion(FacesContext context, DataTable table, List<Object> rowsPortion) {
            exported.addAll(rowsPortion);
        }

        @Override
        protected void exportCellValue(FacesContext context, DataTable table, UIColumn col, ColumnValue columnValue, int index) {
            // not used
        }

        @Override
        protected Object createDocument(FacesContext context) {
            return null;
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public String getFileExtension() {
            return ".txt";
        }
    }
}
