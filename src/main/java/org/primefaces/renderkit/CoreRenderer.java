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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.PropertyNotFoundException;
import javax.faces.application.Resource;
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
import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.context.RequestContext;
import org.primefaces.convert.ClientConverter;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.ClientValidator;
import org.primefaces.validate.bean.BeanValidationMetadata;
import org.primefaces.validate.bean.BeanValidationMetadataMapper;

public abstract class CoreRenderer extends Renderer {

    private static final Logger LOG = Logger.getLogger(CoreRenderer.class.getName());
    
    private static final String SB_RENDER_DOM_EVENTS = CoreRenderer.class.getName() + "#renderDomEvents";
    private static final String SB_BUILD_NON_AJAX_REQUEST = CoreRenderer.class.getName() + "#buildNonAjaxRequest";
    private static final String SB_GET_EVENT_BEHAVIORS = CoreRenderer.class.getName() + "#getEventBehaviors";
    private static final String SB_RENDER_VALIDATOR_IDS = CoreRenderer.class.getName() + "#renderValidatorIds";

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

    protected String getResourceURL(FacesContext context, String value) {
        return ComponentUtils.getResourceURL(context, value);
    }

    protected String getResourceRequestPath(FacesContext context, String resourceName) {
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, "primefaces");

        return resource.getRequestPath();
    }

    protected void renderPassThruAttributes(FacesContext context, UIComponent component, String[] attrs) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //pre-defined attributes
        if(attrs != null && attrs.length > 0) {
            for(String attribute : attrs) {
                Object value = component.getAttributes().get(attribute);

                if(shouldRenderAttribute(value))
                    writer.writeAttribute(attribute, value.toString(), attribute);
            }
        }

        renderDynamicPassThruAttributes(context, component);
    }

    protected void renderDynamicPassThruAttributes(FacesContext context, UIComponent component) throws IOException {
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF22()) {
            RendererUtils.renderPassThroughAttributes(context, component);
        }
    }

    protected void renderDomEvents(FacesContext context, UIComponent component, String[] eventAttrs) throws IOException {
        if(component instanceof ClientBehaviorHolder)
            renderDomEvents(context, component, eventAttrs, ((ClientBehaviorHolder) component).getClientBehaviors());
        else
            renderPassThruAttributes(context, component, eventAttrs);
	}

    private void renderDomEvents(FacesContext context, UIComponent component, String[] eventAttrs, Map<String,List<ClientBehavior>> behaviors) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        StringBuilder builder = null;

        for(String domEvent : eventAttrs) {
            Object eventValue = component.getAttributes().get(domEvent);
            String behaviorEvent = domEvent.substring(2, domEvent.length());
            List<ClientBehavior> eventBehaviors = behaviors.get(behaviorEvent);
            boolean hasEventValue = (eventValue != null);
            boolean hasEventBehaviors = (eventBehaviors != null && !eventBehaviors.isEmpty());

            if(domEvent.equals("onchange") && !hasEventBehaviors) {
                eventBehaviors = behaviors.get("valueChange");
                hasEventBehaviors = (eventBehaviors != null && !eventBehaviors.isEmpty());
                if(hasEventBehaviors)
                    behaviorEvent = "valueChange";
            }

            if(hasEventValue || hasEventBehaviors) {
                if(builder == null) {
                    builder = SharedStringBuilder.get(context, SB_RENDER_DOM_EVENTS);
                }
                
                if(hasEventBehaviors) {
                    String clientId = ((UIComponent) component).getClientId(context);
                    List<ClientBehaviorContext.Parameter> params = new ArrayList<ClientBehaviorContext.Parameter>();
                    params.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.OBSTRUSIVE));
                    ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, (UIComponent) component, behaviorEvent, clientId, params);
                    int size = eventBehaviors.size();
                    boolean chained = false;

                    if(size > 1 || hasEventValue) {
                        builder.append("PrimeFaces.bcn(this,event,[");
                        
                        if(hasEventValue) {
                            builder.append("function(event){").append(eventValue).append("}");
                            chained = true;
                        }
                        
                        for (int i = 0; i < size; i++) {
                            ClientBehavior behavior = eventBehaviors.get(i);
                            String script = behavior.getScript(cbc);
                            if(script != null) {
                                if(chained) {
                                    builder.append(",");
                                }
                                builder.append("function(event){").append(script).append("}");
                                chained = true;
                            }
                        }
                        builder.append("])");
                    }
                    else {
                        ClientBehavior behavior = eventBehaviors.get(0);
                        String script = behavior.getScript(cbc);
                        if(script != null) {
                            builder.append(script);
                        }
                    }
                }
                else if(hasEventValue) {
                    builder.append(eventValue);
                }

                if(builder.length() > 0) {
                    writer.writeAttribute(domEvent, builder.toString(), domEvent);
                    builder.setLength(0);
                }
            }
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

    protected void renderOnchange(FacesContext context, UIComponent component) throws IOException {
        this.renderDomEvent(context, component, "onchange", "change", "valueChange", null);
    }

    protected void renderOnclick(FacesContext context, UIComponent component, String command) throws IOException {
        this.renderDomEvent(context, component, "onclick", "click", "action", command);
    }

    protected void renderDomEvent(FacesContext context, UIComponent component, String domEvent, String behaviorEvent, String behaviorEventAlias, String command) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String callback = buildDomEvent(context, component, domEvent, behaviorEvent, behaviorEventAlias, command);
        if (callback != null) {
            writer.writeAttribute(domEvent, callback, domEvent);
        }
    }

    protected String buildDomEvent(FacesContext context, UIComponent component, String domEvent, String behaviorEvent, String behaviorEventAlias, String command) {
        StringBuilder builder = null;
        Map<String,List<ClientBehavior>> behaviors = null;
        if(component instanceof ClientBehaviorHolder) {
            behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
        }
        Object eventValue = component.getAttributes().get(domEvent);
        String behaviorEventName = behaviorEventAlias;
        if (behaviors != null && behaviors.containsKey(behaviorEvent)) {
            behaviorEventName = behaviorEvent;
        }

        List<ClientBehavior> eventBehaviors = (behaviors == null) ? null : behaviors.get(behaviorEventName);
        boolean hasEventValue = (eventValue != null);
        boolean hasEventBehaviors = (eventBehaviors != null && !eventBehaviors.isEmpty());

        if (hasEventValue || hasEventBehaviors || command != null) {
            if (builder == null) {
                builder = SharedStringBuilder.get(context, SB_RENDER_DOM_EVENTS);
            }

            if (hasEventValue) {
                builder.append(eventValue).append(";");
            }

            if (hasEventBehaviors) {
                ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, (UIComponent) component, behaviorEventName, component.getClientId(context), Collections.EMPTY_LIST);
                int eventBehaviorSize = eventBehaviors.size();
                int commandSize = (command != null) ? (eventBehaviorSize + 1): eventBehaviorSize;

                if (commandSize > 1) {
                    boolean behaviorRendered = false;
                    builder.append("PrimeFaces.bcn(this,event,[");

                    for (int i = 0; i < eventBehaviorSize; i++) {
                        ClientBehavior behavior = eventBehaviors.get(i);
                        String script = behavior.getScript(cbc);

                        if (script != null) {
                            if(!behaviorRendered) {
                                behaviorRendered = true;
                            } else {
                                builder.append(",");
                            }

                            builder.append("function(event){").append(script).append("}");
                        }
                    }

                    if (command != null) {
                        if(behaviorRendered) {
                            builder.append(",");
                        }

                        builder.append("function(event){").append(command).append("}");
                    }

                    builder.append("]);");
                }
                else {
                    ClientBehavior behavior = eventBehaviors.get(0);
                    String script = behavior.getScript(cbc);
                    if (script != null) {
                        builder.append(script);
                    }
                }
            }
            else if (command != null) {
                builder.append(command);
            }
        }

        return (builder == null) ? null : builder.toString();
    }

    private boolean isIgnoredAttribute(String attribute, String[] ignoredAttrs) {
        for (String ignoredAttribute : ignoredAttrs) {
            if (attribute.equals(ignoredAttribute)) {
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
                .form(SearchExpressionFacade.resolveClientId(context, component, source.getForm()))
                .process(component, source.getProcess())
                .update(component, source.getUpdate())
                .async(source.isAsync())
                .global(source.isGlobal())
                .delay(source.getDelay())
                .timeout(source.getTimeout())
                .partialSubmit(source.isPartialSubmit(), source.isPartialSubmitSet(), source.getPartialSubmitFilter())
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
        StringBuilder request = SharedStringBuilder.get(context, SB_BUILD_NON_AJAX_REQUEST);
        String formId = form.getClientId(context);
        Map<String,Object> params = new HashMap<String, Object>();

        if(decodeParam != null) {
            params.put(decodeParam, decodeParam);
        }

        for (UIComponent child : component.getChildren()) {
            if (child instanceof UIParameter) {
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
            Object target = component.getAttributes().get("target");
            request.append(".submit('").append(formId).append("'");

            if(target != null) {
                request.append(",'").append(target).append("'");
            }

            request.append(");return false;");
        }

		return request.toString();
	}

    protected void encodeClientBehaviors(FacesContext context, ClientBehaviorHolder component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String,List<ClientBehavior>> clientBehaviors = component.getClientBehaviors();

        if(clientBehaviors != null && !clientBehaviors.isEmpty()) {
            boolean written = false;
            Collection<String> eventNames = (component instanceof MixedClientBehaviorHolder) ?
                                ((MixedClientBehaviorHolder) component).getUnobstrusiveEventNames() : clientBehaviors.keySet();
            String clientId = ((UIComponent) component).getClientId(context);
            List<ClientBehaviorContext.Parameter> params = new ArrayList<ClientBehaviorContext.Parameter>();
            params.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));

            writer.write(",behaviors:{");
            for(Iterator<String> eventNameIterator = eventNames.iterator(); eventNameIterator.hasNext();) {
                String eventName = eventNameIterator.next();
                List<ClientBehavior> eventBehaviors = clientBehaviors.get(eventName);

                if(eventBehaviors != null && !eventBehaviors.isEmpty()) {
                    if(written) {
                        writer.write(",");
                    }
                    
                    int eventBehaviorsSize = eventBehaviors.size();

                    writer.write(eventName + ":");
                    writer.write("function(ext,event) {");
                    
                    if(eventBehaviorsSize > 1) {
                        boolean chained = false;
                        writer.write("PrimeFaces.bcnu(ext,event,[");
                        
                        for(int i = 0; i < eventBehaviorsSize; i++) {
                            ClientBehavior behavior = eventBehaviors.get(i);
                            ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, (UIComponent) component, eventName, clientId, params);
                            String script = behavior.getScript(cbc);

                            if(script != null) {
                                if(chained) {
                                    writer.write(",");
                                }
                                
                                writer.write("function(ext,event) {");
                                writer.write(script);
                                writer.write("}");
                                
                                chained = true;
                            }
                        }
                        
                        writer.write("]);");
                    }
                    else {
                        ClientBehavior behavior = eventBehaviors.get(0);
                        ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, (UIComponent) component, eventName, clientId, params);
                        String script = behavior.getScript(cbc);

                        if(script != null) {
                            writer.write(script);
                        }
                    }

                    writer.write("}");
                    
                    written = true;
                }
            }

            writer.write("}");
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
               String clientId = component.getClientId(context);

               if(behaviorSource != null && clientId.equals(behaviorSource)) {
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

    protected String escapeText(String text) {
        return ComponentUtils.escapeText(text);
    }

    protected String getEventBehaviors(FacesContext context, ClientBehaviorHolder cbh, String event, List<ClientBehaviorContext.Parameter> parameters) {
        List<ClientBehavior> behaviors = cbh.getClientBehaviors().get(event);
        StringBuilder sb = SharedStringBuilder.get(context, SB_GET_EVENT_BEHAVIORS);

        if(behaviors != null && !behaviors.isEmpty()) {
            UIComponent component = (UIComponent) cbh;
            String clientId = component.getClientId(context);
            List<ClientBehaviorContext.Parameter> params;
            if(parameters != null && !parameters.isEmpty()) {
                params = parameters;
            } else {
               params = Collections.emptyList();
            }

            for (int i = 0; i < behaviors.size(); i++) {
                ClientBehavior behavior = behaviors.get(i);
                ClientBehaviorContext cbc = ClientBehaviorContext.createClientBehaviorContext(context, component, event, clientId, params);
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
        
        Converter converter = null;
        
        try {
            converter = ComponentUtils.getConverter(context, comp);
        }
        catch (PropertyNotFoundException e) {
            String message = "Skip rendering of CSV metadata for component \"" + comp.getClientId(context) + "\" because"
                    + " the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOG.log(Level.FINE, message);
            return;
        }
        
        
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
            BeanValidationMetadata beanValidationMetadata = BeanValidationMetadataMapper.resolveValidationMetadata(context, comp, requestContext);
            renderValidationMetadataMap(context, beanValidationMetadata.getAttributes());
            validatorIds.addAll(beanValidationMetadata.getValidatorIds());
        }

        //required validation
        if(component.isRequired()) {
            writer.writeAttribute(HTML.VALIDATION_METADATA.REQUIRED, "true", null);
        }

        //validators
        Validator[] validators = component.getValidators();
        if (validators != null) {
            for (Validator validator : validators) {
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

        if(isGrouped()) {
            writer.writeAttribute(HTML.VALIDATION_METADATA.GROUPED, "true", null);
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
        StringBuilder builder = SharedStringBuilder.get(context, SB_RENDER_VALIDATOR_IDS);

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

    protected boolean isGrouped() {
        return false;
    }
}