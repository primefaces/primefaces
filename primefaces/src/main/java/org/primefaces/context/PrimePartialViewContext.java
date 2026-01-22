/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.config.PrimeConfiguration;
import org.primefaces.csp.CspPartialResponseWriter;
import org.primefaces.util.Constants;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialResponseWriter;
import jakarta.faces.context.PartialViewContext;
import jakarta.faces.context.PartialViewContextWrapper;
import jakarta.faces.event.PhaseId;

public class PrimePartialViewContext extends PartialViewContextWrapper {

    private PartialResponseWriter writer;

    public PrimePartialViewContext(PartialViewContext wrapped) {
        super(wrapped);
    }

    @Override
    public void processPartial(PhaseId phaseId) {
        getWrapped().processPartial(phaseId);
    }

    @Override
    public void setPartialRequest(boolean value) {
        getWrapped().setPartialRequest(value);
    }

    @Override
    public PartialResponseWriter getPartialResponseWriter() {
        if (writer == null) {
            PartialResponseWriter parentWriter = getWrapped().getPartialResponseWriter();
            FacesContext context = FacesContext.getCurrentInstance();
            PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(context).getConfig();
            if (config.isCsp()) {
                writer = new CspPartialResponseWriter(parentWriter, context, PrimeFacesContext.getCspState(context));
            }
            else {
                writer = new PrimePartialResponseWriter(parentWriter);
            }
        }

        return writer;
    }

    @Override
    public boolean isAjaxRequest() {
        return getWrapped().isAjaxRequest()
                || FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey(
                        Constants.RequestParams.PARTIAL_REQUEST_PARAM);
    }

    @Override
    public boolean isPartialRequest() {
        return getWrapped().isPartialRequest()
                || FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey(
                        Constants.RequestParams.PARTIAL_PROCESS_PARAM);
    }
}
