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

import org.primefaces.application.resource.MoveScriptsToBottomResponseWriter;
import org.primefaces.application.resource.MoveScriptsToBottomState;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.csp.CspResponseWriter;
import org.primefaces.csp.CspState;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextWrapper;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.context.ResponseWriterWrapper;

/**
 * Custom {@link FacesContextWrapper} to init and release our {@link PrimeRequestContext}.
 */
public class PrimeFacesContext extends FacesContextWrapper {

    private final boolean moveScriptsToBottom;
    private final boolean csp;
    private final boolean markInputAsInvalidOnErrorMsg;

    private MoveScriptsToBottomState moveScriptsToBottomState;
    private CspState cspState;
    private PrimeExternalContext externalContext;

    public PrimeFacesContext(FacesContext wrapped) {
        super(wrapped);

        PrimeRequestContext requestContext = new PrimeRequestContext(wrapped);
        PrimeRequestContext.setCurrentInstance(requestContext, wrapped);

        PrimeConfiguration config = requestContext.getApplicationContext().getConfig();

        moveScriptsToBottom = config.isMoveScriptsToBottom();
        if (moveScriptsToBottom) {
            moveScriptsToBottomState = new MoveScriptsToBottomState(config.isMoveScriptsToBottomDeferred());
        }

        csp = config.isCsp();
        if (csp) {
            cspState = getCspState(this);
        }

        markInputAsInvalidOnErrorMsg = config.isMarkInputAsInvalidOnErrorMsg();

        FacesContext.setCurrentInstance(this);
    }

    @Override
    public ExternalContext getExternalContext() {
        if (externalContext == null) {
            externalContext = new PrimeExternalContext(getWrapped().getExternalContext());
        }
        return externalContext;
    }

    @Override
    public void setResponseWriter(ResponseWriter writer) {

        boolean alreadyWrapped = false;

        ResponseWriter wrappedWriter = writer;
        while (wrappedWriter != null) {
            if (wrappedWriter instanceof ResponseWriterWrapper) {
                if (wrappedWriter instanceof MoveScriptsToBottomResponseWriter
                        || wrappedWriter instanceof CspResponseWriter) {
                    alreadyWrapped = true;
                    break;
                }
                wrappedWriter = ((ResponseWriterWrapper) wrappedWriter).getWrapped();
            }
            else {
                break;
            }
        }

        if (!alreadyWrapped) {
            if (csp && !getPartialViewContext().isAjaxRequest()) {
                writer = new CspResponseWriter(writer, cspState);
            }

            if (moveScriptsToBottom && !getPartialViewContext().isAjaxRequest()) {
                writer = new MoveScriptsToBottomResponseWriter(writer, moveScriptsToBottomState);
            }
        }

        getWrapped().setResponseWriter(writer);
    }

    @Override
    public void release() {
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(getWrapped());
        if (requestContext != null) {
            requestContext.release();
        }

        super.release();

        // just to be sure!
        FacesContext.setCurrentInstance(null);
    }

    @Override
    public void addMessage(String clientId, FacesMessage message) {
        super.addMessage(clientId, message);

        if (markInputAsInvalidOnErrorMsg
                && clientId != null
                && message != null
                && FacesMessage.SEVERITY_ERROR.equals(message.getSeverity())) {

            getViewRoot().invokeOnComponent(this, clientId, (context, target) -> {
                if (target instanceof UIInput) {
                    ((UIInput) target).setValid(false);
                }
            });
        }
    }

    public static CspState getCspState(FacesContext context) {
        CspState cspState = (CspState) context.getAttributes().get(CspState.class.getName());
        if (cspState == null) {
            cspState = new CspState(context);
            context.getAttributes().put(CspState.class.getName(), cspState);
        }

        return cspState;
    }
}
