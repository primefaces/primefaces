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
package org.primefaces.component.captcha;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class CaptchaBase extends UIInput implements Widget {

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

    public java.lang.String getTheme() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.theme, "light");
    }

    public void setTheme(java.lang.String _theme) {
        getStateHelper().put(PropertyKeys.theme, _theme);
    }

    public java.lang.String getLanguage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.language, "en");
    }

    public void setLanguage(java.lang.String _language) {
        getStateHelper().put(PropertyKeys.language, _language);
    }

    public int getTabindex() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.tabindex, 0);
    }

    public void setTabindex(int _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public java.lang.String getLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(java.lang.String _label) {
        getStateHelper().put(PropertyKeys.label, _label);
    }

    public java.lang.String getCallback() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.callback, null);
    }

    public void setCallback(java.lang.String _callback) {
        getStateHelper().put(PropertyKeys.callback, _callback);
    }

    public java.lang.String getExpired() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.expired, null);
    }

    public void setExpired(java.lang.String _expired) {
        getStateHelper().put(PropertyKeys.expired, _expired);
    }

    public java.lang.String getSize() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.size, null);
    }

    public void setSize(java.lang.String _size) {
        getStateHelper().put(PropertyKeys.size, _size);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}