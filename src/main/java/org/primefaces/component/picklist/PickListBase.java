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
package org.primefaces.component.picklist;

import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

public abstract class PickListBase extends UIInput implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

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
        escape,
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
        filterDelay,
        escapeValue
    }

    public PickListBase() {
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

    public String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public String getItemLabel() {
        return (String) getStateHelper().eval(PropertyKeys.itemLabel, null);
    }

    public void setItemLabel(String itemLabel) {
        getStateHelper().put(PropertyKeys.itemLabel, itemLabel);
    }

    public Object getItemValue() {
        return getStateHelper().eval(PropertyKeys.itemValue, null);
    }

    public void setItemValue(Object itemValue) {
        getStateHelper().put(PropertyKeys.itemValue, itemValue);
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

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, "fade");
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEffectSpeed() {
        return (String) getStateHelper().eval(PropertyKeys.effectSpeed, "fast");
    }

    public void setEffectSpeed(String effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, effectSpeed);
    }

    public String getAddLabel() {
        return (String) getStateHelper().eval(PropertyKeys.addLabel, "Add");
    }

    public void setAddLabel(String addLabel) {
        getStateHelper().put(PropertyKeys.addLabel, addLabel);
    }

    public String getAddAllLabel() {
        return (String) getStateHelper().eval(PropertyKeys.addAllLabel, "Add All");
    }

    public void setAddAllLabel(String addAllLabel) {
        getStateHelper().put(PropertyKeys.addAllLabel, addAllLabel);
    }

    public String getRemoveLabel() {
        return (String) getStateHelper().eval(PropertyKeys.removeLabel, "Remove");
    }

    public void setRemoveLabel(String removeLabel) {
        getStateHelper().put(PropertyKeys.removeLabel, removeLabel);
    }

    public String getRemoveAllLabel() {
        return (String) getStateHelper().eval(PropertyKeys.removeAllLabel, "Remove All");
    }

    public void setRemoveAllLabel(String removeAllLabel) {
        getStateHelper().put(PropertyKeys.removeAllLabel, removeAllLabel);
    }

    public String getMoveUpLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveUpLabel, "Move Up");
    }

    public void setMoveUpLabel(String moveUpLabel) {
        getStateHelper().put(PropertyKeys.moveUpLabel, moveUpLabel);
    }

    public String getMoveTopLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveTopLabel, "Move Top");
    }

    public void setMoveTopLabel(String moveTopLabel) {
        getStateHelper().put(PropertyKeys.moveTopLabel, moveTopLabel);
    }

    public String getMoveDownLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveDownLabel, "Move Down");
    }

    public void setMoveDownLabel(String moveDownLabel) {
        getStateHelper().put(PropertyKeys.moveDownLabel, moveDownLabel);
    }

    public String getMoveBottomLabel() {
        return (String) getStateHelper().eval(PropertyKeys.moveBottomLabel, "Move Bottom");
    }

    public void setMoveBottomLabel(String moveBottomLabel) {
        getStateHelper().put(PropertyKeys.moveBottomLabel, moveBottomLabel);
    }

    public boolean isShowSourceControls() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showSourceControls, false);
    }

    public void setShowSourceControls(boolean showSourceControls) {
        getStateHelper().put(PropertyKeys.showSourceControls, showSourceControls);
    }

    public boolean isShowTargetControls() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showTargetControls, false);
    }

    public void setShowTargetControls(boolean showTargetControls) {
        getStateHelper().put(PropertyKeys.showTargetControls, showTargetControls);
    }

    public String getOnTransfer() {
        return (String) getStateHelper().eval(PropertyKeys.onTransfer, null);
    }

    public void setOnTransfer(String onTransfer) {
        getStateHelper().put(PropertyKeys.onTransfer, onTransfer);
    }

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public boolean isItemDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.itemDisabled, false);
    }

    public void setItemDisabled(boolean itemDisabled) {
        getStateHelper().put(PropertyKeys.itemDisabled, itemDisabled);
    }

    public boolean isShowSourceFilter() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showSourceFilter, false);
    }

    public void setShowSourceFilter(boolean showSourceFilter) {
        getStateHelper().put(PropertyKeys.showSourceFilter, showSourceFilter);
    }

    public boolean isShowTargetFilter() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showTargetFilter, false);
    }

    public void setShowTargetFilter(boolean showTargetFilter) {
        getStateHelper().put(PropertyKeys.showTargetFilter, showTargetFilter);
    }

    public String getFilterMatchMode() {
        return (String) getStateHelper().eval(PropertyKeys.filterMatchMode, null);
    }

    public void setFilterMatchMode(String filterMatchMode) {
        getStateHelper().put(PropertyKeys.filterMatchMode, filterMatchMode);
    }

    public String getFilterFunction() {
        return (String) getStateHelper().eval(PropertyKeys.filterFunction, null);
    }

    public void setFilterFunction(String filterFunction) {
        getStateHelper().put(PropertyKeys.filterFunction, filterFunction);
    }

    public boolean isShowCheckbox() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showCheckbox, false);
    }

    public void setShowCheckbox(boolean showCheckbox) {
        getStateHelper().put(PropertyKeys.showCheckbox, showCheckbox);
    }

    public String getLabelDisplay() {
        return (String) getStateHelper().eval(PropertyKeys.labelDisplay, "tooltip");
    }

    public void setLabelDisplay(String labelDisplay) {
        getStateHelper().put(PropertyKeys.labelDisplay, labelDisplay);
    }

    public String getOrientation() {
        return (String) getStateHelper().eval(PropertyKeys.orientation, "horizontal");
    }

    public void setOrientation(String orientation) {
        getStateHelper().put(PropertyKeys.orientation, orientation);
    }

    public boolean isResponsive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.responsive, false);
    }

    public void setResponsive(boolean responsive) {
        getStateHelper().put(PropertyKeys.responsive, responsive);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public String getFilterEvent() {
        return (String) getStateHelper().eval(PropertyKeys.filterEvent, null);
    }

    public void setFilterEvent(String filterEvent) {
        getStateHelper().put(PropertyKeys.filterEvent, filterEvent);
    }

    public int getFilterDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.filterDelay, Integer.MAX_VALUE);
    }

    public void setFilterDelay(int filterDelay) {
        getStateHelper().put(PropertyKeys.filterDelay, filterDelay);
    }

    public boolean isEscape() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escape, true);
    }

    public void setEscape(boolean escape) {
        getStateHelper().put(PropertyKeys.escape, escape);
    }

    public boolean isEscapeValue() {
        return (Boolean) getStateHelper().eval(PropertyKeys.escapeValue, true);
    }

    public void setEscapeValue(boolean escapeValue) {
        getStateHelper().put(PropertyKeys.escapeValue, escapeValue);
    }
}