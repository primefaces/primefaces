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
package org.primefaces.component.slider;

import org.primefaces.component.api.InputHolder;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class SliderRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
        
        Slider slider = (Slider) component;
        
        String[] inputIds = slider.getFor().split(",");
        if (slider.isRange()) {
            UIInput inputFrom = (UIInput) getTarget(context, slider, inputIds[0].trim());
            UIInput inputTo = (UIInput) getTarget(context, slider, inputIds[1].trim());
            String inputFromClientId = inputFrom.getClientId(context);
            String inputToClientId = inputTo.getClientId(context);
            String valueFromStr = context.getExternalContext().getRequestParameterMap().get(inputFromClientId);
            String valueToStr = context.getExternalContext().getRequestParameterMap().get(inputToClientId);
            double valueFrom = Double.valueOf(valueFromStr.toString());
            double valueTo = Double.valueOf(valueToStr.toString());
            if (valueFrom < slider.getMinValue() || valueFrom > slider.getMaxValue()) {
                valueFrom = slider.getMinValue();
            }
            if (valueTo > slider.getMaxValue() || valueTo < slider.getMinValue()) {
                valueTo = slider.getMaxValue();
            }
            valueFrom = Math.min(valueFrom, valueTo);
            valueTo = Math.max(valueFrom, valueTo);
            // TODO how to re-apply request values for inputFrom and inputTo ?
        } 
        else {
            UIInput input = (UIInput) getTarget(context, slider, inputIds[0].trim());
            String inputClientId = input.getClientId(context);
            if (input instanceof InputHolder) {
                inputClientId += "_hinput";
            }
            Object valueStr = context.getExternalContext().getRequestParameterMap().get(inputClientId);
            double value = Double.valueOf(valueStr.toString());
            value = Math.min(Math.max(value, slider.getMinValue()), slider.getMaxValue());
            // TODO how to re-apply request value for input ?
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Slider slider = (Slider) component;

        encodeMarkup(context, slider);
        encodeScript(context, slider);
    }

    protected void encodeMarkup(FacesContext context, Slider slider) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = slider.getClientId(context);

        writer.startElement("div", slider);
        writer.writeAttribute("id", clientId , "id");
        if (slider.getStyle() != null)  writer.writeAttribute("style", slider.getStyle() , null);
        if (slider.getStyleClass() != null) writer.writeAttribute("class", slider.getStyleClass(), null);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Slider slider) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = slider.getClientId(context);
        boolean range = slider.isRange();
        UIComponent output = getTarget(context, slider, slider.getDisplay());

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Slider", slider.resolveWidgetVar(), clientId);

        if (range) {
            String[] inputIds = slider.getFor().split(",");
            UIComponent inputMin = getTarget(context, slider, inputIds[0].trim());
            UIComponent inputMax = getTarget(context, slider, inputIds[1].trim());
            String inputMinValue = ComponentUtils.getValueToRender(context, inputMin);
            String inputMaxValue = ComponentUtils.getValueToRender(context, inputMax);

            wb.attr("input", inputMin.getClientId(context) + "," + inputMax.getClientId(context))
                    .append(",values:[").append(inputMinValue).append(",").append(inputMaxValue).append("]");
        }
        else {
            UIComponent input = getTarget(context, slider, slider.getFor());
            String inputClientId = input instanceof InputHolder ? ((InputHolder) input).getInputClientId() : input.getClientId(context);

            wb.attr("value", ComponentUtils.getValueToRender(context, input))
                    .attr("input", inputClientId);
        }

        wb.attr("min", slider.getMinValue())
                .attr("max", slider.getMaxValue())
                .attr("animate", slider.isAnimate())
                .attr("step", slider.getStep())
                .attr("orientation", slider.getType())
                .attr("disabled", slider.isDisabled(), false)
                .attr("range", range)
                .attr("displayTemplate", slider.getDisplayTemplate(), null)
                .callback("onSlideStart", "function(event,ui)", slider.getOnSlideStart())
                .callback("onSlide", "function(event,ui)", slider.getOnSlide())
                .callback("onSlideEnd", "function(event,ui)", slider.getOnSlideEnd());

        if (output != null) {
            wb.attr("display", output.getClientId(context));
        }

        encodeClientBehaviors(context, slider);

        wb.finish();
    }

    protected UIComponent getTarget(FacesContext context, Slider slider, String target) {
        if (target == null) {
            return null;
        }
        else {
            UIComponent targetComponent = SearchExpressionFacade.resolveComponent(context, slider, target);

            return targetComponent;
        }
    }
}
