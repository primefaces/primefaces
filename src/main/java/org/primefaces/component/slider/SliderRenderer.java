/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.InputHolder;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.WidgetBuilder;

public class SliderRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
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
        writer.writeAttribute("id", clientId, "id");
        if (slider.getStyle() != null) {
            writer.writeAttribute("style", slider.getStyle(), null);
        }
        if (slider.getStyleClass() != null) {
            writer.writeAttribute("class", slider.getStyleClass(), null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Slider slider) throws IOException {
        String clientId = slider.getClientId(context);
        boolean range = slider.isRange();
        UIComponent output = getTarget(context, slider, slider.getDisplay());

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Slider", slider.resolveWidgetVar(context), clientId);

        if (range) {
            String[] inputIds = slider.getFor().split(",");
            UIComponent inputMin = getTarget(context, slider, inputIds[0]);
            UIComponent inputMax = getTarget(context, slider, inputIds[1]);

            String inputMinValue = ComponentUtils.getValueToRender(context, inputMin);
            if (inputMinValue == null) {
                inputMinValue = Constants.EMPTY_STRING;
            }

            String inputMaxValue = ComponentUtils.getValueToRender(context, inputMax);
            if (inputMaxValue == null) {
                inputMaxValue = Constants.EMPTY_STRING;
            }

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

        UIComponent targetComponent = SearchExpressionFacade.resolveComponent(context, slider, target);
        return targetComponent;
    }
}
