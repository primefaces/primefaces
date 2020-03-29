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
package org.primefaces.component.inputtextarea;

import javax.faces.component.html.HtmlInputTextarea;

import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;

public abstract class InputTextareaBase extends HtmlInputTextarea implements Widget, RTLAware, MixedClientBehaviorHolder {

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
}