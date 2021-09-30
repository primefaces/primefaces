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
package org.primefaces.showcase.convert;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.showcase.domain.Order;

@Named
@FacesConverter("org.primefaces.showcase.converter.OrderConverter")
public class OrderConverter implements Converter<TimelineEvent<Order>>, Serializable {

    private List<TimelineEvent<Order>> events;

    public OrderConverter() {
    }

    @Override
    public TimelineEvent<Order> getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || events == null || events.isEmpty()) {
            return null;
        }

        for (TimelineEvent<Order> event : events) {
            if (event.getData().getNumber() == Integer.valueOf(value)) {
                return event;
            }
        }

        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, TimelineEvent<Order> value) {
        if (value == null) {
            return null;
        }

        return String.valueOf(value.getData().getNumber());
    }

    public List<TimelineEvent<Order>> getEvents() {
        return events;
    }

    public void setEvents(List<TimelineEvent<Order>> events) {
        this.events = events;
    }
}
