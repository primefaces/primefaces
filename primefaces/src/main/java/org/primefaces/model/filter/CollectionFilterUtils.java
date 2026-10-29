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
package org.primefaces.model.filter;

import org.primefaces.util.LangUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Normalizes multivalued field/filter shapes into a uniform {@link Collection}, shared by
 * {@link InFilterConstraint} and the {@code filterValueType="array"} preset's constraints.
 */
final class CollectionFilterUtils {

    private CollectionFilterUtils() {
    }

    /**
     * Normalizes an "array"-preset FIELD value - a {@link Collection}, a plain array (object or primitive), or
     * (defensively) a single scalar value - into a uniform {@link Collection}.
     */
    static Collection<?> toCollection(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof Collection) {
            return (Collection<?>) value;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(Array.get(value, i));
            }
            return list;
        }
        return Collections.singletonList(value);
    }

    /**
     * Normalizes a multivalue FILTER - a bean-bound {@link Collection}/array, or free text typed next to the
     * match-mode dropdown (split on comma, e.g., {@code "Acme, Globex"}) - into a uniform {@link Collection} of
     * tokens. Unlike {@link #toCollection(Object)}, a {@link String} is split rather than wrapped whole, since a
     * typed filter value is never itself the domain value.
     */
    static Collection<?> toFilterTokens(Object filter) {
        if (filter == null) {
            return Collections.emptyList();
        }
        if (filter.getClass().isArray()) {
            int length = Array.getLength(filter);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(Array.get(filter, i));
            }
            return list;
        }
        if (filter instanceof Collection) {
            return (Collection<?>) filter;
        }
        if (filter instanceof String) {
            return Arrays.stream(((String) filter).split(","))
                    .map(String::trim)
                    .filter(LangUtils::isNotBlank)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(filter);
    }
}
