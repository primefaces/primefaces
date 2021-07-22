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
package org.primefaces.application.resource;

import java.io.ByteArrayInputStream;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.ProjectStage;

public class StreamedContentHandler extends BaseDynamicContentHandler {

    private static final Logger LOGGER = Logger.getLogger(StreamedContentHandler.class.getName());

    @Override
    public void handle(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String library = params.get("ln");
        String resourceKey = params.get(Constants.DYNAMIC_CONTENT_PARAM);

        if (resourceKey != null && library != null && library.equals(Constants.LIBRARY)) {
            boolean cache = Boolean.parseBoolean(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));

            try {
                ExternalContext externalContext = context.getExternalContext();
                Map<String, Object> session = externalContext.getSessionMap();
                Map<String, String> dynamicResourcesMapping = (Map) session.get(Constants.DYNAMIC_RESOURCES_MAPPING);

                if (dynamicResourcesMapping != null) {
                    String dynamicContentEL = dynamicResourcesMapping.get(resourceKey);

                    if (dynamicContentEL != null) {
                        ELContext eLContext = context.getELContext();
                        ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(
                                context.getELContext(), dynamicContentEL, Object.class);
                        Object value = ve.getValue(eLContext);

                        if (value == null) {
                            if (context.isProjectStage(ProjectStage.Development)) {
                                LOGGER.log(Level.WARNING,
                                        "Dynamic content resolved to null - skip streaming resource for ValueExpression: {0}",
                                        dynamicContentEL);
                            }
                            sendNotFound(externalContext);
                            return;
                        }

                        if (value instanceof StreamedContent) {
                            StreamedContent streamedContent = (StreamedContent) value;
                            if (streamedContent.getWriter() != null) {
                                setResponseHeaders(streamedContent, externalContext);
                                stream(externalContext, streamedContent.getWriter(), cache);
                            }
                            else {
                                try (InputStream inputStream = streamedContent.getStream()) {
                                    if (inputStream == null) {
                                        if (context.isProjectStage(ProjectStage.Development)) {
                                            LOGGER.log(Level.WARNING,
                                                    "Stream of StreamedContent resolved to null - skip streaming resource for ValueExpression: {0}",
                                                    dynamicContentEL);
                                        }
                                        sendNotFound(externalContext);
                                        return;
                                    }
                                    setResponseHeaders(streamedContent, externalContext);
                                    stream(externalContext, inputStream, cache);
                                }
                            }
                        }
                        else if (value instanceof InputStream) {
                            try (InputStream inputStream = (InputStream) value) {
                                stream(externalContext, inputStream, cache);
                            }
                        }
                        else if (value instanceof byte[]) {
                            try (InputStream inputStream = new ByteArrayInputStream((byte[]) value)) {
                                stream(externalContext, inputStream, cache);
                            }
                        }
                    }
                }

                externalContext.responseFlushBuffer();
                context.responseComplete();
            }
            catch (Exception e) {
                throw new IOException("Error in streaming dynamic resource", e);
            }
        }
    }

    protected void setResponseHeaders(StreamedContent streamedContent, ExternalContext externalContext) {
        if (streamedContent.getContentType() != null) {
            externalContext.setResponseContentType(streamedContent.getContentType());
        }
        if (streamedContent.getContentLength() != null) {
            externalContext.setResponseContentLength(streamedContent.getContentLength());
        }
        if (streamedContent.getContentEncoding() != null) {
            externalContext.setResponseHeader("Content-Encoding", streamedContent.getContentEncoding());
        }
        if (streamedContent.getName() != null) {
            externalContext.setResponseHeader("Content-Disposition", "inline;filename=\"" + streamedContent.getName() + "\"");
        }
    }

    protected void stream(ExternalContext externalContext, InputStream inputStream, boolean cache) throws IOException {
        externalContext.setResponseStatus(HttpServletResponse.SC_OK);

        handleCache(externalContext, cache);

        byte[] buffer = new byte[2048];

        int length;
        while ((length = (inputStream.read(buffer))) >= 0) {
            externalContext.getResponseOutputStream().write(buffer, 0, length);
        }
    }

    protected void stream(ExternalContext externalContext, Consumer<OutputStream> writer, boolean cache) throws IOException {
        externalContext.setResponseStatus(HttpServletResponse.SC_OK);
        handleCache(externalContext, cache);
        writer.accept(externalContext.getResponseOutputStream());
    }

    protected void sendNotFound(ExternalContext externalContext) throws IOException {
        if (externalContext.getRequest() instanceof HttpServletRequest) {
            externalContext.responseSendError(HttpServletResponse.SC_NOT_FOUND,
                    ((HttpServletRequest) externalContext.getRequest()).getRequestURI());
        }
        else {
            externalContext.responseSendError(HttpServletResponse.SC_NOT_FOUND, null);
        }
    }
}
