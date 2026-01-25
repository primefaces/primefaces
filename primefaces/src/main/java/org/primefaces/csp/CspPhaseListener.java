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
package org.primefaces.csp;

import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;

import jakarta.faces.application.ProjectStage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

import org.owasp.encoder.Encode;

public class CspPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    private Lazy<Boolean> enabled;
    private Lazy<Boolean> policyProvided;
    private Lazy<String> customPolicy;
    private Lazy<String> reportOnlyPolicy;

    public CspPhaseListener() {
        enabled = new Lazy<>(() ->
                PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig().isCsp());
        policyProvided = new Lazy<>(() ->
                PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig().isPolicyProvided());
        customPolicy = new Lazy<>(() ->
                PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig().getCspPolicy());
        reportOnlyPolicy = new Lazy<>(() ->
                PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig().getCspReportOnlyPolicy());
    }
    @Override
    public void afterPhase(PhaseEvent event) {

    }

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        initCsp(context, enabled.get(), policyProvided.get(), reportOnlyPolicy.get(), customPolicy.get());
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    public static void initCsp(FacesContext context, Boolean enabled, Boolean policyProvided, String reportOnlyPolicy, String customPolicy) {
        if (Boolean.FALSE.equals(enabled) || Boolean.TRUE.equals(policyProvided)) {
            return;
        }

        ExternalContext externalContext = context.getExternalContext();

        CspState state = PrimeFacesContext.getCspState(context);
        if (state.isInitialized()) {
            // already have run initCsp() once no need to run it again
            return;
        }
        state.setInitialized(true);

        if (LangUtils.isNotBlank(reportOnlyPolicy)) {
            String policy = reportOnlyPolicy.trim();
            if (!policy.contains("script-src") && !policy.contains("default-src")) {
                if (!policy.endsWith(";")) {
                    policy += ";";
                }
                policy += " script-src 'self'";
            }
            policy += " 'nonce-" + state.getNonce() + "';";
            externalContext.addResponseHeader("Content-Security-Policy-Report-Only", policy);
        }
        else {
            if (customPolicy != null) {
                customPolicy = context.getApplication().evaluateExpressionGet(context, customPolicy, String.class);
            }
            String policy = LangUtils.isBlank(customPolicy) ? "script-src 'self'" : customPolicy;
            policy += " 'nonce-" + state.getNonce() + "';";
            externalContext.addResponseHeader("Content-Security-Policy", policy);
        }

        String init = "if(window.PrimeFaces){PrimeFaces.csp.init('" + Encode.forJavaScript(state.getNonce()) + "');}";
        if (context.isProjectStage(ProjectStage.Development)) {
            init += "else{console.log('CSP active but PrimeFaces not included in current view!');}";
        }
        PrimeFaces.current().executeInitScript(init);
    }

}
