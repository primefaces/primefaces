/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.media.player.MediaPlayer;
import org.primefaces.component.media.player.MediaPlayerFactory;
import org.primefaces.component.media.player.PDFPlayer;
import org.primefaces.model.StreamedContent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIParameter;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Media.DEFAULT_RENDERER, componentFamily = Media.COMPONENT_FAMILY)
public class MediaRenderer extends CoreRenderer<Media> {

    @Override
    public void encodeEnd(FacesContext context, Media component) throws IOException {
        MediaPlayer player = resolvePlayer(context, component);
        ResponseWriter writer = context.getResponseWriter();
        String src;
        try {
            src = component.resolveSource(context);
        }
        catch (Exception ex) {
            throw new IOException(ex);
        }

        String sourceParam = player.getSourceParam();
        String type = player.getType();
        if (PDFPlayer.MIME_TYPE.equals(type)) {
            String view = component.getView();
            String zoom = component.getZoom();

            if (view != null) {
                src = src + "#view=" + view;
            }

            if (zoom != null) {
                src += (view != null) ? "&zoom=" + zoom : "#zoom=" + zoom;
            }
        }

        writer.startElement("object", component);
        writer.writeAttribute("type", player.getType(), null);
        writer.writeAttribute("data", src, null);

        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), null);
        }

        renderPassThruAttributes(context, component, HTML.MEDIA_ATTRS);

        if (sourceParam != null) {
            encodeParam(writer, player.getSourceParam(), src, false);
        }

        if (component.getChildCount() > 0) {
            for (UIComponent child : component.getChildren()) {
                if (child instanceof UIParameter) {
                    UIParameter param = (UIParameter) child;

                    encodeParam(writer, param.getName(), param.getValue(), false);
                }
            }
        }

        renderChildren(context, component);

        writer.endElement("object");
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

        Object value = media.getValue();

        // resolve by content-type
        if (value instanceof StreamedContent) {
            Map<String, MediaPlayer> players = MediaPlayerFactory.getPlayers();
            for (MediaPlayer mp : players.values()) {
                if (Objects.equals(mp.getType(), ((StreamedContent) value).getContentType())) {
                    return mp;
                }
            }
        }

        // resolve by filename
        String filename = null;
        if (value instanceof String) {
            filename = (String) value;
        }
        else if (value instanceof StreamedContent) {
            filename = ((StreamedContent) value).getName();
        }

        if (LangUtils.isNotBlank(filename)) {
            Map<String, MediaPlayer> players = MediaPlayerFactory.getPlayers();
            String[] tokens = filename.split("\\.");
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

    @Override
    public void encodeChildren(FacesContext context, Media component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
