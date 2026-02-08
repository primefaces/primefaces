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
package org.primefaces.component.rating;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.RateEvent;

import jakarta.faces.component.UIInput;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "rate", event = RateEvent.class, description = "Fires when a rating is selected.", defaultEvent = true),
    @FacesBehaviorEvent(name = "cancel", event = RateEvent.class, description = "Fires when the rating is canceled.")
})
public abstract class RatingBase extends UIInput implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RatingRenderer";

    public RatingBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "5", description = "Number of stars to display.")
    public abstract int getStars();

    @Property(description = "Disables user interaction.")
    public abstract boolean isDisabled();

    @Property(description = "Disables user interaction without adding disabled visuals.")
    public abstract boolean isReadonly();

    @Property(description = "Client side callback to execute when rate happens.")
    public abstract String getOnRate();

    @Property(defaultValue = "true", description = "When enabled, displays a cancel icon to reset rating value.")
    public abstract boolean isCancel();

    @Property(defaultValue = "0", description = "Position of the output in the tabbing order.")
    public abstract String getTabindex();
}