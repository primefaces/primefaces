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
package org.primefaces.component.outputpanel;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = OutputPanel.DEFAULT_RENDERER, componentFamily = OutputPanel.COMPONENT_FAMILY)
public class OutputPanelRenderer extends CoreRenderer<OutputPanel> {

    private static final String BLOCK = "div";
    private static final String INLINE = "span";

    @Override
    public void decode(FacesContext context, OutputPanel component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId();

        if (params.containsKey(clientId + "_load")) {
            decodeBehaviors(context, component);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, OutputPanel component) throws IOException {
        if (component.isContentLoadRequest(context)) {
            renderChildren(context, component);
        }
        else {
            encodeMarkup(context, component);
            if (isDeferredNecessary(context, component)) {
                encodeScript(context, component);
            }
        }
    }

    public void encodeMarkup(FacesContext context, OutputPanel component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String tag = component.getLayout().equals("block") ? BLOCK : INLINE;
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? OutputPanel.CONTAINER_CLASS : OutputPanel.CONTAINER_CLASS + " " + styleClass;

        writer.startElement(tag, component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        if (isDeferredNecessary(context, component)) {
            UIComponent loadingFacet = component.getLoadingFacet();
            if (FacetUtils.shouldRenderFacet(loadingFacet)) {
                loadingFacet.encodeAll(context);
            }
            else {
                renderLoading(context, component);
            }
        }
        else {
            renderChildren(context, component);
        }

        writer.endElement(tag);
    }

    protected void encodeScript(FacesContext context, OutputPanel component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OutputPanel", component);

        wb.attr("deferred", true)
                .attr("deferredMode", component.getDeferredMode());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void renderLoading(FacesContext context, OutputPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("i", null);
        writer.writeAttribute("class", OutputPanel.LOADING_CLASS, null);
        writer.endElement("i");
    }

    protected boolean isDeferredNecessary(FacesContext context, OutputPanel component) {
        if (!component.isDeferred()) {
            return false;
        }
        if (component.isLoaded() != null) {
            return !component.isLoaded();
        }
        return !context.getPartialViewContext().isAjaxRequest();
    }

    @Override
    public void encodeChildren(FacesContext context, OutputPanel component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
