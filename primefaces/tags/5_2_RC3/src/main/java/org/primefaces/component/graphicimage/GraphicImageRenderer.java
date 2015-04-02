/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.primefaces.util.DynamicResourceBuilder;
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
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        
		writer.startElement("img", image);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("src", imageSrc, null);
		
		if(image.getAlt() == null) writer.writeAttribute("alt", "", null);
		if(image.getStyleClass() != null) writer.writeAttribute("class", image.getStyleClass(), "styleClass");
		
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
            return DynamicResourceBuilder.build(context, image.getValue(), image, image.isCache(), DynamicContentType.STREAMED_CONTENT);
        }
	}
}