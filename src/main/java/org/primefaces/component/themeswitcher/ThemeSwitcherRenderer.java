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
package org.primefaces.component.themeswitcher;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class ThemeSwitcherRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ThemeSwitcher ts = (ThemeSwitcher) component;
		
		encodeMarkup(context, ts);
		encodeScript(context, ts);
	}
	
	protected void encodeMarkup(FacesContext context, ThemeSwitcher ts) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("div", ts);
		writer.writeAttribute("id", ts.getClientId(context), null);
		writer.endElement("div");
	}

	protected void encodeScript(FacesContext context, ThemeSwitcher ts) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function(){");
		
		writer.write(ts.resolveWidgetVar() + " = PrimeFaces.widget.ThemeSwitcher('" + ts.getClientId(context) + "', {");
		
		writer.write("width:" + ts.getWidth());
		writer.write(",height:" + ts.getHeight());
		
		if(ts.getButtonHeight() != 14) writer.write(",buttonHeight:" + ts.getButtonHeight());
		if(ts.getButtonPreText() != null) writer.write(",buttonPreText:'" + ts.getButtonPreText() + "'");
		if(ts.getInitialText() != null) writer.write(",initialText:'" + ts.getInitialText() + "'");
		if(ts.getTheme() != null) writer.write(",loadTheme:'" + ts.getTheme() + "'");
        if(ts.getOnSelect() != null) writer.write(",onSelect: function() {" + ts.getOnSelect() + ";}");
		
		writer.write("});});");
        
		writer.endElement("script");
	}
}