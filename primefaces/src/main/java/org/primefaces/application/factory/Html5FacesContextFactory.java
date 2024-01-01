/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.application.factory;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

/**
 * JSF generates all script tags with 'type="text/javascript"' which throws HTML5 validation warnings.
 * <p>
 * Register it as below in faces-config.xml:
 * </p>
 *
 * <pre>
 *    &lt;faces-config&gt;
 *        &lt;factory&gt;
 *            &lt;faces-context-factory&gt;org.primefaces.extensions.application.Html5ContextFactory&lt;/faces-context-factory&gt;
 *        &lt;/factory&gt;
 *    &lt;/faces-config&gt;
 * </pre>
 *
 * NOTE: Not necessary for Faces 4.0+.
 *
 * @since 12.0.0
 */
public class Html5FacesContextFactory extends FacesContextFactory {

    public Html5FacesContextFactory(FacesContextFactory wrapped) {
        super(wrapped);
    }

    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
                throws FacesException {
        FacesContext wrappedContext = getWrapped().getFacesContext(context, request, response, lifecycle);
        return wrappedContext instanceof Html5FacesContext ? wrappedContext : new Html5FacesContext(wrappedContext);
    }
}
