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
package org.primefaces.component.chart;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.ItemSelectEvent;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "itemSelect", event = ItemSelectEvent.class, description = "Fires when an item is selected.", defaultEvent = true)
})
public abstract class ChartBase extends UIComponentBase implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ChartRenderer";

    public ChartBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Chart model data as a JSON string, either generated from an XDEV model object or provided as raw JSON configuration.")
    public abstract String getValue();

    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Property(description = "Name of javascript function to extend the options of the underlying Chart.js plugin.")
    public abstract String getExtender();

    @Property(description = "Inline style of the canvas element.")
    public abstract String getCanvasStyle();

    @Property(description = "Style class of the canvas element.")
    public abstract String getCanvasStyleClass();
}
