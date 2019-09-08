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
package org.primefaces.component.overlaypanel;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class OverlayPanelRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        OverlayPanel panel = (OverlayPanel) component;

        if (panel.isContentLoadRequest(context)) {
            renderChildren(context, panel);
        }
        else {
            encodeMarkup(context, panel);
            encodeScript(context, panel);
        }
    }

    protected void encodeMarkup(FacesContext context, OverlayPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();
        styleClass = styleClass == null ? OverlayPanel.STYLE_CLASS : OverlayPanel.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", OverlayPanel.CONTENT_CLASS, "styleClass");
        if (!panel.isDynamic()) {
            renderChildren(context, panel);
        }
        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, OverlayPanel panel) throws IOException {
        String target = SearchExpressionFacade.resolveClientId(context, panel, panel.getFor());
        String clientId = panel.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("OverlayPanel", panel.resolveWidgetVar(context), clientId)
                .attr("target", target)
                .attr("showEvent", panel.getShowEvent(), null)
                .attr("hideEvent", panel.getHideEvent(), null)
                .attr("showEffect", panel.getShowEffect(), null)
                .attr("hideEffect", panel.getHideEffect(), null)
                .callback("onShow", "function()", panel.getOnShow())
                .callback("onHide", "function()", panel.getOnHide())
                .attr("my", panel.getMy(), null)
                .attr("at", panel.getAt(), null)
                .attr("collision", panel.getCollision(), null)
                .attr("appendTo", panel.getAppendTo(), null)
                .attr("dynamic", panel.isDynamic(), false)
                .attr("dismissable", panel.isDismissable(), true)
                .attr("showCloseIcon", panel.isShowCloseIcon(), false)
                .attr("modal", panel.isModal(), false)
                .attr("blockScroll", panel.isBlockScroll(), false)
                .attr("showDelay", panel.getShowDelay(), 0);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
