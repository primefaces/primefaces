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

        if (LangUtils.tryToLoadClassForName("org.krysalis.barcode4j.output.AbstractCanvasProvider") != null) {
            handlers.put(DynamicContentType.BARCODE.toString(), new BarcodeHandler());
        }

        if (LangUtils.tryToLoadClassForName("net.glxn.qrgen.QRCode") != null) {
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

}
