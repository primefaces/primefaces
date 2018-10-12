/**
 * Copyright 2009-2018 PrimeTek.
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
import org.primefaces.util.LangUtils;

public class PrimeResourceHandler extends ResourceHandlerWrapper {

    private static final Logger LOGGER = Logger.getLogger(PrimeResourceHandler.class.getName());

    private final Map<String, DynamicContentHandler> handlers;

    private final ResourceHandler wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
        handlers = new HashMap<>();
        handlers.put(DynamicContentType.STREAMED_CONTENT.toString(), new StreamedContentHandler());

        if (isBarcodeHandlerAvailable()) {
            handlers.put(DynamicContentType.BARCODE.toString(), new BarcodeHandler());
        }

        if (isQRCodeHandlerAvailable()) {
            handlers.put(DynamicContentType.QR_CODE.toString(), new QRCodeHandler());
        }
    }

    @Override
    public ResourceHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = super.createResource(resourceName, libraryName);

        if (resource != null && libraryName != null && libraryName.equalsIgnoreCase(Constants.LIBRARY)) {
            return new PrimeResource(resource);
        }
        else {
            return resource;
        }
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        Resource resource = super.createResource(resourceName, libraryName, contentType);

        if (resource != null && libraryName != null && libraryName.equalsIgnoreCase(Constants.LIBRARY)) {
            return new PrimeResource(resource);
        }
        else {
            return resource;
        }
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String handlerType = params.get(Constants.DYNAMIC_CONTENT_TYPE_PARAM);

        if (LangUtils.isValueBlank(handlerType)) {
            super.handleResourceRequest(context);
        }
        else {
            DynamicContentHandler handler = handlers.get(handlerType);
            if (handler == null) {
                LOGGER.warning("No dynamic resource handler registered for: " + handlerType + ". Do you miss a dependency?");
                super.handleResourceRequest(context);
            }
            else {
                handler.handle(context);
            }
        }
    }

    private boolean isBarcodeHandlerAvailable() {
        try {
            Class.forName("org.krysalis.barcode4j.output.AbstractCanvasProvider");
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private boolean isQRCodeHandlerAvailable() {
        try {
            Class.forName("net.glxn.qrgen.QRCode");
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
