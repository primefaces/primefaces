/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;

public class ChipRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Chip chip = (Chip) component;

        decodeBehaviors(context, chip);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Chip chip = (Chip) component;

        encodeMarkup(context, chip);
        encodeScript(context, chip);
    }

    protected void encodeMarkup(FacesContext context, Chip chip) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                    .add(Chip.DEFAULT_STYLE_CLASS)
                    .add(chip.getStyleClass())
                    .add(chip.getImage() != null, Chip.IMAGE_CLASS)
                    .build();

        writer.startElement("div", chip);
        writer.writeAttribute("id", chip.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (chip.getStyle() != null) {
            writer.writeAttribute("style", chip.getStyle(), "style");
        }

        if (chip.getImage() != null) {
            writer.startElement("img", chip);
            writer.writeAttribute("src", chip.getImage(), "src");
            writer.endElement("img");
        }

        if (chip.getIcon() != null) {
            writer.startElement("span", chip);
            writer.writeAttribute("class", Chip.ICON_CLASS + " " + chip.getIcon(), "styleClass");
            writer.endElement("span");
        }

        if (chip.getLabel() != null) {
            writer.startElement("div", chip);
            writer.writeAttribute("class", Chip.TEXT_CLASS, "styleClass");
            writer.write(chip.getLabel());
            writer.endElement("div");
        }

        if (chip.getChildCount() > 0) {
            renderChildren(context, chip);
        }

        if (chip.getRemovable()) {
            writer.startElement("span", chip);
            writer.writeAttribute("tabindex", "0", "tabindex");
            writer.writeAttribute("class", Chip.REMOVE_ICON_CLASS + " " + chip.getRemoveIcon(), "styleClass");
            writer.endElement("span");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Chip chip) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Chip", chip);
        encodeClientBehaviors(context, chip);
        wb.finish();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(final FacesContext fc, final UIComponent component) {
        // nothing to do
    }
}