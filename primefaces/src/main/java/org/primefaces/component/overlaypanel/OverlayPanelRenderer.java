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
package org.primefaces.component.overlaypanel;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = OverlayPanel.DEFAULT_RENDERER, componentFamily = OverlayPanel.COMPONENT_FAMILY)
public class OverlayPanelRenderer extends CoreRenderer<OverlayPanel> {

    @Override
    public void decode(FacesContext context, OverlayPanel component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, OverlayPanel component) throws IOException {
        if (component.isContentLoadRequest(context)) {
            renderChildren(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeMarkup(FacesContext context, OverlayPanel component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? OverlayPanel.STYLE_CLASS : OverlayPanel.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", OverlayPanel.CONTENT_CLASS, "styleClass");
        if (!component.isDynamic()) {
            renderChildren(context, component);
        }
        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, OverlayPanel panel) throws IOException {
        String target = SearchExpressionUtils.resolveClientId(context, panel, panel.getFor());

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OverlayPanel", panel)
                .attr("target", target)
                .attr("showEvent", panel.getShowEvent(), null)
                .attr("hideEvent", panel.getHideEvent(), null)
                .callback("onShow", "function()", panel.getOnShow())
                .callback("onHide", "function()", panel.getOnHide())
                .attr("my", panel.getMy(), null)
                .attr("at", panel.getAt(), null)
                .attr("collision", panel.getCollision(), null)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, panel, panel.getAppendTo()))
                .attr("dynamic", panel.isDynamic(), false)
                .attr("cache", panel.isCache(), true)
                .attr("dismissable", panel.isDismissable(), true)
                .attr("showCloseIcon", panel.isShowCloseIcon(), false)
                .attr("modal", panel.isModal(), false)
                .attr("blockScroll", panel.isBlockScroll(), false)
                .attr("autoHide", panel.isAutoHide(), true)
                .attr("showDelay", panel.getShowDelay(), 0);

        encodeClientBehaviors(context, panel);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, OverlayPanel component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
