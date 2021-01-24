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
package org.primefaces.component.avatar;

import org.primefaces.renderkit.CoreRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;

public class AvatarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Avatar avatar = (Avatar) component;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(Avatar.STYLE_CLASS)
                .add(avatar.getStyleClass())
                .add(avatar.getImage() != null, Avatar.IMAGE_CLASS)
                .add("circle".equals(avatar.getShape()), Avatar.CIRCLE_CLASS)
                .add("large".equals(avatar.getSize()), Avatar.SIZE_LARGE_CLASS)
                .add("xlarge".equals(avatar.getSize()), Avatar.SIZE_XLARGE_CLASS)
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", avatar.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (avatar.getStyle() != null) {
            writer.writeAttribute("style", avatar.getStyle(), "style");
        }

        if (avatar.getChildCount() > 0) {
            renderChildren(context, avatar);
        }
        else {
            encodeDefaultContent(context, avatar);
        }

        writer.endElement("div");
    }

    protected void encodeDefaultContent(FacesContext context, Avatar avatar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (avatar.getLabel() != null) {
            writer.startElement("span", null);
            writer.writeAttribute("class", Avatar.SIZE_TEXT_CLASS, "styleClass");
            writer.write(avatar.getLabel());
            writer.endElement("span");
        }
        else if (avatar.getIcon() != null) {
            String iconStyleClass = getStyleClassBuilder(context)
                .add(Avatar.ICON_CLASS)
                .add(avatar.getIcon())
                .build();

            writer.startElement("span", null);
            writer.writeAttribute("class", iconStyleClass, "styleClass");
            writer.endElement("span");
        }
        else if (avatar.getImage() != null) {
            writer.startElement("img", null);
            writer.writeAttribute("src", avatar.getImage(), null);
            writer.endElement("img");
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
