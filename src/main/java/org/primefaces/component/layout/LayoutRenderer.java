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
package org.primefaces.component.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class LayoutRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;
        String clientId = layout.getClientId(context);

        if (layout.isElementLayout()) {
            writer.startElement("div", layout);
            writer.writeAttribute("id", clientId, "id");

            if (layout.getStyle() != null) {
                writer.writeAttribute("style", layout.getStyle(), "style");
            }
            if (layout.getStyleClass() != null) {
                writer.writeAttribute("class", layout.getStyleClass(), "styleClass");
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;

        if (layout.isElementLayout()) {
            writer.endElement("div");
        }

        encodeScript(context, layout);
    }

    protected void encodeScript(FacesContext context, Layout layout) throws IOException {
        String clientId = layout.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Layout", layout.resolveWidgetVar(context), clientId)
                .attr("full", layout.isFullPage(), false)
                .attr("useStateCookie", layout.isStateful(), false);

        if (layout.isNested()) {
            wb.attr("parent", layout.getParent().getClientId(context));
        }

        wb.callback("onToggle", "function(e)", layout.getOnToggle())
                .callback("onClose", "function(e)", layout.getOnClose())
                .callback("onResize", "function(e)", layout.getOnResize());

        encodeUnits(context, layout, wb);
        encodeClientBehaviors(context, layout);

        wb.finish();
    }

    protected void encodeUnits(FacesContext context, Layout layout, WidgetBuilder wb) throws IOException {
        for (UIComponent child : layout.getChildren()) {
            if (child.isRendered() && child instanceof LayoutUnit) {
                LayoutUnit unit = (LayoutUnit) child;

                wb.append(",").append(unit.getPosition()).append(":{")
                        .append("paneSelector:'#").append(ComponentUtils.escapeSelector(unit.getClientId(context))).append("'")
                        .attr("size", unit.getSize())
                        .attr("resizable", unit.isResizable())
                        .attr("closable", unit.isCollapsible())
                        .attr("minSize", unit.getMinSize(), 50)
                        .attr("maxSize", unit.getMaxSize(), 0)
                        .attr("spacing_open", unit.getGutter(), 6);

                if (unit.isCollapsible()) {
                    wb.attr("spacing_closed", unit.getCollapseSize());
                }

                wb.attr("initHidden", !unit.isVisible(), false)
                        .attr("initClosed", unit.isCollapsed(), false)
                        .attr("fxName", unit.getEffect(), null)
                        .attr("fxSpeed", unit.getEffectSpeed(), null)
                        .attr("resizerTip", layout.getResizeTitle(), null)
                        .attr("togglerTip_closed", layout.getExpandTitle(), null);

                wb.append("}");
            }
        }
    }
}
