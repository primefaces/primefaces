/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.download;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class Download001 implements Serializable {

    private static final long serialVersionUID = -7518459955779385334L;

    public void downloadTxt() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext extCtx = facesContext.getExternalContext();
        extCtx.setResponseContentType("text/plain");
        extCtx.setResponseHeader("Content-Disposition", "attachment; filename=\"text_file.txt\"");
        extCtx.setResponseHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        // Write text data to the output stream
        try (OutputStream output = extCtx.getResponseOutputStream()) {
            output.write("Some text content".getBytes(Charset.forName("UTF-8")));
            output.flush();
            // Following is important! Otherwise JSF will attempt to render the response which obviously
            // will fail since it's already written with text content and closed.
            facesContext.responseComplete();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}