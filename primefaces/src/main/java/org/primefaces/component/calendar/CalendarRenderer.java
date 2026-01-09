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
package org.primefaces.component.calendar;

import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Locale;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Calendar.DEFAULT_RENDERER, componentFamily = Calendar.COMPONENT_FAMILY)
public class CalendarRenderer extends BaseCalendarRenderer<Calendar> {

    @Override
    protected void encodeMarkup(FacesContext context, Calendar component, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String styleClass = component.getStyleClass();
        styleClass = (styleClass == null) ? Calendar.CONTAINER_CLASS : Calendar.CONTAINER_CLASS + " " + styleClass;
        String inputId = clientId + "_input";
        boolean popup = component.isPopup();

        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        //inline container
        if (!popup) {
            writer.startElement("div", null);
            writer.writeAttribute("id", clientId + "_inline", null);
            writer.endElement("div");
        }

        //input
        encodeInput(context, component, inputId, value, popup);

        writer.endElement("span");

    }

    @Override
    protected void encodeScript(FacesContext context, Calendar component, String value) throws IOException {
        Locale locale = component.calculateLocale(context);
        String pattern = component.calculateWidgetPattern();
        String mask = component.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Calendar", component);

        wb.attr("popup", component.isPopup())
                .attr("locale", locale.toString())
                .attr("dateFormat", CalendarUtils.convertPattern(pattern));

        //default date
        Object pagedate = component.getPagedate();
        String defaultDate = null;

        if (component.isConversionFailed()) {
            Class<?> dateType = resolveDateType(context, component);
            defaultDate = CalendarUtils.getValueAsString(context, component, CalendarUtils.now(component, dateType));
        }
        else if (!isValueBlank(value)) {
            defaultDate = value;
        }
        else if (pagedate != null) {
            defaultDate = CalendarUtils.getValueAsString(context, component, pagedate);
        }

        wb.attr("defaultDate", defaultDate, null)
                .attr("numberOfMonths", component.getPages(), 1)
                .attr("minDate", CalendarUtils.getValueAsString(context, component, component.getMindate(), pattern), null)
                .attr("maxDate", CalendarUtils.getValueAsString(context, component, component.getMaxdate(), pattern), null)
                .attr("showButtonPanel", component.isShowButtonPanel(), false)
                .attr("showTodayButton", component.isShowTodayButton(), true)
                .attr("showWeek", component.isShowWeek(), false)
                .attr("disabledWeekends", component.isDisabledWeekends(), false)
                .attr("disabled", component.isDisabled(), false)
                .attr("readonly", component.isReadonly(), false)
                .attr("yearRange", component.getYearRange(), null)
                .attr("focusOnSelect", component.isFocusOnSelect(), false)
                .attr("shortYearCutoff", component.getShortYearCutoff(), null)
                .attr("touchable", ComponentUtils.isTouchable(context, component),  true);

        if (component.isNavigator()) {
            wb.attr("changeMonth", true).attr("changeYear", true);
        }

        if (component.getEffect() != null) {
            wb.attr("showAnim", component.getEffect()).attr("duration", component.getEffectDuration());
        }

        String beforeShowDay = component.getBeforeShowDay();
        if (beforeShowDay != null) {
            wb.nativeAttr("preShowDay", beforeShowDay);
        }

        String beforeShow = component.getBeforeShow();
        if (beforeShow != null) {
            wb.nativeAttr("preShow", beforeShow);
        }

        String showOn = component.getShowOn();
        if (!"focus".equalsIgnoreCase(showOn)) {
            wb.attr("showOn", showOn).attr("buttonTabindex", component.getButtonTabindex());
        }

        if (component.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", component.isSelectOtherMonths());
        }

        if (component.hasTime()) {
            String timeControlType = component.getTimeControlType();

            wb.attr("timeOnly", component.isTimeOnly())
                    .attr("stepHour", component.getStepHour())
                    .attr("stepMinute", component.getStepMinute())
                    .attr("stepSecond", component.getStepSecond())
                    .attr("hourMin", component.getMinHour())
                    .attr("hourMax", component.getMaxHour())
                    .attr("minuteMin", component.getMinMinute())
                    .attr("minuteMax", component.getMaxMinute())
                    .attr("secondMin", component.getMinSecond())
                    .attr("secondMax", component.getMaxSecond())
                    .attr("timeInput", component.isTimeInput())
                    .attr("controlType", timeControlType, null)
                    .attr("showHour", component.getShowHour(), null)
                    .attr("showMinute", component.getShowMinute(), null)
                    .attr("showSecond", component.getShowSecond(), null)
                    .attr("showMillisec", component.getShowMillisec(), null)
                    .attr("oneLine", component.isOneLine())
                    .attr("hour", component.getDefaultHour())
                    .attr("minute", component.getDefaultMinute())
                    .attr("second", component.getDefaultSecond())
                    .attr("millisec", component.getDefaultMillisecond());

            String timeControlObject = component.getTimeControlObject();
            if ("custom".equalsIgnoreCase(timeControlType)) {
                wb.nativeAttr("timeControlObject", timeControlObject);
            }
        }

        if (mask != null && !"false".equals(mask)) {
            String patternTemplate = component.getPattern() == null ? pattern : component.getPattern();
            String maskTemplate = ("true".equals(mask)) ? component.convertPattern(patternTemplate) : mask;
            wb.attr("mask", maskTemplate).attr("maskSlotChar", component.getMaskSlotChar(), "_").attr("maskAutoClear", component.isMaskAutoClear(), true);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }
}
