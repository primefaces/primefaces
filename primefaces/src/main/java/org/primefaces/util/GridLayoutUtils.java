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
package org.primefaces.util;

import java.util.Map;

public class GridLayoutUtils {

    private static final Map<Integer, String> GRID_COLUMN_MAP = MapBuilder.<Integer, String>builder()
            .put(1, "ui-g-12 ui-md-12")
            .put(2, "ui-g-12 ui-md-6")
            .put(3, "ui-g-12 ui-md-4")
            .put(4, "ui-g-12 ui-md-3")
            .put(6, "ui-g-12 ui-md-2")
            .put(12, "ui-g-12 ui-md-1")
            .build();

    private static final Map<Integer, String> FLEX_COLUMN_MAP = MapBuilder.<Integer, String>builder()
            .put(1, "col-12 md:col-12")
            .put(2, "col-12 md:col-6")
            .put(3, "col-12 md:col-4")
            .put(4, "col-12 md:col-3")
            .put(6, "col-12 md:col-2")
            .put(12, "col-12 md:col-1")
            .build();

    private GridLayoutUtils() {
    }

    /**
     * Get grid or flex main container grid Class.
     * @param flex is either flex or grid CSS
     * @return  either flex or grid CSS based on boolean
     */
    public static String getResponsiveClass(boolean flex) {
        return flex ? "ui-grid-responsive" : "ui-grid ui-grid-responsive";
    }

    /**
     * Get grid or flex main container grid Class.
     * @param flex is either flex or grid CSS
     * @return  either flex or grid CSS based on boolean
     */
    public static String getFlexGridClass(boolean flex) {
        return flex ? "grid" : "ui-g";
    }

    /**
     * Get grid or flex column Class.
     * @param flex is either flex or grid CSS
     * @return  either flex or grid CSS based on boolean
     */
    public static String getColumnClass(boolean flex, int columns) {
        return flex ? getFlexColumnClass(columns) : getColumnClass(columns);
    }

    /**
     * Get Grid-CSS-Column-Class.
     * @param columns
     * @return
     */
    public static String getColumnClass(int columns) {
        return GRID_COLUMN_MAP.get(columns);
    }

    /**
     * Get PrimeFlex-Column-Class.
     * @param columns
     * @return
     */
    public static String getFlexColumnClass(int columns) {
        return FLEX_COLUMN_MAP.get(columns);
    }
}
