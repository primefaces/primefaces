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
package org.primefaces.behavior.ajax;

import java.util.ArrayList;
import java.util.Collection;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.render.ClientBehaviorRenderer;
import org.primefaces.component.api.ClientBehaviorRenderingMode;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.AjaxRequestBuilder;

public class AjaxBehaviorRenderer extends ClientBehaviorRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component, ClientBehavior behavior) {
        AjaxBehavior ajaxBehavior = (AjaxBehavior) behavior;

        if (!ajaxBehavior.isDisabled()) {
            AjaxBehaviorEvent event = new AjaxBehaviorEvent(component, behavior);

            PhaseId phaseId = isImmediate(component, ajaxBehavior) ? PhaseId.APPLY_REQUEST_VALUES : PhaseId.INVOKE_APPLICATION;

            event.setPhaseId(phaseId);

            component.queueEvent(event);
        }
    }

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior behavior) {
        AjaxBehavior ajaxBehavior = (AjaxBehavior) behavior;
        if (ajaxBehavior.isDisabled()) {
            return null;
        }

        UIComponent component = behaviorContext.getComponent();

        ClientBehaviorRenderingMode renderingMode = ClientBehaviorRenderingMode.OBSTRUSIVE;

        Collection<ClientBehaviorContext.Parameter> behaviorParameters = behaviorContext.getParameters();
        if (behaviorParameters != null && !behaviorParameters.isEmpty()) {
            // perf optimzation
            if (behaviorParameters instanceof ArrayList) {
                for (int i = 0; i < behaviorParameters.size(); i++) {
                    ClientBehaviorContext.Parameter behaviorParameter =
                            ((ArrayList<ClientBehaviorContext.Parameter>) behaviorParameters).get(i);
                    if (behaviorParameter.getValue() instanceof ClientBehaviorRenderingMode) {
                        renderingMode = (ClientBehaviorRenderingMode) behaviorParameter.getValue();
                        break;
                    }
                }
            }
            else {
                for (ClientBehaviorContext.Parameter behaviorParameter : behaviorParameters) {
                    if (behaviorParameter.getValue() instanceof ClientBehaviorRenderingMode) {
                        renderingMode = (ClientBehaviorRenderingMode) behaviorParameter.getValue();
                        break;
                    }
                }
            }
        }

        String source = behaviorContext.getSourceId();
        String process = ajaxBehavior.getProcess();
        if (process == null) {
            process = "@this";
        }

        AjaxRequestBuilder builder = PrimeRequestContext.getCurrentInstance().getAjaxRequestBuilder();

        String request = builder.init()
                .source(source)
                .event(behaviorContext.getEventName())
                .form(ajaxBehavior, component)
                .process(component, process)
                .update(component, ajaxBehavior.getUpdate())
                .async(ajaxBehavior.isAsync())
                .global(ajaxBehavior.isGlobal())
                .delay(ajaxBehavior.getDelay())
                .timeout(ajaxBehavior.getTimeout())
                .partialSubmit(ajaxBehavior.isPartialSubmit(), ajaxBehavior.isPartialSubmitSet(), ajaxBehavior.getPartialSubmitFilter())
                .resetValues(ajaxBehavior.isResetValues(), ajaxBehavior.isResetValuesSet())
                .ignoreAutoUpdate(ajaxBehavior.isIgnoreAutoUpdate())
                .skipChildren(ajaxBehavior.isSkipChildren())
                .onstart(ajaxBehavior.getOnstart())
                .onerror(ajaxBehavior.getOnerror())
                .onsuccess(ajaxBehavior.getOnsuccess())
                .oncomplete(ajaxBehavior.getOncomplete())
                .params(component)
                .buildBehavior(renderingMode);

        return request;
    }

    private boolean isImmediate(UIComponent component, AjaxBehavior ajaxBehavior) {
        boolean immediate = false;

        if (ajaxBehavior.isImmediateSet()) {
            immediate = ajaxBehavior.isImmediate();
        }
        else if (component instanceof EditableValueHolder) {
            immediate = ((EditableValueHolder) component).isImmediate();
        }
        else if (component instanceof ActionSource) {
            immediate = ((ActionSource) component).isImmediate();
        }

        return immediate;
    }
}
