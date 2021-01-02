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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum SortOrder {

    ASCENDING(1, "asc", "1", "ascending", "ASCENDING", "ASC"),
    DESCENDING(-1, "desc", "-1", "descending", "DESCENDING", "DESC"),
    UNSORTED(0, "", null, "0", "unsorted", "UNSORTED");

    private final Set<Object> values;

    private final Integer intValue;

    SortOrder(int intValue, Object... values) {
        this.intValue = intValue;
        this.values = new HashSet<>(Arrays.asList(values));
    }

    public static SortOrder of(Object order) {
        for (SortOrder o : values()) {
            if (o.intValue.equals(order) || o.values.contains(order)) {
                return o;
            }
        }

        throw new IllegalArgumentException("No SorderOrder matching value: " + order);
    }

    public boolean isAscending() {
        return this == SortOrder.ASCENDING;
    }

    public boolean isDescending() {
        return this == SortOrder.DESCENDING;
    }

    public boolean isUnsorted() {
        return this == SortOrder.UNSORTED;
    }

    public int intValue() {
        return intValue;
    }
}
