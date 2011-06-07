/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.application;

import java.io.IOException;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;

public class PrimeResourceHandler extends ResourceHandlerWrapper {
        
    public static final String DYNAMIC_CONTENT_PARAM = "pfdrid";
    
    private ResourceHandler wrapped;

    public PrimeResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = super.createResource(resourceName, libraryName);

        if(resource != null && libraryName != null && libraryName.equalsIgnoreCase(Constants.LIBRARY)) {
            return new PrimeResource(resource);
        }
        else {
            return resource;
        }
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String library = params.get("ln");
        String dynamicContentId = params.get(DYNAMIC_CONTENT_PARAM);
        
        if(dynamicContentId != null && library != null && library.equals("primefaces")) {
            Map<String,Object> session = context.getExternalContext().getSessionMap();
            String dynamicContentEL = (String) session.get(dynamicContentId);
            ELContext eLContext = context.getELContext();
            ValueExpression ve = context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), dynamicContentEL, StreamedContent.class);
            StreamedContent content = (StreamedContent) ve.getValue(eLContext);
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            
            response.setContentType(content.getContentType());
				
            byte[] buffer = new byte[2048];

            int length;
            while ((length = (content.getStream().read(buffer))) >= 0) {
                response.getOutputStream().write(buffer, 0, length);
            }

            response.setStatus(200);
            response.getOutputStream().flush();
            context.responseComplete();
            
            session.remove(dynamicContentId);
        }
        else {
           super.handleResourceRequest(context); 
        }
    }
}