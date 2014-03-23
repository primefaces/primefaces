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
package org.primefaces.component.media;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;
import javax.el.ValueExpression;
import javax.faces.application.Resource;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.media.player.MediaPlayer;
import org.primefaces.component.media.player.MediaPlayerFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.StringEncrypter;

public class MediaRenderer extends CoreRenderer {

    private static final String SB_GET_MEDIA_SRC = MediaRenderer.class.getName() + "#getMediaSrc";
    private static final String SB_GENERATE_KEY = MediaRenderer.class.getName() + "#generateKey";
    
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Media media = (Media) component;
		MediaPlayer player = resolvePlayer(context, media);
		ResponseWriter writer = context.getResponseWriter();
		String src;
        try {
            src = getMediaSrc(context, media);
        } catch (Exception ex) {
           throw new IOException(ex);
        }
        boolean isIE = AgentUtils.isIE(context);
        String sourceParam = player.getSourceParam();
		
		writer.startElement("object", media);
        writer.writeAttribute("type", player.getType(), null);
        writer.writeAttribute("data", src, null);
        
        if(isIE) {
            encodeIEConfig(writer, player);
        }
		
		if(media.getStyleClass() != null) {
			writer.writeAttribute("class", media.getStyleClass(), null);
		}
        
		renderPassThruAttributes(context, media, HTML.MEDIA_ATTRS);
		
        if(sourceParam != null) {
            encodeParam(writer, player.getSourceParam(), src, false);
        }
        
        for(UIComponent child : media.getChildren()) {
            if(child instanceof UIParameter) {
                UIParameter param = (UIParameter) child;

                encodeParam(writer, param.getName(), param.getValue(), false);
            }
        }
        
        renderChildren(context, media);
        
		
		writer.endElement("object");
	}
    
    protected void encodeIEConfig(ResponseWriter writer, MediaPlayer player) throws IOException {
        writer.writeAttribute("classid", player.getClassId(), null);
        
		if(player.getCodebase() != null) {
			writer.writeAttribute("codebase", player.getCodebase(), null);
		}
    }
     
    protected void encodeParam(ResponseWriter writer, String name, Object value, boolean asAttribute) throws IOException {
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

	protected MediaPlayer resolvePlayer(FacesContext context, Media media) {
		if(media.getPlayer() != null) {
			return MediaPlayerFactory.getPlayer(media.getPlayer());
		}
		else if(media.getValue() instanceof String) {
			Map<String,MediaPlayer> players = MediaPlayerFactory.getPlayers();
			String[] tokens = ((String) media.getValue()).split("\\.");
			String type = tokens[tokens.length-1];
			
			for(MediaPlayer mp : players.values()) {
                for(String supportedType : mp.getSupportedTypes()) {
                    if(supportedType.equalsIgnoreCase(type)) {
                        return mp;
                    }
                }
			}
        } 
		
		throw new IllegalArgumentException("Cannot resolve mediaplayer for media component '" + media.getClientId(context) + "', cannot play source:" + media.getValue());
	}

	protected String getMediaSrc(FacesContext context, Media media) throws Exception {
		String src;
		Object value = media.getValue();
        
        if(value == null) {
            src = null;
        }
        else {
            if(value instanceof StreamedContent) {
                StreamedContent streamedContent = (StreamedContent) value;
                Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent.properties", "primefaces", streamedContent.getContentType());
                String resourcePath = resource.getRequestPath();
                StringEncrypter encrypter = RequestContext.getCurrentInstance().getEncrypter();
                
                ValueExpression expression = ValueExpressionAnalyzer.getExpression(context.getELContext(), media.getValueExpression("value"));
                String rid = encrypter.encrypt(expression.getExpressionString());

                StringBuilder builder = SharedStringBuilder.get(context, SB_GET_MEDIA_SRC);
                        
                builder.append(resourcePath).append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(rid,"UTF-8"));

                for(UIComponent kid : media.getChildren()) {
                    if(kid instanceof UIParameter) {
                        UIParameter param = (UIParameter) kid;
                        Object paramValue = param.getValue();
                        
                        builder.append("&").append(param.getName()).append("=");
                        
                        if(paramValue != null){
                            builder.append(URLEncoder.encode(param.getValue().toString(), "UTF-8"));
                        }
                    }
                }

                src = context.getExternalContext().encodeResourceURL(builder.toString());
            }
            else {
                src = getResourceURL(context, (String) value);
                
                if(src.startsWith("/")) {
                    src = context.getExternalContext().encodeResourceURL(src);
                }
            }
            
            //cache
            src += src.contains("?") ? "&" : "?";
            src += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + media.isCache();

            if (!media.isCache()) {
            	src += "&uid=" + UUID.randomUUID().toString();
            }
        }

		return src;
	}
    

    protected String generateKey() {
        StringBuilder builder = SharedStringBuilder.get(SB_GENERATE_KEY);
        
        return builder.append(Constants.DYNAMIC_CONTENT_PARAM).append("_").append(UUID.randomUUID().toString()).toString();
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}