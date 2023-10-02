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

import java.beans.PropertyDescriptor;
import javax.faces.FacesException;

import org.apache.commons.beanutils.PropertyUtils;

public class PropertyUtilsBeanResolver implements PropertyDescriptorResolver {

    @Override
    public PropertyDescriptor get(Object bean, String name) {
        try {
            return PropertyUtils.getPropertyDescriptor(bean, name);
        }
        catch (ReflectiveOperationException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public PropertyDescriptor get(Class<?> klazz, String name) {
        for(PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(klazz)) {
            if (pd.getName().equals(name)) {
                return pd;
            }
        }

        return null;
    }

    @Override
    public Object getValue(Object bean, String name) {
        try {
            return PropertyUtils.getProperty(bean, name);
        }
        catch (ReflectiveOperationException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public void flush() {
        PropertyUtils.clearDescriptors();
    }
}
