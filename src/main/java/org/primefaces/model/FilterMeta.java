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

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.ColumnBase;
import org.primefaces.component.datatable.feature.FilterFeature;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.model.filter.GlobalFilterConstraint;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import org.primefaces.component.api.UITable;

public class FilterMeta implements Serializable {

    public static final String GLOBAL_FILTER_KEY = "globalFilter";

    private static final long serialVersionUID = 1L;

    private String field;
    private String columnKey;
    private transient UIColumn column;
    private ValueExpression filterBy;
    private Object filterValue;
    private MatchMode matchMode = MatchMode.CONTAINS;
    private transient FilterConstraint constraint;

    public FilterMeta() {
        // NOOP
    }

    FilterMeta(String columnKey, String field, FilterConstraint constraint,
               ValueExpression filterBy, Object filterValue, MatchMode matchMode, UIColumn column) {
        this.field = field;
        this.columnKey = columnKey;
        this.filterBy = filterBy;
        this.constraint = constraint;
        this.filterValue = filterValue;
        this.matchMode = matchMode;
        this.column = column;
    }

    /**
     * @deprecated Use FilterMeta#builder() instead
     */
    @Deprecated
    public FilterMeta(String field, String columnKey, ValueExpression filterByVE, MatchMode filterMatchMode, Object filterValue) {
        this.field = field;
        this.columnKey = columnKey;
        this.filterBy = filterByVE;
        this.constraint = FilterFeature.FILTER_CONSTRAINTS.get(filterMatchMode);
        this.filterValue = filterValue;
        this.matchMode = filterMatchMode;
    }

    public static FilterMeta of(FacesContext context, String var, UIColumn column) {
        if (column instanceof DynamicColumn) {
            ((DynamicColumn) column).applyStatelessModel();
        }

        if (!column.isFilterable()) {
            return null;
        }

        String field = resolveFilterField(context, column);
        if (field == null) {
            return null;
        }

        ValueExpression filterByVE = column.getValueExpression(ColumnBase.PropertyKeys.filterBy.name());
        filterByVE = filterByVE != null ? filterByVE : UITable.createValueExprFromVarField(context, var, field);

        MatchMode matchMode = MatchMode.of(column.getFilterMatchMode());
        FilterConstraint constraint = FilterFeature.FILTER_CONSTRAINTS.get(matchMode);

        if (column.getFilterFunction() != null) {
            constraint = new FunctionFilterConstraint(column.getFilterFunction());
        }

        return new FilterMeta(column.getColumnKey(),
                              field,
                              constraint,
                              filterByVE,
                              column.getFilterValue(),
                              matchMode,
                              column);
    }

    public static FilterMeta of(Object globalFilterValue, MethodExpression globalFilterFunction) {
        FilterConstraint constraint = globalFilterFunction == null
                ? new GlobalFilterConstraint()
                : new FunctionFilterConstraint(globalFilterFunction);

        return new FilterMeta(GLOBAL_FILTER_KEY,
                              GLOBAL_FILTER_KEY,
                              constraint,
                              null,
                              globalFilterValue,
                              MatchMode.GLOBAL,
                              null);
    }

    public String getField() {
        return field;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public ValueExpression getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(ValueExpression filterBy) {
        this.filterBy = filterBy;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
    }

    public UIColumn getColumn() {
        return column;
    }

    public void setColumn(UIColumn column) {
        this.column = column;
    }

    public FilterConstraint getConstraint() {
        return constraint;
    }

    public void setConstraint(FilterConstraint constraint) {
        this.constraint = constraint;
    }

    public boolean isActive() {
        return filterValue != null;
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public boolean isGlobalFilter() {
        return GLOBAL_FILTER_KEY.equals(columnKey);
    }

    public Object getLocalValue(ELContext elContext) {
        if (column instanceof DynamicColumn) {
            ((DynamicColumn) column).applyStatelessModel();
        }
        return filterBy.getValue(elContext);
    }

    static String resolveFilterField(FacesContext context, UIColumn column) {
        ValueExpression columnFilterByVE = column.getValueExpression(ColumnBase.PropertyKeys.filterBy.toString());

        if (column.isDynamic()) {
            String field = column.getField();
            if (field == null) {
                Object filterBy = column.getFilterBy();
                field = (filterBy == null) ? UITable.resolveDynamicField(context, columnFilterByVE) : filterBy.toString();
            }
            return field;
        }
        else {
            String field = column.getField();
            if (field == null) {
                field = (columnFilterByVE == null) ? (String) column.getFilterBy() : UITable.resolveStaticField(columnFilterByVE);
            }
            return field;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final FilterMeta filterBy;

        private Builder() {
            filterBy = new FilterMeta();
        }

        public Builder field(String field) {
            filterBy.field = field;
            return this;
        }

        public Builder filterBy(ValueExpression filterBy) {
            this.filterBy.filterBy = filterBy;
            return this;
        }

        public Builder filterValue(Object filterValue) {
            filterBy.filterValue = filterValue;
            return this;
        }

        public Builder constraint(FilterConstraint constraint) {
            filterBy.constraint = constraint;
            return this;
        }

        public Builder matchMode(MatchMode matchMode) {
            filterBy.matchMode = matchMode;
            return this;
        }

        public FilterMeta build() {
            if (filterBy.matchMode != null) {
                filterBy.constraint = FilterFeature.FILTER_CONSTRAINTS.get(filterBy.matchMode);
            }
            Objects.requireNonNull(filterBy.constraint, "Filter constraint is required");
            Objects.requireNonNull(filterBy.field, "Field is required");
            return filterBy;
        }
    }

    @Override
    public String toString() {
        return "FilterMeta{" +
                "field='" + field + '\'' +
                ", columnKey='" + columnKey + '\'' +
                ", column=" + column +
                ", filterBy=" + filterBy +
                ", filterValue=" + filterValue +
                ", matchMode=" + matchMode +
                ", constraint=" + constraint +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterMeta that = (FilterMeta) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(columnKey, that.columnKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, columnKey);
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        is.defaultReadObject();

        FilterConstraint c = FilterFeature.FILTER_CONSTRAINTS.get(matchMode);
        if (c != null) {
            this.constraint = c;
        }
    }
}
