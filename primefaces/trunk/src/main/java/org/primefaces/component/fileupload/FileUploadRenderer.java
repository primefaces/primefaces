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
package org.primefaces.component.fileupload;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.servlet.ServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.webapp.MultipartRequest;

public class FileUploadRenderer extends CoreRenderer {

    @Override
	public void decode(FacesContext context, UIComponent component) {
		FileUpload fileUpload = (FileUpload) component;
        String clientId = fileUpload.getClientId(context);
		MultipartRequest multipartRequest = getMultiPartRequestInChain(context);
		
		if(multipartRequest != null) {
			FileItem file = multipartRequest.getFileItem(clientId);

            if(fileUpload.getMode().equals("simple")) {
                decodeSimple(context, fileUpload, file);
            }
            else {
                decodeAdvanced(context, fileUpload, file);
            }
		}
    }
	
	public void decodeSimple(FacesContext context, FileUpload fileUpload, FileItem file) {
		if(file.getName().equals(""))
            fileUpload.setSubmittedValue("");
        else
            fileUpload.setSubmittedValue(new DefaultUploadedFile(file));
	}
    
    public void decodeAdvanced(FacesContext context, FileUpload fileUpload, FileItem file) {
		if(file != null) {
            fileUpload.queueEvent(new FileUploadEvent(fileUpload, new DefaultUploadedFile(file)));
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
            
            if(fileUpload.getOnstart() != null) writer.write(",onstart:function() {" + fileUpload.getOnstart() + ";}");
            if(fileUpload.getOncomplete() != null) writer.write(",oncomplete:function() {" + fileUpload.getOncomplete() + ";}");
            
            //restrictions
            if(fileUpload.getAllowTypes() != null) writer.write(",acceptFileTypes:" + fileUpload.getAllowTypes());
            if(fileUpload.getSizeLimit() != Integer.MAX_VALUE) writer.write(",maxFileSize:" + fileUpload.getSizeLimit());
            
            //restriction messages
            if(fileUpload.getInvalidFileMessage() != null) writer.write(",invalidFileMessage:'" + fileUpload.getInvalidFileMessage() + "'");
            if(fileUpload.getInvalidSizeMessage() != null) writer.write(",invalidSizeMessage:'" + fileUpload.getInvalidSizeMessage() + "'");
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
        String styleClass = fileUpload.getStyleClass();
        styleClass = styleClass == null ? FileUpload.CONTAINER_CLASS : FileUpload.CONTAINER_CLASS + " " + styleClass;

		writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "id");
        if(fileUpload.getStyle() != null) 
            writer.writeAttribute("style", fileUpload.getStyle(), "style");
        
        //buttonbar
        writer.startElement("div", fileUpload);
        writer.writeAttribute("class", FileUpload.BUTTON_BAR_CLASS, "styleClass");

        //choose button
        encodeChooseButton(context, fileUpload);
        
        if(fileUpload.isShowButtons() && !fileUpload.isAuto()) {
            encodeButton(context, fileUpload.getUploadLabel(), FileUpload.UPLOAD_BUTTON_CLASS, "ui-icon-arrowreturnthick-1-n");
            encodeButton(context, fileUpload.getCancelLabel(), FileUpload.CANCEL_BUTTON_CLASS, "ui-icon-cancel");
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
    
    protected void encodeChooseButton(FacesContext context, FileUpload fileUpload) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = fileUpload.getClientId(context);
        
        writer.startElement("label", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " " + FileUpload.CHOOSE_BUTTON_CLASS, null);
        
        //button icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " ui-icon-plusthick", null);
        writer.endElement("span");
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(fileUpload.getLabel(), "value");
        writer.endElement("span");

        encodeInputField(context, fileUpload, clientId + "_input");
        
		writer.endElement("label");
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
    
    protected void encodeButton(FacesContext context, String label, String styleClass, String icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("button", null);
		writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " " + styleClass, null);
        
        //button icon
        String iconClass = HTML.BUTTON_LEFT_ICON_CLASS ;
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass + " " + icon, null);
        writer.endElement("span");
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(label, "value");
        writer.endElement("span");

		writer.endElement("button");
    }

    /**
     * Return null if no file is submitted in simple mode
     * 
     * @param context
     * @param component
     * @param submittedValue
     * @return
     * @throws ConverterException 
     */
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        FileUpload fileUpload = (FileUpload) component;
        
        if(fileUpload.getMode().equals("simple") && submittedValue != null && submittedValue.equals("")) {
            return null;
        }
        else {
            return submittedValue;
        }
    } 
}