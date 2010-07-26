/*
 * Copyright 2009 Prime Technology.
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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.resource.ResourceUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.webapp.MultipartRequest;

public class FileUploadRenderer extends CoreRenderer {
	
	public void decode(FacesContext facesContext, UIComponent component) {
		FileUpload fileUpload = (FileUpload) component;
		String clientId = fileUpload.getClientId(facesContext);
		MultipartRequest multipartRequest = getMultiPartRequestInChain(facesContext);
		
		if(multipartRequest != null) {
			FileItem file = multipartRequest.getFileItem(clientId);
			
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

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		FileUpload fileUpload = (FileUpload) component;
		
		encodeScript(facesContext, fileUpload);
		encodeMarkup(facesContext, fileUpload);
	}

	private void encodeScript(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		String actionURL = getActionURL(facesContext);
		String uploadVar = createUniqueWidgetVar(facesContext, fileUpload);
		String cancelImg = fileUpload.getCancelImage() == null ? ResourceUtils.getResourceURL(facesContext, "/jquery/plugins/uploadify/cancel.png") : getResourceURL(facesContext, fileUpload.getCancelImage());
		
		UIComponent parentForm = ComponentUtils.findParentForm(facesContext, fileUpload);
		if(parentForm == null) {
			throw new FacesException("FileUpload component:" + clientId + " needs to be enclosed in a form");
		}
		String formClientId = parentForm.getClientId(facesContext);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + clientId + "', function () { \n");
		
		writer.write(uploadVar + " = new PrimeFaces.widget.Uploader('" + clientId + "', {\n");
		writer.write("uploader:'" + ResourceUtils.getResourceURL(facesContext, "/jquery/plugins/uploadify/uploadify.swf") + "'");
		writer.write(",script:'" + actionURL + "'");
		writer.write(",cancelImg:'" + cancelImg + "'");
		writer.write(",formId:'" + formClientId + "'");
		writer.write(",fileDataName:'" + clientId + "'");
		writer.write(",multi:" + fileUpload.isMultiple());
		writer.write(",auto:" + fileUpload.isAuto());
		
		if(fileUpload.getUpdate() != null) writer.write(",update:'" + fileUpload.getUpdate() + "'");
		if(fileUpload.getImage() != null) writer.write(",buttonImg:'" + getResourceURL(facesContext, fileUpload.getImage()) + "'");
		if(fileUpload.getLabel() != null) writer.write(",buttonText:'" + fileUpload.getLabel() + "'");
		if(fileUpload.getWidth() != null) writer.write(",width:'" + fileUpload.getWidth() + "'");
		if(fileUpload.getHeight() != null) writer.write(",height:'" + fileUpload.getWidth() + "'");
		if(fileUpload.getAllowTypes() != null) writer.write(",fileExt:'" + fileUpload.getAllowTypes() + "'");
		if(fileUpload.getDescription() != null) writer.write(",fileDesc:'" + fileUpload.getDescription() + "'");
		if(shouldRenderAttribute(fileUpload.getSizeLimit())) writer.write(",sizeLimit:" + fileUpload.getSizeLimit());
		if(fileUpload.getWmode() != null) writer.write(",wmode:'" + fileUpload.getWmode() + "'");
		
		writer.write("});\n});\n");						
		
		writer.endElement("script");
	}

	private void encodeMarkup(FacesContext facesContext, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = fileUpload.getClientId(facesContext);
		String uploadVar = createUniqueWidgetVar(facesContext, fileUpload);
		
		writer.startElement("input", null);
		writer.writeAttribute("type", "file", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.endElement("input");
		
		if(!fileUpload.isCustomUI() && !fileUpload.isAuto()) {
			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:" + uploadVar + ".upload();", null);
			writer.write("Upload");
			writer.endElement("a");
			
			writer.write(" | ");
			
			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:" + uploadVar + ".clear();", null);
			writer.write("Clear");
			writer.endElement("a");
		}
	}
}