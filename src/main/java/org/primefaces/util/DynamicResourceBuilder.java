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
package org.primefaces.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.xml.bind.DatatypeConverter;
import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.context.RequestContext;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.StreamedContent;

public class DynamicResourceBuilder {
    
    private static final String SB_BUILD = DynamicResourceBuilder.class.getName() + "#build";
    
    public static String build(FacesContext context, Object value, UIComponent component, boolean cache, DynamicContentType type, boolean stream)
            throws UnsupportedEncodingException {
        
        String src = null;
            
        if (value == null) {
            return "";
        }
        else if (value instanceof String) {
            src = ComponentUtils.getResourceURL(context, (String) value);
        }
        else if (value instanceof StreamedContent) {
            StreamedContent streamedContent = (StreamedContent) value;

            if (stream) {
                Resource resource = context.getApplication().getResourceHandler().createResource("dynamiccontent.properties", "primefaces", streamedContent.getContentType());
                String resourcePath = resource.getRequestPath();

                Map<String,Object> session = context.getExternalContext().getSessionMap();
                Map<String,String> dynamicResourcesMapping = (Map) session.get(Constants.DYNAMIC_RESOURCES_MAPPING);
                if(dynamicResourcesMapping == null) {
                    dynamicResourcesMapping = new HashMap<String, String>();
                    session.put(Constants.DYNAMIC_RESOURCES_MAPPING, dynamicResourcesMapping);
                }
                
                String resourceKey = RequestContext.getCurrentInstance().getEncrypter().encrypt(
                        context.getViewRoot().getViewId() + component.getClientId(context));
                ValueExpression expression = ValueExpressionAnalyzer.getExpression(
                        context.getELContext(), component.getValueExpression("value"));
                
                dynamicResourcesMapping.put(resourceKey, expression.getExpressionString());
                
                StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);
                builder.append(resourcePath)
                        .append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(resourceKey, "UTF-8"))
                        .append("&").append(Constants.DYNAMIC_CONTENT_TYPE_PARAM).append("=").append(type.toString());

                for (UIComponent kid : component.getChildren()) {
                    if (kid instanceof UIParameter) {
                        UIParameter param = (UIParameter) kid;
                        if (!param.isDisable()) {
                            Object paramValue = param.getValue();

                            builder.append("&").append(param.getName()).append("=");

                            if (paramValue != null) {
                                builder.append(URLEncoder.encode(paramValue.toString(), "UTF-8"));
                            }
                        }
                    }
                }

                src = builder.toString();
            }
            else {
                byte[] bytes = toByteArray(streamedContent.getStream());
                String base64 = DatatypeConverter.printBase64Binary(bytes);
                return "data:" + streamedContent.getContentType() + ";base64," + base64;
            }
        }

        if (src != null) {
            src += src.contains("?") ? "&" : "?";
            src += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + cache;

            if (!cache) {
                src += "&uid=" + UUID.randomUUID().toString();
            }
        }

        return context.getExternalContext().encodeResourceURL(src);
    }
    
    public static byte[] toByteArray(InputStream stream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = stream.read(data, 0, data.length)) != -1) {
              buffer.write(data, 0, nRead);
            }

            buffer.flush();

            return buffer.toByteArray();
        }
        catch (Exception e) {
            throw new FacesException("Could not read InputStream to byte[]", e);
        }
    }
}
