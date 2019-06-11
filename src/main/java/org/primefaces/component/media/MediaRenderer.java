/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.media;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.component.media.player.MediaPlayer;
import org.primefaces.component.media.player.MediaPlayerFactory;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.DynamicContentSrcBuilder;
import org.primefaces.util.HTML;

public class MediaRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Media media = (Media) component;
        MediaPlayer player = resolvePlayer(context, media);
        ResponseWriter writer = context.getResponseWriter();
        String src;
        try {
            src = getMediaSrc(context, media);
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }
        boolean isIE = AgentUtils.isIE(context);
        String sourceParam = player.getSourceParam();

        Object value = media.getValue();
        if (value instanceof StreamedContent && player.getType().equals("application/pdf")) {
            StreamedContent streamedContent = (StreamedContent) value;
            if (streamedContent.getName() != null) {
                int index = src.indexOf('?');
                src = src.substring(0, index) + ";/" + streamedContent.getName() + "" + src.substring(index, src.length());
            }
        }

        String type = player.getType();
        if (type != null && type.equals("application/pdf")) {
            String view = media.getView();
            String zoom = media.getZoom();

            if (view != null) {
                src = src + "#view=" + view;
            }

            if (zoom != null) {
                src += (view != null) ? "&zoom=" + zoom : "#zoom=" + zoom;
            }
        }

        writer.startElement("object", media);
        writer.writeAttribute("type", player.getType(), null);
        writer.writeAttribute("data", src, null);

        if (isIE) {
            encodeIEConfig(writer, player);
        }

        if (media.getStyleClass() != null) {
            writer.writeAttribute("class", media.getStyleClass(), null);
        }

        renderPassThruAttributes(context, media, HTML.MEDIA_ATTRS);

        if (sourceParam != null) {
            encodeParam(writer, player.getSourceParam(), src, false);
        }

        for (UIComponent child : media.getChildren()) {
            if (child instanceof UIParameter) {
                UIParameter param = (UIParameter) child;

                encodeParam(writer, param.getName(), param.getValue(), false);
            }
        }

        renderChildren(context, media);

        writer.endElement("object");
    }

    protected void encodeIEConfig(ResponseWriter writer, MediaPlayer player) throws IOException {
        writer.writeAttribute("classid", player.getClassId(), null);

        if (player.getCodebase() != null) {
            writer.writeAttribute("codebase", player.getCodebase(), null);
        }
    }

    protected void encodeParam(ResponseWriter writer, String name, Object value, boolean asAttribute) throws IOException {
        if (value == null) {
            return;
        }

        if (asAttribute) {
            writer.writeAttribute(name, value, null);
        }
        else {
            writer.startElement("param", null);
            writer.writeAttribute("name", name, null);
            writer.writeAttribute("value", value.toString(), null);
            writer.endElement("param");
        }
    }

    protected MediaPlayer resolvePlayer(FacesContext context, Media media) {
        if (media.getPlayer() != null) {
            return MediaPlayerFactory.getPlayer(media.getPlayer());
        }
        else if (media.getValue() instanceof String) {
            Map<String, MediaPlayer> players = MediaPlayerFactory.getPlayers();
            String[] tokens = ((String) media.getValue()).split("\\.");
            String type = tokens[tokens.length - 1];

            for (MediaPlayer mp : players.values()) {
                for (String supportedType : mp.getSupportedTypes()) {
                    if (supportedType.equalsIgnoreCase(type)) {
                        return mp;
                    }
                }
            }
        }

        throw new IllegalArgumentException("Cannot resolve mediaplayer for media component '"
                + media.getClientId(context) + "', cannot play source:" + media.getValue());
    }

    protected String getMediaSrc(FacesContext context, Media media) throws Exception {
        return DynamicContentSrcBuilder.build(context, media.getValue(), media, media.isCache(), DynamicContentType.STREAMED_CONTENT, true);
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
