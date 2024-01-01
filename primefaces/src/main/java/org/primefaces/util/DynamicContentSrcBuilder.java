/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
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
        Class<?> type = ELUtils.getType(context, ve, value);

        // skip null type
        if (type == null) {
            return "";
        }

        if (String.class.isAssignableFrom(type)) {
            String src = ResourceUtils.getResourceURL(context, (String) value.get());
            return ResourceUtils.encodeResourceURL(context, src, cache);
        }
        else if (StreamedContent.class.isAssignableFrom(type)) {
            if (stream) {
                ValueExpression extractedVE = ValueExpressionAnalyzer.getExpression(context.getELContext(), ve);
                return buildStreaming(context, component, extractedVE, cache);
            }
            else {
                StreamedContent streamedContent = (StreamedContent) value.get();
                if (streamedContent.getWriter() != null) {
                    return ResourceUtils.toBase64(context, streamedContent.getWriter(), streamedContent.getContentType());
                }
                return ResourceUtils.toBase64(context, streamedContent.getStream().get(), streamedContent.getContentType());
            }
        }
        else if (byte[].class.isAssignableFrom(type)) {
            if (stream) {
                ValueExpression extractedVE = ValueExpressionAnalyzer.getExpression(context.getELContext(), ve);
                return buildStreaming(context, component, extractedVE, cache);
            }
            else {
                return ResourceUtils.toBase64(context, (byte[]) value.get());
            }
        }
        else if (InputStream.class.isAssignableFrom(type)) {
            if (stream) {
                ValueExpression extractedVE = ValueExpressionAnalyzer.getExpression(context.getELContext(), ve);
                return buildStreaming(context, component, extractedVE, cache);
            }
            else {
                return ResourceUtils.toBase64(context, (InputStream) value.get());
            }
        }

        return "";
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

            return ResourceUtils.encodeResourceURL(context, builder.toString(), cache);
        }
        catch (UnsupportedEncodingException ex) {
            throw new FacesException(ex);
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
