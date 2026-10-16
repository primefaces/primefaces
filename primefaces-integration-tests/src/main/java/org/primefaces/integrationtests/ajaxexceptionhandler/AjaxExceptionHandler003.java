/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import lombok.Data;

@Named
@RequestScoped
@Data
public class AjaxExceptionHandler003 implements Serializable {

    private static final long serialVersionUID = 4271620417938617452L;

    /**
     * Simulates a successful response without any content.
     */
    public void emptyResponse() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().setResponseStatus(200);
        context.responseComplete();
    }

    /**
     * Simulates a well-formed response which is not a partial response, e.g. a Facelets login page after a session timeout.
     */
    public void noPartialResponse() throws IOException {
        writeResponse("<html><body>Login</body></html>");
    }

    /**
     * Simulates a response which cannot be parsed, e.g. a HTML login page returned by a proxy.
     */
    public void unparseableResponse() throws IOException {
        writeResponse("<html><body>Login<br></body></html>");
    }

    private void writeResponse(String content) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        externalContext.setResponseStatus(200);
        externalContext.setResponseContentType("text/html");
        externalContext.getResponseOutputWriter().write(content);
        context.responseComplete();
    }

}
