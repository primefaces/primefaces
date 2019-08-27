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

public class SearchExpressionHint {

    public static final int NONE = 0x0;

    /**
     * Checks if the {@link UIComponent} has a renderer or not. This check is currently only useful for the update attributes, as a component without
     * renderer can't be updated.
     */
    public static final int VALIDATE_RENDERER = 0x1;

    public static final int IGNORE_NO_RESULT = 0x2;

    public static final int PARENT_FALLBACK = 0x4;

    public static final int SKIP_UNRENDERED = 0x8;

    /**
     * Indicate that some expressions can be resolved on the client side.
     */
    public static final int RESOLVE_CLIENT_SIDE = 0x16;

    private SearchExpressionHint() {
    }
}
