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
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.StringEncrypter;

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
            StringEncrypter encrypter = RequestContext.getCurrentInstance().getEncrypter();
            String rid = encrypter.encrypt((String) value);
            StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);

            src = builder.append(resourcePath).append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(rid, "UTF-8"))
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
        
        writer.writeAttribute("src", src, null);

        renderPassThruAttributes(context, barcode, HTML.IMG_ATTRS);
        
		writer.endElement("img");
	}
}
