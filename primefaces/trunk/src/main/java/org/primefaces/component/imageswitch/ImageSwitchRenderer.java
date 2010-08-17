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
package org.primefaces.component.imageswitch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.DynamicContentStreamer;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;

public class ImageSwitchRenderer extends CoreRenderer {
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ImageSwitch imageSwitch = (ImageSwitch) component;
		
		encodeMarkup(facesContext, imageSwitch);
		encodeScript(facesContext, imageSwitch);
	}

	private void encodeScript(FacesContext facesContext, ImageSwitch imageSwitch) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = imageSwitch.getClientId(facesContext);
		String imageId = clientId.replaceAll(String.valueOf(UINamingContainer.getSeparatorChar(facesContext)), "_") + "_img";
			
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(imageSwitch.resolveWidgetVar() + " = new PrimeFaces.widget.ImageSwitch('" + imageId + "',{");
		writer.write("effect:'" + imageSwitch.getEffect() + "'");
		writer.write(",speed:" + imageSwitch.getSpeed());
		writer.write(",slideshowSpeed:" + imageSwitch.getSlideshowSpeed());
		writer.write(",slideshowAuto:" + imageSwitch.isSlideshowAuto());
		writer.write("},");
		writer.write(getImagesAsJSArray(facesContext, imageSwitch));
		writer.write(");\n");

		writer.endElement("script");
	}
	
	private List<String> getImages(FacesContext facesContext, ImageSwitch imageSwitch) {
		List<String> images = new ArrayList<String>();
		
		for(UIComponent child : imageSwitch.getChildren()) {
			if(child instanceof GraphicImage) {
				GraphicImage image = (GraphicImage) child;
				
				if(image.isRendered())
					images.add(getImageSrc(facesContext, image));
			}
		}
		
		return images;
	}
 	
	private void encodeMarkup(FacesContext facesContext, ImageSwitch imageSwitch) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = imageSwitch.getClientId(facesContext);
		String imageId = clientId.replaceAll(String.valueOf(UINamingContainer.getSeparatorChar(facesContext)), "_") + "_img";
		
		writer.startElement("div", imageSwitch);
		writer.writeAttribute("id", clientId, "id");
		if(imageSwitch.getStyle() != null) writer.writeAttribute("style", imageSwitch.getStyle(), null);
		if(imageSwitch.getStyleClass() != null) writer.writeAttribute("class", imageSwitch.getStyleClass(), null);
		
		GraphicImage firstImage = (GraphicImage) imageSwitch.getChildren().get(0);
		writer.startElement("img", null);
		writer.writeAttribute("id", imageId, null);
		writer.writeAttribute("alt", firstImage.getAlt(), null);
		writer.writeAttribute("src", getImageSrc(facesContext, firstImage), null);
		writer.endElement("img");
		
		writer.endElement("div");
	}
	
	private String getImagesAsJSArray(FacesContext facesContext, ImageSwitch imageSwitch) {
		List<String> images = getImages(facesContext, imageSwitch);
		StringBuilder array = new StringBuilder();
		array.append("[");
	
		for (Iterator<String> iterator = images.iterator(); iterator.hasNext();) {
			array.append("'");
			array.append(iterator.next());
			array.append("'");
			
			if(iterator.hasNext())
				array.append(",");
		}
		
		array.append("]");
		
		return array.toString();
	}

	@Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Render children in encodeEnd
	}

	@Override
	public boolean getRendersChildren() {
		return true;
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
			builder.append(DynamicContentStreamer.DYNAMIC_CONTENT_PARAM).append("=").append(expressionParamValue);
			
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