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

import org.primefaces.component.api.UICalendar;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

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

import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;

public abstract class BaseCalendarRenderer<T extends UICalendar> extends InputRenderer<T> {

    @Override
    public void decode(FacesContext context, T component) {
        if (!shouldDecode(component)) {
            return;
        }

        String param = component.getClientId(context) + "_input";
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(param);

        if (submittedValue != null) {
            component.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, T component) throws IOException {
        String markupValue = CalendarUtils.getValueAsString(context, component);
        String widgetValue = component.isTimeOnly() ? CalendarUtils.getTimeOnlyValueAsString(context, component) : markupValue;

        // #6068 ensure min is before max
        component.validateMinMax(context);

        encodeMarkup(context, component, markupValue);
        encodeScript(context, component, widgetValue);
    }

    protected abstract void encodeMarkup(FacesContext context, T component, String value) throws IOException;

    protected abstract void encodeScript(FacesContext context, T component, String value) throws IOException;

    protected void encodeInput(FacesContext context, T component, String id, String value, boolean popup) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String type = popup ? component.getType() : "hidden";
        String inputStyle = component.getInputStyle();

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
            String inputStyleClass = createStyleClass(component, "inputStyleClass", UICalendar.INPUT_STYLE_CLASS);
            readonly = component.isReadonly() || component.isReadonlyInput();
            disabled = component.isDisabled();

            writer.writeAttribute("class", inputStyleClass, null);
            writer.writeAttribute(HTML.ARIA_ROLE, "combobox", null);
            writer.writeAttribute(HTML.ARIA_AUTOCOMPLETE, "none", null);
            writer.writeAttribute(HTML.ARIA_HASPOPUP, "dialog", null);
            writer.writeAttribute(HTML.ARIA_EXPANDED, "false", null);
            writer.writeAttribute(HTML.ARIA_CONTROLS,  component.getClientId() + "_panel", null);

            if (inputStyle != null) {
                writer.writeAttribute("style", inputStyle, null);
            }

            renderPassThruAttributes(context, component, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
            renderDomEvents(context, component, HTML.INPUT_TEXT_EVENTS);
        }

        renderAccessibilityAttributes(context, component, disabled, readonly);
        renderValidationMetadata(context, component);

        writer.endElement("input");
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object value) throws ConverterException {
        String submittedValue = isValueBlank((String) value) ? null : ((String) value).trim();
        if (submittedValue == null) {
            return null;
        }

        T calendar = (T) component;

        //Delegate to user supplied converter if defined
        Class<?> type = resolveDateType(context, calendar);
        if (type != null) {
            Converter<?> converter = resolveConverter(context, calendar, type);
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

    protected Date convertToLegacyDateAPI(FacesContext context, T component, String submittedValue) {
        //Code for backward-compatibility with java.util.Date - may be removed at some point in the future
        SimpleDateFormat format = new SimpleDateFormat(component.calculatePattern(), component.calculateLocale(context));
        format.setLenient(false);
        format.setTimeZone(TimeZone.getTimeZone(CalendarUtils.calculateZoneId(component.getTimeZone())));

        try {
            return format.parse(submittedValue);
        }
        catch (ParseException e) {
            throw createConverterException(context, component, submittedValue, format.format(new Date()));
        }
    }

    protected Temporal convertToJava8DateTimeAPI(FacesContext context, T component, Class<?> type, String submittedValue) {
        if (type == LocalDate.class || type == YearMonth.class || (type == LocalDateTime.class && !component.hasTime())) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(component.calculatePattern())
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1) //because of Month Picker which does not contain day of month
                    .parseDefaulting(ChronoField.ERA, 1)
                    .toFormatter(component.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(component.getTimeZone()))
                    .withResolverStyle(resolveResolverStyle(component.getResolverStyle()));

            try {
                Temporal result;
                if (type == LocalDate.class) {
                    result = LocalDate.parse(submittedValue, formatter);
                }
                else if (type == LocalDateTime.class) {
                    result = LocalDate.parse(submittedValue, formatter).atStartOfDay();
                }
                else {
                    result = YearMonth.parse(submittedValue, formatter);
                }

                return result;
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, component, submittedValue, formatter.format(LocalDateTime.now()));
            }
        }
        else if (type == LocalTime.class) {
            String pattern = component instanceof Calendar ? component.calculatePattern() : component.calculateTimeOnlyPattern();
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern(pattern, component.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(component.getTimeZone()))
                    .withResolverStyle(resolveResolverStyle(component.getResolverStyle()));

            try {
                return LocalTime.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, component, submittedValue, formatter.format(LocalDateTime.now()));
            }
        }
        else if (type == LocalDateTime.class) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern(component.calculatePattern())
                    .parseDefaulting(ChronoField.ERA, 1)
                    .toFormatter(component.calculateLocale(context))
                    .withZone(CalendarUtils.calculateZoneId(component.getTimeZone()))
                    .withResolverStyle(resolveResolverStyle(component.getResolverStyle()));

            try {
                return LocalDateTime.parse(submittedValue, formatter);
            }
            catch (DateTimeParseException e) {
                throw createConverterException(context, component, submittedValue, formatter.format(LocalDateTime.now()));
            }
        }

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
                                                          T component,
                                                          String submittedValue,
                                                          Object param1) {
        component.setConversionFailed(true);

        FacesMessage message = null;
        Object[] params = new Object[3];
        params[0] = submittedValue;
        params[1] = param1;
        params[2] = ComponentUtils.getLabel(context, component);

        if (component.isTimeOnly()) {
            message = MessageFactory.getFacesMessage(context, "jakarta.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
        }
        else if (component.hasTime()) {
            message = MessageFactory.getFacesMessage(context, "jakarta.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
        }
        else {
            message = MessageFactory.getFacesMessage(context, "jakarta.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
        }

        return new ConverterException(message);
    }

    protected Class<?> resolveDateType(FacesContext context, T component) {
        ValueExpression ve = component.getValueExpression("value");

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
            if (component.isTimeOnly()) {
                type = LocalTime.class;
            }
            else if (component.hasTime()) {
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

    protected Converter resolveConverter(FacesContext context, T calendar, Class<?> type) {
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
