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
package org.primefaces.component.imageswitch;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ImageSwitchRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ImageSwitch imageSwitch = (ImageSwitch) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = imageSwitch.getClientId(context);

        writer.startElement("div", imageSwitch);
        writer.writeAttribute("id", clientId, "id");

        if (imageSwitch.getStyle() != null) {
            writer.writeAttribute("style", imageSwitch.getStyle(), null);
        }
        if (imageSwitch.getStyleClass() != null) {
            writer.writeAttribute("class", imageSwitch.getStyleClass(), null);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ImageSwitch imageSwitch = (ImageSwitch) component;
        String clientId = imageSwitch.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        int slideshowSpeed = imageSwitch.isSlideshowAuto() ? imageSwitch.getSlideshowSpeed() : 0;

        writer.endElement("div");

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ImageSwitch", imageSwitch.resolveWidgetVar(context), clientId)
                .attr("fx", imageSwitch.getEffect())
                .attr("speed", imageSwitch.getSpeed())
                .attr("timeout", slideshowSpeed)
                .attr("startingSlide", imageSwitch.getActiveIndex(), 0);

        wb.finish();
    }

}
