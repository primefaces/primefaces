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
package org.primefaces.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ValueReference;

public class InterceptingResolver extends ELResolver {

    private final ELResolver delegate;
    private ValueReference valueReference;

    public InterceptingResolver(ELResolver delegate) {
        this.delegate = delegate;
    }

    public ValueReference getValueReference() {
        return valueReference;
    }

    // Capture the base and property rather than write the value
    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        // currently not used -> see #7114
        if (base != null && property != null) {
            context.setPropertyResolved(true);
            valueReference = new ValueReference(base, property.toString());
        }
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        context.setPropertyResolved(true);
        valueReference = new ValueReference(base, property.toString());

        return delegate.getValue(context, base, property);
    }

    // The rest of the methods simply delegate to the existing context
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return delegate.getType(context, base, property);
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return delegate.isReadOnly(context, base, property);
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return delegate.getFeatureDescriptors(context, base);
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return delegate.getCommonPropertyType(context, base);
    }

    // @Override
    // < EL 2.2 compatibility
    @Override
    public Object invoke(ELContext context,
            Object base,
            Object method,
            Class<?>[] paramTypes,
            Object[] params) {
        return delegate.invoke(context, base, method, paramTypes, params);
    }
}
