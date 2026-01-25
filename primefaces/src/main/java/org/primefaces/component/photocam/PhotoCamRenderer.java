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
package org.primefaces.component.photocam;

import org.primefaces.event.CaptureEvent;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.PhaseId;
import jakarta.faces.render.FacesRenderer;
import jakarta.xml.bind.DatatypeConverter;

@FacesRenderer(rendererType = PhotoCam.DEFAULT_RENDERER, componentFamily = PhotoCam.COMPONENT_FAMILY)
public class PhotoCamRenderer extends CoreRenderer<PhotoCam> {

    @Override
    public void decode(FacesContext context, PhotoCam component) {
        String dataParam = component.getClientId(context) + "_data";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (params.containsKey(dataParam)) {
            String image = params.get(dataParam);
            image = image.substring(22);

            CaptureEvent event = new CaptureEvent(component, DatatypeConverter.parseBase64Binary(image), image);
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);

            component.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, PhotoCam component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, PhotoCam component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, PhotoCam component) throws IOException {

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PhotoCam", component)
                .attr("width", component.getWidth(), 320)
                .attr("height", component.getHeight(), 240)
                .attr("photoWidth", component.getPhotoWidth(), 320)
                .attr("photoHeight", component.getPhotoHeight(), 240)
                .attr("format", component.getFormat(), null)
                .attr("jpegQuality", component.getJpegQuality(), 90)
                .attr("autoStart", component.isAutoStart(), true)
                .attr("device", component.getDevice(), null)
                .callback("onCameraError", "function(errorObj)", component.getOnCameraError());

        if (component.getUpdate() != null) {
            wb.attr("update", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getUpdate()));
        }
        if (component.getProcess() != null) {
            wb.attr("process", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getProcess()));
        }

        wb.finish();
    }

}
