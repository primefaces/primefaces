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
package org.primefaces.component.tooltip;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Tooltip.DEFAULT_RENDERER, componentFamily = Tooltip.COMPONENT_FAMILY)
public class TooltipRenderer extends CoreRenderer<Tooltip> {

    @Override
    public void encodeEnd(FacesContext context, Tooltip component) throws IOException {
        String target = SearchExpressionUtils.resolveClientIdsForClientSide(
                context, component, component.getFor());

        encodeMarkup(context, component, target);
        encodeScript(context, component, target);
    }

    protected void encodeMarkup(FacesContext context, Tooltip component, String target) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (target != null) {
            String styleClass = component.getStyleClass();
            styleClass = styleClass == null ? Tooltip.CONTAINER_CLASS : Tooltip.CONTAINER_CLASS + " " + styleClass;
            styleClass = styleClass + " ui-tooltip-" + component.getPosition();

            writer.startElement("div", component);
            writer.writeAttribute("id", component.getClientId(context), null);
            writer.writeAttribute("class", styleClass, "styleClass");
            writer.writeAttribute("role", "tooltip", null);

            if (component.getStyle() != null) {
                writer.writeAttribute("style", component.getStyle(), "style");
            }

            writer.startElement("div", component);
            writer.writeAttribute("class", "ui-tooltip-arrow", null);
            writer.endElement("div");

            writer.startElement("div", component);
            writer.writeAttribute("class", "ui-tooltip-text ui-shadow", null);

            if (component.getChildCount() > 0) {
                renderChildren(context, component);
            }
            else {
                String valueToRender = ComponentUtils.getValueToRender(context, component);
                if (valueToRender != null) {
                    if (component.isEscape()) {
                        writer.writeText(valueToRender, "value");
                    }
                    else {
                        writer.write(valueToRender);
                    }
                }
            }

            writer.endElement("div");

            writer.endElement("div");
        }
    }

    protected void encodeScript(FacesContext context, Tooltip component, String target) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Tooltip", component)
                .attr("showEvent", component.getShowEvent(), null)
                .attr("hideEvent", component.getHideEvent(), null)
                .attr("showEffect", component.getShowEffect(), null)
                .attr("hideEffect", component.getHideEffect(), null)
                .attr("showDelay", component.getShowDelay(), 150)
                .attr("hideDelay", component.getHideDelay(), 0)
                .attr("target", target, null)
                .attr("globalSelector", component.getGlobalSelector(), null)
                .attr("escape", component.isEscape(), true)
                .attr("trackMouse", component.isTrackMouse(), false)
                .attr("position", component.getPosition(), "right")
                .attr("myPos", component.getMy(), null)
                .attr("atPos", component.getAt(), null)
                .attr("delegate", component.isDelegate(), false)
                .attr("styleClass", component.getStyleClass(), null)
                .attr("autoHide", component.isAutoHide(), true)
                .returnCallback("beforeShow", "function()", component.getBeforeShow())
                .callback("onShow", "function()", component.getOnShow())
                .callback("onHide", "function()", component.getOnHide());

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, Tooltip component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
