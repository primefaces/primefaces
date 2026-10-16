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

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import lombok.Data;

@Named
@RequestScoped
@Data
public class AjaxExceptionHandler004 implements Serializable {

    private static final long serialVersionUID = 1953286320563874205L;

    public void connectionError() {
        sendConnectionError();
    }

    /**
     * The request of the p:ajaxExceptionHandler itself (its update) fails too, like when the server is still not reachable.
     */
    public void preRenderView() {
        FacesContext context = FacesContext.getCurrentInstance();
        String source = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");
        if ("handler".equals(source)) {
            sendConnectionError();
        }
    }

    private void sendConnectionError() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().setResponseStatus(504);
        context.responseComplete();
    }

}
