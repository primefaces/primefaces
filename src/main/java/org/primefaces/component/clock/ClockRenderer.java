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
package org.primefaces.component.clock;

import java.io.IOException;
import java.util.Date;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ClockRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Clock clock = (Clock) component;
        
        encodeMarkup(context, clock);
        encodeScript(context, clock);
    }
    
    protected void encodeMarkup(FacesContext context, Clock clock) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("span", clock);
        writer.writeAttribute("id", clock.getClientId(), null);
        writer.writeAttribute("class", Clock.STYLE_CLASS, null);
        writer.endElement("span");
    }

    protected void encodeScript(FacesContext context, Clock clock) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = clock.getClientId(context);
        String mode = clock.getMode();
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.widget("Clock", clock.resolveWidgetVar(), clientId, false);
        wb.attr("mode", mode)
            .attr("pattern", clock.getPattern(), null);
        
        if(mode.equals("server")) {
            wb.attr("value", new Date().getTime());
        }

        startScript(writer, clientId);
        writer.write(wb.build());
        endScript(writer);
    }
}
