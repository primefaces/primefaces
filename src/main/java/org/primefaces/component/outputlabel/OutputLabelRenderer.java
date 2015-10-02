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
package org.primefaces.component.outputlabel;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import org.primefaces.component.api.InputHolder;
import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.metadata.transformer.AbstractInputMetadataTransformer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

public class OutputLabelRenderer extends CoreRenderer {

    private static final Logger LOG = Logger.getLogger(OutputLabelRenderer.class.getName());
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        OutputLabel label = (OutputLabel) component;
        String clientId = label.getClientId(context);
        String value = ComponentUtils.getValueToRender(context, label);
        UIComponent target = null;
        String targetClientId = null;
        UIInput input = null;
        String styleClass = label.getStyleClass();
        styleClass = styleClass == null ? OutputLabel.STYLE_CLASS : OutputLabel.STYLE_CLASS + " " + styleClass;
        
        String _for = label.getFor();

        if(_for != null) {
            target = SearchExpressionFacade.resolveComponent(context, label, _for);
            
            if(target instanceof InputHolder) {
                InputHolder inputHolder = ((InputHolder) target);
                targetClientId = inputHolder.getInputClientId();
                inputHolder.setLabelledBy(clientId);
            }
            else {
                targetClientId = target.getClientId(context);
            }
            
            if(target instanceof UIInput) {
                input = (UIInput) target;
                
                if(value != null && (input.getAttributes().get("label") == null || input.getValueExpression("label") == null)) {
                    ValueExpression ve = label.getValueExpression("value");
                    
                    if(ve != null) {
                        input.setValueExpression("label", ve);
                    }
                    else {
                        String labelString = value;
                        int colonPos = labelString.lastIndexOf(":");
                        
                        if(colonPos != -1) {
                            labelString = labelString.substring(0, colonPos);
                        }
                        
                        input.getAttributes().put("label", labelString);
                    }
                }
                
                if(!input.isValid()) {
                    styleClass = styleClass + " ui-state-error";
                }
            }
        }

        writer.startElement("label", label);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        renderPassThruAttributes(context, label, HTML.LABEL_ATTRS);
        
        if(target != null) {
            writer.writeAttribute("for", targetClientId, "for");
        }
        
        if(value != null) {            
            if(label.isEscape())
                writer.writeText(value, "value");
            else
                writer.write(value);
        }
        
        renderChildren(context, label);

        if (input != null && label.isIndicateRequired()) {

            ConfigContainer config = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
            
            if (input.isRequired()) {
                encodeRequiredIndicator(writer, label);
            }
            // if transformMetadataEnabled, skip the @NotNull checking...
            // the BeanValidationInputMetadataTransformer will set the marker to true or false
            else if (config.isTransformMetadataEnabled()) {
                if (AbstractInputMetadataTransformer.isMarkedAsRequired(input)) {
                    encodeRequiredIndicator(writer, label);    
                }
            }
            else if (config.isBeanValidationAvailable() && isNotNullDefined(input, context)) {
                encodeRequiredIndicator(writer, label);
            }
        }
        
        writer.endElement("label");        
    }
    
    protected void encodeRequiredIndicator(ResponseWriter writer, OutputLabel label) throws IOException {
        writer.startElement("span", label);
        writer.writeAttribute("class", OutputLabel.REQUIRED_FIELD_INDICATOR_CLASS, null);
        writer.write("*");
        writer.endElement("span");   
    }
    
    protected boolean isNotNullDefined(UIInput input, FacesContext context) {

        // skip @NotNull check
        // see GitHub #14
        if (!RequestContext.getCurrentInstance().getApplicationContext().getConfig().isInterpretEmptyStringAsNull()) {
            return false;
        }
        
        try {
            Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractDefaultConstraintDescriptors(
                    context, RequestContext.getCurrentInstance(), input.getValueExpression("value"));
            if (constraints != null && !constraints.isEmpty()) {
                for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                    if (constraintDescriptor.getAnnotation().annotationType().equals(NotNull.class)) {
                        return true;
                    }
                }
            }
        }
        catch (PropertyNotFoundException e)  {
            String message = "Skip evaluating @NotNull for outputPanel and referenced component \"" + input.getClientId(context) + "\" because"
                    + " the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOG.log(Level.FINE, message);
        }

        return false;
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
