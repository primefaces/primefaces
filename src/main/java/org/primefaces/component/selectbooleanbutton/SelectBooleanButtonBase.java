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
package org.primefaces.component.selectbooleanbutton;

import javax.faces.component.html.HtmlSelectBooleanCheckbox;

import org.primefaces.component.api.Widget;

public abstract class SelectBooleanButtonBase extends HtmlSelectBooleanCheckbox implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectBooleanButtonRenderer";

    public enum PropertyKeys {

        widgetVar,
        onLabel,
        offLabel,
        onIcon,
        offIcon;
    }

    public SelectBooleanButtonBase() {
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

    public String getOnLabel() {
        return (String) getStateHelper().eval(PropertyKeys.onLabel, null);
    }

    public void setOnLabel(String onLabel) {
        getStateHelper().put(PropertyKeys.onLabel, onLabel);
    }

    public String getOffLabel() {
        return (String) getStateHelper().eval(PropertyKeys.offLabel, null);
    }

    public void setOffLabel(String offLabel) {
        getStateHelper().put(PropertyKeys.offLabel, offLabel);
    }

    public String getOnIcon() {
        return (String) getStateHelper().eval(PropertyKeys.onIcon, null);
    }

    public void setOnIcon(String onIcon) {
        getStateHelper().put(PropertyKeys.onIcon, onIcon);
    }

    public String getOffIcon() {
        return (String) getStateHelper().eval(PropertyKeys.offIcon, null);
    }

    public void setOffIcon(String offIcon) {
        getStateHelper().put(PropertyKeys.offIcon, offIcon);
    }
}