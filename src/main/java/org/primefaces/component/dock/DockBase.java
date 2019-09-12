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
package org.primefaces.component.dock;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

public abstract class DockBase extends AbstractMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DockRenderer";

    public enum PropertyKeys {

        widgetVar,
        model,
        position,
        itemWidth,
        maxWidth,
        proximity,
        halign
    }

    public DockBase() {
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

    @Override
    public org.primefaces.model.menu.MenuModel getModel() {
        return (org.primefaces.model.menu.MenuModel) getStateHelper().eval(PropertyKeys.model, null);
    }

    public void setModel(org.primefaces.model.menu.MenuModel model) {
        getStateHelper().put(PropertyKeys.model, model);
    }

    public String getPosition() {
        return (String) getStateHelper().eval(PropertyKeys.position, "bottom");
    }

    public void setPosition(String position) {
        getStateHelper().put(PropertyKeys.position, position);
    }

    public int getItemWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.itemWidth, 40);
    }

    public void setItemWidth(int itemWidth) {
        getStateHelper().put(PropertyKeys.itemWidth, itemWidth);
    }

    public int getMaxWidth() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxWidth, 50);
    }

    public void setMaxWidth(int maxWidth) {
        getStateHelper().put(PropertyKeys.maxWidth, maxWidth);
    }

    public int getProximity() {
        return (Integer) getStateHelper().eval(PropertyKeys.proximity, 90);
    }

    public void setProximity(int proximity) {
        getStateHelper().put(PropertyKeys.proximity, proximity);
    }

    public String getHalign() {
        return (String) getStateHelper().eval(PropertyKeys.halign, "center");
    }

    public void setHalign(String halign) {
        getStateHelper().put(PropertyKeys.halign, halign);
    }
}