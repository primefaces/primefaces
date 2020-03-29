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
package org.primefaces.component.datascroller;

import org.primefaces.component.api.UIData;
import org.primefaces.component.api.Widget;

public abstract class DataScrollerBase extends UIData implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataScrollerRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        chunkSize,
        mode,
        scrollHeight,
        buffer,
        virtualScroll,
        startAtBottom
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

    public boolean isVirtualScroll() {
        return (Boolean) getStateHelper().eval(PropertyKeys.virtualScroll, false);
    }

    public void setVirtualScroll(boolean virtualScroll) {
        getStateHelper().put(PropertyKeys.virtualScroll, virtualScroll);
    }

    public boolean isStartAtBottom() {
        return (Boolean) getStateHelper().eval(PropertyKeys.startAtBottom, false);
    }

    public void setStartAtBottom(boolean startAtBottom) {
        getStateHelper().put(PropertyKeys.startAtBottom, startAtBottom);
    }
}