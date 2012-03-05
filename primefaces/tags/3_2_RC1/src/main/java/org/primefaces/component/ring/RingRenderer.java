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
package org.primefaces.component.ring;

import java.io.IOException;
import java.util.Collection;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class RingRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Ring ring = (Ring) component;

        encodeMarkup(context, ring);
        encodeScript(context, ring);
	}

	public void encodeMarkup(FacesContext context, Ring ring) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Collection<?> value = (Collection<?>) ring.getValue();
        String var = ring.getVar();
        String style = ring.getStyle();
        String styleClass = ring.getStyleClass();
        styleClass = styleClass == null ? Ring.STYLE_CLASS : Ring.STYLE_CLASS + " " + ring.getStyleClass();

        writer.startElement("ul", ring);
        writer.writeAttribute("id", ring.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        for(int rowIndex = 0; rowIndex < ring.getRowCount(); rowIndex++) {
            ring.setRowIndex(rowIndex);

            writer.startElement("li", ring);
            writer.writeAttribute("class", "ui-state-default ui-corner-all", null);

            for(UIComponent child : ring.getChildren()) {
                child.encodeAll(context);
            }

            writer.endElement("li");
        }

        ring.setRowIndex(-1);

        writer.endElement("ul");
	}

    public void encodeScript(FacesContext context, Ring ring) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ring.getClientId(context);
        String easing = ring.getEasing();

        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Ring','" + ring.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",startingChild:" + ring.getFirst());

        if(easing != null) 
            writer.write(",easing:'" + easing + "'");

        writer.write("},'ring');});");

        endScript(writer);
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}
