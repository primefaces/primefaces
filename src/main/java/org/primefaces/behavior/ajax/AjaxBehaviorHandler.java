/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.behavior.ajax;

import java.util.Map;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;

import org.primefaces.behavior.base.AbstractBehaviorHandler;
import org.primefaces.component.api.PrimeClientBehaviorHolder;

public class AjaxBehaviorHandler extends AbstractBehaviorHandler<AjaxBehavior> {

    private static final Class[] EMPTY_PARAMS = new Class[]{};
    private static final Class[] ARG_PARAMS = new Class[]{AjaxBehaviorEvent.class};

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
    private final TagAttribute partialSubmit;
    private final TagAttribute resetValues;
    private final TagAttribute ignoreAutoUpdate;
    private final TagAttribute delay;
    private final TagAttribute timeout;
    private final TagAttribute partialSubmitFilter;
    private final TagAttribute form;
    private final TagAttribute skipChildren;

    public AjaxBehaviorHandler(BehaviorConfig config) {
        super(config);
        this.process = this.getAttribute(AjaxBehavior.PropertyKeys.process.name());
        this.update = this.getAttribute(AjaxBehavior.PropertyKeys.update.name());
        this.onstart = this.getAttribute(AjaxBehavior.PropertyKeys.onstart.name());
        this.onerror = this.getAttribute(AjaxBehavior.PropertyKeys.onerror.name());
        this.onsuccess = this.getAttribute(AjaxBehavior.PropertyKeys.onsuccess.name());
        this.oncomplete = this.getAttribute(AjaxBehavior.PropertyKeys.oncomplete.name());
        this.disabled = this.getAttribute(AjaxBehavior.PropertyKeys.disabled.name());
        this.immediate = this.getAttribute(AjaxBehavior.PropertyKeys.immediate.name());
        this.listener = this.getAttribute(AjaxBehavior.PropertyKeys.listener.name());
        this.global = this.getAttribute(AjaxBehavior.PropertyKeys.global.name());
        this.async = this.getAttribute(AjaxBehavior.PropertyKeys.async.name());
        this.partialSubmit = this.getAttribute(AjaxBehavior.PropertyKeys.partialSubmit.name());
        this.resetValues = this.getAttribute(AjaxBehavior.PropertyKeys.resetValues.name());
        this.ignoreAutoUpdate = this.getAttribute(AjaxBehavior.PropertyKeys.ignoreAutoUpdate.name());
        this.delay = this.getAttribute(AjaxBehavior.PropertyKeys.delay.name());
        this.timeout = this.getAttribute(AjaxBehavior.PropertyKeys.timeout.name());
        this.partialSubmitFilter = this.getAttribute(AjaxBehavior.PropertyKeys.partialSubmitFilter.name());
        this.form = this.getAttribute(AjaxBehavior.PropertyKeys.form.name());
        this.skipChildren = this.getAttribute(AjaxBehavior.PropertyKeys.skipChildren.name());
    }

    @Override
    protected AjaxBehavior createBehavior(FaceletContext ctx, String eventName, UIComponent parent) {
        Application application = ctx.getFacesContext().getApplication();
        AjaxBehavior behavior = (AjaxBehavior) application.createBehavior(AjaxBehavior.BEHAVIOR_ID);

        setBehaviorAttribute(ctx, behavior, this.process, AjaxBehavior.PropertyKeys.process.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.update, AjaxBehavior.PropertyKeys.update.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.onstart, AjaxBehavior.PropertyKeys.onstart.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.onerror, AjaxBehavior.PropertyKeys.onerror.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.onsuccess, AjaxBehavior.PropertyKeys.onsuccess.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.oncomplete, AjaxBehavior.PropertyKeys.oncomplete.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.disabled, AjaxBehavior.PropertyKeys.disabled.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.immediate, AjaxBehavior.PropertyKeys.immediate.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.global, AjaxBehavior.PropertyKeys.global.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.async, AjaxBehavior.PropertyKeys.async.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.partialSubmit, AjaxBehavior.PropertyKeys.partialSubmit.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.listener, AjaxBehavior.PropertyKeys.listener.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.resetValues, AjaxBehavior.PropertyKeys.resetValues.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.ignoreAutoUpdate, AjaxBehavior.PropertyKeys.ignoreAutoUpdate.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.delay, AjaxBehavior.PropertyKeys.delay.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.timeout, AjaxBehavior.PropertyKeys.timeout.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.partialSubmitFilter, AjaxBehavior.PropertyKeys.partialSubmitFilter.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.form, AjaxBehavior.PropertyKeys.form.getExpectedType());
        setBehaviorAttribute(ctx, behavior, this.skipChildren, AjaxBehavior.PropertyKeys.skipChildren.getExpectedType());

        if (listener != null) {

            Class<? extends BehaviorEvent> eventMappingClass = null;

            if (parent instanceof PrimeClientBehaviorHolder) {
                Map<String, Class<? extends BehaviorEvent>> mapping = ((PrimeClientBehaviorHolder) parent).getBehaviorEventMapping();
                if (mapping != null) {
                    eventMappingClass = mapping.get(eventName);
                }
            }

            if (eventMappingClass == null) {
                behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(
                        this.listener.getMethodExpression(ctx, Void.class, EMPTY_PARAMS),
                        this.listener.getMethodExpression(ctx, Void.class, ARG_PARAMS)));
            }
            else {
                behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(
                        this.listener.getMethodExpression(ctx, Void.class, EMPTY_PARAMS),
                        this.listener.getMethodExpression(ctx, Void.class, ARG_PARAMS),
                        this.listener.getMethodExpression(ctx, Void.class, new Class[]{eventMappingClass})));
            }
        }

        return behavior;
    }
}
