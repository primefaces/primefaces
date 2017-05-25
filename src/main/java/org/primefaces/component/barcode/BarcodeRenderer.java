/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.barcode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;

public class BarcodeRenderer extends CoreRenderer {
    
    private static final String SB_BUILD = BarcodeRenderer.class.getName() + "#build";
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Barcode barcode = (Barcode) component;
		String clientId = barcode.getClientId(context);
        String styleClass = barcode.getStyleClass();
		String src = null;
        Object value = barcode.getValue();
        String type = barcode.getType();
        DynamicContentType dynamicContentType = type.equals("qr") ? DynamicContentType.QR_CODE : DynamicContentType.BARCODE;
        
        if(value == null) {
            return;
        }
        
        try {
            Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent.properties", "primefaces", "image/png");
            String resourcePath = resource.getRequestPath();
  
            String sessionKey = UUID.randomUUID().toString();
            Map<String,Object> session = context.getExternalContext().getSessionMap();
            Map<String,String> barcodeMapping = (Map) session.get(Constants.BARCODE_MAPPING);
            if(barcodeMapping == null) {
                barcodeMapping = new HashMap<String, String>();
                session.put(Constants.BARCODE_MAPPING, barcodeMapping);
            }
            barcodeMapping.put(sessionKey, (String) value);
            StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);

            src = builder.append(resourcePath).append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(sessionKey, "UTF-8"))
                    .append("&").append(Constants.DYNAMIC_CONTENT_TYPE_PARAM).append("=").append(dynamicContentType.toString())
                    .append("&gen=").append(type)
                    .append("&fmt=").append(barcode.getFormat())
                    .append("&").append(Constants.DYNAMIC_CONTENT_CACHE_PARAM).append("=").append(barcode.isCache())
                    .append("&ori=").append(barcode.getOrientation())
                    .toString();
        } 
        catch (UnsupportedEncodingException ex) {
            throw new IOException(ex);
        }
        
		writer.startElement("img", barcode);
        if(shouldWriteId(component)) writer.writeAttribute("id", clientId, "id");
        if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");
        
        writer.writeAttribute("src", context.getExternalContext().encodeResourceURL(src), null);

        renderPassThruAttributes(context, barcode, HTML.IMG_ATTRS);
        
		writer.endElement("img");
	}
}
