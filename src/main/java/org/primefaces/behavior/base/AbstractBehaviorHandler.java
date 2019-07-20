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
package org.primefaces.behavior.base;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectHandler;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.BehaviorHolderAttachedObjectHandler;
import javax.faces.view.BehaviorHolderAttachedObjectTarget;
import javax.faces.view.facelets.*;

import org.primefaces.behavior.ajax.AjaxBehaviorHandler;
import org.primefaces.config.PrimeEnvironment;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.LangUtils;

public abstract class AbstractBehaviorHandler<E extends AbstractBehavior>
        extends TagHandler implements BehaviorHolderAttachedObjectHandler {

    protected static final String MOJARRA_ATTACHED_OBJECT_HANDLERS_KEY = "javax.faces.RetargetableHandlers";
    protected static final String MOJARRA_22_ATTACHED_OBJECT_HANDLERS_KEY = "javax.faces.view.AttachedObjectHandlers";

    protected static Method myfacesGetCompositionContextInstance;
    protected static Method myfacesAddAttachedObjectHandler;

    private final TagAttribute event;

    public AbstractBehaviorHandler(TagConfig config) {
        super(config);

        event = getAttribute("event");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        String eventName = getEventName();

        if (UIComponent.isCompositeComponent(parent)) {
            boolean tagApplied = false;
            if (parent instanceof ClientBehaviorHolder) {
                applyAttachedObject(faceletContext, parent);
                tagApplied = true;
            }

            BeanInfo componentBeanInfo = (BeanInfo) parent.getAttributes().get(UIComponent.BEANINFO_KEY);
            if (null == componentBeanInfo) {
                throw new TagException(tag, "Composite component does not have BeanInfo attribute");
            }

            BeanDescriptor componentDescriptor = componentBeanInfo.getBeanDescriptor();
            if (null == componentDescriptor) {
                throw new TagException(tag, "Composite component BeanInfo does not have BeanDescriptor");
            }

            List<AttachedObjectTarget> targetList = (List<AttachedObjectTarget>) componentDescriptor.getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);
            if (null == targetList && !tagApplied) {
                throw new TagException(tag, "Composite component does not support behavior events");
            }

            boolean supportedEvent = false;
            if (targetList != null) {
                for (int i = 0; i < targetList.size(); i++) {
                    AttachedObjectTarget target = targetList.get(i);
                    if (target instanceof BehaviorHolderAttachedObjectTarget) {
                        BehaviorHolderAttachedObjectTarget behaviorTarget = (BehaviorHolderAttachedObjectTarget) target;
                        if ((null != eventName && eventName.equals(behaviorTarget.getName()))
                                || (null == eventName && behaviorTarget.isDefaultEvent())) {
                            supportedEvent = true;
                            break;
                        }
                    }
                }
            }

            if (supportedEvent) {
                // Workaround to implementation specific composite component handlers
                FacesContext context = FacesContext.getCurrentInstance();
                PrimeEnvironment environment = PrimeApplicationContext.getCurrentInstance(context).getEnvironment();
                if (environment.isMojarra()) {
                    addAttachedObjectHandlerToMojarra(environment, parent);
                }
                else {
                    addAttachedObjectHandlerToMyFaces(parent, faceletContext);
                }
            }
            else {
                if (!tagApplied) {
                    throw new TagException(tag, "Composite component does not support event " + eventName);
                }
            }
        }
        else if (parent instanceof ClientBehaviorHolder) {
            applyAttachedObject(faceletContext, parent);
        }
        else {
            throw new TagException(tag, "Unable to attach behavior to non-ClientBehaviorHolder parent");
        }
    }

    @Override
    public String getEventName() {
        if (event == null) {
            return null;
        }

        if (event.isLiteral()) {
            return event.getValue();
        }
        else {
            FaceletContext faceletContext = getFaceletContext(FacesContext.getCurrentInstance());
            ValueExpression expression = event.getValueExpression(faceletContext, String.class);
            return (String) expression.getValue(faceletContext);
        }
    }

    protected void setBehaviorAttribute(FaceletContext ctx, E behavior, TagAttribute attr, Class<?> type) {
        if (attr != null) {
            String attributeName = attr.getLocalName();
            if (attr.isLiteral()) {
                behavior.setLiteral(attributeName, attr.getObject(ctx, type));
            }
            else {
                behavior.setValueExpression(attributeName, attr.getValueExpression(ctx, type));
            }
        }
    }

    protected FaceletContext getFaceletContext(FacesContext context) {
        FaceletContext faceletContext = (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        if (faceletContext == null) {
            faceletContext = (FaceletContext) context.getAttributes().get("com.sun.faces.facelets.FACELET_CONTEXT");
        }

        return faceletContext;
    }

    @Override
    public void applyAttachedObject(FacesContext context, UIComponent parent) {
        FaceletContext faceletContext = getFaceletContext(context);
        applyAttachedObject(faceletContext, parent);
    }

    public void applyAttachedObject(FaceletContext faceletContext, UIComponent parent) {
        ClientBehaviorHolder holder = (ClientBehaviorHolder) parent;

        String eventName = getEventName();

        if (null == eventName) {
            eventName = holder.getDefaultEventName();
            if (null == eventName) {
                throw new TagException(tag, "Event attribute could not be determined: " + eventName);
            }
        }
        else {
            Collection<String> eventNames = holder.getEventNames();
            if (!eventNames.contains(eventName)) {
                throw new TagException(tag, "Event:" + eventName + " is not supported.");
            }
        }

        Application application = faceletContext.getFacesContext().getApplication();
        E behavior = (E) application.createBehavior(getBehaviorId());
        init(faceletContext, behavior, eventName, parent);
        holder.addClientBehavior(eventName, behavior);
    }

    public abstract String getBehaviorId();

    @Override
    public String getFor() {
        return null;
    }

    protected void addAttachedObjectHandlerToMojarra(PrimeEnvironment environment, UIComponent component) {

        String key = MOJARRA_ATTACHED_OBJECT_HANDLERS_KEY;
        if (environment.isAtLeastJsf22()) {
            key = MOJARRA_22_ATTACHED_OBJECT_HANDLERS_KEY;
        }

        Map<String, Object> attrs = component.getAttributes();
        List<AttachedObjectHandler> result = (List<AttachedObjectHandler>) attrs.computeIfAbsent(key, k -> new ArrayList());
        result.add(this);
    }

    protected void addAttachedObjectHandlerToMyFaces(UIComponent component, FaceletContext ctx) {
        try {
            if (myfacesGetCompositionContextInstance == null || myfacesAddAttachedObjectHandler == null) {
                Class<?> clazz = LangUtils.tryToLoadClassForName("org.apache.myfaces.view.facelets.FaceletCompositionContext");
                myfacesGetCompositionContextInstance = clazz.getDeclaredMethod("getCurrentInstance", FaceletContext.class);
                myfacesAddAttachedObjectHandler = clazz.getDeclaredMethod("addAttachedObjectHandler", UIComponent.class, AttachedObjectHandler.class);
            }

            Object faceletCompositionContextInstance = myfacesGetCompositionContextInstance.invoke(null, ctx);
            myfacesAddAttachedObjectHandler.invoke(faceletCompositionContextInstance, component, this);
        }
        catch (Exception ex) {
            Logger.getLogger(AjaxBehaviorHandler.class.getName()).log(Level.SEVERE, "Could not add AttachedObjectHandler to MyFaces!", ex);
        }
    }

    protected void init(FaceletContext ctx, E behavior, String eventName, UIComponent parent) {
        for (BehaviorAttribute attr : behavior.getAllAttributes()) {
            TagAttribute tag = getAttribute(attr.getName());
            setBehaviorAttribute(ctx, behavior, tag, attr.getExpectedType());
        }
    }
}
