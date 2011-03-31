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
			FileItem file = multipartRequest.getFileItem(clientId);

			if(file != null) {
				UploadedFile uploadedFile = new DefaultUploadedFile(file);

                if(fileUpload.getMode().equals("simple"))
                    fileUpload.setSubmittedValue(uploadedFile);
                else
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
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		FileUpload fileUpload = (FileUpload) component;
		
		encodeMarkup(context, fileUpload);
		encodeScript(context, fileUpload);
	}

	protected void encodeScript(FacesContext context, FileUpload fileUpload) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = fileUpload.getClientId(context);
        
        String mode = fileUpload.getMode();
				
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function(){");
		
		writer.write(fileUpload.resolveWidgetVar() + " = new PrimeFaces.widget.FileUpload('" + clientId + "', {");

        writer.write("mode:'" + mode + "'");

        if(!mode.equals("simple")) {
            String update = fileUpload.getUpdate();
            String process = fileUpload.getProcess();
            
            writer.write(",auto:" + fileUpload.isAuto());
            writer.write(",customUI:" + fileUpload.isCustomUI());
            writer.write(",dragDropSupport:" + fileUpload.isDragDropSupport());
            writer.write(",uploadLabel:'" + fileUpload.getUploadLabel() + "'");
            writer.write(",cancelLabel:'" + fileUpload.getCancelLabel() + "'");

            if(update != null) writer.write(",update:'" + ComponentUtils.findClientIds(context, fileUpload, update) + "'");
            if(process != null) writer.write(",process:'" + ComponentUtils.findClientIds(context, fileUpload, process) + "'");

            if(fileUpload.getOncomplete() != null) writer.write(",oncomplete:function(event, files, index, xhr, handler) {" + fileUpload.getOncomplete() + ";}");

            //file restrictions
            if(fileUpload.getAllowTypes() != null) writer.write(",allowTypes:'" + fileUpload.getAllowTypes() + "'");
            if(fileUpload.getSizeLimit() != Integer.MAX_VALUE) writer.write(",sizeLimit:" + fileUpload.getSizeLimit());
            if(fileUpload.getFileLimit() != Integer.MAX_VALUE) writer.write(",fileLimit:" + fileUpload.getFileLimit());
            if(fileUpload.getSizeExceedMessage() != null) writer.write(",sizeExceedMessage:'" + fileUpload.getSizeExceedMessage() + "'");
            if(fileUpload.getInvalidFileMessage() != null) writer.write(",invalidFileMessage:'" + fileUpload.getInvalidFileMessage() + "'");
            if(fileUpload.getErrorMessageDelay() != Integer.MAX_VALUE) writer.write(",errorMessageDelay:" + fileUpload.getErrorMessageDelay());
        }

		writer.write("});});");
		
		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
		if(fileUpload.getMode().equals("simple"))
            encodeSimpleMarkup(context, fileUpload);
        else
            encodeAdvancedMarkup(context, fileUpload);
	}

    protected void encodeAdvancedMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
		String clientId = fileUpload.getClientId(context);
        String styleClass = fileUpload.getStyleClass();
        styleClass = styleClass == null ? FileUpload.CONTAINER_CLASS : FileUpload.CONTAINER_CLASS + " " + styleClass;

		writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
		if(fileUpload.getStyle() != null) writer.writeAttribute("style", fileUpload.getStyle(), "style");

        writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId + "_browser", "id");
        writer.writeAttribute("class", "ui-helper-hidden", null);

		encodeInputField(context, fileUpload, clientId + "_input");

        writer.startElement("button", null);
        writer.write("Upload");
        writer.endElement("button");

        writer.startElement("div", null);
        writer.write(fileUpload.getLabel());
        writer.endElement("div");

        writer.endElement("div");

        writer.startElement("table", null);
        writer.writeAttribute("id", clientId + "_files", null);
        writer.writeAttribute("class", "ui-fileupload-files ui-widget", null);
        writer.endElement("table");

        if(!fileUpload.isCustomUI() && !fileUpload.isAuto()) {
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-fileupload-controls", null);
            encodeButton(context, fileUpload, fileUpload.getUploadLabel(), "ui-fileupload-upload-button");
            encodeButton(context, fileUpload, fileUpload.getCancelLabel(), "ui-fileupload-cancel-button");
            writer.endElement("div");
        }

		writer.endElement("div");
    }

    protected void encodeSimpleMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        encodeInputField(context, fileUpload, fileUpload.getClientId(context));
    }

    protected void encodeInputField(FacesContext context, FileUpload fileUpload, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
		writer.writeAttribute("type", "file", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
        if(fileUpload.isMultiple()) {
            writer.writeAttribute("multiple", "multiple", null);
        }
		writer.endElement("input");
    }

    protected void encodeButton(FacesContext facesContext, FileUpload fileUpload, String label, String styleClass) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
		writer.writeAttribute("class", styleClass, null);
        writer.write(label);
        writer.endElement("button");
	}
}