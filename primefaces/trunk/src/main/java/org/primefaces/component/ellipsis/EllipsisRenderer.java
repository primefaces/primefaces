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
package org.primefaces.component.ellipsis;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class EllipsisRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Ellipsis ellipsis = (Ellipsis) component;
        Object value = ellipsis.getValue();
        //String styleClass = ellipsis.getStyleClass();
        //styleClass = styleClass == null ? Ellipsis.STYLE_CLASS : Ellipsis.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", ellipsis);
        writer.writeAttribute("class", "ui-widget ui-ellipsis", "id");
        writer.writeAttribute("onclick", "PrimeFaces.ellipsis($(this));", null);
        
        if (value != null) {
            String text = value.toString();
            
            if (ellipsis.isEscape()) {
                writer.writeText(text, "value");    
            } else {
                writer.write(text);
            }
        }
        
        renderChildren(context, ellipsis); //?????????????????????????????????
        
        writer.endElement("div");
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //nothing to do
    }
}
