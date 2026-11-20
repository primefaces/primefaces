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
package org.primefaces.component.api;

import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.MatchMode;
import org.primefaces.model.filter.FilterConstraints;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.LangUtils;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;

/**
 * Resolves the match modes an end user may pick for a column filter, as configured by the {@code filterMatchModeOptions}
 * attribute of the column or (as default for all columns) of the table. Only used by {@link UITable}, for tables that
 * support it ({@link UITable#isFilterMatchModeOptionsSupported()}).
 * <p>
 * Supported values: not set or {@code none} (no picker), {@code auto} (preset derived from the column's value type), a preset
 * ({@code text}, {@code numeric}, {@code date}), a comma-separated list of {@link MatchMode} operators (e.g.
 * {@code contains,startsWith,equals}), or a {@code Collection<MatchMode>}.
 * <p>
 * The picked match mode is submitted per column as request parameter {@code <column clientId>:filterMatchMode} with every
 * filter request and only accepted if it is one of the column's options. Picking another match mode always sends a filter
 * request, also with an empty filter value, so the server side {@link FilterMeta} (and with it AJAX updates and the multi view
 * state) always knows the picked match mode.
 */
final class FilterMatchModeOptions {

    static final String ATTRIBUTE = "filterMatchModeOptions";

    static final String NONE = "none";
    static final String AUTO = "auto";

    static final List<MatchMode> TEXT = List.of(
            MatchMode.STARTS_WITH, MatchMode.NOT_STARTS_WITH, MatchMode.ENDS_WITH, MatchMode.NOT_ENDS_WITH,
            MatchMode.CONTAINS, MatchMode.NOT_CONTAINS, MatchMode.EXACT, MatchMode.NOT_EXACT);

    static final List<MatchMode> NUMERIC = List.of(
            MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN, MatchMode.LESS_THAN_EQUALS,
            MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS);

    static final List<MatchMode> DATE = NUMERIC;

    /**
     * Separator of the values of an {@code in} / {@code notIn} filter: not a comma, which is part of decimal numbers and date
     * patterns in many locales.
     */
    static final String VALUE_SEPARATOR = ";";

    /**
     * Match modes that are not offered by the picker (yet): {@code global} is no column filter, and the multi-value modes
     * need a value separator.
     */
    private static final Set<MatchMode> UNSUPPORTED = Set.of(
            MatchMode.GLOBAL, MatchMode.IN, MatchMode.NOT_IN, MatchMode.BETWEEN, MatchMode.NOT_BETWEEN);

    private FilterMatchModeOptions() {
        // NOOP
    }

    /**
     * Resolves the match mode options of a column and applies the initial match mode (see {@link #initialMatchMode}) to its
     * {@link FilterMeta}.
     *
     * @param context the {@link FacesContext}
     * @param table the table
     * @param column the column
     * @param filterMeta the column's {@link FilterMeta}
     * @throws FacesException if {@code filterMatchMode} is set but not one of the options
     */
    static void apply(FacesContext context, UITable<?> table, UIColumn column, FilterMeta filterMeta) {
        List<MatchMode> options = resolve(context, table, column);
        filterMeta.setMatchModeOptions(options);
        if (options.isEmpty()) {
            return;
        }

        MatchMode matchMode = initialMatchMode(column, options);
        filterMeta.setMatchMode(matchMode);
        filterMeta.setConstraint(FilterConstraints.of(matchMode));
    }

    /**
     * The initial match mode of a column with a match mode picker: the column's {@code filterMatchMode} if it is set, otherwise
     * the first option (not the default {@code startsWith}).
     *
     * @param column the column
     * @param options the column's match mode options, not empty
     * @return the initial match mode
     * @throws FacesException if {@code filterMatchMode} is set but not one of the options
     */
    static MatchMode initialMatchMode(UIColumn column, List<MatchMode> options) {
        if (!isFilterMatchModeSet(column)) {
            return options.get(0);
        }

        MatchMode matchMode = MatchMode.of(column.getFilterMatchMode());
        if (!options.contains(matchMode)) {
            throw new FacesException("filterMatchMode '" + column.getFilterMatchMode() + "' of column '" + column.getColumnKey()
                    + "' is not one of its filterMatchModeOptions " + operators(options));
        }
        return matchMode;
    }

    /**
     * Checks the match mode of a {@link FilterMeta} passed with the {@code filterBy} attribute of the table: like
     * {@code filterMatchMode}, it must be one of the column's options.
     *
     * @param filterMeta the column's {@link FilterMeta}
     * @param matchMode the match mode of the {@code filterBy} {@link FilterMeta}
     * @throws FacesException if the column has a match mode picker and the match mode is not one of its options
     */
    static void validateFilterByMatchMode(FilterMeta filterMeta, MatchMode matchMode) {
        List<MatchMode> options = filterMeta.getMatchModeOptions();
        if (!options.isEmpty() && !options.contains(matchMode)) {
            throw new FacesException("Match mode '" + (matchMode == null ? null : matchMode.operator()) + "' of the filterBy for column '"
                    + filterMeta.getColumnKey() + "' is not one of its filterMatchModeOptions " + operators(options));
        }
    }

    /**
     * Converts the submitted filter value of a column with a match mode picker with the column's converter. The multi-value
     * match modes get a {@link List}: {@code in} / {@code notIn} the tokens of {@code value} split by {@link #VALUE_SEPARATOR},
     * {@code between} / {@code notBetween} the two values {@code value} and {@code valueTo}.
     *
     * @param context the {@link FacesContext}
     * @param column the column
     * @param matchMode the column's match mode
     * @param value the submitted filter value
     * @param valueTo the submitted second filter value, only used by {@code between} / {@code notBetween}
     * @return the filter value, {@code null} if it is empty or does not convert (a multi-value filter is only active if all its
     *         values convert)
     */
    static Object convertFilterValue(FacesContext context, UIColumn column, MatchMode matchMode, String value, String valueTo) {
        try {
            switch (matchMode) {
                case IN:
                case NOT_IN: {
                    List<Object> values = new ArrayList<>();
                    for (String token : LangUtils.isBlank(value) ? new String[0] : value.split(VALUE_SEPARATOR)) {
                        if (LangUtils.isNotBlank(token)) {
                            values.add(convert(context, column, token.trim()));
                        }
                    }
                    return values.isEmpty() || values.contains(null) ? null : values;
                }
                case BETWEEN:
                case NOT_BETWEEN: {
                    if (LangUtils.isBlank(value) || LangUtils.isBlank(valueTo)) {
                        return null;
                    }
                    Object from = convert(context, column, value.trim());
                    Object to = convert(context, column, valueTo.trim());
                    return from == null || to == null ? null : Arrays.asList(from, to);
                }
                default:
                    return convert(context, column, value);
            }
        }
        catch (ConverterException e) {
            return null;
        }
    }

    private static Object convert(FacesContext context, UIColumn column, String value) {
        return ComponentUtils.getConvertedValue(context, column.asUIComponent(), column.getConverter(), value);
    }

    /**
     * Whether {@code filterMatchMode} is set, the getters of {@link Column} and {@link Columns} return the default otherwise.
     */
    private static boolean isFilterMatchModeSet(UIColumn column) {
        UIComponent component = column.asUIComponent();
        if (component instanceof Column c) {
            return c.isFilterMatchModeSet();
        }
        if (component instanceof Columns c) {
            return c.isFilterMatchModeSet();
        }
        return LangUtils.isNotBlank(column.getFilterMatchMode());
    }

    /**
     * Resolves the match modes an end user may pick for a column filter.
     *
     * @param context the {@link FacesContext}
     * @param table the table, its {@code filterMatchModeOptions} is the default for all columns
     * @param column the column
     * @return the match modes in the order of the picker, empty if the column gets no picker
     * @throws FacesException if the configured value is invalid
     */
    static List<MatchMode> resolve(FacesContext context, UITable<?> table, UIColumn column) {
        // a custom filter (facet or function) does not filter by match mode, same check as the renderer
        if (FacetUtils.shouldRenderFacet(column.getFilterFacet(), table.isRenderEmptyFacets()) || column.getFilterFunction() != null) {
            return Collections.emptyList();
        }

        Object value = column.asUIComponent().getAttributes().get(ATTRIBUTE);
        if (isUnset(value)) {
            value = table.getFilterMatchModeOptions();
        }
        if (isUnset(value)) {
            return Collections.emptyList();
        }

        if (value instanceof Collection<?> collection) {
            List<MatchMode> options = new ArrayList<>(collection.size());
            for (Object option : collection) {
                if (!(option instanceof MatchMode matchMode)) {
                    throw new FacesException("filterMatchModeOptions of column '" + column.getColumnKey()
                            + "' must be a String or a Collection<MatchMode>, but contains " + option);
                }
                options.add(matchMode);
            }
            return validate(column, options);
        }

        if (!(value instanceof String)) {
            throw new FacesException("filterMatchModeOptions of column '" + column.getColumnKey()
                    + "' must be a String or a Collection<MatchMode>, but is " + value.getClass().getName());
        }

        String options = ((String) value).trim();
        switch (options) {
            case NONE:
                return Collections.emptyList();
            case AUTO:
                return resolveAuto(context, table, column);
            case "text":
                return TEXT;
            case "numeric":
                return NUMERIC;
            case "date":
                return DATE;
            default:
                return validate(column, parseOperators(column, options));
        }
    }

    private static boolean isUnset(Object value) {
        return value == null || value instanceof String string && LangUtils.isBlank(string);
    }

    private static List<MatchMode> parseOperators(UIColumn column, String options) {
        List<MatchMode> matchModes = new ArrayList<>();
        for (String operator : options.split(",")) {
            try {
                matchModes.add(MatchMode.of(operator.trim()));
            }
            catch (UnsupportedOperationException e) {
                throw new FacesException("Unknown value '" + operator.trim() + "' in filterMatchModeOptions '" + options
                        + "' of column '" + column.getColumnKey() + "'. Valid values are none, auto, text, numeric, date"
                        + " or a comma-separated list of match modes, e.g. contains,startsWith,equals", e);
            }
        }
        return matchModes;
    }

    private static List<MatchMode> validate(UIColumn column, List<MatchMode> options) {
        for (MatchMode matchMode : options) {
            if (UNSUPPORTED.contains(matchMode)) {
                throw new FacesException("Match mode '" + matchMode.operator() + "' in filterMatchModeOptions of column '"
                        + column.getColumnKey() + "' is not supported by the match mode picker");
            }
        }
        return List.copyOf(new LinkedHashSet<>(options));
    }

    private static List<String> operators(List<MatchMode> options) {
        return options.stream().map(MatchMode::operator).toList();
    }

    /**
     * The preset for the column's value type: text, numeric or date. Numeric and date values are compared with the converted
     * filter value, so without a converter the column gets no picker. Other types (e.g. boolean, enum) get no picker.
     */
    private static List<MatchMode> resolveAuto(FacesContext context, UITable<?> table, UIColumn column) {
        Class<?> type = resolveColumnType(context, table, column);
        if (type == null) {
            return Collections.emptyList();
        }
        if (type == String.class || type == Character.class || type == char.class) {
            return TEXT;
        }

        List<MatchMode> preset;
        if (type != boolean.class && type.isPrimitive() || Number.class.isAssignableFrom(type)) {
            preset = NUMERIC;
        }
        else if (Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type) && Comparable.class.isAssignableFrom(type)) {
            preset = DATE;
        }
        else {
            return Collections.emptyList();
        }
        return column.getConverter() == null ? Collections.emptyList() : preset;
    }

    /**
     * Resolves the type of the column's property without row data: the row type is the element type of the table's
     * {@code value} property, the column's {@code field} (or the field resolved from {@code filterBy}) is then resolved as
     * (nested) bean property on it.
     *
     * @return the type or {@code null} if it cannot be resolved
     */
    private static Class<?> resolveColumnType(FacesContext context, UITable<?> table, UIColumn column) {
        try {
            ValueExpression valueVE = ((UIComponent) table).getValueExpression("value");
            if (valueVE == null) {
                return null;
            }

            ValueReference reference = ValueExpressionAnalyzer.getReference(context.getELContext(), valueVE);
            if (reference == null || reference.getBase() == null || reference.getProperty() == null) {
                return null;
            }

            Class<?> rowType = LangUtils.getTypeFromCollectionProperty(reference.getBase(), String.valueOf(reference.getProperty()));
            if (rowType == null) {
                return null;
            }

            String field = column.getField();
            if (LangUtils.isBlank(field)) {
                ValueExpression filterByVE = column.getValueExpression(Column.PropertyKeys.filterBy.name());
                field = filterByVE == null ? null : column.resolveField(context, filterByVE);
            }
            if (LangUtils.isBlank(field)) {
                return null;
            }

            return PrimeApplicationContext.getCurrentInstance(context).getPropertyDescriptorResolver().get(rowType, field).getPropertyType();
        }
        catch (RuntimeException e) {
            return null;
        }
    }
}
