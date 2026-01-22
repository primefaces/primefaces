/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.barcode.BarcodeHandler;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.application.Resource;
import jakarta.faces.application.ResourceHandler;
import jakarta.faces.application.ResourceHandlerWrapper;
import jakarta.faces.context.FacesContext;

public class PrimeResourceHandler extends ResourceHandlerWrapper {

    private static final Logger LOGGER = Logger.getLogger(PrimeResourceHandler.class.getName());

    private final Map<String, DynamicContentHandler> handlers;

    public PrimeResourceHandler(ResourceHandler wrapped) {
        super(wrapped);
        handlers = new HashMap<>();
        handlers.put(DynamicContentType.STREAMED_CONTENT.toString(), new StreamedContentHandler());

        if (LangUtils.isClassAvailable("uk.org.okapibarcode.output.SvgRenderer")) {
            handlers.put(DynamicContentType.BARCODE.toString(), new BarcodeHandler());
            handlers.put(DynamicContentType.QR_CODE.toString(), new BarcodeHandler());
        }
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = super.createResource(resourceName, libraryName);
        return wrapResource(resource, libraryName);
    }

    @Override
    public Resource createResource(String resourceName, String libraryName, String contentType) {
        Resource resource = super.createResource(resourceName, libraryName, contentType);
        return wrapResource(resource, libraryName);
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String handlerType = params.get(Constants.DYNAMIC_CONTENT_TYPE_PARAM);

        if (LangUtils.isBlank(handlerType)) {
            super.handleResourceRequest(context);
        }
        else {
            DynamicContentHandler handler = handlers.get(handlerType);
            if (handler == null) {
                LOGGER.log(Level.WARNING,
                        "No dynamic resource handler registered for: [{0}]. Are you missing a dependency?",
                        new Object[]{handlerType});
                super.handleResourceRequest(context);
            }
            else {
                handler.handle(context);
            }
        }
    }

    private Resource wrapResource(Resource resource, String libraryName) {
        if (resource != null && libraryName != null
                    && (libraryName.toLowerCase().startsWith(Constants.LIBRARY))) {
            return new PrimeResource(resource);
        }
        else {
            return resource;
        }
    }
}
