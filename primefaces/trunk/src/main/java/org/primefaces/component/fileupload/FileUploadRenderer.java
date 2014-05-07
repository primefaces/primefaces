/*
 * Copyright 2009-2014 PrimeTek.
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
import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class FileUploadRenderer extends CoreRenderer {
            
    @Override
	public void decode(FacesContext context, UIComponent component) {
		FileUpload fileUpload = (FileUpload) component;
        
        if(!fileUpload.isDisabled()) {
            ConfigContainer cc = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
            String uploader = cc.getUploader();
            boolean isAtLeastJSF22 = cc.isAtLeastJSF22();
            
            if(uploader.equals("auto")) {
                if(isAtLeastJSF22)
                    NativeFileUploadDecoder.decode(context, fileUpload);
                else
                    CommonsFileUploadDecoder.decode(context, fileUpload);
            }
            else if(uploader.equals("native")) {
                if(!isAtLeastJSF22) {
                    throw new FacesException("native uploader requires at least a JSF 2.2 runtime");
                }
                
                NativeFileUploadDecoder.decode(context, fileUpload);
            }
            else if(uploader.equals("commons")) {
                CommonsFileUploadDecoder.decode(context, fileUpload);
            }            
        }
    }

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		FileUpload fileUpload = (FileUpload) component;
		
		encodeMarkup(context, fileUpload);
        
        if(fileUpload.getMode().equals("advanced"))
            encodeScript(context, fileUpload);
	}

	protected void encodeScript(FacesContext context, FileUpload fileUpload) throws IOException {
		String clientId = fileUpload.getClientId(context);
        String update = fileUpload.getUpdate();
        String process = fileUpload.getProcess();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("FileUpload", fileUpload.resolveWidgetVar(), clientId, "fileupload");
        
        wb.attr("auto", fileUpload.isAuto(), false)
            .attr("dnd", fileUpload.isDragDropSupport(), true)
            .attr("update", SearchExpressionFacade.resolveComponentsForClient(context, fileUpload, update), null)
            .attr("process", SearchExpressionFacade.resolveComponentsForClient(context, fileUpload, process), null)
            .attr("maxFileSize", fileUpload.getSizeLimit(), Long.MAX_VALUE)
            .attr("fileLimit", fileUpload.getFileLimit(), Integer.MAX_VALUE)
            .attr("invalidFileMessage", fileUpload.getInvalidFileMessage(), null)
            .attr("invalidSizeMessage", fileUpload.getInvalidSizeMessage(), null)
            .attr("fileLimitMessage", fileUpload.getFileLimitMessage(), null)
            .attr("messageTemplate", fileUpload.getMessageTemplate(), null)
            .attr("previewWidth", fileUpload.getPreviewWidth(), 80)
            .attr("disabled", fileUpload.isDisabled(), false)
            .callback("onstart", "function()", fileUpload.getOnstart())
            .callback("onerror", "function()", fileUpload.getOnerror())
            .callback("oncomplete", "function()", fileUpload.getOncomplete());

        if(fileUpload.getAllowTypes() != null) {
            wb.append(",allowTypes:").append(fileUpload.getAllowTypes());
        }
        
        wb.finish();
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
        String style = fileUpload.getStyle();
        String styleClass = fileUpload.getStyleClass();
        styleClass = styleClass == null ? FileUpload.CONTAINER_CLASS : FileUpload.CONTAINER_CLASS + " " + styleClass;
        boolean disabled = fileUpload.isDisabled();

		writer.startElement("div", fileUpload);
		writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, styleClass);
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        //buttonbar
        writer.startElement("div", fileUpload);
        writer.writeAttribute("class", FileUpload.BUTTON_BAR_CLASS, null);

        //choose button
        encodeChooseButton(context, fileUpload, disabled);
        
        if(!fileUpload.isAuto()) {
            encodeButton(context, fileUpload.getUploadLabel(), FileUpload.UPLOAD_BUTTON_CLASS, "ui-icon-arrowreturnthick-1-n");
            encodeButton(context, fileUpload.getCancelLabel(), FileUpload.CANCEL_BUTTON_CLASS, "ui-icon-cancel");
        }
        
        writer.endElement("div");
        
        //content
        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.CONTENT_CLASS, null);
        
        writer.startElement("table", null);
        writer.writeAttribute("class", FileUpload.FILES_CLASS, null);
        writer.startElement("tbody", null);
        writer.endElement("tbody");
        writer.endElement("table");
        
        writer.endElement("div");

		writer.endElement("div");
    }

    protected void encodeSimpleMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        encodeInputField(context, fileUpload, fileUpload.getClientId(context), "simple");
    }
    
    protected void encodeChooseButton(FacesContext context, FileUpload fileUpload, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = fileUpload.getClientId(context);
        String cssClass = HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " " + FileUpload.CHOOSE_BUTTON_CLASS;
        if(disabled) {
            cssClass += " ui-state-disabled";
        }
        
        writer.startElement("span", null);
        writer.writeAttribute("class", cssClass, null);
        
        //button icon 
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " ui-icon-plusthick", null);
        writer.endElement("span");
        
        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(fileUpload.getLabel(), "value");
        writer.endElement("span");

        if(!disabled) {
            encodeInputField(context, fileUpload, clientId + "_input", "advanced");
        }
        
		writer.endElement("span");
    }

    protected void encodeInputField(FacesContext context, FileUpload fileUpload, String clientId, String mode) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
		writer.writeAttribute("type", "file", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
        
        if(fileUpload.isMultiple()) writer.writeAttribute("multiple", "multiple", null);
        if(fileUpload.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        
        if(mode.equals("simple")) {
            String style = fileUpload.getStyle();
            String styleClass = fileUpload.getStyleClass();
            
            if(style != null) writer.writeAttribute("style", style, "style");
            if(styleClass != null) writer.writeAttribute("class", styleClass, "styleClass");
        }
        
        renderDynamicPassThruAttributes(context, fileUpload);
        
		writer.endElement("input");
    }
    
    protected void encodeButton(FacesContext context, String label, String styleClass, String icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String cssClass = HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " ui-state-disabled " + styleClass;
        
        writer.startElement("button", null);
		writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", cssClass, null);
        writer.writeAttribute("disabled", "disabled", null);
        
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
}