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
package org.primefaces.util;

import javax.faces.context.FacesContext;

public class SharedStringBuilder {

    private SharedStringBuilder() {
    }

    /**
     * Get a shared {@link StringBuilder} instance.
     * This is required as e.g. 100 e.g. {@link org.primefaces.expression.SearchExpressionFacade#resolveClientId} calls would create
     * 300 {@link StringBuilder} instances!
     *
     * @param context The {@link FacesContext}
     * @param key The key for the {@link FacesContext} attributes.
     * @param initialSize The initial size for the {@link StringBuilder}.
     * @return The shared {@link StringBuilder} instance
     */
    public static StringBuilder get(FacesContext context, String key, int initialSize) {
        StringBuilder builder = (StringBuilder) context.getAttributes().get(key);

        if (builder == null) {
            builder = new StringBuilder(initialSize);
            context.getAttributes().put(key, builder);
        }
        else {
            builder.setLength(0);
        }

        return builder;
    }

    /**
     * Get a shared {@link StringBuilder} instance.
     * This is required as e.g. 100 e.g. {@link org.primefaces.expression.SearchExpressionFacade#resolveClientId} calls would create
     * 300 {@link StringBuilder} instances!
     *
     * @param context The {@link FacesContext}
     * @param key The key for the {@link FacesContext} attributes.
     * @return The shared {@link StringBuilder} instance
     */
    public static StringBuilder get(FacesContext context, String key) {
        return get(context, key, 16);
    }

    /**
     * Get a shared {@link StringBuilder} instance.
     * This is required as e.g. 100 e.g. {@link org.primefaces.expression.SearchExpressionFacade#resolveClientId} calls would create
     * 300 {@link StringBuilder} instances!
     *
     * @param key The key for the {@link FacesContext} attributes.
     * @return The shared {@link StringBuilder} instance
     */
    public static StringBuilder get(String key) {
        return get(FacesContext.getCurrentInstance(), key);
    }

    /**
     * Get a shared {@link StringBuilder} instance.
     * This is required as e.g. 100 e.g. {@link org.primefaces.expression.SearchExpressionFacade#resolveClientId} calls would create
     * 300 {@link StringBuilder} instances!
     *
     * @param key The key for the {@link FacesContext} attributes.
     * @param initialSize The initial size for the {@link StringBuilder}.
     * @return The shared {@link StringBuilder} instance
     */
    public static StringBuilder get(String key, int initialSize) {
        return get(FacesContext.getCurrentInstance(), key, initialSize);
    }
}
