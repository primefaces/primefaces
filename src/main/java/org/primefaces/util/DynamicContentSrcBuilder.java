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
package org.primefaces.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.StreamedContent;

public class DynamicContentSrcBuilder {

    private static final String SB_BUILD = DynamicContentSrcBuilder.class.getName() + "#build";

    private DynamicContentSrcBuilder() {
    }

    public static String build(FacesContext context, Object value, UIComponent component, boolean cache, DynamicContentType type, boolean stream)
            throws UnsupportedEncodingException {

        String src = null;

        if (value == null) {
            return "";
        }
        else if (value instanceof String) {
            src = ResourceUtils.getResourceURL(context, (String) value);
        }
        else if (value instanceof StreamedContent) {
            StreamedContent streamedContent = (StreamedContent) value;

            if (stream) {
                Resource resource = context.getApplication().getResourceHandler().createResource(
                        "dynamiccontent.properties", "primefaces", streamedContent.getContentType());
                String resourcePath = resource.getRequestPath();

                Map<String, Object> session = context.getExternalContext().getSessionMap();
                Map<String, String> dynamicResourcesMapping = (Map) session.get(Constants.DYNAMIC_RESOURCES_MAPPING);
                if (dynamicResourcesMapping == null) {
                    dynamicResourcesMapping = new LimitedSizeHashMap<>(200);
                    session.put(Constants.DYNAMIC_RESOURCES_MAPPING, dynamicResourcesMapping);
                }

                ValueExpression expression = ValueExpressionAnalyzer.getExpression(
                        context.getELContext(), component.getValueExpression("value"));

                String expressionString = expression.getExpressionString();
                String resourceKey = md5(expressionString);

                dynamicResourcesMapping.put(resourceKey, expressionString);

                StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD);
                builder.append(resourcePath)
                        .append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(resourceKey, "UTF-8"))
                        .append("&").append(Constants.DYNAMIC_CONTENT_TYPE_PARAM).append("=").append(type.toString());

                for (int i = 0; i < component.getChildCount(); i++) {
                    UIComponent child = component.getChildren().get(i);
                    if (child instanceof UIParameter) {
                        UIParameter param = (UIParameter) child;
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

    private static String md5(String input) {

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException ex) {
            throw new FacesException(ex);
        }

        byte[] bytes = messageDigest.digest(input.getBytes());

        return new BigInteger(1, bytes).toString(16);
    }
}
