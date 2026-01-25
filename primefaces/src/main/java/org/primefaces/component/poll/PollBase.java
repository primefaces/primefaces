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
package org.primefaces.component.poll;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AjaxSource;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class PollBase extends UIComponentBase implements AjaxSource, Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PollRenderer";

    public PollBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "2", description = "Interval in seconds to do periodic AJAX requests.")
    public abstract Object getInterval();

    @Property(description = "An action listener to process when command is executed.")
    public abstract jakarta.el.MethodExpression getListener();

    @Property(description = "When set true, process validations logic is executed at apply request values phase for this component.")
    public abstract boolean isImmediate();

    @Property(defaultValue = "true", description = "In autoStart mode, polling starts automatically on page load, to start polling on demand set to false.")
    public abstract boolean isAutoStart();

    @Property(description = "Stops polling when true.")
    public abstract boolean isStop();

    @Property(description = "Type of interval value. Valid values are \"second\" and \"millisecond\".", defaultValue = "second")
    public abstract String getIntervalType();

    @Property(description = "Client side callback to execute when the poller is activated. Return false to prevent poller from starting.")
    public abstract String getOnactivated();

    @Property(description = "Client side callback to execute when the poller is deactivated. Return false to prevent poller from stopping.")
    public abstract String getOndeactivated();
}