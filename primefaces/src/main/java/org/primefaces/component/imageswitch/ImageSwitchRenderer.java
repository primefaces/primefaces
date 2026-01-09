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
package org.primefaces.component.imageswitch;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = ImageSwitch.DEFAULT_RENDERER, componentFamily = ImageSwitch.COMPONENT_FAMILY)
public class ImageSwitchRenderer extends CoreRenderer<ImageSwitch> {

    @Override
    public void encodeBegin(FacesContext context, ImageSwitch component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }
        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), null);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, ImageSwitch component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int slideshowSpeed = component.isSlideshowAuto() ? component.getSlideshowSpeed() : 0;

        writer.endElement("div");

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ImageSwitch", component)
                .attr("fx", component.getEffect())
                .attr("speed", component.getSpeed())
                .attr("timeout", slideshowSpeed)
                .attr("startingSlide", component.getActiveIndex(), 0);

        wb.finish();
    }

}
