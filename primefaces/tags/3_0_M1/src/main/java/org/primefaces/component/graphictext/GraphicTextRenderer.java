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
package org.primefaces.component.graphictext;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.DynamicContentStreamer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;

public class GraphicTextRenderer extends CoreRenderer {

	public static final String KEY_FONTNAME = "fontname";
	public static final String KEY_FONTSTYLE = "fontstyle";
	public static final String KEY_FONTSIZE = "fontsize";
	public static final String KEY_GRAPHICTEXT = "graphictext";

	public void encodeEnd(FacesContext facesContext, UIComponent component)
			throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		GraphicText graphicText = (GraphicText) component;
		String clientId = graphicText.getClientId(facesContext);

		writer.startElement("img", graphicText);
		writer.writeAttribute("id", clientId, "id");
		writer.writeAttribute("src", constructDynaImgUrl(facesContext, graphicText), null);
		if(graphicText.getAlt() == null) writer.writeAttribute("alt", "", null);	//xhtml

		renderPassThruAttributes(facesContext, graphicText, HTML.IMG_ATTRS);

		if (graphicText.getStyleClass() != null)
			writer.writeAttribute("class", graphicText.getStyleClass(), "styleClass");

		writer.endElement("img");
	}

	private String constructDynaImgUrl(FacesContext facesContext, GraphicText graphicText) {
		Object value = graphicText.getValue();
		
		if(value instanceof String) {
			StringBuilder builder = new StringBuilder();
			builder.append("?").append(DynamicContentStreamer.GRAPHIC_TEXT_PARAM).append("=").append("true").
				append("&").append(KEY_FONTNAME).append("=").append(graphicText.getFontName()).
				append("&").append(KEY_FONTSTYLE).append("=").append(graphicText.getFontStyle()).
				append("&").append(KEY_FONTSIZE).append("=").append(graphicText.getFontSize()).
				append("&").append(KEY_GRAPHICTEXT).append("=").append(value);
			
			return builder.toString();
		}
		return "";
		
	}
}