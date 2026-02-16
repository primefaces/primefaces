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
package org.primefaces.component.chart;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.FastStringWriter;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Chart.DEFAULT_RENDERER, componentFamily = Chart.COMPONENT_FAMILY)
public class ChartRenderer extends CoreRenderer<Chart> {

    @Override
    public void decode(FacesContext context, Chart component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Chart component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Chart component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        String canvasStyle = component.getCanvasStyle();
        String canvasStyleClass = component.getCanvasStyleClass();
        styleClass = (styleClass != null) ? "ui-chart " + styleClass : "ui-chart";

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (LangUtils.isNotEmpty(style)) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("canvas", null);
        writer.writeAttribute("id", clientId + "_canvas", null);
        writer.writeAttribute(HTML.ARIA_ROLE, "img", null);
        writer.writeAttribute(HTML.ARIA_LABEL, component.getAriaLabel(), null);
        if (LangUtils.isNotEmpty(canvasStyle)) {
            writer.writeAttribute("style", canvasStyle, null);
        }
        if (LangUtils.isNotEmpty(canvasStyleClass)) {
            writer.writeAttribute("class", canvasStyleClass, null);
        }
        writer.endElement("canvas");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Chart component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Chart", component)
                .nativeAttr("config", renderConfig(context, component))
                .nativeAttr("extender", component.getExtender());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    /**
     * Allow value to be a property or a facet of raw JSON.
     */
    protected String renderConfig(FacesContext context, Chart component) throws IOException {
        UIComponent facet = component.getValueFacet();
        if (FacetUtils.shouldRenderFacet(facet)) {
            // swap writers
            ResponseWriter originalWriter = context.getResponseWriter();
            FastStringWriter fsw = new FastStringWriter();
            ResponseWriter clonedWriter = originalWriter.cloneWithWriter(fsw);
            context.setResponseWriter(clonedWriter);

            // encode the component
            facet.encodeAll(context);

            // restore the original writer
            context.setResponseWriter(originalWriter);
            return fsw.toString();
        }
        else {
            return component.getValue();
        }
    }

}
