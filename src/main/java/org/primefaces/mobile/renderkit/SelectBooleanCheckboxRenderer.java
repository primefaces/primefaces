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
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.util.ComponentUtils;

public class SelectBooleanCheckboxRenderer extends org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckboxRenderer {
    
    @Override
    public void encodeMarkup(FacesContext context, SelectBooleanCheckbox checkbox) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String inputId = clientId + "_input";
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, checkbox));
        boolean disabled = checkbox.isDisabled();
        String style = checkbox.getStyle();
        String styleClass = checkbox.getStyleClass();
        styleClass = (styleClass == null) ? SelectBooleanCheckbox.MOBILE_STYLE_CLASS: SelectBooleanCheckbox.MOBILE_STYLE_CLASS + " " + styleClass;
        
        writer.startElement("div", checkbox);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

        encodeLabel(context, checkbox, inputId, checked);
        encodeInput(context, checkbox, inputId, checked, disabled);
        
        writer.endElement("div");
    }
    
    protected void encodeLabel(FacesContext context, SelectBooleanCheckbox checkbox, String inputId, boolean checked) throws IOException {
        String itemLabel = checkbox.getItemLabel();
        
        if(itemLabel != null) {
            String labelClass = checked ? SelectBooleanCheckbox.MOBILE_LABEL_ON_CLASS: SelectBooleanCheckbox.MOBILE_LABEL_OFF_CLASS;
            ResponseWriter writer = context.getResponseWriter();            
            writer.startElement("label", null);
            writer.writeAttribute("for", inputId, null);
            writer.writeAttribute("class", labelClass, null);
            writer.writeText(itemLabel, null);
            writer.endElement("label");
        }
    }
    
    @Override
    protected void encodeInput(FacesContext context, SelectBooleanCheckbox checkbox, String inputId, boolean checked, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);
        writer.writeAttribute("data-role", "none", null);

        if (checked) writer.writeAttribute("checked", "checked", null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        
        renderOnchange(context, checkbox);
        renderDynamicPassThruAttributes(context, checkbox);
        
        writer.endElement("input");
    }
}
