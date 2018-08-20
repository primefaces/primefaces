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
package org.primefaces.component.datascroller;

import org.primefaces.component.api.UIData;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class DataScrollerBase extends UIData implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataScrollerRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        chunkSize,
        mode,
        scrollHeight,
        buffer
    }

    public DataScrollerBase() {
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

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public int getChunkSize() {
        return (Integer) getStateHelper().eval(PropertyKeys.chunkSize, 0);
    }

    public void setChunkSize(int chunkSize) {
        getStateHelper().put(PropertyKeys.chunkSize, chunkSize);
    }

    public String getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "document");
    }

    public void setMode(String mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public String getScrollHeight() {
        return (String) getStateHelper().eval(PropertyKeys.scrollHeight, null);
    }

    public void setScrollHeight(String scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    public int getBuffer() {
        return (Integer) getStateHelper().eval(PropertyKeys.buffer, 10);
    }

    public void setBuffer(int buffer) {
        getStateHelper().put(PropertyKeys.buffer, buffer);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}