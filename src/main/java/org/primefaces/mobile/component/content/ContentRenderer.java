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
package org.primefaces.mobile.component.content;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class ContentRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Content content = (Content) component;
        String style = content.getStyle();
        String styleClass = content.getStyleClass();
        styleClass = (styleClass == null) ? "ui-content" : "ui-content " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("role", "main", null);
        writer.writeAttribute("class", styleClass, null);
        
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        
        renderDynamicPassThruAttributes(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        context.getResponseWriter().endElement("div");
    }
}