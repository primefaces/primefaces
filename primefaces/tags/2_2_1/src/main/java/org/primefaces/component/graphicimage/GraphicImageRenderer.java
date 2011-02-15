/*
 * Copyright 2009-2011 Prime Technology.
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
import java.util.UUID;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.DynamicContentStreamer;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class GraphicImageRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		GraphicImage image = (GraphicImage) component;
		String clientId = image.getClientId(context);
		String imageSrc = image.getValue() == null ? "" : getImageSrc(context, image);
		
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
		Object value = image.getValue();

		//Create url for dynamic or static image
		if(value instanceof StreamedContent) {
			ValueExpression valueVE = image.getValueExpression("value");
			String expressionString = valueVE.getExpressionString();
			String expressionParamValue = expressionString.substring(2, expressionString.length() - 1);

            String actionURL = getActionURL(context);
            StringBuilder builder = new StringBuilder();
            
            char queryChar = actionURL.contains("?") ? '&' : '?';

            builder.append(actionURL).append(queryChar).append(DynamicContentStreamer.DYNAMIC_CONTENT_PARAM).append("=").append(expressionParamValue);
			
			for(UIComponent kid : image.getChildren()) {
				if(kid instanceof UIParameter) {
					UIParameter param = (UIParameter) kid;
					
					builder.append("&").append(param.getName()).append("=").append(param.getValue());
				}
			}
			
			src = builder.toString();
		}
		else {
	        src = getResourceURL(context, (String) value);
		}
		
		//Add caching if needed
		if(!image.isCache()) {
			src += src.contains("?") ? "&" : "?";
			
			src = src + "primefaces_image=" + UUID.randomUUID().toString();
		}

		return src;
	}
}