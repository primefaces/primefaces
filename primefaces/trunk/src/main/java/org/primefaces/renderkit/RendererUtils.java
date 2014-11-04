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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.util.HTML;

public class RendererUtils {
    
    private RendererUtils() {}
    
    public static void encodeCheckbox(FacesContext context, boolean checked) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon = checked ? HTML.CHECKBOX_CHECKED_ICON_CLASS : HTML.CHECKBOX_UNCHECKED_ICON_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_CLASS, null);
                
        writer.startElement("div", null);
        writer.writeAttribute("class", HTML.CHECKBOX_BOX_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");

        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    public static void encodeCheckbox(FacesContext context, boolean checked, boolean partialSelected, boolean disabled, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon;
        String boxClass = disabled ? HTML.CHECKBOX_BOX_CLASS + " ui-state-disabled" : HTML.CHECKBOX_BOX_CLASS;
        String containerClass = (styleClass == null) ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;
        
        if (checked)
            icon = HTML.CHECKBOX_CHECKED_ICON_CLASS;
        else if (partialSelected)
            icon = HTML.CHECKBOX_PARTIAL_CHECKED_ICON_CLASS;
        else
            icon = HTML.CHECKBOX_UNCHECKED_ICON_CLASS;
        
        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);
                
        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");

        writer.endElement("div");
        
        writer.endElement("div");
    }
    
    public static void renderPassThroughAttributes(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String, Object> passthroughAttributes = component.getPassThroughAttributes(false);
        
        if (passthroughAttributes != null && !passthroughAttributes.isEmpty()) {
            for (Map.Entry<String, Object> attribute : passthroughAttributes.entrySet()) {
               
                Object attributeValue = attribute.getValue();
                if (attributeValue != null) {
                    String value = null;
                    
                    if (attributeValue instanceof ValueExpression) {
                        Object expressionValue = ((ValueExpression) attributeValue).getValue(context.getELContext());
                        if (expressionValue != null) {
                            value = expressionValue.toString();
                        }
                    }
                    else {
                        value = attributeValue.toString();
                    }
                    
                    if (value != null) {
                        writer.writeAttribute(attribute.getKey(), value, null);
                    }
                }
            }
        }
    }
}
