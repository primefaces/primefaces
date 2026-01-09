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
package org.primefaces.component.ajaxstatus;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = AjaxStatus.DEFAULT_RENDERER, componentFamily = AjaxStatus.COMPONENT_FAMILY)
public class AjaxStatusRenderer extends CoreRenderer<AjaxStatus> {

    @Override
    public void encodeEnd(FacesContext context, AjaxStatus component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, AjaxStatus component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context)
                .init("AjaxStatus", component)
                .attr("delay", component.getDelay());

        wb.callback(AjaxStatus.START, "function()", component.getOnstart())
                .callback(AjaxStatus.ERROR, "function(xhr,settings,error)", component.getOnerror())
                .callback(AjaxStatus.SUCCESS, "function(xhr,settings)", component.getOnsuccess())
                .callback(AjaxStatus.COMPLETE, "function(xhr,settings,args)", component.getOncomplete());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, AjaxStatus component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }
        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), "styleClass");
        }

        UIComponent startFacet = component.getStartFacet();
        if (FacetUtils.shouldRenderFacet(startFacet)) {
            encodeFacet(context, clientId, startFacet, AjaxStatus.START, true);
        }

        UIComponent successFacet = component.getSuccessFacet();
        if (FacetUtils.shouldRenderFacet(successFacet)) {
            encodeFacet(context, clientId, successFacet, AjaxStatus.SUCCESS, true);
        }

        UIComponent completeFacet = component.getCompleteFacet();
        if (FacetUtils.shouldRenderFacet(completeFacet)) {
            encodeFacet(context, clientId, completeFacet, AjaxStatus.COMPLETE, true);
        }

        UIComponent errorFacet = component.getErrorFacet();
        if (FacetUtils.shouldRenderFacet(errorFacet)) {
            encodeFacet(context, clientId, errorFacet, AjaxStatus.ERROR, true);
        }

        UIComponent defaultFacet = component.getDefaultFacet();
        if (FacetUtils.shouldRenderFacet(defaultFacet)) {
            encodeFacet(context, clientId, defaultFacet, AjaxStatus.DEFAULT, false);
        }

        writer.endElement("div");
    }

    protected void encodeFacet(FacesContext context, String clientId, UIComponent facet, String facetName, boolean hidden) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_" + facetName, null);
        if (hidden) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChild(context, facet);

        writer.endElement("div");
    }
}
