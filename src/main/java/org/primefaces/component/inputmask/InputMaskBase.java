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
package org.primefaces.component.inputmask;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class InputMaskBase extends HtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputMaskRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        mask,
        slotChar,
        autoClear,
        type;
    }

    public InputMaskBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getPlaceholder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(java.lang.String _placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, _placeholder);
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public java.lang.String getMask() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.mask, null);
    }

    public void setMask(java.lang.String _mask) {
        getStateHelper().put(PropertyKeys.mask, _mask);
    }

    public java.lang.String getSlotChar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.slotChar, null);
    }

    public void setSlotChar(java.lang.String _slotChar) {
        getStateHelper().put(PropertyKeys.slotChar, _slotChar);
    }

    public boolean isAutoClear() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoClear, true);
    }

    public void setAutoClear(boolean _autoClear) {
        getStateHelper().put(PropertyKeys.autoClear, _autoClear);
    }

    public java.lang.String getType() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.type, "text");
    }

    public void setType(java.lang.String _type) {
        getStateHelper().put(PropertyKeys.type, _type);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}