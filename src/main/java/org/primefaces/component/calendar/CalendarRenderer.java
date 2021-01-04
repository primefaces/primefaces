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
package org.primefaces.component.calendar;

import java.io.IOException;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.UICalendar;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class CalendarRenderer extends BaseCalendarRenderer {

    @Override
    protected void encodeMarkup(FacesContext context, UICalendar uicalendar, String value) throws IOException {
        Calendar calendar = (Calendar) uicalendar;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = calendar.getClientId(context);
        String styleClass = calendar.getStyleClass();
        styleClass = (styleClass == null) ? Calendar.CONTAINER_CLASS : Calendar.CONTAINER_CLASS + " " + styleClass;
        String inputId = clientId + "_input";
        boolean popup = calendar.isPopup();

        writer.startElement("span", calendar);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (calendar.getStyle() != null) {
            writer.writeAttribute("style", calendar.getStyle(), null);
        }

        //inline container
        if (!popup) {
            writer.startElement("div", null);
            writer.writeAttribute("id", clientId + "_inline", null);
            writer.endElement("div");
        }

        //input
        encodeInput(context, calendar, inputId, value, popup);

        writer.endElement("span");

    }

    @Override
    protected void encodeScript(FacesContext context, UICalendar uicalendar, String value) throws IOException {
        Calendar calendar = (Calendar) uicalendar;
        Locale locale = calendar.calculateLocale(context);
        String pattern = calendar.calculateWidgetPattern();
        String mask = calendar.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Calendar", calendar);

        wb.attr("popup", calendar.isPopup())
                .attr("locale", locale.toString())
                .attr("dateFormat", CalendarUtils.convertPattern(pattern));

        //default date
        Object pagedate = calendar.getPagedate();
        String defaultDate = null;

        if (calendar.isConversionFailed()) {
            Class<?> dateType = resolveDateType(context, calendar);
            defaultDate = CalendarUtils.getValueAsString(context, calendar, CalendarUtils.now(uicalendar, dateType));
        }
        else if (!isValueBlank(value)) {
            defaultDate = value;
        }
        else if (pagedate != null) {
            defaultDate = CalendarUtils.getValueAsString(context, calendar, pagedate);
        }

        wb.attr("defaultDate", defaultDate, null)
                .attr("numberOfMonths", calendar.getPages(), 1)
                .attr("minDate", CalendarUtils.getValueAsString(context, calendar, calendar.getMindate(), pattern), null)
                .attr("maxDate", CalendarUtils.getValueAsString(context, calendar, calendar.getMaxdate(), pattern), null)
                .attr("showButtonPanel", calendar.isShowButtonPanel(), false)
                .attr("showTodayButton", calendar.isShowTodayButton(), true)
                .attr("showWeek", calendar.isShowWeek(), false)
                .attr("disabledWeekends", calendar.isDisabledWeekends(), false)
                .attr("disabled", calendar.isDisabled(), false)
                .attr("readonly", calendar.isReadonly(), false)
                .attr("yearRange", calendar.getYearRange(), null)
                .attr("focusOnSelect", calendar.isFocusOnSelect(), false)
                .attr("touchable", ComponentUtils.isTouchable(context, calendar),  true);

        if (calendar.isNavigator()) {
            wb.attr("changeMonth", true).attr("changeYear", true);
        }

        if (calendar.getEffect() != null) {
            wb.attr("showAnim", calendar.getEffect()).attr("duration", calendar.getEffectDuration());
        }

        String beforeShowDay = calendar.getBeforeShowDay();
        if (beforeShowDay != null) {
            wb.nativeAttr("preShowDay", beforeShowDay);
        }

        String beforeShow = calendar.getBeforeShow();
        if (beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        String showOn = calendar.getShowOn();
        if (!"focus".equalsIgnoreCase(showOn)) {
            wb.attr("showOn", showOn).attr("buttonTabindex", calendar.getButtonTabindex());
        }

        if (calendar.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", calendar.isSelectOtherMonths());
        }

        if (calendar.hasTime()) {
            String timeControlType = calendar.getTimeControlType();

            wb.attr("timeOnly", calendar.isTimeOnly())
                    .attr("stepHour", calendar.getStepHour())
                    .attr("stepMinute", calendar.getStepMinute())
                    .attr("stepSecond", calendar.getStepSecond())
                    .attr("hourMin", calendar.getMinHour())
                    .attr("hourMax", calendar.getMaxHour())
                    .attr("minuteMin", calendar.getMinMinute())
                    .attr("minuteMax", calendar.getMaxMinute())
                    .attr("secondMin", calendar.getMinSecond())
                    .attr("secondMax", calendar.getMaxSecond())
                    .attr("timeInput", calendar.isTimeInput())
                    .attr("controlType", timeControlType, null)
                    .attr("showHour", calendar.getShowHour(), null)
                    .attr("showMinute", calendar.getShowMinute(), null)
                    .attr("showSecond", calendar.getShowSecond(), null)
                    .attr("showMillisec", calendar.getShowMillisec(), null)
                    .attr("oneLine", calendar.isOneLine())
                    .attr("hour", calendar.getDefaultHour())
                    .attr("minute", calendar.getDefaultMinute())
                    .attr("second", calendar.getDefaultSecond())
                    .attr("millisec", calendar.getDefaultMillisec());

            String timeControlObject = calendar.getTimeControlObject();
            if ("custom".equalsIgnoreCase(timeControlType)) {
                wb.nativeAttr("timeControlObject", timeControlObject);
            }
        }

        if (mask != null && !"false".equals(mask)) {
            String patternTemplate = calendar.getPattern() == null ? pattern : calendar.getPattern();
            String maskTemplate = ("true".equals(mask)) ? calendar.convertPattern(patternTemplate) : mask;
            wb.attr("mask", maskTemplate).attr("maskSlotChar", calendar.getMaskSlotChar(), "_").attr("maskAutoClear", calendar.isMaskAutoClear(), true);
        }

        encodeClientBehaviors(context, calendar);

        wb.finish();
    }
}
