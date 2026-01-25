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
package org.primefaces.component.chip;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Chip.DEFAULT_RENDERER, componentFamily = Chip.COMPONENT_FAMILY)
public class ChipRenderer extends CoreRenderer<Chip> {

    @Override
    public void decode(FacesContext context, Chip component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Chip component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Chip component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                    .add(Chip.STYLE_CLASS)
                    .add(component.getStyleClass())
                    .add(component.getImage() != null, Chip.IMAGE_CLASS)
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        if (component.getChildCount() > 0) {
            renderChildren(context, component);
        }
        else {
            encodeDefaultContent(context, component);
        }

        writer.endElement("div");
    }

    protected void encodeDefaultContent(FacesContext context, Chip component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (component.getImage() != null) {
            writer.startElement("img", null);
            writer.writeAttribute("src", component.getImage(), null);
            writer.endElement("img");
        }
        else if (component.getIcon() != null) {
            String iconStyleClass = getStyleClassBuilder(context)
                    .add(Chip.ICON_CLASS)
                    .add(component.getIcon())
                    .build();

            writer.startElement("span", component);
            writer.writeAttribute("class", iconStyleClass, null);
            writer.endElement("span");
        }

        if (component.getLabel() != null) {
            writer.startElement("div", component);
            writer.writeAttribute("class", Chip.TEXT_CLASS, null);
            writer.writeText(component.getLabel(), "label");
            writer.endElement("div");
        }

        if (component.isRemovable()) {
            String removeIconStyleClass = getStyleClassBuilder(context)
                    .add(Chip.REMOVE_ICON_CLASS)
                    .add(component.getRemoveIcon())
                    .build();

            writer.startElement("span", component);
            writer.writeAttribute("tabindex", "0", null);
            writer.writeAttribute("class", removeIconStyleClass, null);
            writer.endElement("span");
        }
    }

    protected void encodeScript(FacesContext context, Chip component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Chip", component);
        encodeClientBehaviors(context, component);
        wb.finish();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext fc, Chip component) {
        // nothing to do
    }
}