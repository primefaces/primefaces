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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.fileupload.FileUpload;

public class FileUploadRenderer extends org.primefaces.component.fileupload.FileUploadRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        FileUpload fileUpload = (FileUpload) component;
        String clientId = fileUpload.getClientId(context);
		String style = fileUpload.getStyle();
        String styleClass = fileUpload.getStyleClass();
        styleClass = (styleClass == null) ? FileUpload.MOBILE_CONTAINER_CLASS : FileUpload.MOBILE_CONTAINER_CLASS + " " + styleClass;
        if(fileUpload.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }
        
        writer.startElement("div", null);
        writer.writeAttribute("id", this, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
                
        writer.writeAttribute("class", styleClass, null);
        
        encodeInputField(context, fileUpload, clientId + "_input");
        
        writer.endElement("div");
	}

    @Override
    protected void encodeInputField(FacesContext context, FileUpload fileUpload, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", fileUpload);
        writer.writeAttribute("data-role", "none", null);
		writer.writeAttribute("type", "file", null);
		writer.writeAttribute("name", clientId, null);
        
        if(fileUpload.isMultiple()) writer.writeAttribute("multiple", "multiple", null);
        if(fileUpload.getStyle() != null) writer.writeAttribute("style", fileUpload.getStyle(), "style");
        if(fileUpload.getStyleClass() != null) writer.writeAttribute("class", fileUpload.getStyleClass(), "styleClass");
        if(fileUpload.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        
        renderDynamicPassThruAttributes(context, fileUpload);
        
		writer.endElement("input");
    }
    
    @Override
    public String getSimpleInputDecodeId(FileUpload fileUpload, FacesContext context) {
        return fileUpload.getClientId(context) + "_input";
    }
}
