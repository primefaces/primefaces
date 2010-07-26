/*
 * Copyright 2009 Prime Technology.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class CoreRenderer extends Renderer {
	
	private final static String WIDGET_VAR_PREFIX = "widget_";
	
	protected void renderScriptDependency(FacesContext facesContext, String scriptPath) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.writeAttribute("src", ResourceUtils.getResourceURL(facesContext, scriptPath), null);
		writer.endElement("script");
	}
	
	protected void renderCSSDependency(FacesContext facesContext, String cssPath) throws IOException{
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.startElement("link", null);
		writer.writeAttribute("rel", "stylesheet", null);
		writer.writeAttribute("type", "text/css", null);
		writer.writeAttribute("href", ResourceUtils.getResourceURL(facesContext, cssPath), null);
		writer.endElement("link");
	}
	
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
		if(value.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
			return value;
		} else {
			String url = facesContext.getApplication().getViewHandler().getResourceURL(facesContext, value);
			
			return facesContext.getExternalContext().encodeResourceURL(url);
		}
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
		req.append("PrimeFaces.ajax.AjaxRequest('");
		req.append(getActionURL(facesContext));
		req.append("',{");
		req.append("formId:'");
		req.append(formId);
		req.append("'");
		
		if(source.isAsync()) req.append(",async:true");
		
		//source
		if(source.getOnstart() != null) req.append(",onstart:function(xhr){" + source.getOnstart() + ";}");
		if(source.getOnerror() != null) req.append(",onerror:function(xhr, status, error){" + source.getOnerror() + ";}");
		if(source.getOnsuccess() != null) req.append(",onsuccess:function(data, status, xhr, args){" + source.getOnsuccess() + ";}"); 
		if(source.getOncomplete() != null) req.append(",oncomplete:function(xhr, status, args){" + source.getOncomplete() + ";}");

		req.append(",global:" + source.isGlobal());
		
		req.append("},{");
		
		req.append("'" + decodeParam + "'");
		req.append(":");
		req.append("'" + decodeParam + "'");
		
		if(source.getUpdate() != null) {
			req.append(",'" + Constants.PARTIAL_UPDATE_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, component, source.getUpdate()) + "'");
		}
		
		if(source.getProcess() != null) {
			req.append(",'" + Constants.PARTIAL_PROCESS_PARAM + "':");
			req.append("'" + ComponentUtils.findClientIds(facesContext, component, source.getProcess()) + "'");
		}
		
		for(UIComponent child : component.getChildren()) {
			if(child instanceof UIParameter) {
				UIParameter parameter = (UIParameter) child;
				
				req.append(",");
				req.append("'" + parameter.getName() + "'");
				req.append(":");
				req.append("'" + parameter.getValue() + "'");
			}
		}
		
		req.append("});");
		
		req.append("return false;");
		
		return req.toString();
	}
	
	protected String buildNonAjaxRequest(FacesContext facesContext, UIComponent component, String formId, String decodeParam) {		
		Map<String,Object> params = new HashMap<String, Object>();
		String process = (String) component.getAttributes().get("process");
		boolean isPartialProcess = process != null;
		
		for(UIComponent child : component.getChildren()) {
			if(child instanceof UIParameter) {
				UIParameter parameter = (UIParameter) child;
				params.put(parameter.getName(), parameter.getValue());
			}
		}
		
		StringBuffer request = new StringBuffer();
		request.append("PrimeFaces.addSubmitParam('" + formId + "', {'" + decodeParam + "':'" + decodeParam + "'");
		
		if(!params.isEmpty() || isPartialProcess) {
			if(isPartialProcess) {
				request.append("," + Constants.PARTIAL_PROCESS_PARAM + ":'" + ComponentUtils.findClientIds(facesContext, component, process) + "'");
			}
			
			if(!params.isEmpty()) {
				for(Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
					String paramName = iterator.next();
					Object paramValue = (Object) params.get(paramName);
					String toSend = paramValue != null ? paramValue.toString() : "";
					
					request.append(",'" + paramName + "':'" + toSend + "'");
				}
			}
		}
		
		request.append("});");
		request.append("jQuery(PrimeFaces.escapeClientId('" + formId + "')).submit();");
		
		return request.toString();
	}
	
	protected String escapeText(String value) {
		return value == null ? "" : value.replaceAll("'", "\\\\'");
	}
}