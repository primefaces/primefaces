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
package org.primefaces.component.barcode;

import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.faces.application.Resource;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Barcode.DEFAULT_RENDERER, componentFamily = Barcode.COMPONENT_FAMILY)
public class BarcodeRenderer extends CoreRenderer<Barcode> {

    private static final String SB_BUILD = BarcodeRenderer.class.getName() + "#build";

    @Override
    public void encodeEnd(FacesContext context, Barcode component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = component.getStyleClass();
        String src = null;
        Object value = component.getValue();
        String type = component.getType();
        DynamicContentType dynamicContentType = "qr".equals(type) ? DynamicContentType.QR_CODE : DynamicContentType.BARCODE;

        if (value == null) {
            return;
        }

        Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent.properties", "primefaces", "image/png");
        String resourcePath = resource.getRequestPath();

        String sessionKey = UUID.randomUUID().toString();
        Map<String, Object> session = context.getExternalContext().getSessionMap();
        Map<String, String> barcodeMapping = (Map<String, String>) session.get(Constants.BARCODE_MAPPING);
        if (barcodeMapping == null) {
            barcodeMapping = new HashMap<>();
            session.put(Constants.BARCODE_MAPPING, barcodeMapping);
        }
        barcodeMapping.put(sessionKey, (String) value);
        StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);

        src = builder.append(resourcePath)
                .append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(sessionKey, StandardCharsets.UTF_8))
                .append("&").append(Constants.DYNAMIC_CONTENT_TYPE_PARAM).append("=").append(dynamicContentType)
                .append("&gen=").append(type)
                .append("&fmt=").append(component.getFormat())
                .append("&qrec=").append(component.getQrErrorCorrection())
                .append("&hrp=").append(component.getHrp())
                .append("&").append(Constants.DYNAMIC_CONTENT_CACHE_PARAM).append("=").append(component.isCache())
                .append("&ori=").append(component.getOrientation())
                .append("&mag=").append(component.getMagnification())
                .append("&mh=").append(component.getQuietZoneHorizontal())
                .append("&mv=").append(component.getQuietZoneVertical())
                .toString();

        writer.startElement("img", component);
        if (shouldWriteId(component)) {
            writer.writeAttribute("id", clientId, "id");
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        writer.writeAttribute("src", context.getExternalContext().encodeResourceURL(src), null);

        renderPassThruAttributes(context, component, HTML.IMG_ATTRS);

        writer.endElement("img");
    }
}
