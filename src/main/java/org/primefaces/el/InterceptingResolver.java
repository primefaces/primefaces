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
        if(base != null && property != null) {
            context.setPropertyResolved(true);
            valueReference = new ValueReference(base, property.toString());
        }
    }

    // The rest of the methods simply delegate to the existing context
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        return delegate.getValue(context, base, property);
    }

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
