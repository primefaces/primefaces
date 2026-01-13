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
package org.primefaces.component.fieldset;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.ToggleEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "toggle", event = ToggleEvent.class, description = "Fires when fieldset is toggled.", defaultEvent = true)
})
public abstract class FieldsetBase extends UIPanel implements Widget, PrimeClientBehaviorHolder, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.FieldsetRenderer";

    public FieldsetBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Custom content for the legend. Alternative to legend attribute.")
    public abstract UIComponent getLegendFacet();

    @Property(description = "Text content for the legend.")
    public abstract String getLegend();

    @Property(defaultValue = "false", description = "Makes fieldset toggleable.")
    public abstract boolean isToggleable();

    @Property(defaultValue = "500", description = "Speed of toggling in milliseconds.")
    public abstract int getToggleSpeed();

    @Property(defaultValue = "false", description = "Renders fieldset as collapsed.")
    public abstract boolean isCollapsed();

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Property(defaultValue = "true", description = "Defines whether HTML in legend text is escaped or not.")
    public abstract boolean isEscape();

    @Property(description = "Advisory tooltip information.")
    public abstract String getTitle();

    @Property(defaultValue = "false", description = "Enables lazy loading for fieldset content.")
    public abstract boolean isDynamic();

}