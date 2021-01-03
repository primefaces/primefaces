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
package org.primefaces.component.cache;

import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;


public class UICache extends UICacheBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Cache";

    public boolean isCacheSetInCurrentRequest() {
        return getFacesContext().getAttributes().containsKey(this.getClientId() + "setInCurrentRequest");
    }

    public void setCacheSetInCurrentRequest(boolean cacheSetInCurrentRequest) {
        getFacesContext().getAttributes().put(this.getClientId() + "setInCurrentRequest", cacheSetInCurrentRequest);
    }

    @Override
    protected boolean isVisitable(VisitContext visitContext) {
        return isDisabled() || isCacheSetInCurrentRequest();
    }

    protected boolean shouldProcess() {
        return isDisabled() || isCacheSetInCurrentRequest() || isProcessEvents();
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (shouldProcess()) {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (shouldProcess()) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (shouldProcess()) {
            super.processUpdates(context);
        }
    }
}