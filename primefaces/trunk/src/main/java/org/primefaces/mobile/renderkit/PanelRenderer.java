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
import org.primefaces.component.panel.Panel;

public class PanelRenderer extends org.primefaces.component.panel.PanelRenderer {
    
    @Override
    protected void encodeScript(FacesContext context, Panel panel) throws IOException {
        //No widget
    }
    
    @Override
    protected void encodeMarkup(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        UIComponent header = panel.getFacet("header");
        String headerText = panel.getHeader();
    
        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("data-role", "collapsible", null);
        writer.writeAttribute("data-collapsed", panel.isCollapsed(), null);
        
        //header
        writer.startElement("h4", null);
        if (header != null) {
            renderChild(context, header);
        } else if (headerText != null) {
            writer.write(headerText);
        }
        writer.endElement("h4");
        
        //content
        writer.startElement("p", null);
        renderChildren(context, panel);
        writer.endElement("p");
        
        writer.endElement("div");
    }
}
