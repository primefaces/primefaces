/*
 * Copyright 2009-2013 PrimeTek.
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
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Logger;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.RequestContext;

import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.StringEncrypter;

public class GraphicImageRenderer extends CoreRenderer {
    
    private final static Logger logger = Logger.getLogger(GraphicImageRenderer.class.getName());

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
                Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent.properties", "primefaces", streamedContent.getContentType());
                String resourcePath = resource.getRequestPath();
                StringEncrypter strEn = RequestContext.getCurrentInstance().getEncrypter();
                String rid = strEn.encrypt(image.getValueExpression("value").getExpressionString());
                StringBuilder builder = new StringBuilder(resourcePath);

                builder.append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(rid, "UTF-8"));

                for(UIComponent kid : image.getChildren()) {
                    if(kid instanceof UIParameter) {
                        UIParameter param = (UIParameter) kid;
                        Object paramValue = param.getValue();
                        
                        builder.append("&").append(param.getName()).append("=");
                        
                        if(paramValue != null){
                            builder.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
                        }
                    }
                }
                
                src = builder.toString();
            }

            if(src != null) {
                src += src.contains("?") ? "&" : "?";
                src += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + image.isCache();
                
                if (!image.isCache()) {
                	src += "&uid=" + UUID.randomUUID().toString();
                }
            }
            
            src = context.getExternalContext().encodeResourceURL(src);
        }

		return src;
	}
    
    protected String generateKey() {
        StringBuilder builder = new StringBuilder();
        
        return builder.append(Constants.DYNAMIC_CONTENT_PARAM).append("_").append(UUID.randomUUID().toString()).toString();
    }
}