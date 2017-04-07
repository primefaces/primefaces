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
package org.primefaces.application.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;

public class StreamedContentHandler extends BaseDynamicContentHandler {

    private final static Logger LOG = Logger.getLogger(StreamedContentHandler.class.getName());
    
    public void handle(FacesContext context) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String library = params.get("ln");
        String resourceKey = (String) params.get(Constants.DYNAMIC_CONTENT_PARAM);

        if (resourceKey != null && library != null && library.equals(Constants.LIBRARY)) {
            StreamedContent streamedContent = null;
            boolean cache = Boolean.valueOf(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));

            try {
                ExternalContext externalContext = context.getExternalContext();
                Map<String,Object> session = externalContext.getSessionMap();
                Map<String,String> dynamicResourcesMapping = (Map) session.get(Constants.DYNAMIC_RESOURCES_MAPPING);

                if (dynamicResourcesMapping != null) {
                    String dynamicContentEL = dynamicResourcesMapping.get(resourceKey);

                    if (dynamicContentEL != null) {
                        ELContext eLContext = context.getELContext();
                        ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), dynamicContentEL, StreamedContent.class);
                        streamedContent = (StreamedContent) ve.getValue(eLContext);

                        if (streamedContent == null || streamedContent.getStream() == null) {
                            if (externalContext.getRequest() instanceof HttpServletRequest) {
                                externalContext.responseSendError(HttpServletResponse.SC_NOT_FOUND,
                                    ((HttpServletRequest) externalContext.getRequest()).getRequestURI());
                            }
                            else {
                                externalContext.responseSendError(HttpServletResponse.SC_NOT_FOUND, null);
                            }
                            return;
                        }

                        externalContext.setResponseStatus(HttpServletResponse.SC_OK);
                        externalContext.setResponseContentType(streamedContent.getContentType());                    

                        handleCache(externalContext, cache);

                        if(streamedContent.getContentLength() != null){
                            externalContext.setResponseContentLength(streamedContent.getContentLength());
                        }

                        if(streamedContent.getContentEncoding() != null) {
                            externalContext.setResponseHeader("Content-Encoding", streamedContent.getContentEncoding());
                        }
                        
                        if(streamedContent.getName() != null) {
                            externalContext.setResponseHeader("Content-Disposition", "inline;filename=\"" + streamedContent.getName() + "\"");
                        }

                        byte[] buffer = new byte[2048];

                        int length;
                        InputStream inputStream = streamedContent.getStream();
                        while ((length = (inputStream.read(buffer))) >= 0) {
                            externalContext.getResponseOutputStream().write(buffer, 0, length);
                        }
                    }
                }

                externalContext.responseFlushBuffer();
                context.responseComplete();

            } catch(Exception e) {
                LOG.log(Level.SEVERE, "Error in streaming dynamic resource.", e);
                throw new IOException(e);
            }
            finally {
                //cleanup
                if(streamedContent != null && streamedContent.getStream() != null) {
                    streamedContent.getStream().close();
                }
            }
        }
    }
}
