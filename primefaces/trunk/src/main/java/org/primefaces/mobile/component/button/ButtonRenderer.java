/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.mobile.component.button;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class ButtonRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Button button = (Button) component;

        writer.startElement("a", component);
        writer.writeAttribute("href", "#" + button.getHref(), null);
        writer.writeAttribute("data-role", "button", null);

        if(button.getIcon() != null) writer.writeAttribute("data-icon", button.getIcon(), null);
        if(button.getIconPos() != null) writer.writeAttribute("data-iconpos", button.getIconPos(), null);
        if(button.getRole() != null) writer.writeAttribute("data-rel", button.getRole(), null);
        if(button.getTransition() != null) writer.writeAttribute("data-transition", button.getTransition(), null);
        if(button.getSwatch() != null) writer.writeAttribute("data-theme", button.getSwatch(), null);
        if(button.getStyle() != null) writer.writeAttribute("style", button.getStyle(), null);
        if(button.getStyleClass() != null) writer.writeAttribute("class", button.getStyleClass(), null);
        
        writer.writeText(button.getValue(), null);
        writer.endElement("a");
    }
}
