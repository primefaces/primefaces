/*
 * Copyright 2009-2014 PrimeTek.
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
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectHandler;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.BehaviorHolderAttachedObjectHandler;
import javax.faces.view.BehaviorHolderAttachedObjectTarget;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

import org.primefaces.behavior.ajax.AjaxBehaviorHandler;
import org.primefaces.context.RequestContext;

public abstract class AbstractBehaviorHandler<E extends AbstractBehavior>
	extends TagHandler implements BehaviorHolderAttachedObjectHandler {

	private final TagAttribute event;
	
    public AbstractBehaviorHandler(TagConfig config) {
		super(config);

		this.event = this.getAttribute("event");
	}

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
            
            List<AttachedObjectTarget> targetList = (List<AttachedObjectTarget>)componentDescriptor.getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);
            if (null == targetList && !tagApplied) {
                throw new TagException(tag, "Composite component does not support behavior events");
            }
            
            boolean supportedEvent = false;
            for (AttachedObjectTarget target : targetList) {
                if (target instanceof BehaviorHolderAttachedObjectTarget) {
                    BehaviorHolderAttachedObjectTarget behaviorTarget = (BehaviorHolderAttachedObjectTarget) target;
                    if ((null != eventName && eventName.equals(behaviorTarget.getName()))
                        || (null == eventName && behaviorTarget.isDefaultEvent())) {
                        supportedEvent = true;
                        break;
                    }
                }
            }
            
            if(supportedEvent) {
                // Workaround to implementation specific composite component handlers
            	FacesContext context = FacesContext.getCurrentInstance();
                if (context.getExternalContext().getApplicationMap().containsKey("com.sun.faces.ApplicationAssociate")) {
                	addAttachedObjectHandlerToMojarra(parent);
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
            throw new TagException(this.tag, "Unable to attach behavior to non-ClientBehaviorHolder parent");
        }
    }

    public String getEventName() {
    	if (event == null) {
    		return null;
    	}

    	if (event.isLiteral()) {
    		return event.getValue();
    	} else {
    		FaceletContext faceletContext = getFaceletContext(FacesContext.getCurrentInstance());
    		ValueExpression expression = event.getValueExpression(faceletContext, String.class);
    		return (String) expression.getValue(faceletContext);
    	}
    }

    protected abstract E createBehavior(FaceletContext ctx, String eventName);

    protected void setBehaviorAttribute(FaceletContext ctx, E behavior, TagAttribute attr, Class<?> type) {
    	if (attr != null) {
    		String attributeName = attr.getLocalName();
            if (attr.isLiteral()) {
                behavior.setLiteral(attributeName, attr.getObject(ctx, type));
            } else {
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
                throw new TagException(this.tag, "Event attribute could not be determined: "  + eventName);
            }
        } else {
            Collection<String> eventNames = holder.getEventNames();
            if (!eventNames.contains(eventName)) {
                throw new TagException(this.tag,  "Event:" + eventName + " is not supported.");
            }
        }

        ClientBehaviorBase behavior = createBehavior(faceletContext, eventName);
        holder.addClientBehavior(eventName, behavior);
    }

    
	public String getFor() {
		return null;
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Mojarra ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	protected static String MOJARRA_ATTACHED_OBJECT_HANDLERS_KEY = "javax.faces.RetargetableHandlers";
	protected static String MOJARRA_22_ATTACHED_OBJECT_HANDLERS_KEY = "javax.faces.view.AttachedObjectHandlers";

	protected void addAttachedObjectHandlerToMojarra(UIComponent component) {

    	String key = MOJARRA_ATTACHED_OBJECT_HANDLERS_KEY;
    	if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22())
    	{
    		key = MOJARRA_22_ATTACHED_OBJECT_HANDLERS_KEY;
    	}
    	
        Map<String, Object> attrs = component.getAttributes();
        List<AttachedObjectHandler> result = (List<AttachedObjectHandler>) attrs.get(key);

        if (result == null) {
            result = new ArrayList<AttachedObjectHandler>();
            attrs.put(key, result);
        }
        
        result.add(this);
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ MyFaces ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
    protected static Method MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE;
    protected static Method MYFACES_ADD_ATTACHED_OBJECT_HANDLER;
	
    protected void addAttachedObjectHandlerToMyFaces(UIComponent component, FaceletContext ctx) {
        try {
        	if (MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE == null || MYFACES_ADD_ATTACHED_OBJECT_HANDLER == null) {
                
                Class<?> clazz = null;
                try {
                    clazz = Class.forName("org.apache.myfaces.view.facelets.FaceletCompositionContext");
                }
                catch (ClassNotFoundException cnfe) {
                    clazz = Class.forName("org.apache.myfaces.view.facelets.FaceletCompositionContext",
                            true,
                            Thread.currentThread().getContextClassLoader());
                }

        		MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE = clazz.getDeclaredMethod("getCurrentInstance", FaceletContext.class);
        		MYFACES_ADD_ATTACHED_OBJECT_HANDLER = clazz.getDeclaredMethod("addAttachedObjectHandler", UIComponent.class, AttachedObjectHandler.class);
        	}

            Object faceletCompositionContextInstance = MYFACES_GET_COMPOSITION_CONTEXT_INSTANCE.invoke(null, ctx);
            MYFACES_ADD_ATTACHED_OBJECT_HANDLER.invoke(faceletCompositionContextInstance, component, this);
        } 
        catch (Exception ex) {
            Logger.getLogger(AjaxBehaviorHandler.class.getName()).log(Level.SEVERE, "Could not add AttachedObjectHandler to MyFaces!", ex);
        }
	}
}
