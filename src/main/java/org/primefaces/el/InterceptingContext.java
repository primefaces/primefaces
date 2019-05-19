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
