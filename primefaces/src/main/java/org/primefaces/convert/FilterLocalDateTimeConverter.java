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
package org.primefaces.convert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.datepicker.DatePicker;

public class FilterLocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public LocalDateTime getAsObject(FacesContext context, UIComponent component, String value) {
        DatePicker datePicker = assertDatePicker(component);
        if (value == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = getDateTimeFormatter(context, datePicker);
            LocalDate localDate = LocalDate.parse(value, formatter);
            return localDate.atStartOfDay();
        }
        catch (Exception ex) {
            throw new ConverterException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDateTime value) {
        DatePicker datePicker = assertDatePicker(component);
        if (value == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = getDateTimeFormatter(context, datePicker);
            return formatter.format(value);
        }
        catch (Exception ex) {
            throw new ConverterException(ex.getMessage(), ex);
        }
    }

    static DatePicker assertDatePicker(UIComponent component) {
        if (!(component instanceof DatePicker)) {
            throw new FacesException("Component must be a DatePicker");
        }
        return (DatePicker) component;
    }

    static DateTimeFormatter getDateTimeFormatter(FacesContext context, DatePicker datePicker) {
        String pattern = datePicker.calculatePattern();
        Locale locale = datePicker.calculateLocale(context);
        return DateTimeFormatter.ofPattern(pattern, locale);
    }

}
