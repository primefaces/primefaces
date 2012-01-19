/*
 * Copyright 2009-2011 Prime Teknoloji.
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
package org.primefaces.component.lightbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class LightBoxRenderer extends CoreRenderer {

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LightBox lb = (LightBox) component;
		String clientId = lb.getClientId(context);
		
		writer.startElement("div", lb);
        writer.writeAttribute("id", clientId, "id");

        if(lb.getStyle() != null) writer.writeAttribute("style", lb.getStyle(), null);
        if(lb.getStyleClass() != null) writer.writeAttribute("class", lb.getStyleClass(), null);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LightBox lb = (LightBox) component;

		writer.endElement("div");

        encodeScript(context, component);
	}

	public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        LightBox lb = (LightBox) component;
		String clientId = lb.getClientId(context);
        
        startScript(writer, clientId);
        writer.write("$(function() {");
        writer.write("PrimeFaces.cw('LightBox','" + lb.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
		writer.write("});});");

		endScript(writer);
    }
}