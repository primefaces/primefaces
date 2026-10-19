/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.ajaxexceptionhandler;

import java.io.IOException;
import java.io.Serializable;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import lombok.Data;

@Named
@RequestScoped
@Data
public class AjaxExceptionHandler008 implements Serializable {

    private static final long serialVersionUID = 7409652250131957319L;

    /**
     * Simulates the default Faces AJAX error response (Mojarra prefixes the error name with 'class '),
     * which is written if no PrimeExceptionHandler is configured.
     */
    public void mojarraError() throws IOException {
        writeErrorResponse("class java.lang.IllegalStateException", "Simulated IllegalStateException");
    }

    /**
     * Simulates the default Faces AJAX error response without 'class ' prefix.
     */
    public void plainError() throws IOException {
        writeErrorResponse("java.lang.IllegalStateException", "Simulated IllegalStateException");
    }

    /**
     * Simulates the default Faces AJAX error response for an exception without a specific p:ajaxExceptionHandler.
     */
    public void otherError() throws IOException {
        writeErrorResponse("class java.lang.UnsupportedOperationException", "Simulated UnsupportedOperationException");
    }

    private void writeErrorResponse(String errorName, String errorMessage) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        externalContext.setResponseStatus(200);
        externalContext.setResponseContentType("text/xml");
        externalContext.getResponseOutputWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<partial-response><error>"
                + "<error-name>" + errorName + "</error-name>"
                + "<error-message><![CDATA[" + errorMessage + "]]></error-message>"
                + "</error></partial-response>");
        context.responseComplete();
    }

}
