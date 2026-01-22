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
package org.primefaces.component.datatable.feature;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataTableFeatures {

    // order matters: filter should be decoded before selection
    private static final Map<Class<? extends DataTableFeature>, DataTableFeature> ALL_FEATURES = Stream.of(
                    new DraggableColumnsFeature(),
                    new FilterFeature(),
                    new PageFeature(),
                    new SortFeature(),
                    new ResizableColumnsFeature(),
                    new SelectionFeature(),
                    new RowEditFeature(),
                    new CellEditFeature(),
                    new RowExpandFeature(),
                    new ScrollFeature(),
                    new DraggableRowsFeature(),
                    new AddRowFeature())
            .collect(Collectors.toMap(DataTableFeature::getClass, Function.identity(), (u, v) -> u, LinkedHashMap::new));

    private DataTableFeatures() {
        // NOOP
    }

    public static Collection<DataTableFeature> all() {
        return ALL_FEATURES.values();
    }

    public static <T extends DataTableFeature> T get(Class<T> feature) {
        return (T) Optional.ofNullable(ALL_FEATURES.get(feature))
                .orElseThrow(() -> new UnsupportedOperationException("Feature " + feature.getName() + " not supported"));
    }

    public static <T extends DataTableFeature> T replace(Class<T> original, T feature) {
        if (!original.isAssignableFrom(feature.getClass())) {
            throw new IllegalArgumentException(original.getName() + " is not assignable from " + feature.getClass().getName());
        }

        if (!ALL_FEATURES.containsKey(original)) {
            throw new UnsupportedOperationException("Feature " + original.getName() + " not supported");
        }

        return (T) ALL_FEATURES.replace(original, feature);
    }

    // -- Utility methods to facilitate access to certain features

    public static SortFeature sortFeature() {
        return get(SortFeature.class);
    }

    public static FilterFeature filterFeature() {
        return get(FilterFeature.class);
    }

    public static SelectionFeature selectionFeature() {
        return get(SelectionFeature.class);
    }

    public static RowExpandFeature rowExpandFeature() {
        return get(RowExpandFeature.class);
    }
}
