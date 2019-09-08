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
package org.primefaces.component.growl;

import javax.faces.component.UIMessages;

import org.primefaces.component.api.UINotification;
import org.primefaces.component.api.Widget;

public abstract class GrowlBase extends UIMessages implements Widget, UINotification {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GrowlRenderer";

    public enum PropertyKeys {

        widgetVar,
        sticky,
        life,
        escape,
        severity,
        keepAlive,
        skipDetailIfEqualsSummary
    }

    public GrowlBase() {
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

    public boolean isSticky() {
        return (Boolean) getStateHelper().eval(PropertyKeys.sticky, false);
    }

    public void setSticky(boolean sticky) {
        getStateHelper().put(PropertyKeys.sticky, sticky);
    }

    public int getLife() {
        return (Integer) getStateHelper().eval(PropertyKeys.life, 6000);
    }

    public void setLife(int life) {
        getStateHelper().put(PropertyKeys.life, life);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    @Override
    public String getSeverity() {
        return (String) getStateHelper().eval(PropertyKeys.severity, null);
    }

    public void setSeverity(String severity) {
        getStateHelper().put(PropertyKeys.severity, severity);
    }

    public boolean isKeepAlive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.keepAlive, false);
    }

    public void setKeepAlive(boolean keepAlive) {
        getStateHelper().put(PropertyKeys.keepAlive, keepAlive);
    }

    @Override
    public boolean isSkipDetailIfEqualsSummary() {
        return (Boolean) getStateHelper().eval(PropertyKeys.skipDetailIfEqualsSummary, false);
    }

    public void setSkipDetailIfEqualsSummary(boolean skipDetailIfEqualsSummary) {
        getStateHelper().put(PropertyKeys.skipDetailIfEqualsSummary, skipDetailIfEqualsSummary);
    }
}