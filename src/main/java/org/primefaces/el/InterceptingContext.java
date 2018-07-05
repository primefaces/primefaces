/**
 * Copyright 2009-2018 PrimeTek.
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
