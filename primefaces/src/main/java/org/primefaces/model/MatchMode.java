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

    STARTS_WITH("startsWith"),
    NOT_STARTS_WITH("notStartsWith"),

    ENDS_WITH("endsWith"),
    NOT_ENDS_WITH("notEndsWith"),

    CONTAINS("contains"),
    NOT_CONTAINS("notContains"),

    EXACT("exact"),
    NOT_EXACT("notExact"),

    LESS_THAN("lt", "<"),
    LESS_THAN_EQUALS("lte", "<="),

    GREATER_THAN("gt", ">"),
    GREATER_THAN_EQUALS("gte", ">="),

    EQUALS("equals", "="),
    NOT_EQUALS("notEquals", "!="),

    IN("in"),
    NOT_IN("notIn"),

    BETWEEN("between"),
    NOT_BETWEEN("notBetween"),

    /**
     * Matches when the field value is {@code null}, an empty/blank string, or an empty {@code Collection}/array.
         */
    IS_EMPTY("empty", false),
    /**
     * Matches when the field value is neither {@code null}, an empty/blank string, nor an empty
     * {@code Collection}/array.
     */
    NOT_EMPTY("notEmpty", false),
    /**
     * Matches when the field value is strictly {@code null}, unlike {@link #IS_EMPTY} which also matches a
     * non-null but blank string.
     */
    IS_NULL("null", false),
    /**
     * Matches when the field value is not {@code null} (a blank string still matches).
     */
    NOT_NULL("notNull", false),
    /**
     * Matches when the field value, as a string, matches the filter value interpreted as a regular expression.
         */
    MATCHES_REGEX("regex"),

    /**
     * Matches when the field value is {@code Boolean.TRUE} (or the string {@code "true"}).
     */
    IS_TRUE("true", false),
    /**
     * Matches when the field value is strictly {@code Boolean.FALSE} (or the string {@code "false"}) - a
     * {@code null} value matches neither {@link #IS_TRUE} nor {@link #IS_FALSE}; use {@link #IS_NULL} for that.
         */
    IS_FALSE("false", false),

    /**
     * "No filter selected" placeholder - {@link FilterMeta#isActive()} always treats it as inactive, regardless
     * of {@link #requiresValue()}. Needed as the default option for a dropdown built entirely from value-less
     * modes (e.g., {@link #BOOLEAN_OPTIONS}): unlike "contains" or "equals", none of "true"/"false"/"is null"/
     * "is not null" has a natural "nothing typed yet" resting state, so without this placeholder such a column
     * would silently start filtered (to whichever mode happens to be first) the moment the page renders.
         */
    ALL("all", false),

    // -- relative-date predicates for filterValueType="date", each computed against LocalDate.now() at
    // the moment the filter runs - value-less, like IS_EMPTY/IS_TRUE/etc.
    IS_TODAY("today", false),
    IS_YESTERDAY("yesterday", false),
    IS_TOMORROW("tomorrow", false),
    IS_THIS_WEEK("thisWeek", false),
    IS_LAST_WEEK("lastWeek", false),
    IS_NEXT_WEEK("nextWeek", false),
    IS_THIS_MONTH("thisMonth", false),
    IS_LAST_MONTH("lastMonth", false),
    IS_NEXT_MONTH("nextMonth", false),
    IS_THIS_QUARTER("thisQuarter", false),
    IS_LAST_QUARTER("lastQuarter", false),
    IS_NEXT_QUARTER("nextQuarter", false),
    IS_THIS_YEAR("thisYear", false),
    IS_LAST_YEAR("lastYear", false),
    IS_NEXT_YEAR("nextYear", false),

    /**
     * Matches the last N days up to and including today; N is the typed filter value.
     */
    LAST_N_DAYS("lastNDays"),
    /**
     * Matches the next N days starting today; N is the typed filter value.
     */
    NEXT_N_DAYS("nextNDays"),
    /**
     * Matches within N days of today in either direction; N is the typed filter value.
     */
    RELATIVE_DATE("relativeDate"),

    /**
     * Matches the last N minutes up to and including now; N is the typed filter value. Works on a bare
     * {@code LocalTime} field too (a cyclic 24h clock - the window can wrap past midnight), not just a full
     * date+time value.
     */
    LAST_N_MINUTES("lastNMinutes"),
    /**
     * Matches the next N minutes starting now; N is the typed filter value. See {@link #LAST_N_MINUTES} for the
     * bare-{@code LocalTime} wraparound note.
     */
    NEXT_N_MINUTES("nextNMinutes"),
    /**
     * Matches the last N hours up to and including now; N is the typed filter value. See {@link #LAST_N_MINUTES}
     * for the bare-{@code LocalTime} wraparound note.
     */
    LAST_N_HOURS("lastNHours"),
    /**
     * Matches the next N hours starting now; N is the typed filter value. See {@link #LAST_N_MINUTES} for the
     * bare-{@code LocalTime} wraparound note.
     */
    NEXT_N_HOURS("nextNHours"),

    /**
     * Matches when a {@code Collection} or array field value contains the single typed filter value. Unlike
     * {@link #CONTAINS} (a string substring match), this expects the field itself to be multivalue - e.g., a
     * {@code List<String>} of tags.
     */
    ARRAY_CONTAINS("arrayContains"),
    /**
     * Matches when a {@code Collection}/array field value does not contain the typed filter value.
     * See {@link #ARRAY_CONTAINS}.
     */
    ARRAY_NOT_CONTAINS("arrayNotContains"),
    /**
     * Matches when a {@code Collection}/array field value contains at least one of the comma-separated typed
     * filter values (a non-empty intersection). See {@link #ARRAY_CONTAINS}.
     */
    CONTAINS_ANY("containsAny"),
    /**
     * Matches when a {@code Collection}/array field value contains every one of the comma-separated typed
     * filter values (the field is a superset of the typed values). See {@link #ARRAY_CONTAINS}.
     */
    CONTAINS_ALL("containsAll"),
    /**
     * Matches when a {@code Collection}/array field value contains none of the comma-separated typed filter
     * values (an empty intersection) - the negation of {@link #CONTAINS_ANY}.
     */
    CONTAINS_NONE("containsNone"),

    GLOBAL("global");

    /**
     * Preset of match modes offered for a numeric {@code filterValueType="numeric"} column filter,
     * letting the user pick a comparator (=, !=, &lt;, &gt;, &lt;=, &gt;=), plus "(not) between", "is (not) null"
     * and "(not) in list".
     */
    public static final List<MatchMode> NUMERIC_OPTIONS = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_NULL, NOT_NULL, IN, NOT_IN);

    /**
     * Preset of match modes offered for a {@code filterValueType="text"} column filter: the classic string
     * operators plus "is (not) empty", "is (not) null", "matches regex" and "(not) in list".
     */
    public static final List<MatchMode> TEXT_OPTIONS = List.of(
            CONTAINS, NOT_CONTAINS, STARTS_WITH, NOT_STARTS_WITH, ENDS_WITH, NOT_ENDS_WITH, EQUALS, NOT_EQUALS,
            IS_EMPTY, NOT_EMPTY, IS_NULL, NOT_NULL, MATCHES_REGEX, IN, NOT_IN);

    /**
     * Preset of match modes offered for a {@code filterValueType="date"} column filter: the comparators
     * (labeled "Is"/"Is Not"/"Before"/"Before or On"/"After"/"After or On" for this preset specifically - see
     * {@link org.primefaces.component.datatable.DataTableRenderer}), "(not) between", "is (not) empty", a set of
     * relative-date predicates (today/yesterday/tomorrow, this/last/next week/month/quarter/year), and
     * "last/next N days"/"relative date" (typed as a number of days).
     */
    public static final List<MatchMode> DATE_OPTIONS = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_EMPTY, NOT_EMPTY,
            IS_TODAY, IS_YESTERDAY, IS_TOMORROW,
            IS_THIS_WEEK, IS_LAST_WEEK, IS_NEXT_WEEK,
            IS_THIS_MONTH, IS_LAST_MONTH, IS_NEXT_MONTH,
            IS_THIS_QUARTER, IS_LAST_QUARTER, IS_NEXT_QUARTER,
            IS_THIS_YEAR, IS_LAST_YEAR, IS_NEXT_YEAR,
            LAST_N_DAYS, NEXT_N_DAYS, RELATIVE_DATE);

    /**
     * Preset of match modes offered for a {@code filterValueType="boolean"} column filter: "All" (no
     * filter, the default), "true", "false", "is null" and "is not null" - every option is value-less, so the
     * filter value {@code <input>} stays hidden no matter which is selected.
     */
    public static final List<MatchMode> BOOLEAN_OPTIONS = List.of(ALL, IS_TRUE, IS_FALSE, IS_NULL, NOT_NULL);

    /**
     * Preset of match modes offered for a {@code filterValueType="enum"} column filter (a Java
     * {@code enum} value): "is"/"is not" (labeled "Is"/"Is Not" for this preset specifically - see
     * {@link org.primefaces.component.datatable.DataTableRenderer}), "is any of"/"is none of" (labeled
     * "Is Any Of"/"Is None Of", a multi-value match against {@link #IN}/{@link #NOT_IN}), and "is (not) empty".
     * Every mode here already exists for other presets - this preset just curates a relevant subset with
     * enum-appropriate labels.
     */
    public static final List<MatchMode> ENUM_OPTIONS = List.of(EQUALS, NOT_EQUALS, IN, NOT_IN, IS_EMPTY, NOT_EMPTY);

    /**
     * Preset of match modes offered for a {@code filterValueType="array"} column filter (a multivalue
     * field, e.g., a {@code List<String>} of tags): "contains"/"does not contain" (a single value),
     * "contains any"/"contains all"/"contains none" (a comma-separated list of values), and "is (not) empty".
         */
    public static final List<MatchMode> ARRAY_OPTIONS = List.of(
            ARRAY_CONTAINS, ARRAY_NOT_CONTAINS, CONTAINS_ANY, CONTAINS_ALL, CONTAINS_NONE, IS_EMPTY, NOT_EMPTY);

    /**
     * Preset of match modes offered for a {@code filterValueType="time"} column filter (a bare,
     * date-less time-of-day, e.g., {@code LocalTime}): the comparators, "(not) between", "is (not) empty", and
     * "last/next N minutes/hours". No day/week/month/... predicates - a bare time-of-day has no date component
     * for those to apply to.
     */
    public static final List<MatchMode> TIME_OPTIONS = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_EMPTY, NOT_EMPTY,
            LAST_N_MINUTES, NEXT_N_MINUTES, LAST_N_HOURS, NEXT_N_HOURS);

    /**
     * Preset of match modes offered for a {@code filterValueType="datetime"} column filter (a full
     * date+time value, e.g., {@code LocalDateTime}): every {@link #DATE_OPTIONS} mode (the date component still
     * has calendar-day/week/month/... meaning) plus "last/next N minutes/hours" for time-of-day precision.
         */
    public static final List<MatchMode> DATETIME_OPTIONS;
    static {
        List<MatchMode> modes = new ArrayList<>(DATE_OPTIONS);
        modes.add(LAST_N_MINUTES);
        modes.add(NEXT_N_MINUTES);
        modes.add(LAST_N_HOURS);
        modes.add(NEXT_N_HOURS);
        DATETIME_OPTIONS = Collections.unmodifiableList(modes);
    }

    private final String operator;
    private final String symbol;
    private final boolean requiresValue;

    MatchMode(String operator) {
        this(operator, null, true);
    }

    MatchMode(String operator, String symbol) {
        this(operator, symbol, true);
    }

    MatchMode(String operator, boolean requiresValue) {
        this(operator, null, requiresValue);
    }

    MatchMode(String operator, String symbol, boolean requiresValue) {
        this.operator = operator;
        this.symbol = symbol;
        this.requiresValue = requiresValue;
    }

    public String operator() {
        return operator;
    }

    /**
     * The mathematical symbol for this match mode (e.g., {@code "<="} for {@link #LESS_THAN_EQUALS}), if it has one.
     * Comparison operators shared by the {@code numeric} and {@code date} {@code filterValueType} presets
     * have a symbol; string-matching operators (contains, starts with, ...) do not, as they have no natural
     * mathematical notation.
     *
     * @return the symbol, or {@code null} if this match mode has none
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
     * a curated preset of {@link MatchMode}s; an explicit comma separated list of match mode operators
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
                return NUMERIC_OPTIONS;
            case "text":
                return TEXT_OPTIONS;
            case "date":
                return DATE_OPTIONS;
            case "boolean":
                return BOOLEAN_OPTIONS;
            case "enum":
                return ENUM_OPTIONS;
            case "array":
                return ARRAY_OPTIONS;
            case "time":
                return TIME_OPTIONS;
            case "datetime":
                return DATETIME_OPTIONS;
            default:
                return Arrays.stream(filterValueType.split(","))
                        .map(String::trim)
                        .filter(LangUtils::isNotBlank)
                        .map(MatchMode::of)
                        .collect(Collectors.toList());
        }
    }
}
