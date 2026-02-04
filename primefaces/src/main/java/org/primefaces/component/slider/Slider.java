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
package org.primefaces.component.slider;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.SlideEndEvent;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Slider.COMPONENT_TYPE, namespace = Slider.COMPONENT_FAMILY)
@FacesComponentDescription("Slider is used to provide input with various customization options like orientation, display modes and skinning.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Slider extends SliderBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Slider";
    public static final String VALUE_OUT_OF_RANGE = "primefaces.slider.OUT_OF_RANGE";

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.slideEnd)) {
                double sliderValue = Double.parseDouble(params.get(clientId + "_slideValue"));
                SlideEndEvent slideEndEvent = new SlideEndEvent(this, behaviorEvent.getBehavior(), sliderValue);
                slideEndEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(slideEndEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if (!isValid()) {
            return;
        }

        if ("true".equals(getRange())) {
            String[] inputIds = getFor().split(",");
            UIInput inputFrom = (UIInput) SearchExpressionUtils.contextlessResolveComponent(context, this, inputIds[0]);
            UIInput inputTo = (UIInput) SearchExpressionUtils.contextlessResolveComponent(context, this, inputIds[1]);
            String valueFromStr = getValueAsStringOfAttachedInput(context, inputFrom);
            String valueToStr = getValueAsStringOfAttachedInput(context, inputTo);
            if (LangUtils.isBlank(valueFromStr) || LangUtils.isBlank(valueToStr)) {
                return;
            }

            double valueFrom = Double.parseDouble(valueFromStr);
            double valueTo = Double.parseDouble(valueToStr);
            if (valueTo < valueFrom) {
                setValid(false);
                inputFrom.setValid(false);
                inputTo.setValid(false);
            }
            else {
                if (valueFrom < getMinValue() || valueFrom > getMaxValue()) {
                    setValid(false);
                    inputFrom.setValid(false);
                }
                if (valueTo > getMaxValue() || valueTo < getMinValue()) {
                    setValid(false);
                    inputTo.setValid(false);
                }
            }
        }
        else {
            UIInput input = (UIInput) SearchExpressionUtils.contextlessResolveComponent(context, this, getFor());
            String value = getValueAsStringOfAttachedInput(context, input);
            if (LangUtils.isBlank(value)) {
                return;
            }

            double submittedValueDouble = Double.parseDouble(value);
            if (submittedValueDouble < getMinValue() || submittedValueDouble > getMaxValue()) {
                setValid(false);
                input.setValid(false);
            }
        }

        if (!isValid()) {
            String validatorMessage = getValidatorMessage();
            FacesMessage msg = null;
            if (validatorMessage != null) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
            }
            else {
                Object params = ComponentUtils.getLabel(context, this);
                msg = MessageFactory.getFacesMessage(context, VALUE_OUT_OF_RANGE, FacesMessage.SEVERITY_ERROR, params);
            }
            context.addMessage(getClientId(context), msg);
        }
    }

    protected String getValueAsStringOfAttachedInput(FacesContext context, UIInput input) {
        // first try the submitted value
        Object submittedValue = input.getSubmittedValue();
        if (submittedValue != null) {
            if (submittedValue instanceof String) {
                return (String) submittedValue;
            }

            // actually it should be string
            Converter converter = ComponentUtils.getConverter(context, input);
            if (converter != null) {
                return converter.getAsString(context, this, submittedValue);
            }

            throw new FacesException("No converter available for: " + submittedValue.getClass());
        }

        // fallback to value and convert it back to string
        if (input.isLocalValueSet()) {
            Object value = input.getValue();

            if (value != null) {
                if (value instanceof String) {
                    return (String) value;
                }

                Converter converter = ComponentUtils.getConverter(context, input);
                if (converter != null) {
                    return converter.getAsString(context, this, value);
                }

                throw new FacesException("No converter available for: " + value.getClass());
            }
        }

        return null;
    }
}