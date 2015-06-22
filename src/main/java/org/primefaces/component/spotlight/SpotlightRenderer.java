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
package org.primefaces.component.spotlight;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class SpotlightRenderer extends CoreRenderer {
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Spotlight spotlight = (Spotlight) component;
        
        encodeMarkup(context, spotlight);
        encodeScript(context, spotlight);
	}

    private void encodeMarkup(FacesContext context, Spotlight spotlight) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("span", spotlight);
        writer.writeAttribute("id", spotlight.getClientId(context), null);
        writer.endElement("span");
    }

    private void encodeScript(FacesContext context, Spotlight spotlight) throws IOException {
        String clientId = spotlight.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Spotlight", spotlight.resolveWidgetVar(), clientId);
        
        wb.attr("target", SearchExpressionFacade.resolveClientIds(context, spotlight, spotlight.getTarget()));
        wb.attr("active", spotlight.isActive(), false);
        
        wb.finish();
    }
}
