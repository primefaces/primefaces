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

import org.primefaces.component.api.InputHolder;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Slider.DEFAULT_RENDERER, componentFamily = Slider.COMPONENT_FAMILY)
public class SliderRenderer extends CoreRenderer<Slider> {

    @Override
    public void decode(FacesContext context, Slider component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Slider component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Slider component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        String styleClass = getStyleClassBuilder(context)
                .add(component.getStyleClass())
                .add(component.isReadonly(), "ui-state-readonly")
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }
        if (LangUtils.isNotEmpty(styleClass)) {
            writer.writeAttribute("class", styleClass, null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Slider component) throws IOException {
        String range = component.getRange();
        UIComponent output = getTarget(context, component, component.getDisplay());

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Slider", component);

        if ("true".equals(range)) {
            String[] inputIds = component.getFor().split(",");
            UIComponent inputMin = getTarget(context, component, inputIds[0]);
            UIComponent inputMax = getTarget(context, component, inputIds[1]);

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
            UIComponent input = getTarget(context, component, component.getFor());
            String inputClientId = input instanceof InputHolder ? ((InputHolder) input).getInputClientId() : input.getClientId(context);

            wb.attr("value", ComponentUtils.getValueToRender(context, input))
                    .attr("input", inputClientId);
        }

        wb.attr("min", component.getMinValue())
                .attr("max", component.getMaxValue())
                .attr("animate", component.isAnimate())
                .attr("step", component.getStep())
                .attr("orientation", component.getType())
                .attr("disabled", component.isDisabled(), false)
                .attr("displayTemplate", component.getDisplayTemplate(), null)
                .attr("touchable", ComponentUtils.isTouchable(context, component),  true)
                .callback("onSlideStart", "function(event,ui)", component.getOnSlideStart())
                .callback("onSlide", "function(event,ui)", component.getOnSlide())
                .callback("onSlideEnd", "function(event,ui)", component.getOnSlideEnd());

        switch (range) {
            case "true":
            case "false":
                wb.attr("range", Boolean.valueOf(range));
                break;
            default:
                wb.attr("range", range);
                break;
        }

        if (output != null) {
            wb.attr("display", output.getClientId(context));
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected UIComponent getTarget(FacesContext context, Slider component, String target) {
        if (target == null) {
            return null;
        }

        UIComponent targetComponent = SearchExpressionUtils.contextlessResolveComponent(context, component, target);
        return targetComponent;
    }
}
