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
import org.primefaces.util.LangUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Built-in filter operators
 */
public enum MatchMode {

    STARTS_WITH("startsWith", "pi-align-left", "≺"),
    NOT_STARTS_WITH("notStartsWith", "pi-align-center", "⊀"),

    ENDS_WITH("endsWith", "pi-align-right", "≻"),
    NOT_ENDS_WITH("notEndsWith", "pi-align-justify", "⊁"),

    CONTAINS("contains", "pi-search", "∋"),
    NOT_CONTAINS("notContains", "pi-search-minus", "∌"),

    EXACT("exact", "pi-lock", "≡"),
    NOT_EXACT("notExact", "pi-unlock", "≢"),

    LESS_THAN("lt", "pi-angle-left", "<"),
    LESS_THAN_EQUALS("lte", "pi-angle-double-left", "≤"),

    GREATER_THAN("gt", "pi-angle-right", ">"),
    GREATER_THAN_EQUALS("gte", "pi-angle-double-right", "≥"),

    EQUALS("equals", "pi-equals", "="),
    NOT_EQUALS("notEquals", "pi-ban", "≠"),

    IN("in", "pi-list", "∈"),
    NOT_IN("notIn", "pi-times", "∉"),

    BETWEEN("between", "pi-arrows-h", "↔"),
    NOT_BETWEEN("notBetween", "pi-sliders-h", "↮"),

    /**
     * Matches when the field value is {@code null}, an empty/blank string, or an empty {@code Collection}/array.
     */
    IS_EMPTY("empty", "pi-circle", "∅", false),
    /**
     * Matches when the field value is neither {@code null}, an empty/blank string, nor an empty
     * {@code Collection}/array.
     */
    NOT_EMPTY("notEmpty", "pi-circle-fill", "∃", false),
    /**
     * Matches when the field value is strictly {@code null}, unlike {@link #IS_EMPTY} which also matches a
     * non-null but blank string.
     */
    IS_NULL("null", "pi-question-circle", "○", false),
    /**
     * Matches when the field value is not {@code null} (a blank string still matches).
     */
    NOT_NULL("notNull", "pi-check-circle", "●", false),
    /**
     * Matches when the field value, as a string, matches the filter value interpreted as a regular expression.
     */
    MATCHES_REGEX("regex", "pi-asterisk", "*"),
    /**
     * Matches when the field value is strictly {@code Boolean.TRUE} (or the string {@code "true"}).
     */
    IS_TRUE("true", "pi-check", "✓", false),
    /**
     * Matches when the field value is strictly {@code Boolean.FALSE} (or the string {@code "false"}).
     */
    IS_FALSE("false", "pi-times-circle", "✗", false),
    /**
     * "No filter selected" placeholder - {@link FilterMeta#isActive()} always treats it as inactive, regardless
     * of {@link #requiresValue()}. Needed as the default option for a dropdown built entirely from value-less
     * modes (e.g., {@link #BOOLEAN_MATCH_MODES}): unlike "contains" or "equals", none of "true"/"false"/"is null"/
     * "is not null" has a natural "nothing typed yet" resting state, so without this placeholder such a column
     * would silently start filtered (to whichever mode happens to be first) the moment the page renders.
     */
    ALL("all", "pi-sliders-v", "∀", false),
    /**
     * Relative-date predicates for filterValueType="date", each computed against LocalDate.now() at the moment
     * the filter runs - value-less, like IS_EMPTY/IS_TRUE/etc.
     */
    IS_TODAY("today", "pi-sun", "◆", false),
    IS_YESTERDAY("yesterday", "pi-moon", "◁", false),
    IS_TOMORROW("tomorrow", "pi-arrow-right", "▷", false),
    IS_THIS_WEEK("thisWeek", "pi-calendar", "▣", false),
    IS_LAST_WEEK("lastWeek", "pi-calendar-minus", "◀", false),
    IS_NEXT_WEEK("nextWeek", "pi-calendar-plus", "▶", false),
    IS_THIS_MONTH("thisMonth", "pi-table", "▦", false),
    IS_LAST_MONTH("lastMonth", "pi-arrow-circle-left", "⇐", false),
    IS_NEXT_MONTH("nextMonth", "pi-arrow-circle-right", "⇒", false),
    IS_THIS_QUARTER("thisQuarter", "pi-th-large", "▤", false),
    IS_LAST_QUARTER("lastQuarter", "pi-chevron-circle-left", "↞", false),
    IS_NEXT_QUARTER("nextQuarter", "pi-chevron-circle-right", "↠", false),
    IS_THIS_YEAR("thisYear", "pi-book", "▥", false),
    IS_LAST_YEAR("lastYear", "pi-caret-left", "↢", false),
    IS_NEXT_YEAR("nextYear", "pi-caret-right", "↣", false),
    /**
     * Matches the last N days up to and including today; N is the typed filter value.
     */
    LAST_N_DAYS("lastNDays", "pi-history", "⏮"),
    /**
     * Matches the next N days starting today; N is the typed filter value.
     */
    NEXT_N_DAYS("nextNDays", "pi-stopwatch", "⏭"),
    /**
     * Matches within N days of today in either direction; N is the typed filter value.
     */
    RELATIVE_DATE("relativeDate", "pi-compass", "⟲"),
    /**
     * Matches the last N minutes up to and including now; N is the typed filter value. Works on a bare
     * {@code LocalTime} field too (a cyclic 24h clock - the window can wrap past midnight), not just a full
     * date+time value.
     */
    LAST_N_MINUTES("lastNMinutes", "pi-clock", "⏪"),
    /**
     * Matches the next N minutes starting now; N is the typed filter value. See {@link #LAST_N_MINUTES} for the
     * bare-{@code LocalTime} wraparound note.
     */
    NEXT_N_MINUTES("nextNMinutes", "pi-bolt", "⏩"),
    /**
     * Matches the last N hours up to and including now; N is the typed filter value. See {@link #LAST_N_MINUTES}
     * for the bare-{@code LocalTime} wraparound note.
     */
    LAST_N_HOURS("lastNHours", "pi-calendar-clock", "↺"),
    /**
     * Matches the next N hours starting now; N is the typed filter value. See {@link #LAST_N_MINUTES} for the
     * bare-{@code LocalTime} wraparound note.
     */
    NEXT_N_HOURS("nextNHours", "pi-refresh", "↻"),
    /**
     * Matches when a {@code Collection} or array field value contains the single typed filter value. Unlike
     * {@link #CONTAINS} (a string substring match), this expects the field itself to be multivalue - e.g., a
     * {@code List<String>} of tags.
     */
    ARRAY_CONTAINS("arrayContains", "pi-tags", "⊇"),
    /**
     * Matches when a {@code Collection}/array field value does not contain the typed filter value.
     * See {@link #ARRAY_CONTAINS}.
     */
    ARRAY_NOT_CONTAINS("arrayNotContains", "pi-tag", "⊉"),
    /**
     * Matches when a {@code Collection}/array field value contains at least one of the comma-separated typed
     * filter values (a non-empty intersection). See {@link #ARRAY_CONTAINS}.
     */
    CONTAINS_ANY("containsAny", "pi-share-alt", "∪"),
    /**
     * Matches when a {@code Collection}/array field value contains every one of the comma-separated typed
     * filter values (the field is a superset of the typed values). See {@link #ARRAY_CONTAINS}.
     */
    CONTAINS_ALL("containsAll", "pi-check-square", "∩"),
    /**
     * Matches when a {@code Collection}/array field value contains none of the comma-separated typed filter
     * values (an empty intersection) - the negation of {@link #CONTAINS_ANY}.
     */
    CONTAINS_NONE("containsNone", "pi-minus-circle", "⊘"),

    GLOBAL("global", "pi-globe", "⊛");

    /**
     * Preset of match modes offered for a numeric {@code filterValueType="numeric"} column filter.
     */
    public static final List<MatchMode> NUMERIC_MATCH_MODES = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_NULL, NOT_NULL, IN, NOT_IN);

    /**
     * Preset of match modes offered for a {@code filterValueType="text"} column filter.
     */
    public static final List<MatchMode> TEXT_MATCH_MODES = List.of(
            CONTAINS, NOT_CONTAINS, STARTS_WITH, NOT_STARTS_WITH, ENDS_WITH, NOT_ENDS_WITH, EQUALS, NOT_EQUALS,
            IS_EMPTY, NOT_EMPTY, IS_NULL, NOT_NULL, MATCHES_REGEX, IN, NOT_IN);

    /**
     * Preset of match modes offered for a {@code filterValueType="date"} column filter.
     */
    public static final List<MatchMode> DATE_MATCH_MODES = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_EMPTY, NOT_EMPTY,
            IS_TODAY, IS_YESTERDAY, IS_TOMORROW,
            IS_THIS_WEEK, IS_LAST_WEEK, IS_NEXT_WEEK,
            IS_THIS_MONTH, IS_LAST_MONTH, IS_NEXT_MONTH,
            IS_THIS_QUARTER, IS_LAST_QUARTER, IS_NEXT_QUARTER,
            IS_THIS_YEAR, IS_LAST_YEAR, IS_NEXT_YEAR,
            LAST_N_DAYS, NEXT_N_DAYS, RELATIVE_DATE);

    /**
     * Preset of match modes offered for a {@code filterValueType="boolean"} column filter.
     */
    public static final List<MatchMode> BOOLEAN_MATCH_MODES = List.of(ALL, IS_TRUE, IS_FALSE, IS_NULL, NOT_NULL);

    /**
     * Preset of match modes offered for a {@code filterValueType="enum"} column filter (a Java
     * {@code enum} value).
     */
    public static final List<MatchMode> ENUM_MATCH_MODES = List.of(EQUALS, NOT_EQUALS, IN, NOT_IN, IS_EMPTY, NOT_EMPTY);

    /**
     * Preset of match modes offered for a {@code filterValueType="array"} column filter (a multivalue
     * field, e.g., a {@code List<String>} of tags).
         */
    public static final List<MatchMode> ARRAY_MATCH_MODES = List.of(
            ARRAY_CONTAINS, ARRAY_NOT_CONTAINS, CONTAINS_ANY, CONTAINS_ALL, CONTAINS_NONE, IS_EMPTY, NOT_EMPTY);

    /**
     * Preset of match modes offered for a {@code filterValueType="time"} column filter.
     */
    public static final List<MatchMode> TIME_MATCH_MODES = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_EMPTY, NOT_EMPTY,
            LAST_N_MINUTES, NEXT_N_MINUTES, LAST_N_HOURS, NEXT_N_HOURS);

    /**
     * Preset of match modes offered for a {@code filterValueType="datetime"} column filter.
         */
    public static final List<MatchMode> DATETIME_MATCH_MODES;
    static {
        List<MatchMode> modes = new ArrayList<>(DATE_MATCH_MODES);
        modes.add(LAST_N_MINUTES);
        modes.add(NEXT_N_MINUTES);
        modes.add(LAST_N_HOURS);
        modes.add(NEXT_N_HOURS);
        DATETIME_MATCH_MODES = Collections.unmodifiableList(modes);
    }

    private final String operator;
    private final String icon;
    private final String symbol;
    private final boolean requiresValue;

    MatchMode(String operator, String icon, String symbol) {
        this(operator, icon, symbol, true);
    }

    MatchMode(String operator, String icon, String symbol, boolean requiresValue) {
        this.operator = operator;
        this.icon = icon;
        this.symbol = symbol;
        this.requiresValue = requiresValue;
    }

    public String operator() {
        return operator;
    }

    /**
     * The PrimeIcons class (e.g., {@code "pi-equals"} for {@link #EQUALS}) representing this match mode. Not
     * currently rendered anywhere itself - {@link #symbol()} is the visible glyph in the filter match-mode
     * overlay menu and the column header's active-mode badge - but kept as a distinct, always-populated
     * per-mode identifier for callers that want an icon-font glyph instead of a Unicode character.
     *
     * @return the PrimeIcons class, without the leading {@code "pi "} base class
     */
    public String icon() {
        return icon;
    }

    /**
     * A single, distinct Unicode character standing in for this match mode (e.g., {@code "≠"} for
     * {@link #NOT_EQUALS}, {@code "∅"} for {@link #IS_EMPTY}) - every match mode has one. Rendered as the
     * visible glyph in the first column of the filter match-mode overlay menu, and beside the filter trigger
     * icon in the column header once this mode is the active (non-default) selection, so the reader can tell
     * which kind of filter is applied to a column without opening the menu.
     *
     * @return the single-character symbol
     */
    public String symbol() {
        return symbol;
    }

    /**
     * Whether this match mode needs a filter value to be typed in, e.g., {@link #CONTAINS} does but
     * {@link #IS_EMPTY} does not - the mode alone is the entire predicate. A column's filter value
     * {@code <input>} is hidden while a match mode with {@code requiresValue() == false} is selected.
         *
     * @return {@code true} unless this match mode is a value-less predicate
     */
    public boolean requiresValue() {
        return requiresValue;
    }

    /**
     * An example hint for the syntax the filter value {@code <input>} expects, shown as its placeholder while
     * this match mode is selected (e.g., {@code "min,max"} for {@link #BETWEEN}).
     *
     * @return the placeholder hint, or {@code null} if this match mode expects a single plain value
     */
    public String placeholderHint() {
        switch (this) {
            case BETWEEN:
            case NOT_BETWEEN:
                return "min,max";
            case IN:
            case NOT_IN:
            case CONTAINS_ANY:
            case CONTAINS_ALL:
            case CONTAINS_NONE:
                return "value1, value2, ...";
            case LAST_N_DAYS:
            case NEXT_N_DAYS:
            case RELATIVE_DATE:
            case LAST_N_MINUTES:
            case NEXT_N_MINUTES:
            case LAST_N_HOURS:
            case NEXT_N_HOURS:
                return "e.g., 30";
            default:
                return null;
        }
    }

    public static MatchMode of(String operator) {
        if (LangUtils.isBlank(operator)) {
            return UIColumn.DEFAULT_FILTER_MATCH_MODE;
        }

        for (MatchMode mode : MatchMode.values()) {
            if (mode.operator().equals(operator)) {
                return mode;
            }
        }
        throw new UnsupportedOperationException("Unknown match mode: " + operator);
    }

    /**
     * Resolves the list of match modes an end user may pick from a column's filter match-mode dropdown.
     * <p>
     * Accepts either one of the shorthand keywords {@code "numeric"}, {@code "text"}, {@code "date"},
     * {@code "boolean"}, {@code "time"}, {@code "datetime"}, {@code "enum"} or {@code "array"}, which expand to
     * a curated preset of {@link MatchMode}s; an explicit comma-separated list of match mode operators
     * (e.g., {@code "equals,notEquals,lt,gt,lte,gte"}); or {@code "none"}, which opts a column out of the
     * dropdown even though its {@code filterValueType} would otherwise be auto-derived from its Java type.
     *
     * @param filterValueType the value of the column's {@code filterValueType} attribute
     * @return the resolved, ordered list of selectable match modes; empty if {@code filterValueType} is blank or {@code "none"}
     */
    public static List<MatchMode> parseOptions(String filterValueType) {
        if (LangUtils.isBlank(filterValueType)) {
            return Collections.emptyList();
        }

        switch (filterValueType.trim()) {
            case "none":
                return Collections.emptyList();
            case "numeric":
                return NUMERIC_MATCH_MODES;
            case "text":
                return TEXT_MATCH_MODES;
            case "date":
                return DATE_MATCH_MODES;
            case "boolean":
                return BOOLEAN_MATCH_MODES;
            case "enum":
                return ENUM_MATCH_MODES;
            case "array":
                return ARRAY_MATCH_MODES;
            case "time":
                return TIME_MATCH_MODES;
            case "datetime":
                return DATETIME_MATCH_MODES;
            default:
                return Arrays.stream(filterValueType.split(","))
                        .map(String::trim)
                        .filter(LangUtils::isNotBlank)
                        .map(MatchMode::of)
                        .collect(Collectors.toList());
        }
    }
}
