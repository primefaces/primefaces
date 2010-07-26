/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.tooltip;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.ComponentUtils;

public class TooltipRenderer extends CoreRenderer {

	private static String [] definedStyles = new String[]{"cream","dark","green","light","red","blue"};
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Tooltip tooltip = (Tooltip) component;
		
		encodeScript(facesContext, tooltip);
	}

	private void encodeScript(FacesContext facesContext, Tooltip tooltip) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String var = createUniqueWidgetVar(facesContext, tooltip);
		boolean global = tooltip.isGlobal();
		String forComponent = null;
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		if(!global) {
			forComponent = tooltip.getParent().getClientId(facesContext);
			writer.write("PrimeFaces.onContentReady('" + forComponent + "', function() {\n");
		}
			
		writer.write(var + " = new PrimeFaces.widget.Tooltip({");
		writer.write("global:" + global);
		if(!global) {
			writer.write(",forComponent:'" + forComponent + "'");
			writer.write(",content:'");
			if(tooltip.getValue() == null)
				renderChildren(facesContext, tooltip);
			else
				writer.write(ComponentUtils.getStringValueToRender(facesContext, tooltip).replaceAll("'", "\\\\'"));
			
			writer.write("'");
		}
		
		//Events
		writer.write(",show:{when:{event:'" + tooltip.getShowEvent()+"'}, delay:" + tooltip.getShowDelay() + ", effect:{length:" + tooltip.getShowEffectLength() + ", type: '"+tooltip.getShowEffect() + "'}}");
		writer.write(",hide:{when:{event:'" + tooltip.getHideEvent()+"'}, delay:" + tooltip.getHideDelay() + ", effect:{length:" + tooltip.getHideEffectLength() + ", type: '"+tooltip.getHideEffect() + "'}}");
	
		//Position
		writer.write(",position: {corner:{");
		writer.write("target:'" + tooltip.getTargetPosition() + "'");
		writer.write(",tooltip:'" + tooltip.getPosition() + "'");
		writer.write("}}");

		//Style
		String style = tooltip.getStyle();
		if(ArrayUtils.contains(definedStyles, style))
			writer.write(",style:{name:'" + style + "'}");
		else
			writer.write(",style:" + style  + "\n");
		
		writer.write("});\n");	
		
		if(!global) {
			writer.write("});\n");	
		}
		
		writer.endElement("script");
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		
	}

	public boolean getRendersChildren() {
		return true;
	}
}