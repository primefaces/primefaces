/**
 * Copyright 2009-2018 PrimeTek.
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ClockRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Clock clock = (Clock) component;

        if (clock.isSyncRequest()) {
            PrimeFaces.current().ajax().addCallbackParam("datetime", System.currentTimeMillis());
            context.renderResponse();
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Clock clock = (Clock) component;

        encodeMarkup(context, clock);
        encodeScript(context, clock);
    }

    protected void encodeMarkup(FacesContext context, Clock clock) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = clock.getClientId(context);

        if (clock.getDisplayMode().equals("analog")) {
            writer.startElement("div", clock);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("class", Clock.ANALOG_STYLE_CLASS, null);
            writer.endElement("div");
        }
        else {
            writer.startElement("span", clock);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("class", Clock.STYLE_CLASS, null);
            writer.endElement("span");
        }
    }

    protected void encodeScript(FacesContext context, Clock clock) throws IOException {
        String clientId = clock.getClientId(context);
        String mode = clock.getMode();
        WidgetBuilder wb = getWidgetBuilder(context);

        wb.init("Clock", clock.resolveWidgetVar(), clientId);
        wb.attr("mode", mode)
                .attr("pattern", clock.getPattern(), null)
                .attr("displayMode", clock.getDisplayMode())
                .attr("locale", context.getViewRoot().getLocale().toString());

        if (mode.equals("server")) {
            wb.attr("value", getValueWithTimeZone(context, clock));

            if (clock.isAutoSync()) {
                wb.attr("autoSync", true).attr("syncInterval", clock.getSyncInterval());
            }
        }

        wb.finish();
    }

    protected String getValueWithTimeZone(FacesContext context, Clock clock) {
        Locale locale = context.getViewRoot().getLocale();
        String value = "";

        if (locale != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", locale);
            dateFormat.setTimeZone(clock.calculateTimeZone());

            value = dateFormat.format(new Date());
        }

        return value;
    }
}
