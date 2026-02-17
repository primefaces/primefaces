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
package org.primefaces.component.slider;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeUIInput;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.TouchAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SlideEndEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "slideEnd", event = SlideEndEvent.class, description = "Fired when slide ends.", defaultEvent = true)
})
public abstract class SliderBase extends PrimeUIInput implements Widget, TouchAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SliderRenderer";

    public SliderBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Id of the input text that the slider will be used for.")
    public abstract String getFor();

    @Property(description = "Id of the component to display the slider value.")
    public abstract String getDisplay();

    @Property(description = "Minimum value of the slider.", defaultValue = "0.0")
    public abstract double getMinValue();

    @Property(description = "Maximum value of the slider.", defaultValue = "100.0")
    public abstract double getMaxValue();

    @Property(description = "Boolean value to enable/disable the animated move when background of slider is clicked.",
            defaultValue = "true")
    public abstract boolean isAnimate();

    @Property(description = "Sets the type of the slider, \"horizontal\" or \"vertical\".",
            defaultValue = "horizontal")
    public abstract String getType();

    @Property(description = "Fixed pixel increments that the slider move in.",
            defaultValue = "1.0")
    public abstract double getStep();

    @Property(description = "Disables or enables the component.",
            defaultValue = "false")
    public abstract boolean isDisabled();

    @Property(description = "Flag indicating that this component will prevent changes by the user.",
            defaultValue = "false")
    public abstract boolean isReadonly();

    @Property(description = "Client side callback to execute when slide begins.")
    public abstract String getOnSlideStart();

    @Property(description = "Client side callback to execute during sliding.")
    public abstract String getOnSlide();

    @Property(description = "Client side callback to execute when slide ends.")
    public abstract String getOnSlideEnd();

    @Property(description = "When set `true`, two handles are provided for selection a range."
            + " Another types `false` for disable range style and `max` for shows range handle to the slider max.",
            defaultValue = "min")
    public abstract String getRange();

    @Property(description = "String template to use when updating the display. Valid placeholders are {value}, {min} and {max}.")
    public abstract String getDisplayTemplate();
}