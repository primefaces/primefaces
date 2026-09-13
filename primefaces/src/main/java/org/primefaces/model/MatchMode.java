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
    IS_EMPTY("empty", "pi-circle", "⊝", false),
    /**
     * Matches when the field value is neither {@code null}, an empty/blank string, nor an empty
     * {@code Collection}/array.
     */
    NOT_EMPTY("notEmpty", "pi-circle-fill", "⊜", false),
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
    ARRAY_CONTAINS("arrayContains", "pi-tags", "⊚"),
    /**
     * Matches when a {@code Collection}/array field value does not contain the typed filter value.
     * See {@link #ARRAY_CONTAINS}.
     */
    ARRAY_NOT_CONTAINS("arrayNotContains", "pi-tag", "⊖"),
    /**
     * Matches when a {@code Collection}/array field value contains at least one of the comma-separated typed
     * filter values (a non-empty intersection). See {@link #ARRAY_CONTAINS}.
     */
    CONTAINS_ANY("containsAny", "pi-share-alt", "⊃"),
    /**
     * Matches when a {@code Collection}/array field value contains every one of the comma-separated typed
     * filter values (the field is a superset of the typed values). See {@link #ARRAY_CONTAINS}.
     */
    CONTAINS_ALL("containsAll", "pi-check-square", "⊇"),
    /**
     * Matches when a {@code Collection}/array field value contains none of the comma-separated typed filter
     * values (an empty intersection) - the negation of {@link #CONTAINS_ANY}.
     */
    CONTAINS_NONE("containsNone", "pi-minus-circle", "⊅"),

    GLOBAL("global", "pi-globe", "⊛");

    /**
     * {@code filterValueType} token that opts a column out of the match-mode dropdown entirely.
     */
    public static final String NONE_TOKEN = "none";

    /**
     * {@code filterValueType} token that adds the opt-in relative predicates of the preset it accompanies -
     * see {@link #parseOptions(String)}.
     */
    public static final String SHORTCUTS_TOKEN = "shortcuts";

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
     * Preset of match modes offered for a {@code filterValueType="date"} column filter: the comparators that
     * work off a date the end user picks or types. The calendar shortcuts ("today", "this week", ...) are
     * deliberately NOT part of this preset - a column opts into those with the {@code "shortcuts"} token
     * (see {@link #DATE_SHORTCUT_MATCH_MODES} and {@link #parseOptions(String)}), which keeps the default
     * menu short enough to fit on screen.
     */
    public static final List<MatchMode> DATE_MATCH_MODES = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_EMPTY, NOT_EMPTY);

    /**
     * Opt-in calendar shortcuts for a {@code "date"}/{@code "datetime"} column - value-less predicates
     * resolved against the clock at filter time, plus the three that take a plain number of days. Added to a
     * column's menu by the {@code "shortcuts"} token, e.g., {@code filterValueType="date,shortcuts"}.
     */
    public static final List<MatchMode> DATE_SHORTCUT_MATCH_MODES = List.of(
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
     * Preset of match modes offered for a {@code filterValueType="time"} column filter. As with
     * {@link #DATE_MATCH_MODES}, the relative windows are opt-in via {@code "shortcuts"} - see
     * {@link #TIME_SHORTCUT_MATCH_MODES}.
     */
    public static final List<MatchMode> TIME_MATCH_MODES = List.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS,
            BETWEEN, NOT_BETWEEN, IS_EMPTY, NOT_EMPTY);

    /**
     * Opt-in relative windows for a {@code "time"}/{@code "datetime"} column, e.g.
     * {@code filterValueType="time,shortcuts"}.
     */
    public static final List<MatchMode> TIME_SHORTCUT_MATCH_MODES = List.of(
            LAST_N_MINUTES, NEXT_N_MINUTES, LAST_N_HOURS, NEXT_N_HOURS);

    /**
     * Preset of match modes offered for a {@code filterValueType="datetime"} column filter - the same
     * comparators as {@link #DATE_MATCH_MODES}, since a datetime is picked or typed the same way.
     */
    public static final List<MatchMode> DATETIME_MATCH_MODES = DATE_MATCH_MODES;

    /**
     * Opt-in shortcuts for a {@code "datetime"} column: the calendar shortcuts of a date column plus the
     * time-of-day windows, since a datetime carries both. Added by {@code filterValueType="datetime,shortcuts"}.
     */
    public static final List<MatchMode> DATETIME_SHORTCUT_MATCH_MODES;
    static {
        List<MatchMode> modes = new ArrayList<>(DATE_SHORTCUT_MATCH_MODES);
        modes.addAll(TIME_SHORTCUT_MATCH_MODES);
        DATETIME_SHORTCUT_MATCH_MODES = Collections.unmodifiableList(modes);
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
     * {@link #NOT_EQUALS}, {@code "⊝"} for {@link #IS_EMPTY}) - every match mode has one. Rendered as the
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
     * Message key of an example hint for the syntax the filter value {@code <input>} expects, shown as its
     * placeholder while this match mode is selected (e.g., "min,max" for {@link #BETWEEN}). Only three shapes
     * exist, so the modes share one key each rather than carrying one key per mode.
     * <p>
     * A key rather than the text itself: the hint reaches the end user, so it is localized like the match
     * mode labels next to it - resolved through {@code MessageFactory} by
     * {@code DataTableRenderer#encodeFilterMatchModeMenu()} and {@code #encodeFilterInput()}, which have the
     * {@code FacesContext} this enum does not.
     *
     * @return the placeholder hint's message key, or {@code null} if this match mode expects a single plain value
     */
    public String placeholderHintKey() {
        switch (this) {
            case BETWEEN:
            case NOT_BETWEEN:
                return "primefaces.datatable.filterMatchMode.placeholderHint.RANGE";
            case IN:
            case NOT_IN:
            case CONTAINS_ANY:
            case CONTAINS_ALL:
            case CONTAINS_NONE:
                return "primefaces.datatable.filterMatchMode.placeholderHint.LIST";
            case LAST_N_DAYS:
            case NEXT_N_DAYS:
            case RELATIVE_DATE:
            case LAST_N_MINUTES:
            case NEXT_N_MINUTES:
            case LAST_N_HOURS:
            case NEXT_N_HOURS:
                return "primefaces.datatable.filterMatchMode.placeholderHint.COUNT";
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
     * The value is a comma-separated list whose entries are resolved left to right and concatenated, skipping
     * anything already present, so order in equals order in the menu. Each entry is one of:
     * <ul>
     *   <li>a preset keyword - {@code "numeric"}, {@code "text"}, {@code "date"}, {@code "time"},
     *       {@code "datetime"}, {@code "boolean"}, {@code "enum"} or {@code "array"};</li>
     *   <li>{@code "shortcuts"}, which adds the opt-in relative predicates matching the preset keyword used
     *       alongside it: {@link #DATE_SHORTCUT_MATCH_MODES} for {@code "date"},
     *       {@link #TIME_SHORTCUT_MATCH_MODES} for {@code "time"} and both for {@code "datetime"} (also the
     *       fallback when the list names no preset at all, e.g., a hand-written operator list). They are opt-in
     *       because there are 18 to 22 of them - always-on they made the menu taller than the viewport;</li>
     *   <li>a single match mode operator, e.g., {@code "equals"} or {@code "lt"}.</li>
     * </ul>
     * {@code "none"} anywhere in the list wins outright and opts the column out of the dropdown, even though
     * its {@code filterValueType} would otherwise be auto-derived from its Java type.
     * <p>
     * So {@code "date"} yields the ten date comparators, {@code "date,shortcuts"} those plus every calendar
     * shortcut, and {@code "date,today,thisWeek"} those plus exactly two of them.
     *
     * @param filterValueType the value of the column's {@code filterValueType} attribute
     * @return the resolved, ordered list of selectable match modes; empty if {@code filterValueType} is blank or {@code "none"}
     * @throws UnsupportedOperationException if an entry is neither a keyword nor a known match mode operator
     */
    public static List<MatchMode> parseOptions(String filterValueType) {
        if (LangUtils.isBlank(filterValueType)) {
            return Collections.emptyList();
        }

        List<String> tokens = Arrays.stream(filterValueType.split(","))
                .map(String::trim)
                .filter(LangUtils::isNotBlank)
                .collect(Collectors.toList());
        if (tokens.contains(NONE_TOKEN)) {
            return Collections.emptyList();
        }

        // resolved up front rather than as the loop reaches it, so "shortcuts,date" behaves like
        // "date,shortcuts" - which preset it belongs to is a property of the whole list, not of the position
        List<MatchMode> shortcuts = resolveShortcuts(tokens);

        List<MatchMode> options = new ArrayList<>();
        for (String token : tokens) {
            if (SHORTCUTS_TOKEN.equals(token)) {
                addAllAbsent(options, shortcuts);
                continue;
            }

            List<MatchMode> preset = presetOf(token);
            // MatchMode.of() throws on an unknown operator, which is what we want: a typo in filterValueType
            // should surface at render time, not silently render a menu missing an entry
            addAllAbsent(options, preset != null ? preset : Collections.singletonList(MatchMode.of(token)));
        }
        return options;
    }

    /**
     * Whether a {@code filterValueType} names the given preset keyword. Callers that need to know a column's
     * value SHAPE - which labels to use, whether to render a shadow date picker - must ask this rather than
     * compare the attribute to the keyword, since the value is a list: {@code "date,shortcuts"} is every bit
     * as much a date column as a plain {@code "date"}.
     *
     * @param filterValueType the value of the column's {@code filterValueType} attribute, may be {@code null}
     * @param keyword the preset keyword to look for, e.g., {@code "date"}
     * @return {@code true} if the keyword is one of the comma-separated entries
     */
    public static boolean hasKeyword(String filterValueType, String keyword) {
        if (LangUtils.isBlank(filterValueType)) {
            return false;
        }
        for (String token : filterValueType.split(",")) {
            if (keyword.equals(token.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * The preset a keyword expands to, or {@code null} if the token is not a preset keyword (so the caller can
     * fall back to reading it as a single match mode operator).
     */
    private static List<MatchMode> presetOf(String token) {
        switch (token) {
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
                return null;
        }
    }

    /**
     * Which shortcut group {@code "shortcuts"} stands for in this list: the date one next to {@code "date"},
     * the time one next to {@code "time"}, and both next to {@code "datetime"} or when no preset keyword is
     * named at all (a hand-written operator list, where the superset is the useful reading).
     */
    private static List<MatchMode> resolveShortcuts(List<String> tokens) {
        boolean date = tokens.contains("date");
        boolean time = tokens.contains("time");
        if (tokens.contains("datetime") || (date && time) || (!date && !time)) {
            return DATETIME_SHORTCUT_MATCH_MODES;
        }
        return date ? DATE_SHORTCUT_MATCH_MODES : TIME_SHORTCUT_MATCH_MODES;
    }

    private static void addAllAbsent(List<MatchMode> target, List<MatchMode> toAdd) {
        for (MatchMode mode : toAdd) {
            if (!target.contains(mode)) {
                target.add(mode);
            }
        }
    }
}
