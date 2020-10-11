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
package org.primefaces.model;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.ColumnBase;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.component.headerrow.HeaderRowBase;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Objects;

public class SortMeta implements Serializable, Comparable<SortMeta> {

    public static final Integer MIN_PRIORITY = Integer.MAX_VALUE;
    public static final Integer MAX_PRIORITY = Integer.MIN_VALUE;

    private static final long serialVersionUID = 1L;

    private String columnKey;
    private String sortField;
    private SortOrder sortOrder = SortOrder.UNSORTED;
    private ValueExpression sortBy;
    private MethodExpression sortFunction;
    private Integer priority = MIN_PRIORITY;
    private transient Object component;

    public SortMeta() {
        // NOOP
    }

    SortMeta(String columnKey, String sortField, SortOrder sortOrder, MethodExpression sortFunction,
             ValueExpression sortBy, Integer priority, Object component) {
        this.columnKey = columnKey;
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.sortFunction = sortFunction;
        this.sortBy = sortBy;
        this.priority = priority;
        this.component = component;
    }

    public static SortMeta of(FacesContext context, String var, UIColumn column) {
        if (column instanceof DynamicColumn) {
            ((DynamicColumn) column).applyStatelessModel();
        }

        if (!column.isSortable()) {
            return null;
        }

        String field = resolveSortField(context, column);
        if (field == null) {
            return null;
        }

        SortOrder order = SortOrder.of(column.getSortOrder());
        ValueExpression sortByVE = column.getValueExpression(ColumnBase.PropertyKeys.sortBy.name());
        sortByVE = sortByVE != null ? sortByVE : DataTable.createValueExprFromVarField(context, var, field);

        return new SortMeta(column.getColumnKey(),
                            field,
                            order,
                            column.getSortFunction(),
                            sortByVE,
                            column.getSortPriority(),
                            column);
    }

    public static SortMeta of(FacesContext context, String var, HeaderRow headerRow) {
        SortOrder order = SortOrder.of(headerRow.getSortOrder());
        ValueExpression groupByVE = headerRow.getValueExpression(HeaderRowBase.PropertyKeys.groupBy.name());
        groupByVE = groupByVE != null ? groupByVE : DataTable.createValueExprFromVarField(context, var, headerRow.getField());

        return new SortMeta(headerRow.getClientId(context),
                            headerRow.getField(),
                            order,
                            headerRow.getSortFunction(),
                            groupByVE,
                            MAX_PRIORITY,
                            headerRow);
    }

    @Override
    public int compareTo(SortMeta o) {
        int result = getPriority().compareTo(o.priority);
        if (result == 0) {
            return -1 * Boolean.compare(isActive(), o.isActive());
        }
        return result;
    }

    public String getSortField() {
        return sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public MethodExpression getSortFunction() {
        return sortFunction;
    }

    public void setSortFunction(MethodExpression sortFunction) {
        this.sortFunction = sortFunction;
    }

    public ValueExpression getSortBy() {
        return sortBy;
    }

    public void setSortBy(ValueExpression sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isActive() {
        return sortOrder != SortOrder.UNSORTED;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Object getComponent() {
        return component;
    }

    public String getColumnKey() {
        return columnKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortMeta sortMeta = (SortMeta) o;
        return Objects.equals(columnKey, sortMeta.columnKey) &&
                Objects.equals(sortField, sortMeta.sortField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnKey, sortField);
    }

    @Override
    public String toString() {
        return "SortMeta{" +
                "columnKey='" + columnKey + '\'' +
                ", sortField='" + sortField + '\'' +
                ", sortOrder=" + sortOrder +
                ", sortBy=" + sortBy +
                ", sortFunction=" + sortFunction +
                ", priority=" + priority +
                '}';
    }

    static String resolveSortField(FacesContext context, UIColumn column) {
        ValueExpression columnSortByVE = column.getValueExpression(ColumnBase.PropertyKeys.sortBy.toString());

        if (column.isDynamic()) {
            String field = column.getField();
            if (field == null) {
                Object sortByProperty = column.getSortBy();
                field = (sortByProperty == null) ? DataTable.resolveDynamicField(context, columnSortByVE) : sortByProperty.toString();
            }
            return field;
        }
        else {
            String field = column.getField();
            if (field == null) {
                field = (columnSortByVE == null) ? (String) column.getSortBy() : DataTable.resolveStaticField(columnSortByVE);
            }
            return field;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SortMeta sortMeta;

        private Builder() {
            sortMeta = new SortMeta();
        }

        public Builder field(String field) {
            sortMeta.sortField = field;
            return this;
        }

        public Builder sortOrder(SortOrder sortOrder) {
            sortMeta.sortOrder = sortOrder;
            return this;
        }

        public Builder sortBy(ValueExpression sortBy) {
            sortMeta.sortBy = sortBy;
            return this;
        }

        public Builder sortFunction(MethodExpression sortFunction) {
            sortMeta.sortFunction = sortFunction;
            return this;
        }

        public Builder priority(Integer priority) {
            sortMeta.priority = priority;
            return this;
        }

        public SortMeta build() {
            return sortMeta;
        }
    }
}
