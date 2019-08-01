/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.datepicker.DatePickerRenderer;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

public abstract class BaseCalendarRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        UICalendar uicalendar = (UICalendar) component;

        if (!shouldDecode(uicalendar)) {
            return;
        }

        String param = uicalendar.getClientId(context) + "_input";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(param);

        if (submittedValue != null) {
            uicalendar.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, uicalendar);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        UICalendar uicalendar = (UICalendar) component;
        String markupValue = CalendarUtils.getValueAsString(context, uicalendar);
        String widgetValue = uicalendar.isTimeOnly() ? CalendarUtils.getTimeOnlyValueAsString(context, uicalendar) : markupValue;

        encodeMarkup(context, uicalendar, markupValue);
        encodeScript(context, uicalendar, widgetValue);
    }

    protected abstract void encodeMarkup(FacesContext context, UICalendar uicalendar, String value) throws IOException;

    protected abstract void encodeScript(FacesContext context, UICalendar uicalendar, String value) throws IOException;

    protected void encodeInput(FacesContext context, UICalendar uicalendar, String id, String value, boolean popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String type = popup ? uicalendar.getType() : "hidden";
        String inputStyle = uicalendar.getInputStyle();
        String inputStyleClass = uicalendar.getInputStyleClass();

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", type, null);

        if (!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        boolean readonly = false;
        boolean disabled = false;

        if (popup) {
            inputStyleClass = (inputStyleClass == null) ? UICalendar.INPUT_STYLE_CLASS
                                                        : UICalendar.INPUT_STYLE_CLASS + " " + inputStyleClass;
            readonly = uicalendar.isReadonly() || uicalendar.isReadonlyInput();

            if (uicalendar.isDisabled()) {
                inputStyleClass = inputStyleClass + " ui-state-disabled";
                disabled = true;
            }
            if (!uicalendar.isValid()) {
                inputStyleClass = inputStyleClass + " ui-state-error";
            }

            writer.writeAttribute("class", inputStyleClass, null);

            if (inputStyle != null) {
                writer.writeAttribute("style", inputStyle, null);
            }

            renderPassThruAttributes(context, uicalendar, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, uicalendar, HTML.INPUT_TEXT_EVENTS);
        }

        renderAccessibilityAttributes(context, uicalendar, disabled, readonly);
        renderValidationMetadata(context, uicalendar);

        writer.endElement("input");
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        String submittedValue = isValueBlank((String) value) ? null : ((String) value).trim();
        if (submittedValue == null) {
            return null;
        }

        UICalendar calendar = (UICalendar) component;

        //Delegate to user supplied converter if defined
        Class type = resolveDateType(context, calendar);
        Converter converter = resolveConverter(context, calendar, type);
        if (converter != null) {
            try {
                return converter.getAsObject(context, calendar, submittedValue);
            }
            catch (ConverterException e) {
                calendar.setConversionFailed(true);
                throw e;
            }
        }

        // Java 8 Date/Time API
        if (Temporal.class.isAssignableFrom(type)) {
            return convertToJava8DateTimeAPI(context, calendar, type, submittedValue);
        }
        else if (Date.class.isAssignableFrom(type)) {
            return convertToLegacyDateAPI(context, calendar, submittedValue);
        }

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, type.getName() + " not supported", null);
        throw new ConverterException(message);
    }

    protected Date convertToLegacyDateAPI(FacesContext context, UICalendar calendar, String submittedValue) {
        //Code for backward-compatibility with java.util.Date - may be removed at some point in the future
        SimpleDateFormat format = new SimpleDateFormat(calendar.calculatePattern(), calendar.calculateLocale(context));
        format.setLenient(false);
        format.setTimeZone(TimeZone.getTimeZone(CalendarUtils.calculateZoneId(calendar.getTimeZone())));

        try {
            return format.parse(submittedValue);
        }
        catch (ParseException e) {
            throw createConverterException(context, calendar, submittedValue, format.format(new Date()));
        }
    }

    protected Temporal convertToJava8DateTimeAPI(FacesContext context, UICalendar calendar, Class type, String submittedValue) {
        if (type == LocalDate.class || type == YearMonth.class) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(calendar.calculatePattern())
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1) //because of Month Picker which does not contain day of month
                    .toFormatter(calendar.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(calendar.getTimeZone()));

            try {
                return type == LocalDate.class
                        ? LocalDate.parse(submittedValue, formatter)
                        : YearMonth.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, calendar, submittedValue, formatter.format(LocalDate.now()));
            }
        }
        else if (type == LocalTime.class) {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(calendar.calculateTimeOnlyPattern(), calendar.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(calendar.getTimeZone()));

            try {
                return LocalTime.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, calendar, submittedValue, formatter.format(LocalDate.now()));
            }
        }
        else if (type == LocalDateTime.class) {
            //known issue: https://github.com/primefaces/primefaces/issues/4625
            //known issue: https://github.com/primefaces/primefaces/issues/4626

            //TODO: remove temporary (ugly) work-around for adding fixed time-pattern
            String pattern = calendar.calculatePattern();
            if (this instanceof DatePickerRenderer) {
                pattern += " HH:mm";
            }

            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(pattern)
                    .toFormatter(calendar.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(calendar.getTimeZone()));

            try {
                return LocalDateTime.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, calendar, submittedValue, formatter.format(LocalDate.now()));
            }
        }

        //TODO: implement if necessary
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ZonedDateTime not supported", null);
        throw new ConverterException(message);
    }

    protected ConverterException createConverterException(FacesContext context,
                                                          UICalendar calendar,
                                                          String submittedValue,
                                                          Object param1) {
        calendar.setConversionFailed(true);

        FacesMessage message = null;
        Object[] params = new Object[3];
        params[0] = submittedValue;
        params[1] = param1;
        params[2] = MessageFactory.getLabel(context, calendar);

        if (calendar.isTimeOnly()) {
            message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
        }
        else if (calendar.hasTime()) {
            message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
        }
        else {
            message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
        }

        return new ConverterException(message);
    }

    protected Class resolveDateType(FacesContext context, UICalendar calendar) {
        ValueExpression ve = calendar.getValueExpression("value");
        Class type = ve.getType(context.getELContext());

        // If type could not be determined via value-expression try it this way. (Very unlikely, this happens in real world.)
        if (type == null) {
            if (calendar.isTimeOnly()) {
                type = LocalTime.class;
            }
            else if (calendar.hasTime()) {
                type = LocalDateTime.class;
            }
            else {
                type = LocalDate.class;
            }
        }
        else if (Collection.class.isAssignableFrom(type)) {
            //Datepicker with selectionMode = multiple and selectionMode = range.
            ValueReference valueReference = ValueExpressionAnalyzer.getReference(context.getELContext(), ve);
            Object base = valueReference.getBase();
            Object property = valueReference.getProperty();

            try {
                Field field = LangUtils.getUnproxiedClass(base.getClass()).getDeclaredField((String) property);
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type listType = parameterizedType.getActualTypeArguments()[0];
                type = Class.forName(listType.getTypeName());
            }
            catch (ReflectiveOperationException ex) {
                //NOOP
            }
        }

        return type;
    }

    protected Converter resolveConverter(FacesContext context, UICalendar calendar, Class type) {
        //Delegate to user supplied converter if defined
        Converter converter = calendar.getConverter();
        if (converter != null) {
            return converter;
        }

        //Delegate to global defined converter (e.g. joda)
        if (type != null
                && type != Object.class
                && type != Date.class
                && type != LocalDate.class
                && type != LocalDateTime.class
                && type != LocalTime.class) {

            converter = context.getApplication().createConverter(type);
        }

        return converter;
    }
}
