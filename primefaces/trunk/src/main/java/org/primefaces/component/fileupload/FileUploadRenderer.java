/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.fileupload;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.webapp.MultipartRequest;

public class FileUploadRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		FileUpload fileUpload = (FileUpload) component;
        String clientId = fileUpload.getClientId(context);
		MultipartRequest multipartRequest = getMultiPartRequestInChain(context);
		
		if(multipartRequest != null) {
			FileItem file = multipartRequest.getFileItem(clientId + "_input");

			if(file != null) {
				UploadedFile uploadedFile = new DefaultUploadedFile(file);
				fileUpload.queueEvent(new FileUploadEvent(fileUpload, uploadedFile));
			}
		}
	}
	
	/**
	 * Finds our MultipartRequestServletWrapper in case application contains other RequestWrappers
	 */
	private MultipartRequest getMultiPartRequestInChain(FacesContext facesContext) {
		Object request = facesContext.getExternalContext().getRequest();
		
		while(request instanceof ServletRequestWrapper) {
			if(request instanceof MultipartRequest) {
				return (MultipartRequest) request;
			}
			else {
				request = ((ServletRequestWrapper) request).getRequest();
			}
		}
		
		return null;
	}

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		FileUpload fileUpload = (FileUpload) component;
		
		encodeMarkup(facesContext, fileUpload);
		encodeScript(facesContext, fileUpload);
	}

	protected void encodeScript(FacesContext context, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = fileUpload.getClientId(context);
        String update = fileUpload.getUpdate();
        boolean auto = fileUpload.isAuto();
				
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function(){");
		
		writer.write(fileUpload.resolveWidgetVar() + " = new PrimeFaces.widget.FileUpload('" + clientId + "', {");

        writer.write("auto:" + auto);

        if(!auto)
            writer.write(",uploader:'" + ComponentUtils.findClientIds(context, fileUpload, fileUpload.getUploader()) + "'");

        if(update != null)
            writer.write(",update:'" + ComponentUtils.findClientIds(context, fileUpload, update) + "'");

		writer.write("});});");
		
		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		String inputFileId = clientId + "_input";
        boolean auto = fileUpload.isAuto();
        
		writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId, "id");

		if(fileUpload.getStyle() != null) writer.writeAttribute("style", fileUpload.getStyle(), "style");
		if(fileUpload.getStyleClass() != null) writer.writeAttribute("class", fileUpload.getStyleClass(), "styleClass");

        writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId + "_browser", "id");

		writer.startElement("input", null);
		writer.writeAttribute("type", "file", null);
		writer.writeAttribute("id", inputFileId, null);
		writer.writeAttribute("name", inputFileId, null);
		writer.endElement("input");

        writer.startElement("button", null);
        writer.write("Upload");
        writer.endElement("button");

        writer.startElement("div", null);
        writer.write(fileUpload.getLabel());
        writer.endElement("div");

        writer.endElement("div");

        writer.startElement("table", null);
        writer.writeAttribute("id", clientId + "_files", null);
        writer.endElement("table");

		writer.endElement("div");

	}
}