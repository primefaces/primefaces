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
package org.primefaces.component.filedownload;

import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.DynamicContentSrcBuilder;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.IOUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.StateHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

public class FileDownloadActionListener implements ActionListener, StateHolder {

    private ValueExpression value;

    private ValueExpression contentDisposition;

    private ValueExpression monitorKey;

    private ValueExpression store;

    public FileDownloadActionListener() {
        ResourceUtils.addJavascriptResource(FacesContext.getCurrentInstance(), "filedownload/filedownload.js");
    }

    public FileDownloadActionListener(ValueExpression value, ValueExpression contentDisposition, ValueExpression monitorKey, ValueExpression store) {
        this();
        this.value = value;
        this.contentDisposition = contentDisposition;
        this.monitorKey = monitorKey;
        this.store = store;
    }

    @Override
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();
        StreamedContent content = value.getValue(elContext);

        if (content == null) {
            return;
        }

        if (PrimeFaces.current().isAjaxRequest()) {
            ajaxDownload(context,  content);
        }
        else {
            regularDownload(context, content);
        }
    }

    protected void ajaxDownload(FacesContext context, StreamedContent content) {
        UIComponent currentComponent = UIComponent.getCurrentComponent(context);
        String uri = DynamicContentSrcBuilder.buildStreaming(context, currentComponent, value, false);
        String monitorKeyCookieName = ResourceUtils.getMonitorKeyCookieName(context, monitorKey);
        PrimeFaces.current().executeScript(String.format("PrimeFaces.download('%s', '%s', '%s', '%s')",
                uri, content.getContentType(), EscapeUtils.forJavaScript(content.getName()), monitorKeyCookieName));
    }

    protected void regularDownload(FacesContext context, StreamedContent content) {
        ExternalContext externalContext = context.getExternalContext();
        externalContext.setResponseContentType(content.getContentType());
        String contentDispositionValue = contentDisposition != null ? (String) contentDisposition.getValue(context.getELContext()) : "attachment";
        externalContext.setResponseHeader("Content-Disposition", ComponentUtils.createContentDisposition(contentDispositionValue, content.getName()));

        String monitorKeyCookieName = ResourceUtils.getMonitorKeyCookieName(context, monitorKey);

        Map<String, Object> cookieOptions = new HashMap<>(4);
        cookieOptions.put("path", LangUtils.isBlank(externalContext.getRequestContextPath())
                ? "/"
                : externalContext.getRequestContextPath()); // Always add cookies to context root; see #3108
        ResourceUtils.addResponseCookie(context, monitorKeyCookieName, "true", cookieOptions);

        Boolean store = this.store != null ? (Boolean) this.store.getValue(context.getELContext()) : Boolean.FALSE;
        ResourceUtils.addNoCacheControl(externalContext, store);

        if (content.getContentLength() != null) {
            // we can't use externalContext.setResponseContentLength as our contentLength is a long
            externalContext.setResponseHeader("Content-Length", String.valueOf(content.getContentLength()));
        }

        try {
            if (content.getWriter() != null) {
                content.getWriter().accept(externalContext.getResponseOutputStream());
            }
            else {
                try (InputStream is = content.getStream().get()) {
                    IOUtils.copyLarge(is, externalContext.getResponseOutputStream());
                }
            }

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
