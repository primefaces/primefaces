/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
import org.primefaces.component.column.Column;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FilterConstraints;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.model.filter.GlobalFilterConstraint;
import org.primefaces.util.EditableValueHolderState;
import org.primefaces.util.LangUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.context.FacesContext;

public class FilterMeta implements Serializable {

    public static final String GLOBAL_FILTER_KEY = "globalFilter";

    private static final long serialVersionUID = 1L;

    private String field;
    private String columnKey;
    private ValueExpression filterBy;
    private Object filterValue; // should be null if empty string/collection/array/object
    private MatchMode matchMode = MatchMode.CONTAINS;
    private FilterConstraint constraint;
    private boolean normalize = false;
    private boolean filterByGenerated;

    public FilterMeta() {
        // NOOP
    }

    FilterMeta(String columnKey, String field, FilterConstraint constraint,
               ValueExpression filterBy, Object filterValue, MatchMode matchMode, boolean normalize,
               boolean filterByGenerated) {
        this.field = field;
        this.columnKey = columnKey;
        this.filterBy = filterBy;
        this.constraint = constraint;
        this.matchMode = matchMode;
        this.normalize = normalize;
        this.filterByGenerated = filterByGenerated;
        setFilterValue(filterValue);
    }

    public static FilterMeta of(FacesContext context, String var, UIColumn column, boolean normalize) {
        if (column instanceof DynamicColumn) {
            ((DynamicColumn) column).applyStatelessModel();
        }

        if (!column.isFilterable()) {
            return null;
        }

        String field = column.getField();
        ValueExpression filterByVE = column.getValueExpression(Column.PropertyKeys.filterBy.name());
        if (field == null && filterByVE == null) {
            return null;
        }

        boolean filterByGenerated = false;
        if (field == null) {
            field = column.resolveField(context, filterByVE);
        }
        else if (filterByVE == null) {
            filterByVE = UIColumn.createValueExpressionFromField(context, var, field);
            filterByGenerated = true;
        }

        MatchMode matchMode = MatchMode.of(column.getFilterMatchMode());
        FilterConstraint constraint = FilterConstraints.of(matchMode);

        if (column.getFilterFunction() != null) {
            constraint = new FunctionFilterConstraint(column.getFilterFunction());
        }

        Object filterValue = column.getFilterValue();
        if (filterValue == null) {
            EditableValueHolderState state = column.getFilterValueHolder(context);
            if (state != null) {
                filterValue = state.getValue();
            }
        }
        filterValue = LangUtils.normalize(filterValue, normalize);

        return new FilterMeta(column.getColumnKey(),
                              field,
                              constraint,
                              filterByVE,
                              filterValue,
                              matchMode,
                              normalize,
                              filterByGenerated);
    }

    public static FilterMeta of(Object globalFilterValue, MethodExpression globalFilterFunction, boolean normalize) {
        FilterConstraint constraint = globalFilterFunction == null
                ? new GlobalFilterConstraint()
                : new FunctionFilterConstraint(globalFilterFunction);

        return new FilterMeta(GLOBAL_FILTER_KEY,
                              GLOBAL_FILTER_KEY,
                              constraint,
                              null,
                              LangUtils.normalize(globalFilterValue, normalize),
                              MatchMode.GLOBAL,
                              normalize,
                              false);
    }

    public static <T> T resetToNullIfEmpty(T filterValue) {
        if (filterValue != null
                && ((filterValue instanceof String && LangUtils.isBlank((String) filterValue))
                || (filterValue instanceof Collection && ((Collection) filterValue).isEmpty())
                || (filterValue instanceof Iterable && !((Iterable) filterValue).iterator().hasNext())
                || (filterValue.getClass().isArray() && Array.getLength(filterValue) == 0))) {
            filterValue = null;
        }
        return filterValue;
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
        this.filterByGenerated = false;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(Object filterValue) {
        this.filterValue = resetToNullIfEmpty(LangUtils.normalize(filterValue, isNormalize()));
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

    public Object getLocalValue(ELContext elContext, UIColumn column) {
        if (column instanceof DynamicColumn) {
            ((DynamicColumn) column).applyStatelessModel();
        }
        return LangUtils.normalize(filterBy.getValue(elContext), isNormalize());
    }

    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public boolean isFilterByGenerated() {
        return filterByGenerated;
    }

    public void setFilterByGenerated(boolean filterByGenerated) {
        this.filterByGenerated = filterByGenerated;
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

        public Builder normalize(boolean normalize) {
            filterBy.normalize = normalize;
            return this;
        }

        public FilterMeta build() {
            if (filterBy.matchMode != null) {
                filterBy.constraint = FilterConstraints.of(filterBy.matchMode);
            }
            filterBy.filterValue = resetToNullIfEmpty(filterBy.filterValue);
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
                ", filterBy=" + filterBy +
                ", filterValue=" + filterValue +
                ", matchMode=" + matchMode +
                ", constraint=" + constraint +
                ", normalize=" + normalize +
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
}
