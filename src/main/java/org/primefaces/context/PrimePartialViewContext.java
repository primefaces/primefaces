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

import javax.faces.component.EditableValueHolder;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;
import javax.faces.event.PhaseId;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.csp.CspPartialResponseWriter;
import org.primefaces.expression.SearchExpressionConstants;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.visit.ResetInputContextCallback;
import org.primefaces.visit.ResetInputVisitCallback;

public class PrimePartialViewContext extends PartialViewContextWrapper {

    private PartialViewContext wrapped;
    private PartialResponseWriter writer = null;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimePartialViewContext(PartialViewContext wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public PartialViewContext getWrapped() {
        return this.wrapped;
    }

    @Override
    public void processPartial(PhaseId phaseId) {
        if (phaseId == PhaseId.RENDER_RESPONSE) {
            FacesContext context = FacesContext.getCurrentInstance();
            resetValues(context);
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
            writer = new PrimePartialResponseWriter(parentWriter);

            FacesContext context = FacesContext.getCurrentInstance();
            PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(context).getConfig();
            if (config.isCsp()) {
                writer = new CspPartialResponseWriter(writer, context, PrimeFacesContext.getCspState(context));
            }
        }

        return writer;
    }

    @Override
    public boolean isAjaxRequest() {
        return getWrapped().isAjaxRequest()
                || FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("javax.faces.partial.ajax");
    }

    @Override
    public boolean isPartialRequest() {
        return getWrapped().isPartialRequest()
                || FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("javax.faces.partial.execute");
    }

    /**
     * Visit the current renderIds and, if the component is
     * an instance of {@link EditableValueHolder},
     * call its {@link EditableValueHolder#resetValue} method.
     * Use {@link #visitTree} to do the visiting.</p>
     *
     * @param context The current {@link FacesContext}.
     */
    private void resetValues(FacesContext context) {
        Object resetValuesObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.RESET_VALUES_PARAM);
        boolean resetValues = (null != resetValuesObject && "true".equals(resetValuesObject));

        if (resetValues) {
            VisitContext visitContext = null;
            ResetInputContextCallback contextCallback = null;

            for (String renderId : context.getPartialViewContext().getRenderIds()) {
                if (LangUtils.isValueBlank(renderId) || renderId.trim().equals(SearchExpressionConstants.NONE_KEYWORD)) {
                    continue;
                }

                // lazy init
                if (visitContext == null) {
                    visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                }

                if (renderId.equals(SearchExpressionConstants.ALL_KEYWORD)) {
                    context.getViewRoot().visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
                }
                else {
                    // lazy init
                    if (contextCallback == null) {
                        contextCallback = new ResetInputContextCallback(visitContext);
                    }

                    context.getViewRoot().invokeOnComponent(context, renderId, contextCallback);
                }
            }
        }
    }
}
