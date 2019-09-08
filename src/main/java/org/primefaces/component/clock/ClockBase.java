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
package org.primefaces.component.clock;

import javax.faces.component.UIOutput;

import org.primefaces.component.api.Widget;

public abstract class ClockBase extends UIOutput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ClockRenderer";

    public enum PropertyKeys {

        pattern,
        mode,
        autoSync,
        syncInterval,
        timeZone,
        displayMode
    }

    public ClockBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPattern() {
        return (String) getStateHelper().eval(PropertyKeys.pattern, null);
    }

    public void setPattern(String pattern) {
        getStateHelper().put(PropertyKeys.pattern, pattern);
    }

    public String getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "client");
    }

    public void setMode(String mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public boolean isAutoSync() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoSync, false);
    }

    public void setAutoSync(boolean autoSync) {
        getStateHelper().put(PropertyKeys.autoSync, autoSync);
    }

    public int getSyncInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.syncInterval, 60000);
    }

    public void setSyncInterval(int syncInterval) {
        getStateHelper().put(PropertyKeys.syncInterval, syncInterval);
    }

    public Object getTimeZone() {
        return getStateHelper().eval(PropertyKeys.timeZone, null);
    }

    public void setTimeZone(Object timeZone) {
        getStateHelper().put(PropertyKeys.timeZone, timeZone);
    }

    public String getDisplayMode() {
        return (String) getStateHelper().eval(PropertyKeys.displayMode, "digital");
    }

    public void setDisplayMode(String displayMode) {
        getStateHelper().put(PropertyKeys.displayMode, displayMode);
    }
}