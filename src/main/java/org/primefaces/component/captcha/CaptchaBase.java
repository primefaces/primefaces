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
package org.primefaces.component.captcha;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;

public abstract class CaptchaBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CaptchaRenderer";

    public enum PropertyKeys {

        theme,
        language,
        tabindex,
        label,
        callback,
        expired,
        size
    }

    public CaptchaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getTheme() {
        return (String) getStateHelper().eval(PropertyKeys.theme, "light");
    }

    public void setTheme(String theme) {
        getStateHelper().put(PropertyKeys.theme, theme);
    }

    public String getLanguage() {
        return (String) getStateHelper().eval(PropertyKeys.language, "en");
    }

    public void setLanguage(String language) {
        getStateHelper().put(PropertyKeys.language, language);
    }

    public int getTabindex() {
        return (Integer) getStateHelper().eval(PropertyKeys.tabindex, 0);
    }

    public void setTabindex(int tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public String getCallback() {
        return (String) getStateHelper().eval(PropertyKeys.callback, null);
    }

    public void setCallback(String callback) {
        getStateHelper().put(PropertyKeys.callback, callback);
    }

    public String getExpired() {
        return (String) getStateHelper().eval(PropertyKeys.expired, null);
    }

    public void setExpired(String expired) {
        getStateHelper().put(PropertyKeys.expired, expired);
    }

    public String getSize() {
        return (String) getStateHelper().eval(PropertyKeys.size, null);
    }

    public void setSize(String size) {
        getStateHelper().put(PropertyKeys.size, size);
    }
}