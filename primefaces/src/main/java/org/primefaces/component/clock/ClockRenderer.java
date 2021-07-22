/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LocaleUtils;
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
        String mode = clock.getMode();
        WidgetBuilder wb = getWidgetBuilder(context);
        Locale locale = LocaleUtils.getCurrentLocale(context);

        wb.init("Clock", clock);
        wb.attr("mode", mode)
                .attr("pattern", clock.getPattern(), null)
                .attr("displayMode", clock.getDisplayMode())
                .attr("locale", locale.toString());

        if ("server".equals(mode)) {
            wb.attr("value", getValueWithTimeZone(locale, clock));

            if (clock.isAutoSync()) {
                wb.attr("autoSync", true).attr("syncInterval", clock.getSyncInterval());
            }
        }

        wb.finish();
    }

    protected String getValueWithTimeZone(Locale locale, Clock clock) {
        if (locale == null) {
            return Constants.EMPTY_STRING;
        }

        TemporalAccessor time = LocalDateTime.now();
        Object value = clock.getValue();
        if (value != null) {
            if (value instanceof Date) {
                time = CalendarUtils.convertDate2LocalDateTime((Date) value);
            }
            else if (value instanceof TemporalAccessor) {
                time = (TemporalAccessor) value;
            }
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", locale)
                    .withZone(CalendarUtils.calculateZoneId(clock.getTimeZone()));
        return dateTimeFormatter.format(time);
    }
}
