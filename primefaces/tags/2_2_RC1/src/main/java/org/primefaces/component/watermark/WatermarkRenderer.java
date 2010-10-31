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
package org.primefaces.component.watermark;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class WatermarkRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Watermark watermark = (Watermark) component;
		String target = null;
		
		if(watermark.getFor() != null) {
			String _for = watermark.getFor();	
			UIComponent forComponent = watermark.findComponent(_for);
			if(forComponent == null) {
				throw new FacesException("Cannot find component \"" + _for + "\" in view.");
			}
			target = ComponentUtils.escapeJQueryId(forComponent.getClientId(context));
			
		} else if(watermark.getForElement() != null) {
			target = watermark.getForElement();
		} else {
			throw new FacesException("Either for or forElement options must be used to define a watermark");
		}
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("jQuery(function() {");
		writer.write("jQuery('" + target + "').watermark('" + watermark.getValue() + "', {className:'ui-watermark'});");
		writer.write("});");
		
		writer.endElement("script");
	}
}