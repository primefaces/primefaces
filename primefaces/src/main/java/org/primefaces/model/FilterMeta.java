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
package org.primefaces.model;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.column.Column;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.filter.FilterConstraint;
import org.primefaces.model.filter.FilterConstraints;
import org.primefaces.model.filter.FunctionFilterConstraint;
import org.primefaces.model.filter.GlobalFilterConstraint;
import org.primefaces.util.EditableValueHolderState;
import org.primefaces.util.LangUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

public class FilterMeta implements Serializable {

    public static final String GLOBAL_FILTER_KEY = "globalFilter";

    private static final long serialVersionUID = 1L;

    private String field;
    private String columnKey;
    private ValueExpression filterBy;
    private Object filterValue; // should be null if empty string/collection/array/object
    private MatchMode matchMode = MatchMode.CONTAINS;
    private List<MatchMode> matchModeOptions = Collections.emptyList();
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

    public static FilterMeta of(FacesContext context, UITable<?> table, UIColumn column, boolean normalize) {
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
            filterByVE = UIColumn.createValueExpressionFromField(context, table.getVar(), field);
            filterByGenerated = true;
        }

        List<MatchMode> matchModeOptions = MatchMode.parseOptions(resolveFilterValueType(context, table, column));
        MatchMode matchMode = matchModeOptions.isEmpty() || matchModeOptions.contains(MatchMode.of(column.getFilterMatchMode()))
                ? MatchMode.of(column.getFilterMatchMode())
                : matchModeOptions.get(0);
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

        FilterMeta filterMeta = new FilterMeta(column.getColumnKey(),
                              field,
                              constraint,
                              filterByVE,
                              filterValue,
                              matchMode,
                              normalize,
                              filterByGenerated);
        filterMeta.setMatchModeOptions(matchModeOptions);
        return filterMeta;
    }

    /**
     * Resolves the filter value type to use for a column's match-mode dropdown: the page author's explicit
     * {@code filterValueType} (including {@code "none"}, which opts the column out - see {@link MatchMode#parseOptions})
     * always wins. A column that already has its own custom {@code <f:facet name="filter">} or a custom
     * {@code filterFunction} has already opted out of the standard match-mode-driven filtering machinery -
     * auto-adding a dropdown on top of either would silently break it: a facet would get a second, competing
     * picker, and - more subtly - a {@code filterFunction} would have its {@link org.primefaces.model.filter.FunctionFilterConstraint}
     * silently discarded, since {@link UITable#updateFilterByValuesWithFilterRequest} unconditionally rebuilds
     * the constraint from the submitted match mode whenever a column's match mode becomes selectable at all
     * ({@code isMatchModeSelectable()} - i.e. non-empty {@code matchModeOptions}). Both cases return {@code null}.
     * Otherwise, the type is auto-derived from the column's actual Java type - see
     * {@link #resolveColumnJavaType(FacesContext, UITable, UIColumn)}. The {@code "numeric"}/{@code "date"}/
     * {@code "time"}/{@code "datetime"} presets' comparators (equals/less/greater/between) compare via
     * {@link org.primefaces.model.filter.ComparableFilterConstraint}, which requires the raw, still-a-{@code String}
     * filter value to already have been converted to the column's actual type before comparing - without a
     * configured {@code converter} to do that conversion, filtering throws instead of silently misbehaving (see
     * {@code ComparableFilterConstraint#assertAssignable}), so those 4 presets are only offered when the column
     * already has one. In every case where the auto-derived type doesn't apply, fall back to {@code "text"} only
     * if doing so wouldn't silently change the column's own already-working, explicitly configured
     * {@code filterMatchMode} (e.g. {@code "gte"} isn't offered by the {@code "text"} preset) - otherwise return
     * {@code null} rather than risk silently downgrading (or, per the above, breaking) it.
     *
     * @return a preset keyword (or explicit comma-separated match-mode list) suitable for
     * {@link MatchMode#parseOptions(String)}, or {@code null} if the column should get no dropdown at all
     */
    public static String resolveFilterValueType(FacesContext context, UITable<?> table, UIColumn column) {
        String explicit = column.getFilterValueType();
        if (LangUtils.isNotBlank(explicit)) {
            return explicit;
        }

        if (column.getFacet("filter") != null || column.getFilterFunction() != null) {
            return null;
        }

        Class<?> type = resolveColumnJavaType(context, table, column);
        String derived = type == null ? null : resolveValueTypeFromClass(type);
        boolean requiresConverterToCompare = "numeric".equals(derived) || "date".equals(derived)
                || "time".equals(derived) || "datetime".equals(derived);
        if (derived != null && (!requiresConverterToCompare || column.getConverter() != null)) {
            return derived;
        }

        return MatchMode.TEXT_OPTIONS.contains(MatchMode.of(column.getFilterMatchMode())) ? "text" : null;
    }

    /**
     * Reflects the column's own leaf property type against the table's row type, without needing any live row
     * data (works even for an empty table): the table's own {@code value} expression is resolved first (its EL
     * root is a real bean, e.g. {@code #{bean.customers}}, unlike the column's {@code filterBy}/{@code field}
     * expression, whose root is the per-row {@code var} - unbound at this point in the lifecycle), then
     * {@link LangUtils#getTypeFromCollectionProperty(Object, String)} reads the row element type reflectively
     * off that getter's generic signature (the same technique {@code BaseCalendarRenderer#resolveDateType()}
     * already uses for a multi-select date picker), and finally the column's field path (dot-separated for
     * nested properties, e.g. {@code "country.name"}) is walked against that row type via {@link Introspector}.
     *
     * @return the resolved type, or {@code null} if it can't be determined (computed/non-property {@code filterBy},
     * no row type, unresolvable path segment, ...)
     */
    public static Class<?> resolveColumnJavaType(FacesContext context, UITable<?> table, UIColumn column) {
        try {
            ValueExpression tableValueVE = ((UIComponent) table).getValueExpression("value");
            if (tableValueVE == null) {
                return null;
            }

            // deliberately not gated on tableValueVE.getType(elContext): for a plain JSF tag-attribute binding
            // like value="#{bean.customers}", that call unreliably returns null in practice (observed even when
            // the expression resolves to a real, non-empty list) - going straight for the EL AST's (base,
            // property) pair and reflecting on the getter's own generic signature, exactly as
            // LangUtils#getTypeFromCollectionProperty already does for BaseCalendarRenderer#resolveDateType(),
            // sidesteps that unreliability entirely.
            ELContext elContext = context.getELContext();
            ValueReference valueReference = ValueExpressionAnalyzer.getReference(elContext, tableValueVE);
            if (valueReference == null || valueReference.getBase() == null || valueReference.getProperty() == null) {
                return null;
            }

            Class<?> rowType = LangUtils.getTypeFromCollectionProperty(valueReference.getBase(), String.valueOf(valueReference.getProperty()));
            if (rowType == null) {
                return null;
            }

            String field = column.getField();
            if (LangUtils.isBlank(field)) {
                ValueExpression filterByVE = column.getValueExpression(Column.PropertyKeys.filterBy.name());
                if (filterByVE != null) {
                    field = column.resolveField(context, filterByVE);
                }
            }
            if (LangUtils.isBlank(field)) {
                return null;
            }

            Class<?> currentType = rowType;
            for (String segment : field.split("\\.")) {
                currentType = resolveBeanPropertyType(currentType, segment);
                if (currentType == null) {
                    return null;
                }
            }

            return currentType;
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    private static Class<?> resolveBeanPropertyType(Class<?> beanClass, String property) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(beanClass).getPropertyDescriptors()) {
                if (pd.getName().equals(property) && pd.getReadMethod() != null) {
                    return pd.getPropertyType();
                }
            }
        }
        catch (IntrospectionException e) {
            return null;
        }
        return null;
    }

    private static String resolveValueTypeFromClass(Class<?> type) {
        if (Number.class.isAssignableFrom(type)
                || type == int.class || type == long.class || type == double.class
                || type == float.class || type == short.class || type == byte.class) {
            return "numeric";
        }
        if (type == boolean.class || Boolean.class.isAssignableFrom(type)) {
            return "boolean";
        }
        if (type == LocalDate.class) {
            return "date";
        }
        if (type == LocalTime.class) {
            return "time";
        }
        if (type == LocalDateTime.class || Instant.class.isAssignableFrom(type) || OffsetDateTime.class.isAssignableFrom(type)
                || ZonedDateTime.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)) {
            return "datetime";
        }
        if (type.isEnum()) {
            return "enum";
        }
        if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            return "array";
        }
        return "text";
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
                || (filterValue instanceof Collection && ((Collection<?>) filterValue).isEmpty())
                || (filterValue instanceof Iterable && !((Iterable<?>) filterValue).iterator().hasNext())
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
        // MatchMode.ALL is an explicit "no filter selected" placeholder (the default for a dropdown built
        // entirely from value-less modes, e.g., a "boolean" column) - always inactive, regardless of requiresValue().
        if (matchMode == MatchMode.ALL) {
            return false;
        }
        // a value-less match mode (e.g., "is empty", "is null") is its own complete predicate - it applies even
        // though the user never types (and the input never renders) a filter value.
        return filterValue != null || !matchMode.requiresValue();
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
    }

    /**
     * The match modes an end user may pick from this filter's match-mode dropdown; empty when the column's
     * match mode is fixed and no dropdown is rendered.
     */
    public List<MatchMode> getMatchModeOptions() {
        return matchModeOptions;
    }

    public void setMatchModeOptions(List<MatchMode> matchModeOptions) {
        this.matchModeOptions = matchModeOptions == null ? Collections.emptyList() : matchModeOptions;
    }

    public boolean isMatchModeSelectable() {
        return !matchModeOptions.isEmpty();
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

        public Builder matchModeOptions(List<MatchMode> matchModeOptions) {
            filterBy.setMatchModeOptions(matchModeOptions);
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
