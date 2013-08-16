package org.primefaces.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Locale;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.el.VariableMapper;
import javax.faces.el.CompositeComponentExpressionHolder;

public class ValueExpressionAnalyzer
{
    public static ValueReference getReference(ELContext elContext, ValueExpression expression)
    {
        InterceptingResolver resolver = new InterceptingResolver(elContext.getELResolver());

        try
        {
            expression.setValue(new InterceptingContext(elContext, resolver), null);
        }
        catch(ELException ele)
        {
            return null;
        }

        ValueReference reference = resolver.getValueReference();
        if(reference != null)
        {
            Object base = reference.getBase();
            if(base instanceof CompositeComponentExpressionHolder)
            {
                ValueExpression ve = ((CompositeComponentExpressionHolder) base).getExpression((String) reference.getProperty());
                if(ve != null)
                {
                    reference = getReference(elContext, ve);
                }
            }
        }
        return reference;
    }

    private static class InterceptingContext extends ELContext
    {
        private final ELContext context;
        private final ELResolver resolver;

        public InterceptingContext(ELContext context, ELResolver resolver)
        {
            this.context = context;
            this.resolver = resolver;
        }

        // punch in our new ELResolver
        @Override
        public ELResolver getELResolver()
        {
            return resolver;
        }

        // The rest of the methods simply delegate to the existing context

        @Override
        public Object getContext(Class key)
        {
            return context.getContext(key);
        }

        @Override
        public Locale getLocale()
        {
            return context.getLocale();
        }

        @Override
        public boolean isPropertyResolved()
        {
            return context.isPropertyResolved();
        }

        @Override
        public void putContext(Class key, Object contextObject)
        {
            context.putContext(key, contextObject);
        }

        @Override
        public void setLocale(Locale locale)
        {
            context.setLocale(locale);
        }

        @Override
        public void setPropertyResolved(boolean resolved)
        {
            context.setPropertyResolved(resolved);
        }

        @Override
        public FunctionMapper getFunctionMapper()
        {
            return context.getFunctionMapper();
        }

        @Override
        public VariableMapper getVariableMapper()
        {
            return context.getVariableMapper();
        }
    }

    private static class InterceptingResolver extends ELResolver
    {
        private final ELResolver delegate;
        private ValueReference valueReference;

        public InterceptingResolver(ELResolver delegate)
        {
            this.delegate = delegate;
        }

        public ValueReference getValueReference()
        {
            return valueReference;
        }

        // Capture the base and property rather than write the value
        @Override
        public void setValue(ELContext context, Object base, Object property, Object value)
        {
            if(base != null && property != null)
            {
                context.setPropertyResolved(true);
                valueReference = new ValueReference(base, property.toString());
            }
        }

        // The rest of the methods simply delegate to the existing context

        @Override
        public Object getValue(ELContext context, Object base, Object property)
        {
            return delegate.getValue(context, base, property);
        }

        @Override
        public Class<?> getType(ELContext context, Object base, Object property)
        {
            return delegate.getType(context, base, property);
        }

        @Override
        public boolean isReadOnly(ELContext context, Object base, Object property)
        {
            return delegate.isReadOnly(context, base, property);
        }

        @Override
        public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base)
        {
            return delegate.getFeatureDescriptors(context, base);
        }

        @Override
        public Class<?> getCommonPropertyType(ELContext context, Object base)
        {
            return delegate.getCommonPropertyType(context, base);
        }

    }
}