/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.util;

import java.util.Comparator;
import java.util.Map;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.model.ColumnMeta;

public final class ColumnComparators {

    private ColumnComparators() {
        // NOOP
    }

    static class DisplayOrderComparator implements Comparator<UIColumn> {

        private final Map<String, ColumnMeta> columnMeta;

        public DisplayOrderComparator(Map<String, ColumnMeta> columnMeta) {
            this.columnMeta = columnMeta;
        }

        @Override
        public int compare(UIColumn c1, UIColumn c2) {
            if (c1 instanceof DynamicColumn) {
                ((DynamicColumn) c1).applyStatelessModel();
            }

            Integer dp1 = c1.getDisplayPriority();
            ColumnMeta cm1 = columnMeta.get(c1.getColumnKey());
            if (cm1 != null && cm1.getDisplayPriority() != null) {
                dp1 = cm1.getDisplayPriority();
            }

            if (c2 instanceof DynamicColumn) {
                ((DynamicColumn) c2).applyStatelessModel();
            }

            Integer dp2 = c2.getDisplayPriority();
            ColumnMeta cm2 = columnMeta.get(c2.getColumnKey());
            if (cm2 != null && cm2.getDisplayPriority() != null) {
                dp2 = cm2.getDisplayPriority();
            }

            return dp1.compareTo(dp2);
        }
    }

    public static Comparator<UIColumn> displayOrder(Map<String, ColumnMeta> columnMeta) {
        return new DisplayOrderComparator(columnMeta);
    }

}
