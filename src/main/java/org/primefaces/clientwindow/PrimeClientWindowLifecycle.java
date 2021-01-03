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
package org.primefaces.clientwindow;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleWrapper;

// We can't use a ClientWindowFactory as this is registered in the faces-config.xml
// if we would add the client-window-factory entry, pre JSF2.3 would crash
public class PrimeClientWindowLifecycle extends LifecycleWrapper {

    private final Lifecycle wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeClientWindowLifecycle(Lifecycle wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void attachWindow(FacesContext facesContext) {
        ClientWindow clientWindow = facesContext.getExternalContext().getClientWindow();
        if (clientWindow == null) {
            clientWindow = new PrimeClientWindow();
        }

        try {
            facesContext.getExternalContext().setClientWindow(clientWindow);
            clientWindow.decode(facesContext);
        }
        catch (RuntimeException e) {
            facesContext.getExternalContext().setClientWindow(null);
            throw e;
        }
    }

    @Override
    public Lifecycle getWrapped() {
        return wrapped;
    }
}
