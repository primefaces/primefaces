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
package org.primefaces.integrationtests.datepicker;

import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.DateTimeConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DatePicker014 implements Serializable {

    private static final long serialVersionUID = 1L;

    private OwnDateTime dateTime;

    // Use own DateTimeConverter here and bind it to f:dateTimeConverter.
    // Instead of a binding the converter can be set globally in faces-config.xml
    private final transient DateTimeConverter converter = new OnwDateTimeConverter();

    @PostConstruct
    public void init() {
        dateTime = new OwnDateTime(2021, 1, 10, 1, 16, 04);
    }

    public void submit() {
        TestUtils.addMessage("Date", dateTime.toString());
    }

    public static final class OwnDateTime implements Serializable {

        private static final long serialVersionUID = 1L;
        private final int year;
        private final int month;
        private final int day;
        private final int hour;
        private final int minute;
        private final int second;

        public OwnDateTime(int year, int month, int day, int hour, int minute, int second) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        @Override
        public String toString() {
            return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        }
    }

    public static final class OnwDateTimeConverter extends DateTimeConverter {
        @Override
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
            return value.toString();
        }

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            int start = 0;
            int end = value.indexOf("-");
            int year = Integer.parseInt(value.substring(0, end));
            start = end + 1;
            end = value.indexOf("-", start);
            int month = Integer.parseInt(value.substring(start, end));
            start = end + 1;
            end = value.indexOf(" ", start);
            int day = Integer.parseInt(value.substring(start, end));
            start = end + 1;
            end = value.indexOf(":", start);
            int hour = Integer.parseInt(value.substring(start, end));
            start = end + 1;
            end = value.indexOf(":", start);
            int minute = Integer.parseInt(value.substring(start, end));
            start = end + 1;
            int second = Integer.parseInt(value.substring(start, value.length()));
            return new OwnDateTime(year, month, day, hour, minute, second);
        }
    }
}
