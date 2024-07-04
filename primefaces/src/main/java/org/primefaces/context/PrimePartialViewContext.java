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
package org.primefaces.context;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;
import javax.faces.event.PhaseId;

import org.primefaces.config.PrimeConfiguration;
import org.primefaces.config.PrimeEnvironment;
import org.primefaces.csp.CspPartialResponseWriter;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.visit.ResetInputContextCallback;
import org.primefaces.visit.ResetInputVisitCallback;

public class PrimePartialViewContext extends PartialViewContextWrapper {

    private PartialResponseWriter writer;

    public PrimePartialViewContext(PartialViewContext wrapped) {
        super(wrapped);
    }

    @Override
    public void processPartial(PhaseId phaseId) {
        if (phaseId == PhaseId.RENDER_RESPONSE) {
            // fixed in Faces 4.0+ https://github.com/jakartaee/faces/issues/1936
            PrimeEnvironment environment = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getEnvironment();
            if (!environment.isAtLeastJsf40()) {
                resetValues(FacesContext.getCurrentInstance());
            }
        }

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

     /**
     * Backwards compatible support for JSF 2.3 and below.
     * Visit the current renderIds and, if the component is
     * an instance of {@link EditableValueHolder},
     * call its {@link EditableValueHolder#resetValue} method.
     * Use {@link javax.faces.component.UIComponent#visitTree} to do the visiting.</p>
     *
     * @param context The current {@link FacesContext}.
     * @see <a href="https://github.com/jakartaee/faces/issues/1936">Faces Issue #1936</a>
     */
    private void resetValues(FacesContext context) {
        Object resetValuesObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.RESET_VALUES_PARAM);
        boolean resetValues = (null != resetValuesObject && "true".equals(resetValuesObject));

        if (resetValues) {
            VisitContext visitContext = null;
            ResetInputContextCallback contextCallback = null;

            for (String renderId : context.getPartialViewContext().getRenderIds()) {
                String id = LangUtils.defaultIfBlank(renderId, Constants.EMPTY_STRING).trim();
                if (LangUtils.isBlank(id) || "@none".equals(id)) {
                    continue;
                }

                // lazy init
                if (visitContext == null) {
                    visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                }

                if ("@all".equals(id)) {
                    context.getViewRoot().visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
                }
                else {
                    // lazy init
                    if (contextCallback == null) {
                        contextCallback = new ResetInputContextCallback(visitContext);
                    }

                    context.getViewRoot().invokeOnComponent(context, id, contextCallback);
                }
            }
        }
    }
}
