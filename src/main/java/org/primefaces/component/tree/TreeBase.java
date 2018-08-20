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
package org.primefaces.component.tree;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.UITree;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class TreeBase extends UITree implements Widget, RTLAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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
        propagateSelectionUp,
        propagateSelectionDown,
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
        onDrop
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

    public boolean isPropagateSelectionUp() {
        return (Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionUp, true);
    }

    public void setPropagateSelectionUp(boolean propagateSelectionUp) {
        getStateHelper().put(PropertyKeys.propagateSelectionUp, propagateSelectionUp);
    }

    public boolean isPropagateSelectionDown() {
        return (Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionDown, true);
    }

    public void setPropagateSelectionDown(boolean propagateSelectionDown) {
        getStateHelper().put(PropertyKeys.propagateSelectionDown, propagateSelectionDown);
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

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }

    @Override
    public boolean isRTL() {
        return "rtl".equalsIgnoreCase(getDir());
    }
}