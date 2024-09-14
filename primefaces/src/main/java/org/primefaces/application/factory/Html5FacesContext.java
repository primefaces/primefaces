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
package org.primefaces.application.factory;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

/**
 * JSF generates all script tags with 'type="text/javascript"' which throws HTML5 validation warnings.
 * NOTE: Not necessary for Faces 4.0+.
 *
 * @since 12.0.0
 */
public class Html5FacesContext extends FacesContextWrapper {

    public Html5FacesContext(FacesContext context) {
        super(context);
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
        // #12591 - Don't wrap the response writer if it's already a Html5FacesContextResponseWriter
        if (alreadyWrapped(responseWriter)) {
            super.setResponseWriter(responseWriter);
        }
        else {
            super.setResponseWriter(new Html5FacesContextResponseWriter(responseWriter));
        }
    }

    /**
     * Checks if the given ResponseWriter is already wrapped with Html5FacesContextResponseWriter.
     *
     * @param responseWriter The ResponseWriter to check
     * @return true if the ResponseWriter is already wrapped, false otherwise
     */
    private boolean alreadyWrapped(ResponseWriter responseWriter) {
        while (responseWriter instanceof ResponseWriterWrapper) {
            if (responseWriter instanceof Html5FacesContextResponseWriter) {
                return true;
            }
            responseWriter = ((ResponseWriterWrapper) responseWriter).getWrapped();
        }
        return responseWriter instanceof Html5FacesContextResponseWriter;
    }
}