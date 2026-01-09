/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.clock;

import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Clock.DEFAULT_RENDERER, componentFamily = Clock.COMPONENT_FAMILY)
public class ClockRenderer extends CoreRenderer<Clock> {

    @Override
    public void decode(FacesContext context, Clock component) {
        if (component.isSyncRequest()) {
            PrimeFaces.current().ajax().addCallbackParam("datetime", System.currentTimeMillis());
            context.renderResponse();
        }
    }

    @Override
    public void encodeEnd(FacesContext context, Clock component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Clock component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        if (component.getDisplayMode().equals("analog")) {
            writer.startElement("div", component);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("class", Clock.ANALOG_STYLE_CLASS, null);
            writer.endElement("div");
        }
        else {
            writer.startElement("span", component);
            writer.writeAttribute("id", clientId, null);
            writer.writeAttribute("class", Clock.STYLE_CLASS, null);
            writer.endElement("span");
        }
    }

    protected void encodeScript(FacesContext context, Clock component) throws IOException {
        String mode = component.getMode();
        WidgetBuilder wb = getWidgetBuilder(context);
        Locale locale = LocaleUtils.getCurrentLocale(context);

        wb.init("Clock", component);
        wb.attr("mode", mode)
                .attr("pattern", component.getPattern(), null)
                .attr("displayMode", component.getDisplayMode())
                .attr("locale", locale.toString());

        if ("server".equals(mode)) {
            wb.attr("value", getValueWithTimeZone(locale, component));

            if (component.isAutoSync()) {
                wb.attr("autoSync", true).attr("syncInterval", component.getSyncInterval());
            }
        }

        wb.finish();
    }

    protected String getValueWithTimeZone(Locale locale, Clock component) {
        if (locale == null) {
            return Constants.EMPTY_STRING;
        }

        TemporalAccessor time = ZonedDateTime.now();
        Object value = component.getValue();
        if (value != null) {
            if (value instanceof Date) {
                time = CalendarUtils.convertDate2LocalDateTime((Date) value);
            }
            else if (value instanceof TemporalAccessor) {
                time = (TemporalAccessor) value;
            }
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", locale)
                    .withZone(CalendarUtils.calculateZoneId(component.getTimeZone()));
        return dateTimeFormatter.format(time);
    }
}
