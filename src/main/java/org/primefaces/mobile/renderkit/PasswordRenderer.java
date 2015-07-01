/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.primefaces.org/elite/license.xhtml
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.password.Password;
import org.primefaces.context.RequestContext;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class PasswordRenderer extends org.primefaces.component.password.PasswordRenderer {

   @Override
    protected void encodeMarkup(FacesContext context, Password password) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = password.getClientId(context);
        boolean disabled = password.isDisabled();
        
        String inputClass = Password.MOBILE_STYLE_CLASS;
        inputClass = password.isValid() ? inputClass : inputClass + " ui-state-error";
        inputClass = !disabled ? inputClass : inputClass + " ui-state-disabled";
		String styleClass = password.getStyleClass() == null ? inputClass : inputClass + " " + password.getStyleClass();
        
		writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(password.getStyle() != null) { 
            writer.writeAttribute("style", password.getStyle(), null);
        }
		
		encodeInput(context, password, clientId);
        encodeClearIcon(context, password);

		writer.endElement("div");
	}   
    
    @Override
    protected void encodeScript(FacesContext context, Password password) throws IOException {
		String clientId = password.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Password", password.resolveWidgetVar(), clientId);

        wb.finish();
	}
    
    protected void encodeInput(FacesContext context, Password password, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String valueToRender = ComponentUtils.getValueToRender(context, password);
        
        writer.startElement("input", password);
        writer.writeAttribute("data-role", "none", null);
        writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("type", "password", null);           
      
        if(password.isDisabled()) writer.writeAttribute("disabled", "disabled", null);
        if(password.isReadonly()) writer.writeAttribute("readonly", "readonly", null);
        if(valueToRender != null) writer.writeAttribute("value", valueToRender , null);
        
        renderPassThruAttributes(context, password, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, password, HTML.INPUT_TEXT_EVENTS);
        
        if(RequestContext.getCurrentInstance().getApplicationContext().getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, password);
        }
        
        writer.endElement("input");
    }
    
    protected void encodeClearIcon(FacesContext context, Password password) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Password.MOBILE_CLEAR_ICON_CLASS, null);
        writer.endElement("a");
    }
}
