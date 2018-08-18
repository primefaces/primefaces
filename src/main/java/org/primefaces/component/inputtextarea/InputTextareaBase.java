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
package org.primefaces.component.inputtextarea;

import javax.faces.component.html.HtmlInputTextarea;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class InputTextareaBase extends HtmlInputTextarea implements Widget, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputTextareaRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        autoResize,
        maxlength,
        counter,
        counterTemplate,
        completeMethod,
        minQueryLength,
        queryDelay,
        scrollHeight,
        addLine;
    }

    public InputTextareaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

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

    public boolean isAutoResize() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoResize, true);
    }

    public void setAutoResize(boolean _autoResize) {
        getStateHelper().put(PropertyKeys.autoResize, _autoResize);
    }

    public int getMaxlength() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxlength, java.lang.Integer.MAX_VALUE);
    }

    public void setMaxlength(int _maxlength) {
        getStateHelper().put(PropertyKeys.maxlength, _maxlength);
    }

    public java.lang.String getCounter() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.counter, null);
    }

    public void setCounter(java.lang.String _counter) {
        getStateHelper().put(PropertyKeys.counter, _counter);
    }

    public java.lang.String getCounterTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.counterTemplate, null);
    }

    public void setCounterTemplate(java.lang.String _counterTemplate) {
        getStateHelper().put(PropertyKeys.counterTemplate, _counterTemplate);
    }

    public javax.el.MethodExpression getCompleteMethod() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.completeMethod, null);
    }

    public void setCompleteMethod(javax.el.MethodExpression _completeMethod) {
        getStateHelper().put(PropertyKeys.completeMethod, _completeMethod);
    }

    public int getMinQueryLength() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minQueryLength, 3);
    }

    public void setMinQueryLength(int _minQueryLength) {
        getStateHelper().put(PropertyKeys.minQueryLength, _minQueryLength);
    }

    public int getQueryDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.queryDelay, 700);
    }

    public void setQueryDelay(int _queryDelay) {
        getStateHelper().put(PropertyKeys.queryDelay, _queryDelay);
    }

    public int getScrollHeight() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MAX_VALUE);
    }

    public void setScrollHeight(int _scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
    }

    public boolean isAddLine() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.addLine, false);
    }

    public void setAddLine(boolean _addLine) {
        getStateHelper().put(PropertyKeys.addLine, _addLine);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}