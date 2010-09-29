/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.divider;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class DividerRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Divider divider = (Divider) component;
        ResponseWriter writer = context.getResponseWriter();
        String style = divider.getStyle();
        String styleClass = divider.getStyleClass();
        styleClass = styleClass == null ? "ui-divider" : "ui-divider " + styleClass;
        String type = "ui-icon-grip-" + divider.getType() + "-vertical";

        writer.startElement("span", divider);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("span", divider);
        writer.writeAttribute("class", "ui-icon " + type, null);
        writer.endElement("span");
        
        writer.endElement("span");
    }
}
