/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.idlemonitor;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class IdleMonitorBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.IdleMonitorRenderer";

    public enum PropertyKeys {

        widgetVar,
        timeout,
        onidle,
        onactive,
        multiWindowSupport
    }

    public IdleMonitorBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public int getTimeout() {
        return (Integer) getStateHelper().eval(PropertyKeys.timeout, 300000);
    }

    public void setTimeout(int timeout) {
        getStateHelper().put(PropertyKeys.timeout, timeout);
    }

    public String getOnidle() {
        return (String) getStateHelper().eval(PropertyKeys.onidle, null);
    }

    public void setOnidle(String onidle) {
        getStateHelper().put(PropertyKeys.onidle, onidle);
    }

    public String getOnactive() {
        return (String) getStateHelper().eval(PropertyKeys.onactive, null);
    }

    public void setOnactive(String onactive) {
        getStateHelper().put(PropertyKeys.onactive, onactive);
    }

    public boolean isMultiWindowSupport() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiWindowSupport, false);
    }

    public void setMultiWindowSupport(boolean multiWindowSupport) {
        getStateHelper().put(PropertyKeys.multiWindowSupport, multiWindowSupport);
    }
}