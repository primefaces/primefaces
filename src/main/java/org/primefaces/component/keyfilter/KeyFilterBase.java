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
package org.primefaces.component.keyfilter;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class KeyFilterBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.KeyFilterRenderer";

    public enum PropertyKeys {

        widgetVar,
        forValue("for"),
        regEx,
        inputRegEx,
        mask,
        testFunction,
        preventPaste;

        private String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((toString != null) ? toString : super.toString());
        }
    }

    public KeyFilterBase() {
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

    public String getFor() {
        return (String) getStateHelper().eval(PropertyKeys.forValue, null);
    }

    public void setFor(String _for) {
        getStateHelper().put(PropertyKeys.forValue, _for);
    }

    public String getRegEx() {
        return (String) getStateHelper().eval(PropertyKeys.regEx, null);
    }

    public void setRegEx(String regEx) {
        getStateHelper().put(PropertyKeys.regEx, regEx);
    }

    public String getInputRegEx() {
        return (String) getStateHelper().eval(PropertyKeys.inputRegEx, null);
    }

    public void setInputRegEx(String inputRegEx) {
        getStateHelper().put(PropertyKeys.inputRegEx, inputRegEx);
    }

    public String getMask() {
        return (String) getStateHelper().eval(PropertyKeys.mask, null);
    }

    public void setMask(String mask) {
        getStateHelper().put(PropertyKeys.mask, mask);
    }

    public String getTestFunction() {
        return (String) getStateHelper().eval(PropertyKeys.testFunction, null);
    }

    public void setTestFunction(String testFunction) {
        getStateHelper().put(PropertyKeys.testFunction, testFunction);
    }

    public boolean isPreventPaste() {
        return (Boolean) getStateHelper().eval(PropertyKeys.preventPaste, true);
    }

    public void setPreventPaste(boolean preventPaste) {
        getStateHelper().put(PropertyKeys.preventPaste, preventPaste);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}