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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.api.UICalendar;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.*;

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

        // #6068 ensure min is before max
        uicalendar.validateMinMax(context);

        encodeMarkup(context, uicalendar, markupValue);
        encodeScript(context, uicalendar, widgetValue);
    }

    protected abstract void encodeMarkup(FacesContext context, UICalendar uicalendar, String value) throws IOException;

    protected abstract void encodeScript(FacesContext context, UICalendar uicalendar, String value) throws IOException;

    protected void encodeInput(FacesContext context, UICalendar uicalendar, String id, String value, boolean popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String type = popup ? uicalendar.getType() : "hidden";
        String inputStyle = uicalendar.getInputStyle();

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
            String inputStyleClass = createStyleClass(uicalendar, UICalendar.PropertyKeys.inputStyleClass.name(), UICalendar.INPUT_STYLE_CLASS);
            readonly = uicalendar.isReadonly() || uicalendar.isReadonlyInput();
            disabled = uicalendar.isDisabled();

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
        Class<?> type = resolveDateType(context, calendar);
        if (type != null) {
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
                if (calendar.getTimeZone() != null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "'timeZone' attribute is not supported for " + type.getName() + ". Use an explicit converter instead of the built-in.",
                            null);
                    throw new ConverterException(message);
                }
                return convertToJava8DateTimeAPI(context, calendar, type, submittedValue);
            }
            else if (Date.class.isAssignableFrom(type)) {
                return convertToLegacyDateAPI(context, calendar, submittedValue);
            }

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, type.getName() + " not supported", null);
            throw new ConverterException(message);
        }

        return null;
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

    protected Temporal convertToJava8DateTimeAPI(FacesContext context, UICalendar calendar, Class<?> type, String submittedValue) {
        if (type == LocalDate.class || type == YearMonth.class) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(calendar.calculatePattern())
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1) //because of Month Picker which does not contain day of month
                    .parseDefaulting(ChronoField.ERA, 1)
                    .toFormatter(calendar.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(calendar.getTimeZone()))
                    .withResolverStyle(resolveResolverStyle(calendar.getResolverStyle()));

            try {
                return type == LocalDate.class
                        ? LocalDate.parse(submittedValue, formatter)
                        : YearMonth.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, calendar, submittedValue, formatter.format(LocalDateTime.now()));
            }
        }
        else if (type == LocalTime.class) {
            String pattern = calendar instanceof Calendar ? calendar.calculatePattern() : calendar.calculateTimeOnlyPattern();
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(pattern, calendar.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(calendar.getTimeZone()))
                    .withResolverStyle(resolveResolverStyle(calendar.getResolverStyle()));

            try {
                return LocalTime.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, calendar, submittedValue, formatter.format(LocalDateTime.now()));
            }
        }
        else if (type == LocalDateTime.class) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(calendar.calculatePattern())
                    .parseDefaulting(ChronoField.ERA, 1)
                    .toFormatter(calendar.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(calendar.getTimeZone()))
                    .withResolverStyle(resolveResolverStyle(calendar.getResolverStyle()));

            try {
                return LocalDateTime.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, calendar, submittedValue, formatter.format(LocalDateTime.now()));
            }
        }

        //TODO: implement if necessary
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, type.getName() + " not supported", null);
        throw new ConverterException(message);
    }

    private ResolverStyle resolveResolverStyle(String passedResolverStyle) {
        for (ResolverStyle resolverStyle : ResolverStyle.values()) {
            if (resolverStyle.name().equalsIgnoreCase(passedResolverStyle)) {
                return resolverStyle;
            }
        }
        return ResolverStyle.SMART;
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
        params[2] = ComponentUtils.getLabel(context, calendar);

        if (calendar.isTimeOnly()) {
            message = MessageFactory.getFacesMessage("javax.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
        }
        else if (calendar.hasTime()) {
            message = MessageFactory.getFacesMessage("javax.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
        }
        else {
            message = MessageFactory.getFacesMessage("javax.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
        }

        return new ConverterException(message);
    }

    protected Class<?> resolveDateType(FacesContext context, UICalendar calendar) {
        ValueExpression ve = calendar.getValueExpression("value");

        Class<?> type = null;
        if (ve != null) {
            type = ve.getType(context.getELContext());
        }

        /*
        If type could not be determined via value-expression try it this way.
        a) Required for e.g. usage in custom dataTable filters
        b) some Usecases with generics - see https://github.com/primefaces/primefaces/issues/5913
        */
        if (type == null || type.equals(Object.class)) {
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

            type = LangUtils.getTypeFromCollectionProperty(base, (String) property);
        }

        return type;
    }

    protected Converter resolveConverter(FacesContext context, UICalendar calendar, Class<?> type) {
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
                && type != YearMonth.class
                && type != LocalDateTime.class
                && type != LocalTime.class) {

            converter = context.getApplication().createConverter(type);
        }

        return converter;
    }
}
