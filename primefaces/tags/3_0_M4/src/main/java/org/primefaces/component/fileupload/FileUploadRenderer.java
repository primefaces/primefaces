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
				
        startScript(writer, clientId);

        writer.write("$(function(){");
        
        writer.write("PrimeFaces.cw('FileUpload','" + fileUpload.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",mode:'" + mode + "'");
        
        if(!mode.equals("simple")) {
            String update = fileUpload.getUpdate();
            String process = fileUpload.getProcess();
            
            writer.write(",autoUpload:" + fileUpload.isAuto());
            writer.write(",dnd:" + fileUpload.isDragDropSupport());
            
            if(update != null) writer.write(",update:'" + ComponentUtils.findClientIds(context, fileUpload, update) + "'");
            if(process != null) writer.write(",process:'" + ComponentUtils.findClientIds(context, fileUpload, process) + "'");
            
            if(fileUpload.getOnstart() != null) writer.write(",onstart:function(e, start) {" + fileUpload.getOnstart() + ";}");
            if(fileUpload.getOncomplete() != null) writer.write(",oncomplete:function(e, data) {" + fileUpload.getOncomplete() + ";}");
            
            //restrictions
            if(fileUpload.getAllowTypes() != null) writer.write(",acceptFileTypes:" + fileUpload.getAllowTypes());
            if(fileUpload.getSizeLimit() != Integer.MAX_VALUE) writer.write(",maxFileSize:" + fileUpload.getSizeLimit());
            if(fileUpload.getFileLimit() != Integer.MAX_VALUE) writer.write(",maxNumberOfFiles:" + fileUpload.getFileLimit());
            
            //restriction messages
            if(fileUpload.getInvalidFileMessage() != null) writer.write(",invalidFileMessage:'" + fileUpload.getInvalidFileMessage() + "'");
            if(fileUpload.getInvalidSizeMessage() != null) writer.write(",invalidSizeMessage:'" + fileUpload.getInvalidSizeMessage() + "'");
            if(fileUpload.getFileLimitMessage() != null) writer.write(",fileLimitMessage:'" + fileUpload.getFileLimitMessage() + "'");
        }

		writer.write("},'fileupload');});");
		
		endScript(writer);
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
        boolean disabled = fileUpload.isDisabled();
        String styleClass = fileUpload.getStyleClass();
        styleClass = styleClass == null ? FileUpload.CONTAINER_CLASS : FileUpload.CONTAINER_CLASS + " " + styleClass;

		writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if(fileUpload.getStyle() != null) writer.writeAttribute("style", fileUpload.getStyle(), "style");
        
        //buttonbar
        writer.startElement("div", fileUpload);
        writer.writeAttribute("class", FileUpload.BUTTON_BAR_CLASS, "styleClass");
		if(fileUpload.getStyle() != null)
            writer.writeAttribute("style", fileUpload.getStyle(), "style");

        //choose button
        writer.startElement("label", fileUpload);
        writer.writeAttribute("class", FileUpload.CHOOSE_BUTTON_CLASS, null);

        writer.startElement("span", null);
        writer.write(fileUpload.getLabel());
        writer.endElement("span");
        
        encodeInputField(context, fileUpload, clientId + "_input");

        writer.endElement("label");
        
        if(!fileUpload.isShowButtons() && !fileUpload.isAuto()) {
            encodeButton(context, fileUpload, fileUpload.getUploadLabel(), "submit", FileUpload.UPLOAD_BUTTON_CLASS);
            encodeButton(context, fileUpload, fileUpload.getCancelLabel(), "button", FileUpload.CANCEL_BUTTON_CLASS);
        }
        
        writer.endElement("div");
        
        //content
        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.CONTENT_CLASS, null);
        
        writer.startElement("table", null);
        writer.writeAttribute("class", FileUpload.FILES_CLASS, null);
        writer.endElement("table");
        
        writer.endElement("div");

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
        
        if(fileUpload.isMultiple()) writer.writeAttribute("multiple", "multiple", null);
        if(fileUpload.getStyle() != null) writer.writeAttribute("style", fileUpload.getStyle(), "style");
        if(fileUpload.getStyleClass() != null) writer.writeAttribute("class", fileUpload.getStyleClass(), "styleClass");
        if(fileUpload.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        
		writer.endElement("input");
    }

    protected void encodeButton(FacesContext facesContext, FileUpload fileUpload, String label, String type, String styleClass) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("button", null);
        writer.writeAttribute("type", type, null);
		writer.writeAttribute("class", styleClass, null);
        writer.write(label);
        writer.endElement("button");
	}
}