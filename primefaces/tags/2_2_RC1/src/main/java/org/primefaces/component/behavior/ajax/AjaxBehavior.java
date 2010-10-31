/*
 * Copyright 2010 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.behavior.ajax;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.component.behavior.FacesBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.BehaviorEvent;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="core/core.js")
})
@FacesBehavior("org.primefaces.component.AjaxBehavior")
public class AjaxBehavior extends ClientBehaviorBase {

    private String update;
    private String process;
    private boolean global = true;
    private boolean async = false;
    private String oncomplete;
    private String onerror;
    private String onsuccess;
    private String onstart;
    private MethodExpression listener;
    private boolean immediate = false;
    private boolean disabled = false;
    private boolean immediateSet = false;

    private static final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));

    private Map<String, ValueExpression> bindings;

    @Deprecated
    private MethodExpression action;

    @Deprecated
    private MethodExpression actionListener;

    @Override
    public String getRendererType() {
        return "org.primefaces.component.AjaxBehaviorRenderer";
    }
    
    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }
    
    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getOnstart() {
        return onstart;
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;
    }

    public String getOnsuccess() {
        return onsuccess;
    }

    public void setOnsuccess(String onsuccess) {
        this.onsuccess = onsuccess;
    }

    public String getOnerror() {
        return onerror;
    }

    public void setOnerror(String onerror) {
        this.onerror = onerror;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public MethodExpression getListener() {
        return listener;
    }

    public void setListener(MethodExpression listener) {
        this.listener = listener;
    }

    public MethodExpression getAction() {
        return action;
    }

    public void setAction(MethodExpression action) {
        this.action = action;
    }

    public MethodExpression getActionListener() {
        return actionListener;
    }

    public void setActionListener(MethodExpression actionListener) {
        this.actionListener = actionListener;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;

        this.immediateSet = true;
    }

    public boolean isImmediateSet() {
        return immediateSet;
    }

    @Override
    public void broadcast(BehaviorEvent event) throws AbortProcessingException {
        ELContext eLContext = FacesContext.getCurrentInstance().getELContext();

        //Backward compatible implementation of listener invocation
        if(listener != null) {
            try {
                listener.invoke(eLContext, new Object[]{event});
            } catch(IllegalArgumentException exception) {
                listener.invoke(eLContext, new Object[0]);
            }
        }
        else if(action != null) {
            action.invoke(eLContext, new Object[0]);
        }
        else if(actionListener != null) {
            try {
                actionListener.invoke(eLContext, new Object[]{new ActionEvent(event.getComponent())});
            } catch(IllegalArgumentException exception) {
                actionListener.invoke(eLContext, new Object[0]);
            }
        }
    }
}