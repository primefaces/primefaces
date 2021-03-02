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
package org.primefaces.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import org.primefaces.application.resource.DynamicContentType;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.model.StreamedContent;

public class DynamicContentSrcBuilder {

    private static final String SB_BUILD_STREAMING = DynamicContentSrcBuilder.class.getName() + "#buildStreaming";

    private DynamicContentSrcBuilder() {
    }

    public static String build(FacesContext context, UIComponent component, ValueExpression ve, Lazy<Object> value, boolean cache, boolean stream) {

        // no ValueExpression and no literal defined... skip
        if (ve == null && value.get() == null) {
            return "";
        }

        Class<?> type = null;

        // try getExpectedType first, likely returns Object.class
        if (ve != null) {
            type = ve.getExpectedType();
        }

        // fallback to getType
        if ((type == null || type == Object.class) && ve != null) {
            try {
                type = ve.getType(context.getELContext());
            }
            catch (ELException e) {
                // fails if the ValueExpression is actually a MethodExpression, see #7058
            }
        }

        // fallback to the type of the value instance
        if ((type == null || type == Object.class) && value.get() != null) {
            type = value.get().getClass();
        }

        // skip null type
        if (type == null) {
            return "";
        }

        if (String.class.isAssignableFrom(type)) {
            String src = ResourceUtils.getResourceURL(context, (String) value.get());
            return encodeResourceURL(context, src, cache);
        }
        else if (StreamedContent.class.isAssignableFrom(type)) {
            if (stream) {
                ValueExpression extractedVE = ValueExpressionAnalyzer.getExpression(context.getELContext(), ve);
                return buildStreaming(context, component, extractedVE, cache);
            }
            else {
                StreamedContent streamedContent = (StreamedContent) value.get();
                return buildBase64(context, streamedContent.getStream(), streamedContent.getContentType());
            }
        }
        else if (byte[].class.isAssignableFrom(type)) {
            if (stream) {
                ValueExpression extractedVE = ValueExpressionAnalyzer.getExpression(context.getELContext(), ve);
                return buildStreaming(context, component, extractedVE, cache);
            }
            else {
                return buildBase64(context, (byte[]) value.get());
            }
        }
        else if (InputStream.class.isAssignableFrom(type)) {
            if (stream) {
                ValueExpression extractedVE = ValueExpressionAnalyzer.getExpression(context.getELContext(), ve);
                return buildStreaming(context, component, extractedVE, cache);
            }
            else {
                return buildBase64(context, (InputStream) value.get());
            }
        }

        return "";
    }

    public static String buildStreaming(FacesContext context, ValueExpression valueExpression, boolean cache) {
        return buildStreaming(context, null, valueExpression, cache);
    }

    public static String buildStreaming(FacesContext context, UIComponent component, ValueExpression valueExpression, boolean cache) {

        // just a dummy file for streaming
        // JSF will also append the suffix (e.g. -> dynamiccontent.properties.xhtml)
        // the real content type will be written to the response by the StreamedContentHandler
        Resource resource = context.getApplication().getResourceHandler().createResource(
                "dynamiccontent.properties", "primefaces", "text/plain");
        String resourcePath = resource.getRequestPath();

        Map<String, Object> session = context.getExternalContext().getSessionMap();
        Map<String, String> dynamicResourcesMapping = (Map) session.get(Constants.DYNAMIC_RESOURCES_MAPPING);
        if (dynamicResourcesMapping == null) {
            dynamicResourcesMapping = new LimitedSizeHashMap<>(200);
            session.put(Constants.DYNAMIC_RESOURCES_MAPPING, dynamicResourcesMapping);
        }

        String expressionString = valueExpression.getExpressionString();
        String resourceKey = md5(expressionString);

        dynamicResourcesMapping.put(resourceKey, expressionString);

        try {
            StringBuilder builder = SharedStringBuilder.get(context, SB_BUILD_STREAMING);
            builder.append(resourcePath)
                    .append("&").append(Constants.DYNAMIC_CONTENT_PARAM).append("=").append(URLEncoder.encode(resourceKey, "UTF-8"))
                    .append("&").append(Constants.DYNAMIC_CONTENT_TYPE_PARAM).append("=").append(DynamicContentType.STREAMED_CONTENT.toString());

            if (component != null) {
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
            }

            return encodeResourceURL(context, builder.toString(), cache);
        }
        catch (UnsupportedEncodingException ex) {
            throw new FacesException(ex);
        }
    }

    public static String buildBase64(FacesContext context, InputStream is) {
        return buildBase64(context, toByteArray(is), null);
    }

    public static String buildBase64(FacesContext context, InputStream is, String contentType) {
        return buildBase64(context, toByteArray(is), contentType);
    }

    public static String buildBase64(FacesContext context, byte[] bytes) {
        return buildBase64(context, bytes, null);
    }

    public static String buildBase64(FacesContext context, byte[] bytes, String contentType) {
        String base64 = Base64.getEncoder().withoutPadding().encodeToString(bytes);
        if (contentType == null) {
            // try to guess content type from magic numbers
            if (base64.startsWith("R0lGOD")) {
                contentType = "image/gif";
            }
            else if (base64.startsWith("iVBORw")) {
                contentType = "image/png";
            }
            else if (base64.startsWith("/9j/")) {
                contentType = "image/jpeg";
            }
        }
        return "data:" + contentType + ";base64," + base64;
    }

    protected static String encodeResourceURL(FacesContext context, String src, boolean cache) {
        return context.getExternalContext().encodeResourceURL(ResourceUtils.appendCacheBuster(src, cache));
    }

    protected static byte[] toByteArray(InputStream is) {
        try {
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                return buffer.toByteArray();
            }
        }
        catch (Exception e) {
            throw new FacesException("Could not read InputStream to byte[]", e);
        }
    }

    protected static String md5(String input) {

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
