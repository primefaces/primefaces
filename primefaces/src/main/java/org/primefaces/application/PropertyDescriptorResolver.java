/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.application;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.faces.FacesException;

public interface PropertyDescriptorResolver {

    PropertyDescriptor get(Class<?> klazz, String name);

    Object getValue(Object bean, String name);

    default Class<?> getType(Object bean, String name) {
        PropertyDescriptor pd = get(bean, name);
        return pd.getPropertyType();
    }

    default PropertyDescriptor get(Object bean, String name) {
        return get(bean.getClass(), name);
    }

    void flush();

    class DefaultResolver implements PropertyDescriptorResolver {

        private final Map<String, Map<String, PropertyDescriptor>> propertyDescriptorCache;

        private final Pattern NESTED_EXPRESSION_PATTERN = Pattern.compile("\\.");

        public DefaultResolver() {
            propertyDescriptorCache = new ConcurrentHashMap<>();
        }

        @Override
        public PropertyDescriptor get(Class<?> klazz, String expression) {
            PropertyDescriptor pd = null;
            Class<?> parent = klazz;

            for (String field : NESTED_EXPRESSION_PATTERN.split(expression)) {
                pd = getSimpleProperty(parent, field);
                parent = pd.getPropertyType();
            }

            return Objects.requireNonNull(pd);
        }

        @Override
        public Object getValue(Object bean, String expression) {
            try {
                for (String field : NESTED_EXPRESSION_PATTERN.split(expression)) {
                    bean = getSimpleProperty(bean.getClass(), field).getReadMethod().invoke(bean);
                }

                return bean;
            }
            catch (ReflectiveOperationException e) {
                throw new FacesException(e);
            }
        }

        @Override
        public void flush() {
            propertyDescriptorCache.clear();
        }

        private PropertyDescriptor getSimpleProperty(Class<?> klazz, String field) {
            String cacheKey = klazz.getName();
            Map<String, PropertyDescriptor> classCache = propertyDescriptorCache
                    .computeIfAbsent(cacheKey, k -> new ConcurrentHashMap<>());
            return classCache.computeIfAbsent(field, k -> {
                try {
                    return new PropertyDescriptor(k, klazz);
                } catch (IntrospectionException e) {
                    throw new FacesException(e);
                }
            });
        }
    }
}
