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
package org.primefaces.component.sticky;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class StickyRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Sticky sticky = (Sticky) component;
     
        encodeMarkup(context, sticky);
        encodeScript(context, sticky);
    }
    
    protected void encodeMarkup(FacesContext context, Sticky sticky) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", sticky);
        writer.writeAttribute("id", sticky.getClientId(context), null);
        writer.endElement("div");
    }
    
    protected void encodeScript(FacesContext context, Sticky sticky) throws IOException {
        String target = sticky.getTarget();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Sticky", sticky.resolveWidgetVar(), sticky.getClientId(context))
            .attr("target", SearchExpressionFacade.resolveClientIds(context, sticky, target))
            .attr("margin", sticky.getMargin(), 0)
            .finish();
    }
}