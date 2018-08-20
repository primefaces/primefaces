package org.primefaces.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Fluent builder for {@link Map}'s
 *
 * @param <K> key type
 * @param <V> value type
 */
public final class MapBuilder<K, V> {

    private final Map<K, V> map;

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> builder(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>(new HashMap<K, V>());
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return Collections.unmodifiableMap(map);
    }
}
