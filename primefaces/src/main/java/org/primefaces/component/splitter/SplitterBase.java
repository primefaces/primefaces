/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.splitter;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SplitterResizeEvent;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "resizeEnd", event = SplitterResizeEvent.class, description = "Fires when the splitter is resized.", defaultEvent = true)
})
public abstract class SplitterBase extends UIComponentBase implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SplitterRenderer";

    public SplitterBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "horizontal", description = "Orientation of the panels, valid values are \"horizontal\" and \"vertical\".")
    public abstract String getLayout();

    @Property(defaultValue = "4", description = "Size of the divider in pixels.")
    public abstract Integer getGutterSize();

    @Property(description = "Storage identifier of a stateful Splitter.")
    public abstract String getStateKey();

    @Property(defaultValue = "session", description = "Defines where a stateful splitter keeps its state."
            + " Valid values are \"session\" for sessionStorage and \"local\" for localStorage.")
    public abstract String getStateStorage();

    @Property(description = "Client-side callback to execute after resizing end.")
    public abstract String getOnResizeEnd();

    @Property(defaultValue = "5", description = "Step size when pressing arrow keys to resize panel.")
    public abstract int getStep();
}
