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

    private BeanUtils() {
    }

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
