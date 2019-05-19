/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.expression;

public class SearchExpressionConstants {

    public static final String KEYWORD_PREFIX = "@";

    public static final String NONE_KEYWORD =               KEYWORD_PREFIX + "none";
    public static final String THIS_KEYWORD =               KEYWORD_PREFIX + "this";
    public static final String PARENT_KEYWORD =             KEYWORD_PREFIX + "parent";
    public static final String COMPOSITE_KEYWORD =          KEYWORD_PREFIX + "composite";
    public static final String FORM_KEYWORD =               KEYWORD_PREFIX + "form";
    public static final String NAMINGCONTAINER_KEYWORD =    KEYWORD_PREFIX + "namingcontainer";
    public static final String ALL_KEYWORD =                KEYWORD_PREFIX + "all";
    public static final String NEXT_KEYWORD =               KEYWORD_PREFIX + "next";
    public static final String PREVIOUS_KEYWORD =          KEYWORD_PREFIX + "previous";
    public static final String CHILD_KEYWORD =              KEYWORD_PREFIX + "child";
    public static final String WIDGETVAR_KEYWORD =          KEYWORD_PREFIX + "widgetVar";
    public static final String ROW_KEYWORD =                KEYWORD_PREFIX + "row";
    public static final String COLUMN_KEYWORD =             KEYWORD_PREFIX + "column";
    public static final String ID_KEYWORD =                 KEYWORD_PREFIX + "id";
    public static final String ROOT_KEYWORD =               KEYWORD_PREFIX + "root";

    public static final String PFS_PREFIX =                 KEYWORD_PREFIX + "(";

    private SearchExpressionConstants() {
    }
}
