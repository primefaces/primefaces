/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.HashMap;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;

public class FileDownloadActionListener implements ActionListener, StateHolder {

	private ValueExpression value;
	
	private ValueExpression contentDisposition;
	
	public FileDownloadActionListener() {}
	
	public FileDownloadActionListener(ValueExpression value, ValueExpression contentDisposition) {
		this.value = value;
		this.contentDisposition = contentDisposition;
	}

	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();
        StreamedContent content = (StreamedContent) value.getValue(elContext);
        
		if(content == null) {
            return;
        }
        
		ExternalContext externalContext = facesContext.getExternalContext();
		String contentDispositionValue = contentDisposition != null ? (String) contentDisposition.getValue(elContext) : "attachment";	
		
		try {
			externalContext.setResponseContentType(content.getContentType());
			externalContext.setResponseHeader("Content-Disposition", contentDispositionValue + ";filename=\"" + content.getName() + "\"");
			externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<String, Object>());
            
			byte[] buffer = new byte[2048];
			int length;
            InputStream inputStream = content.getStream();
            OutputStream outputStream = externalContext.getResponseOutputStream();
            
			while ((length = (inputStream.read(buffer))) != -1) {
				outputStream.write(buffer, 0, length);
			}
			
			externalContext.setResponseStatus(200);
			externalContext.responseFlushBuffer();
            content.getStream().close();
			facesContext.responseComplete();
		}
        catch(IOException e) {
			throw new FacesException(e);
		}
	}

	public boolean isTransient() {
		return false;
	}

	public void restoreState(FacesContext facesContext, Object state) {
		Object values[] = (Object[]) state;

		value = (ValueExpression) values[0];
		contentDisposition = (ValueExpression) values[1];
	}

	public Object saveState(FacesContext facesContext) {
		Object values[] = new Object[2];

		values[0] = value;
		values[1] = contentDisposition;
		
		return ((Object[]) values);
	}

	public void setTransient(boolean value) {
		
	}
}
