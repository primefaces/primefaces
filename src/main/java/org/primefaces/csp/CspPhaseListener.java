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
package org.primefaces.csp;

import org.owasp.encoder.Encode;
import org.primefaces.PrimeFaces;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.util.LangUtils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

public class CspPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    private String customPolicy;

    @Override
    public void afterPhase(PhaseEvent event) {

    }

    @Override
    public void beforePhase(PhaseEvent event) {
        PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig();
        if (config.isCsp()) {
            customPolicy = config.getCspPolicy();
        }
        else {
            return;
        }

        FacesContext context = event.getFacesContext();
        ExternalContext externalContext = context.getExternalContext();
        // TODO Support portlet environments?
        if (externalContext.getResponse() instanceof HttpServletResponse) {
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            CspState state = PrimeFacesContext.getCspState(context);

            String policy = LangUtils.isValueBlank(customPolicy) ? "script-src 'self'" : customPolicy;
            response.addHeader("Content-Security-Policy", policy + " 'nonce-" + state.getNonce() + "'");

            PrimeFaces.current().executeScript("PrimeFaces.csp.init('" + Encode.forJavaScript(state.getNonce()) + "');");
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}
