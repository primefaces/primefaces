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
package org.primefaces.component.watermark;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class WatermarkRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Watermark watermark = (Watermark) component;
		String _for = watermark.getFor();
		UIComponent forComponent = watermark.findComponent(_for);
		if(forComponent == null) {
			throw new FacesException("Cannot find component \"" + _for + "\" in view.");
		}
		String forClientId = forComponent.getClientId(context);
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		writer.write("PrimeFaces.onContentReady('" + forClientId + "', function() {");
		writer.write("jQuery(PrimeFaces.escapeClientId('" + forClientId + "')).watermark('" + watermark.getValue() + "', {className:'pf-watermark'});");
		writer.write("});");
		writer.endElement("script");
	}
}