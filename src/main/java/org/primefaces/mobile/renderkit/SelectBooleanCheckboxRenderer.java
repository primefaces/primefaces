/*
 * Copyright 2009-2013 PrimeTek.
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
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.util.ComponentUtils;

public class SelectBooleanCheckboxRenderer extends org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckboxRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SelectBooleanCheckbox checkbox = (SelectBooleanCheckbox) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = checkbox.getClientId(context);
        String inputId = clientId + "_input";
        boolean checked = Boolean.valueOf(ComponentUtils.getValueToRender(context, checkbox));
        boolean disabled = checkbox.isDisabled();
        String itemLabel = checkbox.getItemLabel();
        
        writer.startElement("label", component);
        
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, "id");
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "checkbox", null);

        if (checked) writer.writeAttribute("checked", "checked", null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        if (checkbox.getTabindex() != null) writer.writeAttribute("tabindex", checkbox.getTabindex(), null);
        
        renderOnchange(context, checkbox);
        
        writer.endElement("input");
        
        if (itemLabel != null) {
            writer.writeText(itemLabel, null);
        }
        
        writer.endElement("label");
    }
}
