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
package org.primefaces.component.graphicimage;

import java.io.IOException;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.DynamicContentSrcBuilder;
import org.primefaces.util.HTML;

public class GraphicImageRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        GraphicImage image = (GraphicImage) component;
        String clientId = image.getClientId(context);
        String imageSrc;
        try {
            imageSrc = getImageSrc(context, image);
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }

        writer.startElement("img", image);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("src", imageSrc, null);

        if (image.getAlt() == null) {
            writer.writeAttribute("alt", "", null);
        }
        if (image.getStyleClass() != null) {
            writer.writeAttribute("class", image.getStyleClass(), "styleClass");
        }

        renderPassThruAttributes(context, image, HTML.IMG_ATTRS);

        writer.endElement("img");
    }

    protected String getImageSrc(FacesContext context, GraphicImage image) throws Exception {
        String name = image.getName();

        if (name != null) {
            String libName = image.getLibrary();
            ResourceHandler handler = context.getApplication().getResourceHandler();
            Resource res = handler.createResource(name, libName);

            if (res == null) {
                return "RES_NOT_FOUND";
            }
            else {
                String requestPath = res.getRequestPath();
                return context.getExternalContext().encodeResourceURL(requestPath);
            }
        }
        else {
            return DynamicContentSrcBuilder.build(context, image.getValue(), image, image.isCache(), DynamicContentType.STREAMED_CONTENT, image.isStream());
        }
    }
}
