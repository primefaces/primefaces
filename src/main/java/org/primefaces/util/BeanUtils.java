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

import java.util.List;

/**
 * @author jagatai
 */
public class BeanUtils {

    private static final List<Class<?>> PRIMITIVES = LangUtils.<Class<?>>unmodifiableList(Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            String.class);

    public static boolean isBean(Class<?> valueClass) {
        if (valueClass.isArray()) {
            return isBean(valueClass.getComponentType());
        }
        else if (valueClass.isPrimitive()) {
            return false;
        }
        else {
            return !PRIMITIVES.contains(valueClass);
        }
    }

    public static boolean isBean(Object value) {
        if (value instanceof Boolean || value instanceof String || value instanceof Number) {
            return false;
        }
        else if (value.getClass().isArray()) {
            return isBean(value.getClass().getComponentType());
        }
        else if (value.getClass().isPrimitive()) {
            return false;
        }

        return true;
    }
}
