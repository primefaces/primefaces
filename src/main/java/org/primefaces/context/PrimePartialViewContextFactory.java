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
package org.primefaces.context;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

public class PrimePartialViewContextFactory extends PartialViewContextFactory {

    private PartialViewContextFactory parent;

    // #6212 - don't remove it
    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimePartialViewContextFactory() {

    }

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimePartialViewContextFactory(PartialViewContextFactory parent) {
        this.parent = parent;
    }

    @Override
    public PartialViewContextFactory getWrapped() {
        return this.parent;
    }

    @Override
    public PartialViewContext getPartialViewContext(FacesContext fc) {
        PartialViewContext parentContext = getWrapped().getPartialViewContext(fc);

        return new PrimePartialViewContext(parentContext);
    }
}
