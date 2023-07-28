/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.export;

import java.util.Objects;

public class ColumnValue {
    private final Object customValue;
    private final String fallbackValue;

    private ColumnValue(Object customValue, String fallbackValue) {
        this.customValue = customValue;
        this.fallbackValue = fallbackValue;
    }

    public static ColumnValue customValue(Object value) {
        return new ColumnValue(value, null);
    }

    public static ColumnValue fallbackValue(String value) {
        return new ColumnValue(null, value);
    }

    public Object getCustomValue() {
        return customValue;
    }

    public String getFallbackValue() {
        return fallbackValue;
    }

    public boolean isCustomValue() {
        return customValue != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnValue that = (ColumnValue) o;

        if (!Objects.equals(customValue, that.customValue)) return false;
        return Objects.equals(fallbackValue, that.fallbackValue);
    }

    @Override
    public int hashCode() {
        int result = customValue != null ? customValue.hashCode() : 0;
        result = 31 * result + (fallbackValue != null ? fallbackValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (customValue == null && fallbackValue == null) {
            return null;
        }
        else {
            return customValue != null ? customValue.toString() : fallbackValue;
        }
    }
}
