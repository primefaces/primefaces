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
package org.primefaces.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import jakarta.faces.FacesException;

public interface PropertyDescriptorResolver {

    PropertyDescriptor get(Class<?> klazz, String expression);

    Object getValue(Object obj, String expression);

    void setValue(Object obj, String expression, Object value);

    void flush();

    class DefaultResolver implements PropertyDescriptorResolver {

        private static final Pattern NESTED_EXPRESSION_PATTERN = Pattern.compile("\\.");
        private final Map<String, Map<String, PropertyDescriptor>> pdCache;

        public DefaultResolver() {
            pdCache = new ConcurrentHashMap<>();
        }

        @Override
        public PropertyDescriptor get(Class<?> klazz, String expression) {
            String cacheKey = klazz.getName();
            Map<String, PropertyDescriptor> classCache = pdCache.get(cacheKey);
            if (classCache != null) {
                PropertyDescriptor pd = classCache.get(expression);
                if (pd != null) {
                    return pd;
                }
            }

            PropertyDescriptor pd = null;
            Class<?> parent = klazz;

            for (String property : NESTED_EXPRESSION_PATTERN.split(expression)) {
                pd = getSimpleProperty(parent, property);
                parent = pd.getPropertyType();
            }

            return Objects.requireNonNull(pd);
        }

        @Override
        public Object getValue(Object obj, String expression) {
            try {
                for (String property : NESTED_EXPRESSION_PATTERN.split(expression)) {
                    obj = getSimpleProperty(obj.getClass(), property).getReadMethod().invoke(obj);
                    if (obj == null) {
                        break;
                    }
                }

                return obj;
            }
            catch (ReflectiveOperationException e) {
                throw new FacesException(e);
            }
        }

        @Override
        public void setValue(Object obj, String expression, Object value) {
            try {
                String[] expressions = NESTED_EXPRESSION_PATTERN.split(expression);

                for (int i = 0; i < expressions.length; i++) {
                    String property = expressions[i];
                    boolean lastExpression = i + 1 == expressions.length;

                    if (lastExpression) {
                        Method writeMethod = getSimpleProperty(obj.getClass(), property).getWriteMethod();
                        if (writeMethod == null) {
                            throw new FacesException("property '" + property + "' is readonly");
                        }
                        writeMethod.invoke(obj, value);
                    }
                    else {
                        obj = getSimpleProperty(obj.getClass(), property).getReadMethod().invoke(obj);
                        if (obj == null) {
                            throw new FacesException("property '" + property + "' resolved to null");
                        }
                    }
                }
            }
            catch (ReflectiveOperationException e) {
                throw new FacesException(e);
            }
        }

        @Override
        public void flush() {
            pdCache.clear();
        }

        private PropertyDescriptor getSimpleProperty(Class<?> klazz, String property) {
            String cacheKey = klazz.getName();
            return pdCache.computeIfAbsent(cacheKey, k -> new ConcurrentHashMap<>()).computeIfAbsent(property, k -> {
                try {
                    return new PropertyDescriptor(k, klazz);
                }
                catch (IntrospectionException e) {
                    // try fallback without write method, our main concern is to read properties here
                    try {
                        // "is" + capitalize(k) is actually copied from another PropertyDescriptor constructor
                        // it will somehow also work for "get" prefix
                        return new PropertyDescriptor(k,
                                klazz,
                                "is" + LangUtils.capitalize(k),
                                null);
                    }
                    catch (IntrospectionException e2) {
                        throw new FacesException(e2);
                    }
                }
            });
        }
    }
}
