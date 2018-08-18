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
package org.primefaces.component.picklist;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class PickListBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PickListRenderer";

    public enum PropertyKeys {

        widgetVar,
        var,
        itemLabel,
        itemValue,
        style,
        styleClass,
        disabled,
        effect,
        effectSpeed,
        addLabel,
        addAllLabel,
        removeLabel,
        removeAllLabel,
        moveUpLabel,
        moveTopLabel,
        moveDownLabel,
        moveBottomLabel,
        showSourceControls,
        showTargetControls,
        onTransfer,
        label,
        itemDisabled,
        showSourceFilter,
        showTargetFilter,
        filterMatchMode,
        filterFunction,
        showCheckbox,
        labelDisplay,
        orientation,
        responsive,
        tabindex,
        filterEvent,
        filterDelay
    }

    public PickListBase() {
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

    public java.lang.String getVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(java.lang.String _var) {
        getStateHelper().put(PropertyKeys.var, _var);
    }

    public java.lang.String getItemLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.itemLabel, null);
    }

    public void setItemLabel(java.lang.String _itemLabel) {
        getStateHelper().put(PropertyKeys.itemLabel, _itemLabel);
    }

    public java.lang.Object getItemValue() {
        return (java.lang.Object) getStateHelper().eval(PropertyKeys.itemValue, null);
    }

    public void setItemValue(java.lang.Object _itemValue) {
        getStateHelper().put(PropertyKeys.itemValue, _itemValue);
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

    public boolean isDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean _disabled) {
        getStateHelper().put(PropertyKeys.disabled, _disabled);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fade");
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public java.lang.String getEffectSpeed() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effectSpeed, "fast");
    }

    public void setEffectSpeed(java.lang.String _effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, _effectSpeed);
    }

    public java.lang.String getAddLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.addLabel, "Add");
    }

    public void setAddLabel(java.lang.String _addLabel) {
        getStateHelper().put(PropertyKeys.addLabel, _addLabel);
    }

    public java.lang.String getAddAllLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.addAllLabel, "Add All");
    }

    public void setAddAllLabel(java.lang.String _addAllLabel) {
        getStateHelper().put(PropertyKeys.addAllLabel, _addAllLabel);
    }

    public java.lang.String getRemoveLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.removeLabel, "Remove");
    }

    public void setRemoveLabel(java.lang.String _removeLabel) {
        getStateHelper().put(PropertyKeys.removeLabel, _removeLabel);
    }

    public java.lang.String getRemoveAllLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.removeAllLabel, "Remove All");
    }

    public void setRemoveAllLabel(java.lang.String _removeAllLabel) {
        getStateHelper().put(PropertyKeys.removeAllLabel, _removeAllLabel);
    }

    public java.lang.String getMoveUpLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.moveUpLabel, "Move Up");
    }

    public void setMoveUpLabel(java.lang.String _moveUpLabel) {
        getStateHelper().put(PropertyKeys.moveUpLabel, _moveUpLabel);
    }

    public java.lang.String getMoveTopLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.moveTopLabel, "Move Top");
    }

    public void setMoveTopLabel(java.lang.String _moveTopLabel) {
        getStateHelper().put(PropertyKeys.moveTopLabel, _moveTopLabel);
    }

    public java.lang.String getMoveDownLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.moveDownLabel, "Move Down");
    }

    public void setMoveDownLabel(java.lang.String _moveDownLabel) {
        getStateHelper().put(PropertyKeys.moveDownLabel, _moveDownLabel);
    }

    public java.lang.String getMoveBottomLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.moveBottomLabel, "Move Bottom");
    }

    public void setMoveBottomLabel(java.lang.String _moveBottomLabel) {
        getStateHelper().put(PropertyKeys.moveBottomLabel, _moveBottomLabel);
    }

    public boolean isShowSourceControls() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showSourceControls, false);
    }

    public void setShowSourceControls(boolean _showSourceControls) {
        getStateHelper().put(PropertyKeys.showSourceControls, _showSourceControls);
    }

    public boolean isShowTargetControls() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showTargetControls, false);
    }

    public void setShowTargetControls(boolean _showTargetControls) {
        getStateHelper().put(PropertyKeys.showTargetControls, _showTargetControls);
    }

    public java.lang.String getOnTransfer() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.onTransfer, null);
    }

    public void setOnTransfer(java.lang.String _onTransfer) {
        getStateHelper().put(PropertyKeys.onTransfer, _onTransfer);
    }

    public java.lang.String getLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(java.lang.String _label) {
        getStateHelper().put(PropertyKeys.label, _label);
    }

    public boolean isItemDisabled() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.itemDisabled, false);
    }

    public void setItemDisabled(boolean _itemDisabled) {
        getStateHelper().put(PropertyKeys.itemDisabled, _itemDisabled);
    }

    public boolean isShowSourceFilter() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showSourceFilter, false);
    }

    public void setShowSourceFilter(boolean _showSourceFilter) {
        getStateHelper().put(PropertyKeys.showSourceFilter, _showSourceFilter);
    }

    public boolean isShowTargetFilter() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showTargetFilter, false);
    }

    public void setShowTargetFilter(boolean _showTargetFilter) {
        getStateHelper().put(PropertyKeys.showTargetFilter, _showTargetFilter);
    }

    public java.lang.String getFilterMatchMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
    }

    public void setFilterMatchMode(java.lang.String _filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, _filterMatchMode);
    }

    public java.lang.String getFilterFunction() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(java.lang.String _filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, _filterFunction);
    }

    public boolean isShowCheckbox() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showCheckbox, false);
    }

    public void setShowCheckbox(boolean _showCheckbox) {
        getStateHelper().put(PropertyKeys.showCheckbox, _showCheckbox);
    }

    public java.lang.String getLabelDisplay() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.labelDisplay, "tooltip");
    }

    public void setLabelDisplay(java.lang.String _labelDisplay) {
        getStateHelper().put(PropertyKeys.labelDisplay, _labelDisplay);
    }

    public java.lang.String getOrientation() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.orientation, "horizontal");
    }

    public void setOrientation(java.lang.String _orientation) {
        getStateHelper().put(PropertyKeys.orientation, _orientation);
    }

    public boolean isResponsive() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.responsive, false);
    }

    public void setResponsive(boolean _responsive) {
        getStateHelper().put(PropertyKeys.responsive, _responsive);
    }

    public java.lang.String getTabindex() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(java.lang.String _tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, _tabindex);
    }

    public java.lang.String getFilterEvent() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.filterEvent, null);
    }

    public void setFilterEvent(java.lang.String _filterEvent) {
        getStateHelper().put(PropertyKeys.filterEvent, _filterEvent);
    }

    public int getFilterDelay() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.filterDelay, java.lang.Integer.MAX_VALUE);
    }

    public void setFilterDelay(int _filterDelay) {
        getStateHelper().put(PropertyKeys.filterDelay, _filterDelay);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}