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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.util.ComponentUtils;

public class CoreRenderer extends Renderer {
	
	private final static String WIDGET_VAR_PREFIX = "widget_";
	
	protected void renderChildren(FacesContext facesContext, UIComponent component) throws IOException {
		for (Iterator<UIComponent> iterator = component.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = (UIComponent) iterator.next();
			renderChild(facesContext, child);
		}
	}

	protected void renderChild(FacesContext facesContext, UIComponent child) throws IOException {
		if (!child.isRendered()) {
			return;
		}

		child.encodeBegin(facesContext);
		
		if (child.getRendersChildren()) {
			child.encodeChildren(facesContext);
		} else {
			renderChildren(facesContext, child);
		}
		child.encodeEnd(facesContext);
	}
	
	protected String getActionURL(FacesContext facesContext) {
		String actionURL = facesContext.getApplication().getViewHandler().getActionURL(facesContext, facesContext.getViewRoot().getViewId());
		
		return facesContext.getExternalContext().encodeResourceURL(actionURL);
	}
	
    protected String getResourceURL(FacesContext facesContext, String value) {
        if (value.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
            return value;
        } else {
            String url = facesContext.getApplication().getViewHandler().getResourceURL(facesContext, value);

            return facesContext.getExternalContext().encodeResourceURL(url);
        }
    }
    
    protected String getResourceRequestPath(FacesContext facesContext, String resourceName) {
		Resource resource = facesContext.getApplication().getResourceHandler().createResource(resourceName, "primefaces");

        return resource.getRequestPath();
	}
    	
	public boolean isPostback(FacesContext facesContext) {
		return facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext);
	}

	protected void renderPassThruAttributes(FacesContext facesContext, UIComponent component, String var, String[] attrs) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(String event : attrs) {			
			String eventHandler = (String) component.getAttributes().get(event);
			
			if(eventHandler != null)
				writer.write(var + ".addListener(\"" + event.substring(2, event.length()) + "\", function(e){" + eventHandler + ";});\n");
		}
	}
	
	protected void renderPassThruAttributes(FacesContext facesContext, UIComponent component, String[] attrs) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(String attribute : attrs) {
			Object value = component.getAttributes().get(attribute);
			
			if(shouldRenderAttribute(value))
				writer.writeAttribute(attribute, value.toString(), attribute);
		}
	}
	
	protected void renderPassThruAttributes(FacesContext facesContext, UIComponent component, String[] attrs, String[] ignoredAttrs) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(String attribute : attrs) {
			if(isIgnoredAttribute(attribute, ignoredAttrs)) {
				continue;
			}
			
			Object value = component.getAttributes().get(attribute);
			
			if(shouldRenderAttribute(value))
				writer.writeAttribute(attribute, value.toString(), attribute);
		}
	}
	
	private boolean isIgnoredAttribute(String attribute, String[] ignoredAttrs) {
		for(String ignoredAttribute : ignoredAttrs) {
			if(attribute.equals(ignoredAttribute)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Generates a unique javascript variable name if component has a client side widget.
	 * Portlet namespaces are considered if externalcontext is a portlet environment. Algorith works as follows.
	 *
	 * If there's a user provided widgetVar, it's returned without further processing.
	 * ":"s in client id is replace with underscore since ":" is invalid for js variable names.
	 * WIDGET_VAR_SUFFIX is added to the formatted client id.
	 * 
	 * Finally variable name is encoded with ExternalContext.encodeNamespace to make sure it's also unique in a portal
	 *  
	 * @param facesContext
	 * @param component
	 * 
	 * @return
	 */
	protected String createUniqueWidgetVar(FacesContext facesContext, UIComponent component) {
		String widgetVar = (String) component.getAttributes().get("widgetVar");
		if(widgetVar != null)
			return widgetVar;
		
		String regex = "-|" + UINamingContainer.getSeparatorChar(facesContext);
		String formattedClientId = component.getClientId(facesContext).replaceAll(regex, "_");
		
		return WIDGET_VAR_PREFIX + facesContext.getExternalContext().encodeNamespace(formattedClientId);
	}
	
    protected boolean shouldRenderAttribute(Object value) {
        if(value == null)
            return false;
      
        if(value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        else if(value instanceof Number) {
        	Number number = (Number) value;
        	
            if (value instanceof Integer)
                return number.intValue() != Integer.MIN_VALUE;
            else if (value instanceof Double)
                return number.doubleValue() != Double.MIN_VALUE;
            else if (value instanceof Long)
                return number.longValue() != Long.MIN_VALUE;
            else if (value instanceof Byte)
                return number.byteValue() != Byte.MIN_VALUE;
            else if (value instanceof Float)
                return number.floatValue() != Float.MIN_VALUE;
            else if (value instanceof Short)
                return number.shortValue() != Short.MIN_VALUE;
        }
        
        return true;
    }
    
    protected boolean isPostBack() {
    	FacesContext facesContext = FacesContext.getCurrentInstance();
    	return facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext);
    }
   
    public String getEscapedClientId(String clientId){
    	return clientId.replaceAll(":", "\\\\\\\\:");
    }
    
    public boolean isValueEmpty(String value) {
		if (value == null || "".equals(value))
			return true;
		
		return false;
	}
	
	public boolean isValueBlank(String value) {
		if(value == null)
			return true;
		
		return value.trim().equals("");
	}
	
    protected String buildAjaxRequest(FacesContext facesContext, AjaxSource source, String formId, String decodeParam) {
        UIComponent component = (UIComponent) source;

        StringBuilder req = new StringBuilder();
        req.append("PrimeFaces.ajax.AjaxRequest(");

        //url
        req.append("'").append(getActionURL(facesContext)).append("'");

        //options
        req.append(",{formId:'").append(formId).append("'");
        req.append(",async:").append(source.isAsync());
        req.append(",global:").append(source.isGlobal());

        //source
        req.append(",source:'").append(decodeParam).append("'");

        //process
        String process = source.getProcess() != null ? ComponentUtils.findClientIds(facesContext, component, source.getProcess()) : "@all";
        req.append(",process:'").append(process).append("'");

        //update
        if(source.getUpdate() != null) {
            req.append(",update:'").append(ComponentUtils.findClientIds(facesContext, component, source.getUpdate())).append("'");
        }

        //callbacks
        if(source.getOnstart() != null)
            req.append(",onstart:function(xhr){").append(source.getOnstart()).append(";}");
        if(source.getOnerror() != null)
            req.append(",onerror:function(xhr, status, error){").append(source.getOnerror()).append(";}");
        if(source.getOnsuccess() != null)
            req.append(",onsuccess:function(data, status, xhr, args){").append(source.getOnsuccess()).append(";}");
        if(source.getOncomplete() != null)
            req.append(",oncomplete:function(xhr, status, args){").append(source.getOncomplete()).append(";}");

        req.append("}");

        //params
        boolean firstParam = true, hasParam = false;

        for(UIComponent child : component.getChildren()) {
            if(child instanceof UIParameter) {
                UIParameter parameter = (UIParameter) child;
                hasParam = true;

                if(firstParam) {
                    firstParam = false;
                    req.append(",{");
                } else {
                    req.append(",");
                }

                req.append("'").append(parameter.getName()).append("':'").append(parameter.getValue()).append("'");
            }
        }

        if(hasParam) {
            req.append("}");
        }

        req.append(");");

        return req.toString();
    }
	
	protected String buildNonAjaxRequest(FacesContext facesContext, UIComponent component, String formId, String decodeParam) {		
		String process = (String) component.getAttributes().get("process");
        StringBuilder request = new StringBuilder();

        request.append("PrimeFaces").append(addSubmitParam(formId, decodeParam, decodeParam));
		
		for(UIComponent child : component.getChildren()) {
			if(child instanceof UIParameter) {
                UIParameter param = (UIParameter) child;

                request.append(addSubmitParam(formId, param.getName(), (String) param.getValue()));
			}
		}

		request.append(".submit('").append(formId).append("');");
		
		return request.toString();
	}

    protected String addSubmitParam(String parent, String name, String value) {
        StringBuilder builder = new StringBuilder();

        builder.append(".addSubmitParam('")
                    .append(parent).append("','")
                    .append(name)
                    .append("','")
                    .append(value)
                    .append("')");

        return builder.toString();
    }
	
	protected String escapeText(String value) {
		return value == null ? "" : value.replaceAll("'", "\\\\'");
	}
}