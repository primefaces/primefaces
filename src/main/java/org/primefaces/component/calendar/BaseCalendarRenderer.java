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
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.MessageFactory;

import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

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
        UICalendar uicalendar = (UICalendar) component;
        String submittedValue = ((String) value);
        DateTimeFormatter formatter = null;

        if (isValueBlank(submittedValue)) {
            return null;
        }
        else {
            submittedValue = submittedValue.trim();
        }

        //Delegate to user supplied converter if defined
        try {
            Converter converter = uicalendar.getConverter();
            if (converter != null) {
                return converter.getAsObject(context, uicalendar, submittedValue);
            }
        }
        catch (ConverterException e) {
            uicalendar.setConversionFailed(true);

            throw e;
        }

        //Delegate to global defined converter (e.g. joda or java.util.Date)
        try {
            ValueExpression ve = uicalendar.getValueExpression("value");
            if (ve != null) {
                Class type = ve.getType(context.getELContext());
                //if (type != null && type != Object.class && type != Date.class) {
                if (type != null && type != Object.class && type != LocalDate.class && type != LocalDateTime.class && type != LocalTime.class) {
                    //TODO: right decision to check Java8-Types instead of java.util.Date?
                    Converter converter = context.getApplication().createConverter(type);
                    if (converter != null) {
                        return converter.getAsObject(context, uicalendar, submittedValue);
                    }
                }
            }
        }
        catch (ConverterException e) {
            uicalendar.setConversionFailed(true);

            throw e;
        }

        try {
            //TODO: is there some way to dynamically determine the type?

            //inverted code from org.primefaces.convert::DateBackwardCompatiblityConverter - keep synchronized!

            if (uicalendar.isTimeOnly()) {
                formatter =  DateTimeFormatter.ofPattern(uicalendar.calculateTimeOnlyPattern(), uicalendar.calculateLocale(context));
                return LocalTime.parse(submittedValue, formatter);
            }
            else if (uicalendar.hasTime()) {
                //known issue: https://github.com/primefaces/primefaces/issues/4625
                //known issue: https://github.com/primefaces/primefaces/issues/4626

                //TODO: remove temporary (ugly) work-around for adding fixed time-pattern
                String pattern = uicalendar.calculatePattern();
                if (this instanceof DatePickerRenderer) {
                    pattern += " HH:mm";
                }

                formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern(pattern)
                        .toFormatter();
                formatter = formatter.withLocale(uicalendar.calculateLocale(context));
                return LocalDateTime.parse(submittedValue, formatter);
            }
            else {
                formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern(uicalendar.calculatePattern())
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1) //because of Month Picker which does not contain day of month
                        .toFormatter();
                formatter = formatter.withLocale(uicalendar.calculateLocale(context));
                return LocalDate.parse(submittedValue, formatter);
            }
        }
        catch (DateTimeParseException e) {
            uicalendar.setConversionFailed(true);

            FacesMessage message = null;
            Object[] params = new Object[3];
            params[0] = submittedValue;
            params[1] = formatter.format(LocalDate.now());
            params[2] = MessageFactory.getLabel(context, uicalendar);

            if (uicalendar.isTimeOnly()) {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.TIME", FacesMessage.SEVERITY_ERROR, params);
            }
            else if (uicalendar.hasTime()) {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATETIME", FacesMessage.SEVERITY_ERROR, params);
            }
            else {
                message = MessageFactory.getMessage("javax.faces.converter.DateTimeConverter.DATE", FacesMessage.SEVERITY_ERROR, params);
            }

            throw new ConverterException(message);
        }
    }
}
