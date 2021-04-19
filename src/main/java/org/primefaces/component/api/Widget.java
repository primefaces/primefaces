/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.api;

import org.primefaces.util.ComponentUtils;

import javax.faces.context.FacesContext;

public interface Widget extends PrimeUIComponent {

    String CALLBACK_POST_CONSTRUCT = "widgetPostConstruct";
    String CALLBACK_POST_REFRESH = "widgetPostRefresh";
    String CALLBACK_PRE_DESTROY = "widgetPreDestroy";
    String ATTR_WIDGET_VAR_PATTERN = Widget.class.getName() + ".ATTR_WIDGET_VAR_PATTERN";

    enum PropertyKeys {
        widgetVar,
    }

    default String getWidgetVar() {
        return (String) getState().eval(PropertyKeys.widgetVar, null);
    }

    default void setWidgetVar(String widgetVar) {
        getState().put(PropertyKeys.widgetVar, widgetVar);
    }

    @Deprecated
    default String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(this, FacesContext.getCurrentInstance());
    }

    @Deprecated
    default String resolveWidgetVar(FacesContext context) {
        return ComponentUtils.resolveWidgetVar(this, context);
    }
}
