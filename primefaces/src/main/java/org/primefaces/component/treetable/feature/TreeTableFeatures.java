package org.primefaces.component.treetable.feature;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TreeTableFeatures {

    private static final Map<Class<? extends TreeTableFeature>, TreeTableFeature> ALL_FEATURES = Stream.of(
                    new CellEditFeature(),
                    new CollapseFeature(),
                    new ExpandFeature(),
                    new FilterFeature(),
                    new PageFeature(),
                    new ResizableColumnsFeature(),
                    new RowEditFeature(),
                    new SelectionFeature(),
                    new SortFeature())
            .collect(Collectors.toMap(TreeTableFeature::getClass, Function.identity()));

    private TreeTableFeatures() {
        // NOOP
    }

    public static Collection<TreeTableFeature> all() {
        return ALL_FEATURES.values();
    }

    public static <T extends TreeTableFeature> T get(Class<T> feature) {
        return (T) Optional.ofNullable(ALL_FEATURES.get(feature))
                .orElseThrow(() -> new UnsupportedOperationException("Feature " + feature.getName() + " not supported"));
    }

    public static <T extends TreeTableFeature> T replace(Class<T> original, T feature) {
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
}
