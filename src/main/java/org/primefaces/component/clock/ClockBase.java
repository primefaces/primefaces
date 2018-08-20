/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.clock;

import javax.faces.component.UIOutput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class ClockBase extends UIOutput implements Widget {

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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}