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
package org.primefaces.component.media;

import java.io.IOException;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.DynamicContentStreamer;
import org.primefaces.component.media.player.MediaPlayer;
import org.primefaces.component.media.player.MediaPlayerFactory;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.HTML;

public class MediaRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Media media = (Media) component;
		MediaPlayer player = resolvePlayer(facesContext, media);
		
		if(AgentUtils.isIE(facesContext))
			encodeObjectTag(facesContext, media, player);
		else
			encodeEmbedTag(facesContext, media, player);
	}
	
	private void encodeObjectTag(FacesContext facesContext, Media media, MediaPlayer player) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String src = getMediaSrc(facesContext, media);
		
		writer.startElement("object", media);
		writer.writeAttribute("classid", player.getClassId(), null);
		if(player.getCodebase() != null) {
			writer.writeAttribute("codebase", player.getCodebase(), null);
		}
		if(media.getStyleClass() != null) {
			writer.writeAttribute("class", media.getStyleClass(), null);
		}
		renderPassThruAttributes(facesContext, media, HTML.MEDIA_ATTRS);
		
		encodeParam(writer, player.getSourceParam(), src, false);
	
		for(UIComponent child : media.getChildren()) {
			if(child instanceof UIParameter) {
				UIParameter param = (UIParameter) child;
				
				encodeParam(writer, param.getName(), param.getValue(), false);
			}
		}
		
		writer.endElement("object");
	}
	
	private void encodeEmbedTag(FacesContext facesContext, Media media, MediaPlayer player) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String src = getMediaSrc(facesContext, media);
		
		writer.startElement("embed", media);
		writer.writeAttribute("pluginspage", player.getPlugingPage(), null);
		writer.writeAttribute("src", src, null);
		if(media.getStyleClass() != null) {
			writer.writeAttribute("class", media.getStyleClass(), null);
		}
		if(player.getType() != null) {
			writer.writeAttribute("type", player.getType(), null);
		}
		
		renderPassThruAttributes(facesContext, media, HTML.MEDIA_ATTRS);
		
		for(UIComponent child : media.getChildren()) {
			if(child instanceof UIParameter) {
				UIParameter param = (UIParameter) child;
				
				encodeParam(writer, param.getName(), param.getValue(), true);
			}
		}
		
		writer.endElement("embed");
	}

	private void encodeParam(ResponseWriter writer, String name, Object value, boolean asAttribute) throws IOException {
		if(value == null)
			return;
		
		if(asAttribute) {
			writer.writeAttribute(name, value, null);
		} else {
			writer.startElement("param", null);
			writer.writeAttribute("name", name, null);
			writer.writeAttribute("value", value.toString(), null);
			writer.endElement("param");
		}
	}

	private MediaPlayer resolvePlayer(FacesContext facesContext, Media media) {
		if(media.getPlayer() != null) {
			return MediaPlayerFactory.getPlayer(media.getPlayer());
		}
		else if(media.getValue() instanceof String){
			Map<String,MediaPlayer> players = MediaPlayerFactory.getPlayers();
			String[] tokens = ((String) media.getValue()).split("\\.");
			String type = tokens[tokens.length-1];
			
			for(MediaPlayer mp : players.values()) {
				if(mp.isAppropriatePlayer(type))
					return mp;
			}
		}
		
		throw new IllegalArgumentException("Cannot resolve mediaplayer for media component '" + media.getClientId(facesContext) + "', cannot play source:" + media.getValue());
	}

	private String getMediaSrc(FacesContext facesContext, Media media) {
		Object value = media.getValue();
		if(value == null)
			return "";
		
		if(value instanceof StreamedContent) {
			ValueExpression valueVE = media.getValueExpression("value");
			String veString = valueVE.getExpressionString();
			String expressionParamValue = veString.substring(2, veString.length() -1);
			
			String url = getActionURL(facesContext);
			if(url.contains("?"))
				url = url + "&";
			else
				url = url + "?";
			
			StringBuilder builder = new StringBuilder(url);
			builder.append(DynamicContentStreamer.DYNAMIC_CONTENT_PARAM).append("=").append(expressionParamValue);
			
			for(UIComponent kid : media.getChildren()) {
				if(kid instanceof UIParameter) {
					UIParameter param = (UIParameter) kid;
					
					builder.append("&").append(param.getName()).append("=").append(param.getValue());
				}
			}
			
			return builder.toString();
		}
		else {
	        return getResourceURL(facesContext, media.getValue().toString());
		}	
	}
}