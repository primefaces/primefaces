/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

import org.apache.commons.io.FilenameUtils;

/**
 * The HTML <video> element is used to embed sound content in documents.
 */
@FacesRenderer(rendererType = Video.DEFAULT_RENDERER, componentFamily = Video.COMPONENT_FAMILY)
public class VideoRenderer extends CoreRenderer<Video> {

    @Override
    public void encodeEnd(FacesContext context, Video component) throws IOException {
        String clientId = component.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        renderPassThruAttributes(context, component, HTML.LABEL_ATTRS_WITHOUT_EVENTS);

        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? Video.CONTAINER_CLASS : Video.CONTAINER_CLASS + " " + styleClass;
        writer.writeAttribute("class", styleClass, null);

        if (LangUtils.isNotBlank(component.getStyle())) {
            writer.writeAttribute("style", component.getStyle(), null);
        }
        encodeVideo(context, component);
        writer.endElement("div");
    }

    public void encodeVideo(FacesContext context, Video component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context) + "_video";
        writer.startElement("video", null);
        writer.writeAttribute("id", clientId, "id");
        renderPassThruAttributes(context, component, HTML.MEDIA_ATTRS_WITH_EVENTS);

        VideoType player = resolvePlayer(context, component);
        writer.startElement("source", null);
        writer.writeAttribute("src", component.resolveSource(context, component), null);
        writer.writeAttribute("type", player.getMediaType(), null);
        writer.endElement("source");

        renderChildren(context, component);
        writer.endElement("video");
    }

    protected VideoType resolvePlayer(FacesContext context, Video component) {
        if (LangUtils.isNotBlank(component.getPlayer())) {
            return VideoType.valueOf(component.getPlayer().toUpperCase());
        }
        else if (component.getValue() instanceof String) {
            String extension = FilenameUtils.getExtension((String) component.getValue());

            for (VideoType mediaType : VideoType.values()) {
                if (mediaType.getFileExtension().equalsIgnoreCase(extension)) {
                    return mediaType;
                }
            }
        }

        throw new IllegalArgumentException("Cannot resolve mediaplayer for video component '"
                + component.getClientId(context) + "', cannot play source:" + component.getValue());
    }

    @Override
    public void encodeChildren(FacesContext context, Video component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

}
