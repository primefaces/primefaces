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
package org.primefaces.mobile.component.header;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class HeaderRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Header header = (Header) component;
        String title = header.getTitle();
        String swatch = header.getSwatch();
        UIComponent left = header.getFacet("left");
        UIComponent right = header.getFacet("right");
        
        writer.startElement("div", null);
        writer.writeAttribute("id", header.getClientId(context), "id");
        writer.writeAttribute("data-role", "header", null);
        writer.writeAttribute("data-backbtn", "false", null);
        
        if(header.getStyle() != null) writer.writeAttribute("style", header.getStyle(), null);
        if(header.getStyleClass() != null) writer.writeAttribute("class", header.getStyleClass(), null);
        if(swatch != null) writer.writeAttribute("data-theme", swatch, null);
        if(header.isFixed())  writer.writeAttribute("data-position", "fixed", null);
        
        renderDynamicPassThruAttributes(context, component);

        if(left != null) {
            left.encodeAll(context);
        }
            
        if(title != null) {
             writer.startElement("h1", header);
             writer.writeText(title, null);
             writer.endElement("h1");
        }
        
        if(right != null) {
            right.getAttributes().put("styleClass", "ui-btn-right");
            right.encodeAll(context);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        context.getResponseWriter().endElement("div");
    }
}