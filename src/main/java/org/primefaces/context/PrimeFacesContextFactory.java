/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.context;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

/**
 * {@link FacesContextFactory} to wrap the {@link FacesContext} with our {@link PrimeFacesContext}.
 */
public class PrimeFacesContextFactory extends FacesContextFactory {

    private FacesContextFactory wrapped;

    // #6212 - don't remove it
    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeFacesContextFactory() {

    }

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeFacesContextFactory(FacesContextFactory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
            throws FacesException {

        FacesContext wrappedContext = wrapped.getFacesContext(context, request, response, lifecycle);

        if (wrappedContext instanceof PrimeFacesContext) {
            return wrappedContext;
        }

        return new PrimeFacesContext(wrappedContext);
    }

    @Override
    public FacesContextFactory getWrapped() {
        return wrapped;
    }
}
