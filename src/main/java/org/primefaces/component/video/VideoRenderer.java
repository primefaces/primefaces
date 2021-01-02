/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.video;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

/**
 * The HTML <video> element is used to embed sound content in documents.
 */
public class VideoRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Video media = (Video) component;
        String clientId = media.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", media);
        writer.writeAttribute("id", clientId, "id");
        renderPassThruAttributes(context, component, HTML.LABEL_ATTRS_WITHOUT_EVENTS);

        String styleClass = media.getStyleClass();
        styleClass = styleClass == null ? Video.CONTAINER_CLASS : Video.CONTAINER_CLASS + " " + styleClass;
        writer.writeAttribute("class", styleClass, null);

        if (LangUtils.isNotBlank(media.getStyle())) {
            writer.writeAttribute("style", media.getStyle(), null);
        }
        encodeVideo(context, media);
        writer.endElement("div");
    }

    public void encodeVideo(FacesContext context, Video media) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = media.getClientId(context) + "_video";
        writer.startElement("video", null);
        writer.writeAttribute("id", clientId, "id");
        renderPassThruAttributes(context, media, HTML.MEDIA_ATTRS_WITH_EVENTS);

        if (LangUtils.isNotBlank(media.getHeight())) {
            writer.writeAttribute("height", media.getHeight(), null);
        }
        if (LangUtils.isNotBlank(media.getWidth())) {
            writer.writeAttribute("width", media.getWidth(), null);
        }
        if (LangUtils.isNotBlank(media.getPreload())) {
            writer.writeAttribute("preload", media.getPreload(), null);
        }
        if (LangUtils.isNotBlank(media.getPoster())) {
            writer.writeAttribute("poster", media.getPoster(), null);
        }

        VideoType player = resolvePlayer(context, media);
        writer.startElement("source", null);
        writer.writeAttribute("src", media.resolveSource(context, media), null);
        writer.writeAttribute("type", player.getMediaType(), null);
        writer.endElement("source");

        renderChildren(context, media);
        writer.endElement("video");
    }

    protected VideoType resolvePlayer(FacesContext context, Video media) {
        if (LangUtils.isNotBlank(media.getPlayer())) {
            return VideoType.valueOf(media.getPlayer().toUpperCase());
        }
        else if (media.getValue() instanceof String) {
            String extension = FilenameUtils.getExtension((String) media.getValue());

            for (VideoType mediaType : VideoType.values()) {
                if (mediaType.getFileExtension().equalsIgnoreCase(extension)) {
                    return mediaType;
                }
            }
        }

        throw new IllegalArgumentException("Cannot resolve mediaplayer for video component '"
                + media.getClientId(context) + "', cannot play source:" + media.getValue());
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
