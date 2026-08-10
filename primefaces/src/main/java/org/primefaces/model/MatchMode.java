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

    GLOBAL("global");

    /**
     * Preset of match modes offered for a numeric {@code filterMatchModeOptions="numeric"} column filter,
     * letting the user pick a comparator (=, !=, &lt;, &gt;, &lt;=, &gt;=) at runtime. See GitHub #7427.
     */
    public static final List<MatchMode> NUMERIC_OPTIONS = Collections.unmodifiableList(Arrays.asList(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS));

    /**
     * Preset of match modes offered for a {@code filterMatchModeOptions="text"} column filter.
     */
    public static final List<MatchMode> TEXT_OPTIONS = Collections.unmodifiableList(Arrays.asList(
            CONTAINS, NOT_CONTAINS, STARTS_WITH, NOT_STARTS_WITH, ENDS_WITH, NOT_ENDS_WITH, EQUALS, NOT_EQUALS));

    /**
     * Preset of match modes offered for a {@code filterMatchModeOptions="date"} column filter.
     */
    public static final List<MatchMode> DATE_OPTIONS = Collections.unmodifiableList(Arrays.asList(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS));

    private final String operator;
    private final String symbol;

    MatchMode(String operator) {
        this(operator, null);
    }

    MatchMode(String operator, String symbol) {
        this.operator = operator;
        this.symbol = symbol;
    }

    public String operator() {
        return operator;
    }

    /**
     * The mathematical symbol for this match mode (e.g. {@code "<="} for {@link #LESS_THAN_EQUALS}), if it has one.
     * Comparison operators shared by the {@code numeric} and {@code date} {@code filterMatchModeOptions} presets
     * have a symbol; string-matching operators (contains, starts with, ...) do not, as they have no natural
     * mathematical notation.
     *
     * @return the symbol, or {@code null} if this match mode has none
     */
    public String symbol() {
        return symbol;
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
     * Accepts either one of the shorthand keywords {@code "numeric"}, {@code "text"} or {@code "date"}, which
     * expand to a curated preset of {@link MatchMode}s, or an explicit comma separated list of match mode
     * operators (e.g. {@code "equals,notEquals,lt,gt,lte,gte"}).
     *
     * @param filterMatchModeOptions the value of the column's {@code filterMatchModeOptions} attribute
     * @return the resolved, ordered list of selectable match modes; empty if {@code filterMatchModeOptions} is blank
     */
    public static List<MatchMode> parseOptions(String filterMatchModeOptions) {
        if (LangUtils.isBlank(filterMatchModeOptions)) {
            return Collections.emptyList();
        }

        switch (filterMatchModeOptions.trim()) {
            case "numeric":
                return NUMERIC_OPTIONS;
            case "text":
                return TEXT_OPTIONS;
            case "date":
                return DATE_OPTIONS;
            default:
                return Arrays.stream(filterMatchModeOptions.split(","))
                        .map(String::trim)
                        .filter(LangUtils::isNotBlank)
                        .map(MatchMode::of)
                        .collect(Collectors.toList());
        }
    }
}
