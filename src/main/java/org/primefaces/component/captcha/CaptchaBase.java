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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}