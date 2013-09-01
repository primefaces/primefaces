/*
 * Copyright 2009-2013 PrimeTek.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;

import org.primefaces.component.api.AjaxSource;
import org.primefaces.context.RequestContext;
import org.primefaces.convert.ClientConverter;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.ClientValidator;
import org.primefaces.validate.bean.BeanValidationMetadata;
import org.primefaces.validate.bean.BeanValidationResolver;

public abstract class CoreRenderer extends Renderer {
	
	protected void renderChildren(FacesContext context, UIComponent component) throws IOException {
		if (component.getChildCount() > 0) {
			for (int i = 0; i < component.getChildCount(); i++) {
				UIComponent child = (UIComponent) component.getChildren().get(i);
				renderChild(context, child);
			}
		}
	}

	protected void renderChild(FacesContext context, UIComponent child) throws IOException {
		if (!child.isRendered()) {
			return;
		}

		child.encodeBegin(context);
		
		if (child.getRendersChildren()) {
			child.encodeChildren(context);
		} else {
			renderChildren(context, child);
		}
		child.encodeEnd(context);
	}
	
	@Deprecated
	protected String getActionURL(FacesContext context) {
		String actionURL = context.getApplication().getViewHandler().getActionURL(context, context.getViewRoot().getViewId());
		
		return context.getExternalContext().encodeActionURL(actionURL);
	}
	
    protected String getResourceURL(FacesContext context, String value) {
        if (value.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
            return value;
        } else {
            String url = context.getApplication().getViewHandler().getResourceURL(context, value);

            return context.getExternalContext().encodeResourceURL(url);
        }
    }
    
    protected String getResourceRequestPath(FacesContext context, String resourceName) {
		Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, "primefaces");

        return resource.getRequestPath();
	}

    @Deprecated
	public boolean isPostback(FacesContext context) {
		return context.getRenderKit().getResponseStateManager().isPostback(context);
	}

    @Deprecated
    public boolean isAjaxRequest(FacesContext context) {
		return context.getPartialViewContext().isAjaxRequest();
	}

    @Deprecated
	protected void renderPassThruAttributes(FacesContext context, UIComponent component, String var, String[] attrs) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		for(String event : attrs) {			
			String eventHandler = (String) component.getAttributes().get(event);
			
			if(eventHandler != null)
				writer.write(var + ".addListener(\"" + event.substring(2, event.length()) + "\", function(e){" + eventHandler + ";});\n");
		}
	}
	
	protected void renderPassThruAttributes(FacesContext context, UIComponent component, String[] attrs) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
        //pre-defined attributes
		for(String attribute : attrs) {
			Object value = component.getAttributes().get(attribute);
			
			if(shouldRenderAttribute(value))
				writer.writeAttribute(attribute, value.toString(), attribute);
		}
        
        //dynamic attributes       
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22()) {
            RendererUtils.renderPassThroughAttributes(context, component);
        }
	}
	
	protected void renderPassThruAttributes(FacesContext context, UIComponent component, String[] attrs, String[] ignoredAttrs) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
        //pre-defined attributes
		for(String attribute : attrs) {
			if(isIgnoredAttribute(attribute, ignoredAttrs)) {
				continue;
			}
			
			Object value = component.getAttributes().get(attribute);
			
			if(shouldRenderAttribute(value))
				writer.writeAttribute(attribute, value.toString(), attribute);
		}
        
        //dynamic attributes       
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22()) {
            RendererUtils.renderPassThroughAttributes(context, component);
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

	public boolean isValueBlank(String value) {
		return ComponentUtils.isValueBlank(value);
	}
    	    
    protected String buildAjaxRequest(FacesContext context, AjaxSource source, UIComponent form) {
        UIComponent component = (UIComponent) source;
        String clientId = component.getClientId(context);
        
        AjaxRequestBuilder builder = RequestContext.getCurrentInstance().getAjaxRequestBuilder();
        
        builder.init()
        		.source(clientId)
                .process(component, source.getProcess())
                .update(component, source.getUpdate())
                .async(source.isAsync())
                .global(source.isGlobal())
                .partialSubmit(source.isPartialSubmit(), source.isPartialSubmitSet())
                .resetValues(source.isResetValues(), source.isResetValuesSet())
                .ignoreAutoUpdate(source.isIgnoreAutoUpdate())
                .onstart(source.getOnstart())
                .onerror(source.getOnerror())
                .onsuccess(source.getOnsuccess())
                .oncomplete(source.getOncomplete())
                .params(component);
        
        if(form != null) {
            builder.form(form.getClientId(context));
        }
        
        builder.preventDefault();
                
        return builder.build();
    }
	
	protected String buildNonAjaxRequest(FacesContext context, UIComponent component, UIComponent form, String decodeParam, boolean submit) {		
        StringBuilder request = new StringBuilder();
        String formId = form.getClientId(context);
        Map<String,Object> params = new HashMap<String, Object>();
        
        if(decodeParam != null) {
            params.put(decodeParam, decodeParam);
        }
        
		for(UIComponent child : component.getChildren()) {
			if(child instanceof UIParameter) {
                UIParameter param = (UIParameter) child;

                params.put(param.getName(), param.getValue());
			}
		}
        
        //append params
        if(!params.isEmpty()) {
            request.append("PrimeFaces.addSubmitParam('").append(formId).append("',{");
            
            for(Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                Object value = params.get(key);

                request.append("'").append(key).append("':'").append(value).append("'");

                if(it.hasNext())
                    request.append(",");
            }
            
            request.append("})");
        }
        
        if(submit) {
            request.append(".submit('").append(formId).append("');return false;");
        }
		
		return request.toString();
	}

    /**
     * Non-obstrusive way to apply client behaviors.
     * Behaviors are rendered as options to the client side widget and applied by widget to necessary dom element
     */
    protected void encodeClientBehaviors(FacesContext context, ClientBehaviorHolder component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        //ClientBehaviors
        Map<String,List<ClientBehavior>> behaviorEvents = component.getClientBehaviors();

        if(!behaviorEvents.isEmpty()) {
            String clientId = ((UIComponent) component).getClientId(context);
            List<ClientBehaviorContext.Parameter> params = Collections.emptyList();

            writer.write(",behaviors:{");

            for(Iterator<String> eventIterator = behaviorEvents.keySet().iterator(); eventIterator.hasNext();) {
                String event = eventIterator.next();
                String domEvent = event;

                if(event.equalsIgnoreCase("valueChange"))       //editable value holders
                    domEvent = "change";
                else if(event.equalsIgnoreCase("action"))       //commands
                    domEvent = "click";

                writer.write(domEvent);
                writer.write(':');

                writer.write("function(event,ext){PrimeFaces.Behavior.chain(this,event,ext,[");
                List<ClientBehavior> behaviorsByEvent = behaviorEvents.get(event);
                int renderedBehaviors = 0;
                for (int i = 0; i < behaviorsByEvent.size(); i++) {
                    ClientBehavior behavior = behaviorsByEvent.get(i);
                    ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, (UIComponent) component, event, clientId, params);
                    String script = behavior.getScript(cbc);    //could be null if disabled

                    if(script != null) {
                        if (renderedBehaviors > 0) {
                        	writer.write(',');
                        }

                    	writer.write('\'' + escapeJavaScriptForChain(script) + '\'');
                        renderedBehaviors++;
                    }
                }
                writer.write("]);}");

                if(eventIterator.hasNext()) {
                    writer.write(",");
                }
            }

            writer.write("}");
        }
    }

    // Original from MyFaces
    protected String escapeJavaScriptForChain(String javaScript) {
        // first replace \' with \\'
        //String escaped = StringUtils.replace(javaScript, "\\'", "\\\\'");

        // then replace ' with \'
        // (this will replace every \' in the original to \\\')
        //escaped = StringUtils.replace(escaped, '\'', "\\'");

        //return escaped;

        StringBuilder out = null;
        for (int pos = 0; pos < javaScript.length(); pos++)
        {
            char c = javaScript.charAt(pos);

            if (c == '\\' || c == '\'')
            {
                if (out == null)
                {
                    out = new StringBuilder(javaScript.length() + 8);
                    if (pos > 0)
                    {
                        out.append(javaScript, 0, pos);
                    }
                }
                out.append('\\');
            }
            if (out != null)
            {
                out.append(c);
            }
        }

        if (out == null)
        {
            return javaScript;
        }
        else
        {
            return out.toString();
        }
    }
    
    protected void decodeBehaviors(FacesContext context, UIComponent component)  {

        if(!(component instanceof ClientBehaviorHolder)) {
            return;
        }

        Map<String, List<ClientBehavior>> behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        if(behaviors.isEmpty()) {
            return;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String behaviorEvent = params.get("javax.faces.behavior.event");

        if(null != behaviorEvent) {
            List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);

            if(behaviorsForEvent != null && !behaviorsForEvent.isEmpty()) {
               String behaviorSource = params.get("javax.faces.source");
               String clientId = component.getClientId();

               if(behaviorSource != null && clientId.startsWith(behaviorSource)) {
                   for(ClientBehavior behavior: behaviorsForEvent) {
                       behavior.decode(context, component);
                   }
               }
            }
        }
    }
    
    protected void startScript(ResponseWriter writer, String clientId) throws IOException {
        writer.startElement("script", null);
        writer.writeAttribute("id", clientId + "_s", null);
        writer.writeAttribute("type", "text/javascript", null);
    }
    
    protected void endScript(ResponseWriter writer) throws IOException {
        writer.endElement("script");
    }
        
    /**
     * Duplicate code from json-simple project under apache license
     * http://code.google.com/p/json-simple/source/browse/trunk/src/org/json/simple/JSONValue.java
     */
    protected String escapeText(String text) {
        if(text == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }
                
        return sb.toString();
    }
    
    protected String getOnclickBehaviors(FacesContext context, ClientBehaviorHolder cbh) {
        List<ClientBehavior> behaviors = cbh.getClientBehaviors().get("click");
        StringBuilder sb = new StringBuilder();
        
        if(behaviors != null && !behaviors.isEmpty()) {
            UIComponent component = (UIComponent) cbh;
            String clientId = component.getClientId(context);
            List<ClientBehaviorContext.Parameter> params = Collections.emptyList();

            for (int i = 0; i < behaviors.size(); i++) {
                ClientBehavior behavior = behaviors.get(i);
                ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, component, "click", clientId, params);
                String script = behavior.getScript(cbc);

                if(script != null)
                    sb.append(script).append(";");
            }
        }
        
        return sb.length() == 0 ? null : sb.toString();
    }
    
    protected boolean shouldWriteId(UIComponent component) {
        String id = component.getId();
        
        return (null != id) && (!id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX) || ((component instanceof ClientBehaviorHolder) &&
                          ! ((ClientBehaviorHolder) component).getClientBehaviors().isEmpty()));
    }
    
    protected WidgetBuilder getWidgetBuilder(FacesContext context) {
        return RequestContext.getCurrentInstance().getWidgetBuilder();
    }
    
    protected void renderValidationMetadata(FacesContext context, EditableValueHolder component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent comp = (UIComponent) component;
        Converter converter = ComponentUtils.getConverter(context, comp);
        Map<String,Object> attrs = comp.getAttributes();
        Object label = attrs.get("label");
        Object requiredMessage = attrs.get("requiredMessage");
        Object validatorMessage = attrs.get("validatorMessage");
        Object converterMessage = attrs.get("converterMessage");
        List<String> validatorIds = new ArrayList<String>();
        String highlighter = getHighlighter();
        
        RequestContext requestContext = RequestContext.getCurrentInstance();
        
        //messages
        if(label != null) writer.writeAttribute(HTML.VALIDATION_METADATA.LABEL, label, null);
        if(requiredMessage != null) writer.writeAttribute(HTML.VALIDATION_METADATA.REQUIRED_MESSAGE, requiredMessage, null);
        if(validatorMessage != null) writer.writeAttribute(HTML.VALIDATION_METADATA.VALIDATOR_MESSAGE, validatorMessage, null);
        if(converterMessage != null) writer.writeAttribute(HTML.VALIDATION_METADATA.CONVERTER_MESSAGE, converterMessage, null);

        //converter
        if(converter != null && converter instanceof ClientConverter) {
            ClientConverter clientConverter = (ClientConverter) converter;
            Map<String,Object> metadata = clientConverter.getMetadata();
            
            writer.writeAttribute(HTML.VALIDATION_METADATA.CONVERTER, ((ClientConverter) converter).getConverterId(), null);
            
            if(metadata != null && !metadata.isEmpty()) {
                renderValidationMetadataMap(context, metadata);
            }
        }
        
        //bean validation
        if(requestContext.getApplicationContext().getConfig().isBeanValidationAvailable()) {
            BeanValidationMetadata beanValidationMetadata = BeanValidationResolver.resolveValidationMetadata(context, comp, requestContext);
            renderValidationMetadataMap(context, beanValidationMetadata.getAttributes());
            validatorIds.addAll(beanValidationMetadata.getValidatorIds());
        }
        
        //required validation
        if(component.isRequired()) {
            writer.writeAttribute(HTML.VALIDATION_METADATA.REQUIRED, "true", null);
        }

        //validators
        Validator[] validators = component.getValidators();
        if(validators != null) {
            for(int i = 0; i < validators.length; i++) {
                Validator validator = validators[i];

                if(validator instanceof ClientValidator) {
                    ClientValidator clientValidator = (ClientValidator) validator;
                    validatorIds.add(clientValidator.getValidatorId());
                    Map<String,Object> metadata = clientValidator.getMetadata();

                    if(metadata != null && !metadata.isEmpty()) {
                        renderValidationMetadataMap(context, metadata);
                    }
                }
            }
        }
        
        renderValidatorIds(context, validatorIds);
        
        if(highlighter != null) {
            writer.writeAttribute(HTML.VALIDATION_METADATA.HIGHLIGHTER, highlighter, null);
        } 
    }
    
    private void renderValidationMetadataMap(FacesContext context, Map<String,Object> metadata) throws IOException {
        if(metadata == null || metadata.isEmpty()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        
        for(Map.Entry<String, Object> entry : metadata.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(value != null) {
                writer.writeAttribute(key, value, null);
            }
        }
    }
    
    private void renderValidatorIds(FacesContext context, List<String> validatorIds) throws IOException {
        if(validatorIds == null || validatorIds.isEmpty()) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        StringBuilder builder = new StringBuilder();
        
        for(int i = 0; i < validatorIds.size(); i++) {
            if (i != 0) {
                builder.append(',');
            }
            
            String validatorId = validatorIds.get(i);
            builder.append(validatorId);
        }

        writer.writeAttribute(HTML.VALIDATION_METADATA.VALIDATOR_IDS, builder.toString(), null);
    }
    
    protected String getHighlighter() {
        return null;
    }
}