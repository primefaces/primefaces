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
package org.primefaces.mobile.component.buttongroup;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class ButtonGroupRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ButtonGroup group = (ButtonGroup) component;

        writer.startElement("div", group);
        writer.writeAttribute("id", group, null);
        writer.writeAttribute("data-role", "controlgroup", null);
        writer.writeAttribute("data-type", group.getOrientation(), null);

        if(group.getStyle() != null) writer.writeAttribute("style", group.getStyle(), null);
        if(group.getStyleClass() != null) writer.writeAttribute("class", group.getStyleClass(), null);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        context.getResponseWriter().endElement("div");
    }
}