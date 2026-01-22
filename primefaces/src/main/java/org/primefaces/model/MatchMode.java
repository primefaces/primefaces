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

import org.primefaces.component.api.UIColumn;
import org.primefaces.util.LangUtils;

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

    LESS_THAN("lt"),
    LESS_THAN_EQUALS("lte"),

    GREATER_THAN("gt"),
    GREATER_THAN_EQUALS("gte"),

    EQUALS("equals"),
    NOT_EQUALS("notEquals"),

    IN("in"),
    NOT_IN("notIn"),

    BETWEEN("between"),
    NOT_BETWEEN("notBetween"),

    GLOBAL("global");

    private final String operator;

    MatchMode(String operator) {
        this.operator = operator;
    }

    public String operator() {
        return operator;
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
}
