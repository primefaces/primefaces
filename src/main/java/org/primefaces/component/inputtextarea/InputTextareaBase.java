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
        addLine
    }

    public InputTextareaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isAutoResize() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoResize, true);
    }

    public void setAutoResize(boolean autoResize) {
        getStateHelper().put(PropertyKeys.autoResize, autoResize);
    }

    public int getMaxlength() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxlength, Integer.MAX_VALUE);
    }

    public void setMaxlength(int maxlength) {
        getStateHelper().put(PropertyKeys.maxlength, maxlength);
    }

    public String getCounter() {
        return (String) getStateHelper().eval(PropertyKeys.counter, null);
    }

    public void setCounter(String counter) {
        getStateHelper().put(PropertyKeys.counter, counter);
    }

    public String getCounterTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.counterTemplate, null);
    }

    public void setCounterTemplate(String counterTemplate) {
        getStateHelper().put(PropertyKeys.counterTemplate, counterTemplate);
    }

    public javax.el.MethodExpression getCompleteMethod() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.completeMethod, null);
    }

    public void setCompleteMethod(javax.el.MethodExpression completeMethod) {
        getStateHelper().put(PropertyKeys.completeMethod, completeMethod);
    }

    public int getMinQueryLength() {
        return (Integer) getStateHelper().eval(PropertyKeys.minQueryLength, 3);
    }

    public void setMinQueryLength(int minQueryLength) {
        getStateHelper().put(PropertyKeys.minQueryLength, minQueryLength);
    }

    public int getQueryDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.queryDelay, 700);
    }

    public void setQueryDelay(int queryDelay) {
        getStateHelper().put(PropertyKeys.queryDelay, queryDelay);
    }

    public int getScrollHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.scrollHeight, Integer.MAX_VALUE);
    }

    public void setScrollHeight(int scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    public boolean isAddLine() {
        return (Boolean) getStateHelper().eval(PropertyKeys.addLine, false);
    }

    public void setAddLine(boolean addLine) {
        getStateHelper().put(PropertyKeys.addLine, addLine);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}