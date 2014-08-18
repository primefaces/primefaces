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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.FacesContext;
import org.primefaces.application.resource.barcode.BarcodeHandler;
import org.primefaces.util.Constants;

public class PrimeResourceHandler extends ResourceHandlerWrapper {
    
    static Map<String,DynamicContentHandler> HANDLERS;
    
    static {
        HANDLERS = new HashMap<String,DynamicContentHandler>();
        HANDLERS.put("sc", new StreamedContentHandler());
        HANDLERS.put("barcode", new BarcodeHandler());
        HANDLERS.put("qr", new QRCodeHandler());
    }
    
    private final static Logger logger = Logger.getLogger(PrimeResourceHandler.class.getName());
        
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
        String handlerType = params.get(Constants.DYNAMIC_CONTENT_TYPE_PARAM);
        DynamicContentHandler handler = HANDLERS.get(handlerType);
        
        if(handler != null) {
            handler.handle(context);
        } else {
            super.handleResourceRequest(context);
        }
    }
}
