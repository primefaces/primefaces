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
package org.primefaces.component.link;

import org.primefaces.renderkit.OutcomeTargetRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Link.DEFAULT_RENDERER, componentFamily = Link.COMPONENT_FAMILY)
public class LinkRenderer extends OutcomeTargetRenderer<Link> {

    @Override
    public void encodeEnd(FacesContext context, Link component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean shouldWriteId = shouldWriteId(component);
        boolean disabled = component.isDisabled();
        String style = component.getStyle();
        String defaultStyleClass = disabled ? Link.DISABLED_STYLE_CLASS : Link.STYLE_CLASS;
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        if (disabled) {
            writer.startElement("span", component);
            if (shouldWriteId) {
                writer.writeAttribute("id", component.getClientId(context), "id");
            }
            writer.writeAttribute("class", styleClass, "styleClass");
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }

            renderContent(context, component);
            writer.endElement("span");
        }
        else {
            String targetURL = getTargetURL(context, component);
            if (LangUtils.isBlank(targetURL)) {
                targetURL = "#";
            }

            writer.startElement("a", component);
            if (shouldWriteId) {
                writer.writeAttribute("id", component.getClientId(context), "id");
            }
            writer.writeAttribute("href", targetURL, null);
            writer.writeAttribute("class", styleClass, "styleClass");
            renderPassThruAttributes(context, component, HTML.LINK_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, component, HTML.OUTPUT_EVENTS);
            renderContent(context, component);
            writer.endElement("a");
        }
    }

    protected void renderContent(FacesContext context, Link component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Object value = component.getValue();

        if (value != null) {
            if (component.isEscape()) {
                writer.writeText(value, "value");
            }
            else {
                writer.write(value.toString());
            }
        }
        else {
            renderChildren(context, component);
        }
    }

    @Override
    public void encodeChildren(FacesContext context, Link component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
