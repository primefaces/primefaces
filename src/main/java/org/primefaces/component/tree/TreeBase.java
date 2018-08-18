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

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public boolean isDynamic() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean _dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, _dynamic);
    }

    public boolean isCache() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
    }

    public void setCache(boolean _cache) {
        getStateHelper().put(PropertyKeys.cache, _cache);
    }

    public java.lang.String getOnNodeClick() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onNodeClick, null);
    }

    public void setOnNodeClick(java.lang.String _onNodeClick) {
        getStateHelper().put(PropertyKeys.onNodeClick, _onNodeClick);
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public boolean isHighlight() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.highlight, true);
    }

    public void setHighlight(boolean _highlight) {
        getStateHelper().put(PropertyKeys.highlight, _highlight);
    }

    public java.lang.Object getDatakey() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.datakey, null);
    }

    public void setDatakey(java.lang.Object _datakey) {
        getStateHelper().put(PropertyKeys.datakey, _datakey);
    }

    public boolean isAnimate() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, false);
    }

    public void setAnimate(boolean _animate) {
        getStateHelper().put(PropertyKeys.animate, _animate);
    }

    public java.lang.String getOrientation() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.orientation, "vertical");
    }

    public void setOrientation(java.lang.String _orientation) {
        getStateHelper().put(PropertyKeys.orientation, _orientation);
    }

    public boolean isPropagateSelectionUp() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionUp, true);
    }

    public void setPropagateSelectionUp(boolean _propagateSelectionUp) {
        getStateHelper().put(PropertyKeys.propagateSelectionUp, _propagateSelectionUp);
    }

    public boolean isPropagateSelectionDown() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionDown, true);
    }

    public void setPropagateSelectionDown(boolean _propagateSelectionDown) {
        getStateHelper().put(PropertyKeys.propagateSelectionDown, _propagateSelectionDown);
    }

    public java.lang.String getDir() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
    }

    public void setDir(java.lang.String _dir) {
        getStateHelper().put(PropertyKeys.dir, _dir);
    }

    public boolean isDraggable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggable, false);
    }

    public void setDraggable(boolean _draggable) {
        getStateHelper().put(PropertyKeys.draggable, _draggable);
    }

    public boolean isDroppable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.droppable, false);
    }

    public void setDroppable(boolean _droppable) {
        getStateHelper().put(PropertyKeys.droppable, _droppable);
    }

    public java.lang.String getDragdropScope() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dragdropScope, null);
    }

    public void setDragdropScope(java.lang.String _dragdropScope) {
        getStateHelper().put(PropertyKeys.dragdropScope, _dragdropScope);
    }

    public java.lang.String getDragMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dragMode, "self");
    }

    public void setDragMode(java.lang.String _dragMode) {
        getStateHelper().put(PropertyKeys.dragMode, _dragMode);
    }

    public java.lang.String getDropRestrict() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dropRestrict, "none");
    }

    public void setDropRestrict(java.lang.String _dropRestrict) {
        getStateHelper().put(PropertyKeys.dropRestrict, _dropRestrict);
    }

    public int getTabindex() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.tabindex, 0);
    }

    public void setTabindex(int _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public java.lang.Object getFilterBy() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.filterBy, null);
    }

    public void setFilterBy(java.lang.Object _filterBy) {
        getStateHelper().put(PropertyKeys.filterBy, _filterBy);
    }

    public java.lang.String getFilterMatchMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, "startsWith");
    }

    public void setFilterMatchMode(java.lang.String _filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
    }

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public boolean isMultipleDrag() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multipleDrag, false);
    }

    public void setMultipleDrag(boolean _multipleDrag) {
        getStateHelper().put(PropertyKeys.multipleDrag, _multipleDrag);
    }

    public boolean isDropCopyNode() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dropCopyNode, false);
    }

    public void setDropCopyNode(boolean _dropCopyNode) {
        getStateHelper().put(PropertyKeys.dropCopyNode, _dropCopyNode);
    }

    public javax.el.MethodExpression getOnDrop() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.onDrop, null);
    }

    public void setOnDrop(javax.el.MethodExpression _onDrop) {
        getStateHelper().put(PropertyKeys.onDrop, _onDrop);
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