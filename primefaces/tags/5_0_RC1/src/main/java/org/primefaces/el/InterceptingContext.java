package org.primefaces.el;

import java.util.Locale;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

public class InterceptingContext extends ELContext {
    private final ELContext context;
    private final ELResolver resolver;

    public InterceptingContext(ELContext context, ELResolver resolver) {
        this.context = context;
        this.resolver = resolver;
    }

    // punch in our new ELResolver
    @Override
    public ELResolver getELResolver() {
        return resolver;
    }

    // The rest of the methods simply delegate to the existing context
    @Override
    public Object getContext(Class key) {
        return context.getContext(key);
    }

    @Override
    public Locale getLocale() {
        return context.getLocale();
    }

    @Override
    public boolean isPropertyResolved() {
        return context.isPropertyResolved();
    }

    @Override
    public void putContext(Class key, Object contextObject) {
        context.putContext(key, contextObject);
    }

    @Override
    public void setLocale(Locale locale) {
        context.setLocale(locale);
    }

    @Override
    public void setPropertyResolved(boolean resolved) {
        context.setPropertyResolved(resolved);
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return context.getFunctionMapper();
    }

    @Override
    public VariableMapper getVariableMapper() {
        return context.getVariableMapper();
    }
}
