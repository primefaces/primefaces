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
package org.primefaces.component.organigram;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class OrganigramBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OrganigramRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        var,
        selection,
        style,
        styleClass,
        leafNodeConnectorHeight,
        zoom,
        autoScrollToSelection
    }

    public OrganigramBase() {
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

    public org.primefaces.model.OrganigramNode getValue() {
        return (org.primefaces.model.OrganigramNode) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(org.primefaces.model.OrganigramNode value) {
        getStateHelper().put(PropertyKeys.value, value);
    }

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public org.primefaces.model.OrganigramNode getSelection() {
        return (org.primefaces.model.OrganigramNode) getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(org.primefaces.model.OrganigramNode selection) {
        getStateHelper().put(PropertyKeys.selection, selection);
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

    public int getLeafNodeConnectorHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.leafNodeConnectorHeight, 10);
    }

    public void setLeafNodeConnectorHeight(int leafNodeConnectorHeight) {
        getStateHelper().put(PropertyKeys.leafNodeConnectorHeight, leafNodeConnectorHeight);
    }

    public boolean isZoom() {
        return (Boolean) getStateHelper().eval(PropertyKeys.zoom, false);
    }

    public void setZoom(boolean zoom) {
        getStateHelper().put(PropertyKeys.zoom, zoom);
    }

    public boolean isAutoScrollToSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoScrollToSelection, false);
    }

    public void setAutoScrollToSelection(boolean autoScrollToSelection) {
        getStateHelper().put(PropertyKeys.autoScrollToSelection, autoScrollToSelection);
    }
}