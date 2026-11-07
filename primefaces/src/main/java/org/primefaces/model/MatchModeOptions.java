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

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.api.UITable;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.filter.FilterConstraints;
import org.primefaces.util.LangUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

/**
 * Resolves the match modes an end user may pick for a column filter, as configured by the {@code filterMatchModeOptions}
 * attribute of the column or (as default for all columns) of the table.
 * <p>
 * Supported values: not set or {@code none} (no picker), {@code auto} (preset derived from the column's value type), a preset
 * ({@code text}, {@code numeric}, {@code date}), a comma-separated list of {@link MatchMode} operators (e.g.
 * {@code contains,startsWith,equals}), or a {@code Collection<MatchMode>}. DataTable only, TreeTable ignores it.
 * <p>
 * The picked match mode is submitted per column as request parameter {@code <column clientId>:filterMatchMode} with every
 * filter request and only accepted if it is one of the column's options. Picking another match mode always sends a filter
 * request, also with an empty filter value, so the server side {@link FilterMeta} (and with it AJAX updates and the multi view
 * state) always knows the picked match mode.
 */
public final class MatchModeOptions {

    public static final String ATTRIBUTE = "filterMatchModeOptions";

    public static final String NONE = "none";
    public static final String AUTO = "auto";

    public static final List<MatchMode> TEXT = List.of(
            MatchMode.STARTS_WITH, MatchMode.NOT_STARTS_WITH, MatchMode.ENDS_WITH, MatchMode.NOT_ENDS_WITH,
            MatchMode.CONTAINS, MatchMode.NOT_CONTAINS, MatchMode.EXACT, MatchMode.NOT_EXACT);

    public static final List<MatchMode> NUMERIC = List.of(
            MatchMode.EQUALS, MatchMode.NOT_EQUALS, MatchMode.LESS_THAN, MatchMode.LESS_THAN_EQUALS,
            MatchMode.GREATER_THAN, MatchMode.GREATER_THAN_EQUALS);

    public static final List<MatchMode> DATE = NUMERIC;

    /**
     * Match modes that are not offered by the picker (yet): {@code global} is no column filter, and the multi-value modes
     * need a value separator.
     */
    private static final Set<MatchMode> UNSUPPORTED = Set.of(
            MatchMode.GLOBAL, MatchMode.IN, MatchMode.NOT_IN, MatchMode.BETWEEN, MatchMode.NOT_BETWEEN);

    private MatchModeOptions() {
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
    public static void apply(FacesContext context, UITable<?> table, UIColumn column, FilterMeta filterMeta) {
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
    public static MatchMode initialMatchMode(UIColumn column, List<MatchMode> options) {
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
     * The semantic icon class of a match mode, e.g. {@code p-filter-matchmode-starts-with-icon}. It is not table specific,
     * the CSS maps it to an icon token.
     *
     * @param matchMode the match mode
     * @return the icon class
     */
    public static String iconClass(MatchMode matchMode) {
        return "p-filter-matchmode-" + matchMode.operator().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase(Locale.ROOT) + "-icon";
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
    public static List<MatchMode> resolve(FacesContext context, UITable<?> table, UIColumn column) {
        // a custom filter (facet or function) does not filter by match mode
        if (column.getFacet("filter") != null || column.getFilterFunction() != null) {
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
     * {@code value} property, the column's {@code field} (or the field resolved from {@code filterBy}) is then walked as bean
     * property path on it.
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

            Class<?> type = LangUtils.getTypeFromCollectionProperty(reference.getBase(), String.valueOf(reference.getProperty()));
            if (type == null) {
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

            for (String property : field.split("\\.")) {
                type = resolvePropertyType(type, property);
                if (type == null) {
                    return null;
                }
            }
            return type;
        }
        catch (RuntimeException e) {
            return null;
        }
    }

    private static Class<?> resolvePropertyType(Class<?> beanClass, String property) {
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(beanClass).getPropertyDescriptors()) {
                if (pd.getName().equals(property) && pd.getReadMethod() != null) {
                    return pd.getPropertyType();
                }
            }
            return null;
        }
        catch (IntrospectionException e) {
            return null;
        }
    }
}
