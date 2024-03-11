/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectBoolean;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.metadata.BeanValidationMetadataExtractor;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.*;

public class OutputLabelRenderer extends CoreRenderer {

    private static final Logger LOGGER = Logger.getLogger(OutputLabelRenderer.class.getName());

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final OutputLabel label = (OutputLabel) component;
        final String clientId = label.getClientId(context);
        final String value = ComponentUtils.getValueToRender(context, label);

        final StyleClassBuilder styleClassBuilder = getStyleClassBuilder(context)
                .add(OutputLabel.STYLE_CLASS)
                .add(ComponentUtils.isRTL(context, label), OutputLabel.RTL_CLASS)
                .add(label.getStyleClass());

        final EditableValueHolderState state = new EditableValueHolderState();

        final String indicateRequired = label.getIndicateRequired();
        boolean isAuto = "auto".equals(indicateRequired) || "autoSkipDisabled".equals(indicateRequired);

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

                        if (value != null && !(target instanceof SelectCheckboxMenu) &&
                                (input.getAttributes().get("label") == null || input.getValueExpression("label") == null)) {
                            ValueExpression ve = label.getValueExpression("value");

                            if (ve != null) {
                                input.setValueExpression("label", ve);
                            }
                            else {
                                String labelString = value;
                                char separatorChar = UINamingContainer.getSeparatorChar(context);
                                int separatorCharPos = labelString.lastIndexOf(separatorChar);

                                if (separatorCharPos != -1) {
                                    labelString = labelString.substring(0, separatorCharPos);
                                }

                                input.getAttributes().put("label", labelString);
                            }
                        }

                        if (!input.isValid()) {
                            styleClassBuilder.add("ui-state-error");
                        }

                        if (isAuto) {
                            boolean disabled = false;
                            if ("autoSkipDisabled".equals(indicateRequired)) {
                                disabled = ComponentUtils.isDisabledOrReadonly(input);
                            }

                            if (disabled) {
                                state.setRequired(false);
                            }
                            else {
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
                }
            };

            UIComponent forComponent = SearchExpressionUtils.contextlessResolveComponent(context, label, _for);

            if (CompositeUtils.isComposite(forComponent)) {
                CompositeUtils.invokeOnDeepestEditableValueHolder(context, forComponent, callback);
            }
            else {
                callback.invokeContextCallback(context, forComponent);
            }
        }

        boolean withRequiredIndicator = "true".equals(indicateRequired)
                || (isAuto && !isValueBlank(_for) && state.isRequired());
        if (withRequiredIndicator) {
            styleClassBuilder.add("ui-required");
        }

        writer.startElement("label", label);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClassBuilder.build(), "styleClass");
        renderPassThruAttributes(context, label, HTML.LABEL_ATTRS);
        renderDomEvents(context, label, HTML.LABEL_EVENTS);
        renderRTLDirection(context, label);

        if (!isValueBlank(_for)) {
            writer.writeAttribute("for", state.getClientId(), "for");
        }

        if (value != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", "ui-outputlabel-label", null);
            if (label.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value);
            }
            writer.endElement("span");
        }

        renderChildren(context, label);

        if (withRequiredIndicator) {
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
                if (annotationType.equals(NotNull.class) && applicationContext.getConfig().isInterpretEmptyStringAsNull()) {
                    return true;
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
