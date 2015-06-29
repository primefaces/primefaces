/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.growl.Growl;
import org.primefaces.util.WidgetBuilder;

public class GrowlRenderer extends org.primefaces.component.growl.GrowlRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Growl growl = (Growl) component;
        
        encodeMarkup(context, growl);
        encodeScript(context, growl);
	}
    
    protected void encodeMarkup(FacesContext context, Growl growl) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = growl.getClientId(context);
        
        writer.startElement("div", growl);
		writer.writeAttribute("id", clientId, "id");
        
        renderDynamicPassThruAttributes(context, growl);
        
		writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, Growl growl) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Growl", growl.resolveWidgetVar(), growl.getClientId(context));
        wb.attr("sticky", growl.isSticky(), false)
            .attr("life", growl.getLife(), 6000)
            .attr("escape", growl.isEscape(), true)
            .append(",msgs:");
        encodeMessages(context, growl);
        wb.finish();
    }
}