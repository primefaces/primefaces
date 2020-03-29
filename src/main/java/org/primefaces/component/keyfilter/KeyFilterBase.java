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
package org.primefaces.component.keyfilter;

import javax.faces.component.UIComponentBase;

import org.primefaces.component.api.Widget;

public abstract class KeyFilterBase extends UIComponentBase implements Widget {

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
}