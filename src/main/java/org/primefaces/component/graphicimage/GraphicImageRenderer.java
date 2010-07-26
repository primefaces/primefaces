/*
 * Copyright 2009 Prime Technology.
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

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.lifecycle.DynamicImageStreamer;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class GraphicImageRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		GraphicImage image = (GraphicImage) component;
		String clientId = image.getClientId(facesContext);
		
		writer.startElement("img", image);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("src", getImageSrc(facesContext, image), null);
		
		encodePassThruAttributes(facesContext, HTML.IMG_ATTRS, image);
		
		if(image.getStyleClass() != null)
			writer.writeAttribute("class", image.getStyleClass(), "styleClass");
		
		writer.endElement("img");
	}
	
	private void encodePassThruAttributes(FacesContext facesContext, String[] attributes, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		for(String attribute : attributes) {
			Object value = component.getAttributes().get(attribute);
			
			if(shouldRenderAttribute(value))
				writer.writeAttribute(attribute, value.toString(), attribute);
		}
	}
	
	private String getImageSrc(FacesContext facesContext, GraphicImage image) {
		Object value = image.getValue();
		if(value == null)
			return "";
		
		if(value instanceof StreamedContent) {
			ValueExpression valueVE = image.getValueExpression("value");
			String veString = valueVE.getExpressionString();
			String expressionParamValue = veString.substring(2, veString.length() -1);
			
			String url = getActionURL(facesContext);
			if(url.contains("?"))
				url = url + "&";
			else
				url = url + "?";
			
			StringBuilder builder = new StringBuilder(url);
			builder.append(DynamicImageStreamer.DYNAMICIMAGE_PARAM).append("=").append(expressionParamValue);
			
			for(UIComponent kid : image.getChildren()) {
				if(kid instanceof UIParameter) {
					UIParameter param = (UIParameter) kid;
					
					builder.append("&").append(param.getName()).append("=").append(param.getValue());
				}
			}
			
			return builder.toString();
		}
		else {
	        return getResourceURL(facesContext, value.toString());
		}	
	}
}

