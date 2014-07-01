/*
 * Copyright 2009-2014 PrimeTek.
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
}
