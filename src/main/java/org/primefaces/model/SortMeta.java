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
package org.primefaces.model;

import java.io.Serializable;
import java.util.Objects;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.column.ColumnBase;
import org.primefaces.component.headerrow.HeaderRow;
import org.primefaces.component.headerrow.HeaderRowBase;
import org.primefaces.util.LangUtils;

public class SortMeta implements Serializable, Comparable<SortMeta> {

    public static final Integer MIN_PRIORITY = Integer.MAX_VALUE;
    public static final Integer MAX_PRIORITY = Integer.MIN_VALUE;

    private static final long serialVersionUID = 1L;

    private String columnKey;
    private String field;
    private SortOrder order = SortOrder.UNSORTED;
    private ValueExpression sortBy;
    private MethodExpression function;
    private int priority = MIN_PRIORITY;
    private int nullSortOrder;
    private boolean caseSensitiveSort;
    private transient Object component;

    public SortMeta() {
        // NOOP
    }

    SortMeta(String columnKey, String sortField, SortOrder sortOrder, MethodExpression sortFunction,
             ValueExpression sortBy, int priority, int nullSortOrder, boolean caseSensitiveSort, Object component) {
        this.columnKey = columnKey;
        this.field = sortField;
        this.order = sortOrder;
        this.function = sortFunction;
        this.sortBy = sortBy;
        this.priority = priority;
        this.nullSortOrder = nullSortOrder;
        this.caseSensitiveSort = caseSensitiveSort;
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
        if (order.isUnsorted() && column.isGroupRow()) {
            order = SortOrder.ASCENDING;
        }

        ValueExpression sortByVE = column.getValueExpression(ColumnBase.PropertyKeys.sortBy.name());
        sortByVE = sortByVE != null ? sortByVE : UITable.createValueExprFromVarField(context, var, field);

        return new SortMeta(column.getColumnKey(),
                            field,
                            order,
                            column.getSortFunction(),
                            sortByVE,
                            column.getSortPriority(),
                            column.getNullSortOrder(),
                            column.isCaseSensitiveSort(),
                            column);
    }

    public static SortMeta of(FacesContext context, String var, HeaderRow headerRow) {
        SortOrder order = SortOrder.of(headerRow.getSortOrder());
        ValueExpression groupByVE = headerRow.getValueExpression(HeaderRowBase.PropertyKeys.groupBy.name());

        if (groupByVE == null && LangUtils.isValueBlank(headerRow.getField())) {
            throw new FacesException("HeaderRow must have 'groupBy' or 'field' attribute value");
        }

        groupByVE = groupByVE != null ? groupByVE : UITable.createValueExprFromVarField(context, var, headerRow.getField());

        return new SortMeta(headerRow.getClientId(context),
                            headerRow.getField(),
                            order,
                            headerRow.getSortFunction(),
                            groupByVE,
                            MAX_PRIORITY,
                            SortOrder.ASCENDING.intValue(),
                            false,
                            headerRow);
    }

    @Override
    public int compareTo(SortMeta o) {
        int result = Integer.compare(getPriority(), o.priority);
        if (result == 0) {
            return -1 * Boolean.compare(isActive(), o.isActive());
        }
        return result;
    }

    public String getField() {
        return field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public MethodExpression getFunction() {
        return function;
    }

    public void setFunction(MethodExpression function) {
        this.function = function;
    }

    public ValueExpression getSortBy() {
        return sortBy;
    }

    public void setSortBy(ValueExpression sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isActive() {
        return order != SortOrder.UNSORTED;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Object getComponent() {
        return component;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public int getNullSortOrder() {
        return nullSortOrder;
    }

    public boolean isCaseSensitiveSort() {
        return caseSensitiveSort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortMeta sortMeta = (SortMeta) o;
        return Objects.equals(columnKey, sortMeta.columnKey) &&
                Objects.equals(field, sortMeta.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnKey, field);
    }

    @Override
    public String toString() {
        return "SortMeta{" +
                "columnKey='" + columnKey + '\'' +
                ", sortField='" + field + '\'' +
                ", sortOrder=" + order +
                ", sortBy=" + sortBy +
                ", sortFunction=" + function +
                ", priority=" + priority +
                ", nullSortOrder=" + nullSortOrder +
                ", caseSensitiveSort=" + caseSensitiveSort +
                '}';
    }

    static String resolveSortField(FacesContext context, UIColumn column) {
        ValueExpression columnSortByVE = column.getValueExpression(ColumnBase.PropertyKeys.sortBy.toString());

        if (column.isDynamic()) {
            String field = column.getField();
            if (field == null) {
                Object sortByProperty = column.getSortBy();
                field = (sortByProperty == null) ? UITable.resolveDynamicField(context, columnSortByVE) : sortByProperty.toString();
            }
            return field;
        }
        else {
            String field = column.getField();
            if (field == null) {
                field = (columnSortByVE == null) ? (String) column.getSortBy() : UITable.resolveStaticField(columnSortByVE);
            }
            return field;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private SortMeta sortBy;

        private Builder() {
            sortBy = new SortMeta();
        }

        public Builder field(String field) {
            sortBy.field = field;
            return this;
        }

        public Builder order(SortOrder sortOrder) {
            sortBy.order = sortOrder;
            return this;
        }

        public Builder sortBy(ValueExpression sortBy) {
            this.sortBy.sortBy = sortBy;
            return this;
        }

        public Builder function(MethodExpression sortFunction) {
            sortBy.function = sortFunction;
            return this;
        }

        public Builder priority(int priority) {
            sortBy.priority = priority;
            return this;
        }

        public Builder nullSortOrder(int nullSortOrder) {
            sortBy.nullSortOrder = nullSortOrder;
            return this;
        }

        public Builder caseSensitiveSort(boolean caseSensitiveSort) {
            sortBy.caseSensitiveSort = caseSensitiveSort;
            return this;
        }

        public SortMeta build() {
            Objects.requireNonNull(sortBy.field, "Field is required");
            return sortBy;
        }
    }
}
