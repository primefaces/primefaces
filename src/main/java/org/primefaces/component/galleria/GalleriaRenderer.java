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
package org.primefaces.component.galleria;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class GalleriaRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Galleria galleria = (Galleria) component;

        encodeMarkup(context, galleria);
        encodeScript(context, galleria);
    }

    public void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        String var = galleria.getVar();
        String style = galleria.getStyle();
        String styleClass = galleria.getStyleClass();
        styleClass = (styleClass == null) ? Galleria.CONTAINER_CLASS : Galleria.CONTAINER_CLASS + " " + styleClass;
        UIComponent content = galleria.getFacet("content");

        writer.startElement("div", component);
        writer.writeAttribute("id", galleria.getClientId(context), "id");
        writer.writeAttribute("tabindex", galleria.getTabindex(), null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("ul", component);
        writer.writeAttribute("class", Galleria.PANEL_WRAPPER_CLASS, null);

        if (var == null) {
            for (UIComponent child : galleria.getChildren()) {
                if (child.isRendered()) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Galleria.PANEL_CLASS, null);
                    child.encodeAll(context);

                    if (content != null) {
                        writer.startElement("div", null);
                        writer.writeAttribute("class", Galleria.PANEL_CONTENT_CLASS, null);
                        content.encodeAll(context);
                        writer.endElement("div");
                    }

                    writer.endElement("li");
                }
            }
        }
        else {
            Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
            Collection<?> value = (Collection<?>) galleria.getValue();
            if (value != null) {
                for (Iterator<?> it = value.iterator(); it.hasNext(); ) {
                    requestMap.put(var, it.next());

                    writer.startElement("li", null);
                    writer.writeAttribute("class", Galleria.PANEL_CLASS, null);
                    renderChildren(context, galleria);

                    if (content != null) {
                        writer.startElement("div", null);
                        writer.writeAttribute("class", Galleria.PANEL_CONTENT_CLASS, null);
                        content.encodeAll(context);
                        writer.endElement("div");
                    }

                    writer.endElement("li");
                }
            }

            requestMap.remove(var);
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        Galleria galleria = (Galleria) component;
        String clientId = galleria.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);

        if (context.isPostback()) {
            wb.init("Galleria", galleria.resolveWidgetVar(context), clientId);
        }
        else {
            wb.initWithWindowLoad("Galleria", galleria.resolveWidgetVar(context), clientId);
        }

        wb.attr("showFilmstrip", galleria.isShowFilmstrip(), true)
                .attr("frameWidth", galleria.getFrameWidth(), 60)
                .attr("frameHeight", galleria.getFrameHeight(), 40)
                .attr("autoPlay", galleria.isAutoPlay(), true)
                .attr("transitionInterval", galleria.getTransitionInterval(), 4000)
                .attr("effect", galleria.getEffect(), "fade")
                .attr("effectSpeed", galleria.getEffectSpeed(), 500)
                .attr("showCaption", galleria.isShowCaption(), false)
                .attr("panelWidth", galleria.getPanelWidth(), Integer.MIN_VALUE)
                .attr("panelHeight", galleria.getPanelHeight(), Integer.MIN_VALUE)
                .attr("custom", (galleria.getFacet("content") != null));

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
