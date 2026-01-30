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
package org.primefaces.component.progressbar;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponentBase;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "start", event = AjaxBehaviorEvent.class, description = "Fires when progress bar starts."),
    @FacesBehaviorEvent(name = "complete", event = AjaxBehaviorEvent.class, description = "Fires when progress bar completes.", defaultEvent = true),
    @FacesBehaviorEvent(name = "progress", event = AjaxBehaviorEvent.class, description = "Fires during progress bar updates.")
})
public abstract class ProgressBarBase extends UIComponentBase implements Widget, StyleAware, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ProgressBarRenderer";

    public ProgressBarBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "0", description = "Current value of the progress bar.")
    public abstract int getValue();

    @Property(defaultValue = "false", description = "Disables or enables the progress bar.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "Enables AJAX mode where progress is polled from server.")
    public abstract boolean isAjax();

    @Property(defaultValue = "3000", description = "Interval in milliseconds for polling in AJAX mode.")
    public abstract int getInterval();

    @Property(implicitDefaultValue = "{value}", description = "Template for the label displayed on the progress bar.")
    public abstract String getLabelTemplate();

    @Property(defaultValue = "false", description = "When true, progress bar is displayed as read-only.")
    public abstract boolean isDisplayOnly();

    @Property(defaultValue = "true", description = "Defines whether ajax requests are global.")
    public abstract boolean isGlobal();

    @Property(defaultValue = "determinate", description = "Mode of the progress bar, valid values are 'determinate' and 'indeterminate'.")
    public abstract String getMode();

    @Property(defaultValue = "500", description = "Duration of the animation in milliseconds.")
    public abstract int getAnimationDuration();

    @Property(description = "Title attribute of the progress bar.")
    public abstract String getTitle();

    @Property(description = "Severity of the progress bar, valid values are 'info', 'success', 'warning', 'danger'.")
    public abstract String getSeverity();
}
