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
package org.primefaces.cdk.api.behavior;

import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.utils.ReflectionUtils;

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

import jakarta.el.ValueExpression;
import jakarta.faces.application.Application;
import jakarta.faces.component.StateHelper;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.AttachedObjectHandler;
import jakarta.faces.view.AttachedObjectTarget;
import jakarta.faces.view.BehaviorHolderAttachedObjectHandler;
import jakarta.faces.view.BehaviorHolderAttachedObjectTarget;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagException;
import jakarta.faces.view.facelets.TagHandler;

public abstract class PrimeClientBehaviorHandler<E extends PrimeClientBehavior>
        extends TagHandler implements BehaviorHolderAttachedObjectHandler {

    protected static final String MOJARRA_ATTACHED_OBJECT_HANDLERS_KEY = "jakarta.faces.view.AttachedObjectHandlers";
    protected static final Method MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE;
    protected static final Method MYFACES_ADD_ATTACHED_OBJECT_HANDLER;

    private static final Logger LOGGER = Logger.getLogger(PrimeClientBehaviorHandler.class.getName());

    private final TagAttribute event;

    static {
        Class<?> clazz = ReflectionUtils.tryToLoadClassForName("org.apache.myfaces.view.facelets.FaceletCompositionContext");
        if (clazz != null) {
            MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE =
                    ReflectionUtils.tryToLoadMethodForName(clazz, "getCurrentInstance", FaceletContext.class);
            MYFACES_ADD_ATTACHED_OBJECT_HANDLER =
                    ReflectionUtils.tryToLoadMethodForName(clazz, "addAttachedObjectHandler", UIComponent.class, AttachedObjectHandler.class);
        }
        else {
            MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE = null;
            MYFACES_ADD_ATTACHED_OBJECT_HANDLER = null;
        }
    }

    protected PrimeClientBehaviorHandler(TagConfig config) {
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
                if (MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE == null) {
                    Map<String, Object> attrs = parent.getAttributes();
                    List<AttachedObjectHandler> result = (List<AttachedObjectHandler>) attrs.computeIfAbsent(MOJARRA_ATTACHED_OBJECT_HANDLERS_KEY,
                            k -> new ArrayList<AttachedObjectHandler>(5));
                    result.add(this);
                }
                else {
                    try {
                        Object faceletCompositionContextInstance = MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE.invoke(null, faceletContext);
                        MYFACES_ADD_ATTACHED_OBJECT_HANDLER.invoke(faceletCompositionContextInstance, parent, this);
                    }
                    catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, "Could not add AttachedObjectHandler to MyFaces!", ex);
                    }
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
            return expression.getValue(faceletContext);
        }
    }

    protected void setBehaviorAttribute(FaceletContext ctx, E behavior, TagAttribute attr, Class<?> type) {
        if (attr != null) {
            String attributeName = attr.getLocalName();

            StateHelper stateHelper = behavior.getStateHelper();
            if (!(stateHelper instanceof ValueExpressionStateHelper) || attr.isLiteral()) {
                stateHelper.put(attributeName, attr.getObject(ctx, type));

                return;
            }

            ((ValueExpressionStateHelper) stateHelper).setBinding(attributeName, attr.getValueExpression(ctx, type));
        }
    }

    protected FaceletContext getFaceletContext(FacesContext context) {
        return (FaceletContext) context.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
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
                throw new TagException(tag, "Event: " + eventName + " is not supported on "
                        + parent.getClass().getSimpleName() + ".");
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

    protected void init(FaceletContext ctx, E behavior, String eventName, UIComponent parent) {
        for (PrimePropertyKeys property : behavior.getPropertyKeys()) {
            TagAttribute tag = getAttribute(property.getName());
            setBehaviorAttribute(ctx, behavior, tag, property.getType());
        }
    }
}
