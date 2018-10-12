/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
