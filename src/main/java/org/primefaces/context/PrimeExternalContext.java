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

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;

public class PrimeExternalContext extends ExternalContextWrapper {

    private ExternalContext wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeExternalContext(ExternalContext wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExternalContext getWrapped() {
        return wrapped;
    }

    public static PrimeExternalContext getCurrentInstance(FacesContext facesContext) {
        ExternalContext externalContext = facesContext.getExternalContext();

        while (externalContext != null) {
            if (externalContext instanceof PrimeExternalContext) {
                return (PrimeExternalContext) externalContext;
            }

            if (externalContext instanceof ExternalContextWrapper) {
                externalContext = ((ExternalContextWrapper) externalContext).getWrapped();
            }
            else {
                return null;
            }
        }

        return null;
    }
}
