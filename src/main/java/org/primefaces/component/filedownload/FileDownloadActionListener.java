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
package org.primefaces.component.filedownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

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
        String contentDispositionValue = contentDisposition != null ? (String) contentDisposition.getValue(elContext) : "attachment";
        String monitorKeyValue = monitorKey != null ? "_" + (String) monitorKey.getValue(elContext) : "";

        InputStream inputStream = null;

        try {
            externalContext.setResponseContentType(content.getContentType());
            externalContext.setResponseHeader("Content-Disposition", ComponentUtils.createContentDisposition(contentDispositionValue, content.getName()));
            externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE + monitorKeyValue, "true", Collections.<String, Object>emptyMap());

            if (content.getContentLength() != null) {
                externalContext.setResponseContentLength(content.getContentLength().intValue());
            }

            if (PrimeRequestContext.getCurrentInstance(context).isSecure()) {
                externalContext.setResponseHeader("Cache-Control", "public");
                externalContext.setResponseHeader("Pragma", "public");
            }

            byte[] buffer = new byte[2048];
            int length;
            inputStream = content.getStream();
            OutputStream outputStream = externalContext.getResponseOutputStream();

            while ((length = (inputStream.read(buffer))) != -1) {
                outputStream.write(buffer, 0, length);
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
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {

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
