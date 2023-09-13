/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.model;


import javax.faces.convert.Converter;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class RowLimitLazyDataModel<T> extends LazyDataModel<T> {
    private static final int DEFAULT_ROW_LIMIT = 1000;

    private final int rowLimit;

    private int currentRangeIndex;
    private List<T> currentRange;

    protected RowLimitLazyDataModel() {
        this.rowLimit = DEFAULT_ROW_LIMIT;
    }

    protected RowLimitLazyDataModel(int rowLimit) {
        this.rowLimit = rowLimit;
    }

    protected RowLimitLazyDataModel(Converter converter) {
        super(converter);
        this.rowLimit = DEFAULT_ROW_LIMIT;
    }

    protected RowLimitLazyDataModel(int rowLimit, Converter converter) {
        super(converter);
        this.rowLimit = rowLimit;
    }

    public abstract List<T> loadLimitedRows(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy);

    @Override
    public final List<T> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        clearCache();
        int rowCount = getRowCount();
        if (rowCount == 0) {
            return Collections.emptyList();
        }
        else if (pageSize <= rowLimit) {
            return loadLimitedRows(first, pageSize, sortBy, filterBy);
        }
        else {
            return new RangesList(first, sortBy, filterBy, rowCount);
        }
    }

    private void clearCache() {
        this.currentRange = Collections.emptyList();
        this.currentRangeIndex = -1;
    }

    private class RangesList extends AbstractList<T> implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private final int first;
        private final Map<String, SortMeta> sortBy;
        private final Map<String, FilterMeta> filterBy;
        private int rowCount;

        public RangesList(int first, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy, int rowCount) {
            this.first = first;
            this.sortBy = sortBy;
            this.filterBy = filterBy;
            this.rowCount = rowCount;
        }

        @Override
        public T get(int index) {
            int rangeIndex, dataIndex;
            rangeIndex = index / rowLimit;
            dataIndex = index % rowLimit;

            boolean mustQueryNextRange = rangeIndex != currentRangeIndex;
            if (mustQueryNextRange) {
                currentRange = loadLimitedRows(first + rangeIndex * rowLimit, rowLimit, sortBy, filterBy);
                currentRangeIndex = rangeIndex;

                boolean rowCountHasChanged = currentRange.size() < rowLimit;
                if (rowCountHasChanged) {
                    recalculateRowCount(rangeIndex);
                }
            }

            return currentRange.get(dataIndex);
        }

        private void recalculateRowCount(int indexRango) {
            rowCount = indexRango * rowLimit + currentRange.size();
        }

        @Override
        public int size() {
            return rowCount;
        }
    }
}
