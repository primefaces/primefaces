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
package org.primefaces.component.ai.speaker;

import javax.faces.component.UIInput;

import org.primefaces.component.api.Widget;

public abstract class SpeakerBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component.ai.speaker";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ai.speaker.SpeakerRenderer";

    public enum PropertyKeys {

        widgetVar,
        style,
        styleClass,
        process,
        update,
        listener,
        width,
        height,
        language
    }

    public SpeakerBase() {
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

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public String getProcess() {
        return (String) getStateHelper().eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
        getStateHelper().put(PropertyKeys.process, process);
    }

    public String getUpdate() {
        return (String) getStateHelper().eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
        getStateHelper().put(PropertyKeys.update, update);
    }

    public javax.el.MethodExpression getListener() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.listener, null);
    }

    public void setListener(javax.el.MethodExpression listener) {
        getStateHelper().put(PropertyKeys.listener, listener);
    }

    public int getWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.width, 320);
    }

    public void setWidth(int width) {
        getStateHelper().put(PropertyKeys.width, width);
    }

    public int getHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.height, 240);
    }

    public void setHeight(int height) {
        getStateHelper().put(PropertyKeys.height, height);
    }

    public String getLanguage() {
        return (String) getStateHelper().eval(PropertyKeys.language, "en-US");
    }

    public void setLanguage(String language) {
        getStateHelper().put(PropertyKeys.language, language);
    }

}