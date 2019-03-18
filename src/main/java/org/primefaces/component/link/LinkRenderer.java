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
package org.primefaces.component.link;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

public class LinkRenderer extends OutcomeTargetRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Link link = (Link) component;
        boolean shouldWriteId = shouldWriteId(link);
        boolean disabled = link.isDisabled();
        String style = link.getStyle();
        String defaultStyleClass = disabled ? Link.DISABLED_STYLE_CLASS : Link.STYLE_CLASS;
        String styleClass = link.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        if (disabled) {
            writer.startElement("span", link);
            if (shouldWriteId) {
                writer.writeAttribute("id", link.getClientId(context), "id");
            }
            writer.writeAttribute("class", styleClass, "styleClass");
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }

            renderContent(context, link);
            writer.endElement("span");
        }
        else {
            String targetURL = getTargetURL(context, link);
            if (LangUtils.isValueBlank(targetURL)) {
                targetURL = "#";
            }

            writer.startElement("a", link);
            if (shouldWriteId) {
                writer.writeAttribute("id", link.getClientId(context), "id");
            }
            writer.writeAttribute("href", targetURL, null);
            writer.writeAttribute("class", styleClass, "styleClass");
            renderPassThruAttributes(context, link, HTML.LINK_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, link, HTML.OUTPUT_EVENTS);
            renderContent(context, link);
            writer.endElement("a");
        }
    }

    protected void renderContent(FacesContext context, Link link) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = link.getValue();

        if (value != null) {
            if (link.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }
        else {
            renderChildren(context, link);
        }
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
