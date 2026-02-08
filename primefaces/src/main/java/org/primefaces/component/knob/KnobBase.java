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
package org.primefaces.component.knob;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIInput;
import jakarta.faces.event.BehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvent(name = "change", event = BehaviorEvent.class, description = "Fires when the knob value changes.", defaultEvent = true)
public abstract class KnobBase extends UIInput implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.KnobRenderer";

    public KnobBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "0", description = "Minimum value.")
    public abstract int getMin();

    @Property(defaultValue = "100", description = "Maximum value.")
    public abstract int getMax();

    @Property(defaultValue = "1", description = "Step size.")
    public abstract int getStep();

    @Property(description = "Thickness of the knob arc.")
    public abstract Float getThickness();

    @Property(description = "Width of the knob.")
    public abstract Object getWidth();

    @Property(description = "Height of the knob.")
    public abstract Object getHeight();

    @Property(description = "Foreground color of the knob.")
    public abstract Object getForegroundColor();

    @Property(description = "Background color of the knob.")
    public abstract Object getBackgroundColor();

    @Property(description = "Color theme for the knob.")
    public abstract String getColorTheme();

    @Property(description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "true", description = "Shows the label.")
    public abstract boolean isShowLabel();

    @Property(description = "Shows a cursor.")
    public abstract boolean isCursor();

    @Property(defaultValue = "{value}", description = "Template for the label.")
    public abstract String getLabelTemplate();

    @Property(description = "Client side callback to execute when value changes.")
    public abstract String getOnchange();

    @Property(defaultValue = "butt", description = "Line cap style, valid values are \"butt\" and \"round\".")
    public abstract String getLineCap();
}