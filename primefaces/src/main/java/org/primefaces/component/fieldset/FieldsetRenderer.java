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
package org.primefaces.component.fieldset;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Fieldset.DEFAULT_RENDERER, componentFamily = Fieldset.COMPONENT_FAMILY)
public class FieldsetRenderer extends CoreRenderer<Fieldset> {

    @Override
    public void decode(FacesContext context, Fieldset component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);
        String toggleStateParam = clientId + "_collapsed";

        if (params.containsKey(toggleStateParam)) {
            component.setCollapsed(Boolean.parseBoolean(params.get(toggleStateParam)));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Fieldset component) throws IOException {
        if (component.isContentLoadRequest(context)) {
            renderChildren(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeMarkup(FacesContext context, Fieldset component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String widgetVar = component.resolveWidgetVar(context);
        boolean toggleable = component.isToggleable();
        String title = component.getTitle();

        String styleClass = toggleable ? Fieldset.TOGGLEABLE_FIELDSET_CLASS : Fieldset.FIELDSET_CLASS;
        if (component.isCollapsed()) {
            styleClass = styleClass + " ui-hidden-container";
        }
        if (component.getStyleClass() != null) {
            styleClass = styleClass + " " + component.getStyleClass();
        }

        writer.startElement("fieldset", component);
        if (title != null) {
            writer.writeAttribute("title", component.getTitle(), null);
        }
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        renderDynamicPassThruAttributes(context, component);

        encodeLegend(context, component);

        encodeContent(context, component);

        if (toggleable) {
            encodeStateHolder(context, component);
        }

        writer.endElement("fieldset");
    }

    protected void encodeContent(FacesContext context, Fieldset fieldset) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", Fieldset.CONTENT_CLASS, null);
        if (fieldset.isCollapsed()) {
            writer.writeAttribute("style", "display:none", null);
        }

        if (!fieldset.isDynamic()) {
            renderChildren(context, fieldset);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Fieldset component) throws IOException {
        boolean toggleable = component.isToggleable();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Fieldset", component);

        if (toggleable) {
            wb.attr("toggleable", true)
                    .attr("dynamic", component.isDynamic(), false)
                    .attr("collapsed", component.isCollapsed())
                    .attr("toggleSpeed", component.getToggleSpeed());
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeLegend(FacesContext context, Fieldset component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String legendText = component.getLegend();
        UIComponent legend = component.getLegendFacet();
        boolean renderFacet = FacetUtils.shouldRenderFacet(legend);

        if (renderFacet || legendText != null) {
            writer.startElement("legend", null);
            writer.writeAttribute("class", Fieldset.LEGEND_CLASS, null);

            if (component.isToggleable()) {
                writer.writeAttribute("tabindex", component.getTabindex(), null);

                String togglerClass = component.isCollapsed() ? Fieldset.TOGGLER_PLUS_CLASS : Fieldset.TOGGLER_MINUS_CLASS;

                writer.startElement("span", null);
                writer.writeAttribute("class", togglerClass, null);
                writer.endElement("span");
            }

            if (renderFacet) {
                legend.encodeAll(context);
            }
            else {
                if (component.isEscape()) {
                    writer.writeText(legendText, "value");
                }
                else {
                    writer.write(legendText);
                }
            }

            writer.endElement("legend");
        }
    }

    protected void encodeStateHolder(FacesContext context, Fieldset component) throws IOException {
        String name = component.getClientId(context) + "_collapsed";
        renderHiddenInput(context, name, String.valueOf(component.isCollapsed()), false);
    }

    @Override
    public void encodeChildren(FacesContext context, Fieldset component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
