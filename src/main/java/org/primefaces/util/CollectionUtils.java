package org.primefaces.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {

    public static final <T> List<T> unmodifiableList(T... args) {
        return Collections.unmodifiableList(Arrays.asList(args));
    }
}
