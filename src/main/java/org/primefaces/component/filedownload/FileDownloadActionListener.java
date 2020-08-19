/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.filedownload;

import org.apache.commons.io.IOUtils;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.ResourceUtils;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FileDownloadActionListener implements ActionListener, StateHolder {

    private ValueExpression value;

    private ValueExpression contentDisposition;

    private ValueExpression monitorKey;

    public FileDownloadActionListener() {

    }

    public FileDownloadActionListener(ValueExpression value, ValueExpression contentDisposition, ValueExpression monitorKey) {
        this.value = value;
        this.contentDisposition = contentDisposition;
        this.monitorKey = monitorKey;
    }

    @Override
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();
        StreamedContent content = (StreamedContent) value.getValue(elContext);

        if (content == null) {
            return;
        }

        ExternalContext externalContext = context.getExternalContext();
        externalContext.setResponseContentType(content.getContentType());
        String contentDispositionValue = contentDisposition != null ? (String) contentDisposition.getValue(elContext) : "attachment";
        externalContext.setResponseHeader("Content-Disposition", ComponentUtils.createContentDisposition(contentDispositionValue, content.getName()));

        String monitorKeyCookieName = Constants.DOWNLOAD_COOKIE + context.getViewRoot().getViewId().replace('/', '_');
        if (monitorKey != null) {
            String evaluated = (String) monitorKey.getValue(elContext);
            if (!LangUtils.isValueBlank(evaluated)) {
                monitorKeyCookieName += "_" + evaluated;
            }
        }

        Map<String, Object> cookieOptions = new HashMap<>(4);
        cookieOptions.put("path", LangUtils.isValueBlank(externalContext.getRequestContextPath())
                ? "/"
                : externalContext.getRequestContextPath()); // Always add cookies to context root; see #3108
        ResourceUtils.addResponseCookie(context, monitorKeyCookieName, "true", cookieOptions);

        if (content.getContentLength() != null) {
            externalContext.setResponseContentLength(content.getContentLength());
        }

        if (PrimeRequestContext.getCurrentInstance(context).isSecure()) {
            externalContext.setResponseHeader("Cache-Control", "public");
            externalContext.setResponseHeader("Pragma", "public");
        }

        try (InputStream is = content.getStream()) {
            IOUtils.copyLarge(is, externalContext.getResponseOutputStream());

            if (!externalContext.isResponseCommitted()) {
                externalContext.setResponseStatus(200);
            }

            externalContext.responseFlushBuffer();
            context.responseComplete();
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {
        // NOOP
    }

    @Override
    public void restoreState(FacesContext facesContext, Object state) {
        Object[] values = (Object[]) state;

        value = (ValueExpression) values[0];
        contentDisposition = (ValueExpression) values[1];
        monitorKey = (ValueExpression) values[2];
    }

    @Override
    public Object saveState(FacesContext facesContext) {
        Object[] values = new Object[3];

        values[0] = value;
        values[1] = contentDisposition;
        values[2] = monitorKey;

        return (values);
    }
}
