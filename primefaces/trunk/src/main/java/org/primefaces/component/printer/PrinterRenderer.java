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
package org.primefaces.component.printer;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class PrinterRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Printer printer = (Printer) component;
		String parentClientId = printer.getParent().getClientId(facesContext);
		UIComponent target = printer.findComponent(printer.getTarget());
		if(target == null)
			throw new FacesException("Cannot find component \"" + printer.getTarget() + "\" in view.");
		
		writer.startElement("script", printer);
		writer.writeAttribute("type", "text/javascript", null);
			
		writer.write("jQuery(PrimeFaces.escapeClientId('" + parentClientId + "')).click(function(e) {\n");
		writer.write("e.preventDefault();\n");
		writer.write("jQuery(PrimeFaces.escapeClientId('" + target.getClientId(facesContext) + "')).jqprint();\n");
		writer.write("});");

		writer.endElement("script");
	}
}
