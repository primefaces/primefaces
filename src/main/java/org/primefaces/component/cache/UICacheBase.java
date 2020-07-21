/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.cache;

import javax.faces.component.UIPanel;


public abstract class UICacheBase extends UIPanel {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.UICacheRenderer";

    public enum PropertyKeys {

        disabled,
        region,
        key,
        processEvents
    }

    public UICacheBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getRegion() {
        return (String) getStateHelper().eval(PropertyKeys.region, null);
    }

    public void setRegion(String region) {
        getStateHelper().put(PropertyKeys.region, region);
    }

    public String getKey() {
        return (String) getStateHelper().eval(PropertyKeys.key, null);
    }

    public void setKey(String key) {
        getStateHelper().put(PropertyKeys.key, key);
    }

    public boolean isProcessEvents() {
        return (Boolean) getStateHelper().eval(PropertyKeys.processEvents, false);
    }

    public void setProcessEvents(boolean processEvents) {
        getStateHelper().put(PropertyKeys.processEvents, processEvents);
    }

}