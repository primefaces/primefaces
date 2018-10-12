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
package org.primefaces.component.outputlabel;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectBoolean;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;

import org.primefaces.component.api.InputHolder;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.*;

public class OutputLabelRenderer extends CoreRenderer {

    private static final Logger LOGGER = Logger.getLogger(OutputLabelRenderer.class.getName());

    private static final String SB_STYLE_CLASS = OutputLabelRenderer.class.getName() + "#styleClass";

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final OutputLabel label = (OutputLabel) component;
        final String clientId = label.getClientId(context);
        final String value = ComponentUtils.getValueToRender(context, label);

        final StringBuilder styleClass = SharedStringBuilder.get(context, SB_STYLE_CLASS);
        styleClass.append(OutputLabel.STYLE_CLASS);
        if (label.getStyleClass() != null) {
            styleClass.append(" ");
            styleClass.append(label.getStyleClass());
        }

        final EditableValueHolderState state = new EditableValueHolderState();

        final String indicateRequired = label.getIndicateRequired();

        String _for = label.getFor();
        if (!isValueBlank(_for)) {
            ContextCallback callback = new ContextCallback() {
                @Override
                public void invokeContextCallback(FacesContext context, UIComponent target) {
                    if (target instanceof InputHolder) {
                        InputHolder inputHolder = ((InputHolder) target);
                        state.setClientId(inputHolder.getInputClientId());

                        inputHolder.setLabelledBy(clientId);
                    }
                    else {
                        state.setClientId(target.getClientId(context));
                    }

                    if (target instanceof UIInput) {
                        UIInput input = (UIInput) target;

                        if (value != null && (input.getAttributes().get("label") == null || input.getValueExpression("label") == null)) {
                            ValueExpression ve = label.getValueExpression("value");

                            if (ve != null) {
                                input.setValueExpression("label", ve);
                            }
                            else {
                                String labelString = value;
                                int colonPos = labelString.lastIndexOf(':');

                                if (colonPos != -1) {
                                    labelString = labelString.substring(0, colonPos);
                                }

                                input.getAttributes().put("label", labelString);
                            }
                        }

                        if (!input.isValid()) {
                            styleClass.append(" ui-state-error");
                        }

                        if ("auto".equals(indicateRequired)) {
                            state.setRequired(input.isRequired());

                            // fallback if required=false
                            if (!state.isRequired()) {
                                PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);
                                if (applicationContext.getConfig().isBeanValidationEnabled() && isBeanValidationDefined(input, context)) {
                                    state.setRequired(true);
                                }
                            }
                        }
                    }
                }
            };

            UIComponent forComponent = SearchExpressionFacade.resolveComponent(context, label, _for);

            if (CompositeUtils.isComposite(forComponent)) {
                CompositeUtils.invokeOnDeepestEditableValueHolder(context, forComponent, callback);
            }
            else {
                callback.invokeContextCallback(context, forComponent);
            }
        }

        writer.startElement("label", label);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass.toString(), "id");
        renderPassThruAttributes(context, label, HTML.LABEL_ATTRS);

        if (!isValueBlank(_for)) {
            writer.writeAttribute("for", state.getClientId(), "for");
        }

        if (value != null) {
            if (label.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value);
            }
        }

        renderChildren(context, label);

        if ("true".equals(indicateRequired) || ("auto".equals(indicateRequired) && !isValueBlank(_for) && state.isRequired())) {
            encodeRequiredIndicator(writer, label);
        }

        writer.endElement("label");
    }

    protected void encodeRequiredIndicator(ResponseWriter writer, OutputLabel label) throws IOException {
        writer.startElement("span", label);
        writer.writeAttribute("class", OutputLabel.REQUIRED_FIELD_INDICATOR_CLASS, null);
        writer.write("*");
        writer.endElement("span");
    }

    protected boolean isBeanValidationDefined(UIInput input, FacesContext context) {
        try {
            PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);
            Set<ConstraintDescriptor<?>> constraints = BeanValidationMetadataExtractor.extractDefaultConstraintDescriptors(context,
                    applicationContext,
                    ValueExpressionAnalyzer.getExpression(context.getELContext(), input.getValueExpression("value")));

            if (constraints == null || constraints.isEmpty()) {
                return false;
            }

            for (ConstraintDescriptor<?> constraintDescriptor : constraints) {
                Class<? extends Annotation> annotationType = constraintDescriptor.getAnnotation().annotationType();
                // GitHub #14 skip @NotNull check
                if (annotationType.equals(NotNull.class)) {
                    return applicationContext.getConfig().isInterpretEmptyStringAsNull();
                }

                // GitHub #3052 @NotBlank,@NotEmpty Hibernate and BeanValidator 2.0
                String annotationClassName = annotationType.getSimpleName();
                if ("NotBlank".equals(annotationClassName) || "NotEmpty".equals(annotationClassName)) {
                    return true;
                }

                // GitHub #3986
                if (input instanceof UISelectBoolean && annotationType.equals(AssertTrue.class)) {
                    return true;
                }
            }
        }
        catch (PropertyNotFoundException e) {
            String message = "Skip evaluating [@NotNull,@NotBlank,@NotEmpty,@AssertTrue] for outputLabel and referenced component \""
                    + input.getClientId(context)
                    + "\" because the ValueExpression of the \"value\" attribute"
                    + " isn't resolvable completely (e.g. a sub-expression returns null)";
            LOGGER.log(Level.FINE, message);
        }

        return false;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
