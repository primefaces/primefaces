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
package org.primefaces.component.tristatecheckbox;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.Widget;

public abstract class TriStateCheckboxBase extends HtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TriStateCheckboxRenderer";

    public enum PropertyKeys {

        widgetVar,
        stateOneIcon,
        stateTwoIcon,
        stateThreeIcon,
        itemLabel,
        stateOneTitle,
        stateTwoTitle,
        stateThreeTitle;
    }

    public TriStateCheckboxBase() {
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

    public String getStateOneIcon() {
        return (String) getStateHelper().eval(PropertyKeys.stateOneIcon, null);
    }

    public void setStateOneIcon(String stateOneIcon) {
        getStateHelper().put(PropertyKeys.stateOneIcon, stateOneIcon);
    }

    public String getStateTwoIcon() {
        return (String) getStateHelper().eval(PropertyKeys.stateTwoIcon, null);
    }

    public void setStateTwoIcon(String stateTwoIcon) {
        getStateHelper().put(PropertyKeys.stateTwoIcon, stateTwoIcon);
    }

    public String getStateThreeIcon() {
        return (String) getStateHelper().eval(PropertyKeys.stateThreeIcon, null);
    }

    public void setStateThreeIcon(String stateThreeIcon) {
        getStateHelper().put(PropertyKeys.stateThreeIcon, stateThreeIcon);
    }

    public String getItemLabel() {
        return (String) getStateHelper().eval(PropertyKeys.itemLabel, null);
    }

    public void setItemLabel(String itemLabel) {
        getStateHelper().put(PropertyKeys.itemLabel, itemLabel);
    }

    public String getStateOneTitle() {
        return (String) getStateHelper().eval(PropertyKeys.stateOneTitle, null);
    }

    public void setStateOneTitle(String stateOneTitle) {
        getStateHelper().put(PropertyKeys.stateOneTitle, stateOneTitle);
    }

    public String getStateTwoTitle() {
        return (String) getStateHelper().eval(PropertyKeys.stateTwoTitle, null);
    }

    public void setStateTwoTitle(String stateTwoTitle) {
        getStateHelper().put(PropertyKeys.stateTwoTitle, stateTwoTitle);
    }

    public String getStateThreeTitle() {
        return (String) getStateHelper().eval(PropertyKeys.stateThreeTitle, null);
    }

    public void setStateThreeTitle(String stateThreeTitle) {
        getStateHelper().put(PropertyKeys.stateThreeTitle, stateThreeTitle);
    }
}