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
package org.primefaces.component.imagecompare;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ImageCompareRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ImageCompare compare = (ImageCompare) component;

        encodeMarkup(context, compare);
        encodeScript(context, compare);
    }

    protected void encodeScript(FacesContext context, ImageCompare compare) throws IOException {
        String clientId = compare.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ImageCompare", compare.resolveWidgetVar(context), clientId)
                .attr("leftimage", getResourceURL(context, compare.getLeftImage()))
                .attr("rightimage", getResourceURL(context, compare.getRightImage()));
        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, ImageCompare compare) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", compare);
        writer.writeAttribute("style", "width: " + compare.getWidth() + "px;" + "height: " + compare.getHeight() + "px;", null);
        writer.writeAttribute("id", compare.getClientId(context), "id");
        renderDynamicPassThruAttributes(context, compare);
        writer.endElement("div");
    }


}
