/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;

public class GraphicImageRenderer extends CoreRenderer {
    
    private final static Logger logger = Logger.getLogger(GraphicImageRenderer.class.getName());

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		GraphicImage image = (GraphicImage) component;
		String clientId = image.getClientId(context);
		String imageSrc = getImageSrc(context, image);
		
		writer.startElement("img", image);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("src", imageSrc, null);
		
		if(image.getAlt() == null) writer.writeAttribute("alt", "", null);
		if(image.getStyleClass() != null) writer.writeAttribute("class", image.getStyleClass(), "styleClass");
		
		renderPassThruAttributes(context, image, HTML.IMG_ATTRS);

		writer.endElement("img");
	}
	
	protected String getImageSrc(FacesContext context, GraphicImage image) {
		String src = null;
        String name = image.getName();
        
        if(name != null) {
            String libName = image.getLibrary();
            ResourceHandler handler = context.getApplication().getResourceHandler();
            Resource res = handler.createResource(name, libName);
            
            if(res == null) {
                return "RES_NOT_FOUND";
            } 
            else {
            	String requestPath = res.getRequestPath();
                
            	return context.getExternalContext().encodeResourceURL(requestPath);
            }
        }
        else {
            Object value = image.getValue();
            
            if(value == null) {
                return "";
            }
            else  if(value instanceof String) {
                src = getResourceURL(context, (String) value);
            }
            else if(value instanceof StreamedContent) {
                StreamedContent streamedContent = (StreamedContent) value;
                Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent", "primefaces", streamedContent.getContentType());
                String resourcePath = resource.getRequestPath();
                String rid = createUniqueContentId(context);
                StringBuilder builder = new StringBuilder(resourcePath);

                builder.append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(rid);

                for(UIComponent kid : image.getChildren()) {
                    if(kid instanceof UIParameter) {
                        UIParameter param = (UIParameter) kid;

                        builder.append("&").append(param.getName()).append("=").append(param.getValue());
                    }
                }

                src = builder.toString();

                context.getExternalContext().getSessionMap().put(rid, image.getValueExpression("value").getExpressionString());
            }

            if(!image.isCache()) {
                src += src.contains("?") ? "&" : "?";
                src += "primefaces_image=" + UUID.randomUUID().toString();
            }
            
            src = context.getExternalContext().encodeResourceURL(src);
        }

		return src;
	}
    
    protected String createUniqueContentId(FacesContext context) {
        Map<String,Object> session = context.getExternalContext().getSessionMap();
        
        String key = generateKey();
        
        while(session.containsKey(key)) {
            key = generateKey();
        }
        
        return key;
    }
    
    protected String generateKey() {
        StringBuilder builder = new StringBuilder();
        
        return builder.append(Constants.DYNAMIC_CONTENT_PARAM).append("_").append(UUID.randomUUID().toString()).toString();
    }
}