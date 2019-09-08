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
package org.primefaces.component.tree;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.UITree;
import org.primefaces.component.api.Widget;

public abstract class TreeBase extends UITree implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TreeRenderer";

    public enum PropertyKeys {

        widgetVar,
        dynamic,
        cache,
        onNodeClick,
        style,
        styleClass,
        highlight,
        datakey,
        animate,
        orientation,
        dir,
        draggable,
        droppable,
        dragdropScope,
        dragMode,
        dropRestrict,
        tabindex,
        filterBy,
        filterMatchMode,
        disabled,
        multipleDrag,
        dropCopyNode,
        onDrop,
        filterMode
    }

    public TreeBase() {
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

    public boolean isDynamic() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, dynamic);
    }

    public boolean isCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean cache) {
        getStateHelper().put(PropertyKeys.cache, cache);
    }

    public String getOnNodeClick() {
        return (String) getStateHelper().eval(PropertyKeys.onNodeClick, null);
    }

    public void setOnNodeClick(String onNodeClick) {
        getStateHelper().put(PropertyKeys.onNodeClick, onNodeClick);
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

    public boolean isHighlight() {
        return (Boolean) getStateHelper().eval(PropertyKeys.highlight, true);
    }

    public void setHighlight(boolean highlight) {
        getStateHelper().put(PropertyKeys.highlight, highlight);
    }

    public Object getDatakey() {
        return getStateHelper().eval(PropertyKeys.datakey, null);
    }

    public void setDatakey(Object datakey) {
        getStateHelper().put(PropertyKeys.datakey, datakey);
    }

    public boolean isAnimate() {
        return (Boolean) getStateHelper().eval(PropertyKeys.animate, false);
    }

    public void setAnimate(boolean animate) {
        getStateHelper().put(PropertyKeys.animate, animate);
    }

    public String getOrientation() {
        return (String) getStateHelper().eval(PropertyKeys.orientation, "vertical");
    }

    public void setOrientation(String orientation) {
        getStateHelper().put(PropertyKeys.orientation, orientation);
    }

    public String getDir() {
        return (String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(String dir) {
        getStateHelper().put(PropertyKeys.dir, dir);
    }

    public boolean isDraggable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.draggable, false);
    }

    public void setDraggable(boolean draggable) {
        getStateHelper().put(PropertyKeys.draggable, draggable);
    }

    public boolean isDroppable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.droppable, false);
    }

    public void setDroppable(boolean droppable) {
        getStateHelper().put(PropertyKeys.droppable, droppable);
    }

    public String getDragdropScope() {
        return (String) getStateHelper().eval(PropertyKeys.dragdropScope, null);
    }

    public void setDragdropScope(String dragdropScope) {
        getStateHelper().put(PropertyKeys.dragdropScope, dragdropScope);
    }

    public String getDragMode() {
        return (String) getStateHelper().eval(PropertyKeys.dragMode, "self");
    }

    public void setDragMode(String dragMode) {
        getStateHelper().put(PropertyKeys.dragMode, dragMode);
    }

    public String getDropRestrict() {
        return (String) getStateHelper().eval(PropertyKeys.dropRestrict, "none");
    }

    public void setDropRestrict(String dropRestrict) {
        getStateHelper().put(PropertyKeys.dropRestrict, dropRestrict);
    }

    public int getTabindex() {
        return (Integer) getStateHelper().eval(PropertyKeys.tabindex, 0);
    }

    public void setTabindex(int tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public Object getFilterBy() {
        return getStateHelper().eval(PropertyKeys.filterBy, null);
    }

    public void setFilterBy(Object filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, filterBy);
    }

    public String getFilterMatchMode() {
        return (String) getStateHelper().eval(PropertyKeys.filterMatchMode, "startsWith");
    }

    public void setFilterMatchMode(String filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, filterMatchMode);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public boolean isMultipleDrag() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multipleDrag, false);
    }

    public void setMultipleDrag(boolean multipleDrag) {
        getStateHelper().put(PropertyKeys.multipleDrag, multipleDrag);
    }

    public boolean isDropCopyNode() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dropCopyNode, false);
    }

    public void setDropCopyNode(boolean dropCopyNode) {
        getStateHelper().put(PropertyKeys.dropCopyNode, dropCopyNode);
    }

    public javax.el.MethodExpression getOnDrop() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.onDrop, null);
    }

    public void setOnDrop(javax.el.MethodExpression onDrop) {
        getStateHelper().put(PropertyKeys.onDrop, onDrop);
    }

    public String getFilterMode() {
        return (String) getStateHelper().eval(PropertyKeys.filterMode, "lenient");
    }

    public void setFilterMode(String filterMode) {
        getStateHelper().put(PropertyKeys.filterMode, filterMode);
    }
}