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
package org.primefaces.component.blockui;

import javax.faces.component.UIPanel;

import org.primefaces.component.api.Widget;

public abstract class BlockUIBase extends UIPanel implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.BlockUIRenderer";

    public enum PropertyKeys {

        widgetVar,
        trigger,
        block,
        blocked,
        animate,
        styleClass
    }

    public BlockUIBase() {
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

    public String getTrigger() {
        return (String) getStateHelper().eval(PropertyKeys.trigger, null);
    }

    public void setTrigger(String trigger) {
        getStateHelper().put(PropertyKeys.trigger, trigger);
    }

    public String getBlock() {
        return (String) getStateHelper().eval(PropertyKeys.block, null);
    }

    public void setBlock(String block) {
        getStateHelper().put(PropertyKeys.block, block);
    }

    public boolean isBlocked() {
        return (Boolean) getStateHelper().eval(PropertyKeys.blocked, false);
    }

    public void setBlocked(boolean blocked) {
        getStateHelper().put(PropertyKeys.blocked, blocked);
    }

    public boolean isAnimate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animate, true);
    }

    public void setAnimate(boolean animate) {
        getStateHelper().put(PropertyKeys.animate, animate);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }
}