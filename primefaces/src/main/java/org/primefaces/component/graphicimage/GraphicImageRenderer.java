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
package org.primefaces.component.graphicimage;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.DynamicContentSrcBuilder;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.ResourceUtils;

import java.io.IOException;

import jakarta.faces.application.Resource;
import jakarta.faces.application.ResourceHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = GraphicImage.DEFAULT_RENDERER, componentFamily = GraphicImage.COMPONENT_FAMILY)
public class GraphicImageRenderer extends CoreRenderer<GraphicImage> {

    @Override
    public void decode(FacesContext context, GraphicImage component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, GraphicImage component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String imageSrc = getImageSrc(context, component);

        writer.startElement("img", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("src", imageSrc, null);

        if (LangUtils.isBlank(component.getAlt())) {
            // #13790 decorative images should have an empty alt attribute
            writer.writeAttribute("alt", "", null);
        }
        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), "styleClass");
        }

        renderDomEvents(context, component, HTML.IMG_ATTRS);

        writer.endElement("img");
    }

    protected String getImageSrc(FacesContext context, GraphicImage component) {
        String name = component.getName();

        if (name != null) {
            String library = component.getLibrary();
            ResourceHandler handler = context.getApplication().getResourceHandler();
            Resource resource = handler.createResource(name, library);
            if (resource == null) {
                return "RES_NOT_FOUND";
            }

            if (component.isStream()) {
                String requestPath = resource.getRequestPath();
                return context.getExternalContext().encodeResourceURL(requestPath);
            }
            else {
                return ResourceUtils.toBase64(context, resource);
            }
        }
        else {
            return DynamicContentSrcBuilder.build(context, component, component.getValueExpression(GraphicImage.PropertyKeys.value.name()),
                    new Lazy<>(() -> component.getValue()), component.isCache(), component.isStream());
        }
    }
}
