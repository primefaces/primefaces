/*
 * Copyright 2009-2011 Prime Technology.
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

import java.io.IOException;
import java.util.Collection;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.BehaviorHolderAttachedObjectHandler;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

public class AjaxBehaviorHandler extends TagHandler implements BehaviorHolderAttachedObjectHandler  {

    private final TagAttribute event;
    private final TagAttribute process;
    private final TagAttribute update;
    private final TagAttribute onstart;
    private final TagAttribute onerror;
    private final TagAttribute onsuccess;
    private final TagAttribute oncomplete;
    private final TagAttribute disabled;
    private final TagAttribute immediate;
    private final TagAttribute listener;
    private final TagAttribute global;
    private final TagAttribute async;
    
    public AjaxBehaviorHandler(BehaviorConfig config) {
        super(config);
        this.event = this.getAttribute("event");
        this.process = this.getAttribute("process");
        this.update = this.getAttribute("update");
        this.onstart = this.getAttribute("onstart");
        this.onerror = this.getAttribute("onerror");
        this.onsuccess = this.getAttribute("onsuccess");
        this.oncomplete = this.getAttribute("oncomplete");
        this.disabled = this.getAttribute("disabled");
        this.immediate = this.getAttribute("immediate");
        this.listener = this.getAttribute("listener");
        this.global = this.getAttribute("global");
        this.async = this.getAttribute("async");
    }
    
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        String eventName = getEventName();

        if(parent instanceof ClientBehaviorHolder) {
            applyAttachedObject(ctx, parent, eventName);
        } else {
            throw new TagException(this.tag, "Unable to attach <p:ajax> to non-ClientBehaviorHolder parent");
        }
    }

    public String getEventName() {
        return (this.event != null) ? this.event.getValue() : null;
    }

    public void applyAttachedObject(FaceletContext context, UIComponent component, String eventName) {
        ClientBehaviorHolder holder = (ClientBehaviorHolder) component;

        if(null == eventName) {
            eventName = holder.getDefaultEventName();
            if (null == eventName) {
                throw new TagException(this.tag, "Event attribute could not be determined: "  + eventName);
            }
        } else {
            Collection<String> eventNames = holder.getEventNames();
            if (!eventNames.contains(eventName)) {
                throw new TagException(this.tag,  "Event:" + eventName + " is not supported.");
            }
        }

        AjaxBehavior ajaxBehavior = createAjaxBehavior(context, eventName);
        holder.addClientBehavior(eventName, ajaxBehavior);
    }
    
    // Construct our AjaxBehavior from tag parameters.
    private AjaxBehavior createAjaxBehavior(FaceletContext ctx, String eventName) {
        Application application = ctx.getFacesContext().getApplication();
        AjaxBehavior behavior = (AjaxBehavior)application.createBehavior(AjaxBehavior.BEHAVIOR_ID);

        setBehaviorAttribute(ctx, behavior, this.process, String.class);
        setBehaviorAttribute(ctx, behavior, this.update, String.class);
        setBehaviorAttribute(ctx, behavior, this.onstart, String.class);
        setBehaviorAttribute(ctx, behavior, this.onerror, String.class);
        setBehaviorAttribute(ctx, behavior, this.onsuccess, String.class);
        setBehaviorAttribute(ctx, behavior, this.oncomplete, String.class);
        setBehaviorAttribute(ctx, behavior, this.disabled, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.immediate, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.global, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.async, Boolean.class);
        setBehaviorAttribute(ctx, behavior, this.listener, MethodExpression.class);
        
        if(listener != null) {
            behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(
                this.listener.getMethodExpression(ctx, Object.class, new Class[] { AjaxBehaviorEvent.class }),
                this.listener.getMethodExpression(ctx, Object.class, new Class[] { })));
        }
        
        return behavior;
    }

    public String getFor() {
        return null;
    }

    public void applyAttachedObject(FacesContext context, UIComponent parent) {
        FaceletContext ctx = (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        
        applyAttachedObject(ctx, parent, getEventName());
    }
    
    private void setBehaviorAttribute(FaceletContext ctx, AjaxBehavior behavior, TagAttribute attr, Class type) {
        if(attr != null) {
            behavior.setValueExpression(attr.getLocalName(), attr.getValueExpression(ctx, type));
        }    
    }
}