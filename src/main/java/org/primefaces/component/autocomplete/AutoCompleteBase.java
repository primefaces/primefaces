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
package org.primefaces.component.autocomplete;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class AutoCompleteBase extends HtmlInputText implements Widget, InputHolder, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AutoCompleteRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        completeMethod,
        var,
        itemLabel,
        itemValue,
        itemStyleClass,
        maxResults,
        minQueryLength,
        queryDelay,
        forceSelection,
        scrollHeight,
        effect,
        effectDuration,
        dropdown,
        panelStyle,
        panelStyleClass,
        multiple,
        itemtipMyPosition,
        itemtipAtPosition,
        cache,
        cacheTimeout,
        emptyMessage,
        appendTo,
        resultsMessage,
        groupBy,
        queryEvent,
        dropdownMode,
        autoHighlight,
        selectLimit,
        inputStyle,
        inputStyleClass,
        groupByTooltip,
        my,
        at,
        active,
        type,
        moreText,
        unique,
        dynamic,
        autoSelection
    }

    public AutoCompleteBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public javax.el.MethodExpression getCompleteMethod() {
        return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.completeMethod, null);
    }

    public void setCompleteMethod(javax.el.MethodExpression completeMethod) {
        getStateHelper().put(PropertyKeys.completeMethod, completeMethod);
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

    public String getItemStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.itemStyleClass, null);
    }

    public void setItemStyleClass(String itemStyleClass) {
        getStateHelper().put(PropertyKeys.itemStyleClass, itemStyleClass);
    }

    public int getMaxResults() {
        return (Integer) getStateHelper().eval(PropertyKeys.maxResults, Integer.MAX_VALUE);
    }

    public void setMaxResults(int maxResults) {
        getStateHelper().put(PropertyKeys.maxResults, maxResults);
    }

    public int getMinQueryLength() {
        return (Integer) getStateHelper().eval(PropertyKeys.minQueryLength, 1);
    }

    public void setMinQueryLength(int minQueryLength) {
        getStateHelper().put(PropertyKeys.minQueryLength, minQueryLength);
    }

    public int getQueryDelay() {
        return (Integer) getStateHelper().eval(PropertyKeys.queryDelay, 300);
    }

    public void setQueryDelay(int queryDelay) {
        getStateHelper().put(PropertyKeys.queryDelay, queryDelay);
    }

    public boolean isForceSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.forceSelection, false);
    }

    public void setForceSelection(boolean forceSelection) {
        getStateHelper().put(PropertyKeys.forceSelection, forceSelection);
    }

    public int getScrollHeight() {
        return (Integer) getStateHelper().eval(PropertyKeys.scrollHeight, Integer.MAX_VALUE);
    }

    public void setScrollHeight(int scrollHeight) {
        getStateHelper().put(PropertyKeys.scrollHeight, scrollHeight);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public int getEffectDuration() {
        return (Integer) getStateHelper().eval(PropertyKeys.effectDuration, 400);
    }

    public void setEffectDuration(int effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, effectDuration);
    }

    public boolean isDropdown() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dropdown, false);
    }

    public void setDropdown(boolean dropdown) {
        getStateHelper().put(PropertyKeys.dropdown, dropdown);
    }

    public String getPanelStyle() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyle, null);
    }

    public void setPanelStyle(String panelStyle) {
        getStateHelper().put(PropertyKeys.panelStyle, panelStyle);
    }

    public String getPanelStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
    }

    public void setPanelStyleClass(String panelStyleClass) {
        getStateHelper().put(PropertyKeys.panelStyleClass, panelStyleClass);
    }

    public boolean isMultiple() {
        return (Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
    }

    public void setMultiple(boolean multiple) {
        getStateHelper().put(PropertyKeys.multiple, multiple);
    }

    public String getItemtipMyPosition() {
        return (String) getStateHelper().eval(PropertyKeys.itemtipMyPosition, null);
    }

    public void setItemtipMyPosition(String itemtipMyPosition) {
        getStateHelper().put(PropertyKeys.itemtipMyPosition, itemtipMyPosition);
    }

    public String getItemtipAtPosition() {
        return (String) getStateHelper().eval(PropertyKeys.itemtipAtPosition, null);
    }

    public void setItemtipAtPosition(String itemtipAtPosition) {
        getStateHelper().put(PropertyKeys.itemtipAtPosition, itemtipAtPosition);
    }

    public boolean isCache() {
        return (Boolean) getStateHelper().eval(PropertyKeys.cache, false);
    }

    public void setCache(boolean cache) {
        getStateHelper().put(PropertyKeys.cache, cache);
    }

    public int getCacheTimeout() {
        return (Integer) getStateHelper().eval(PropertyKeys.cacheTimeout, 300000);
    }

    public void setCacheTimeout(int cacheTimeout) {
        getStateHelper().put(PropertyKeys.cacheTimeout, cacheTimeout);
    }

    public String getEmptyMessage() {
        return (String) getStateHelper().eval(PropertyKeys.emptyMessage, null);
    }

    public void setEmptyMessage(String emptyMessage) {
        getStateHelper().put(PropertyKeys.emptyMessage, emptyMessage);
    }

    public String getAppendTo() {
        return (String) getStateHelper().eval(PropertyKeys.appendTo, null);
    }

    public void setAppendTo(String appendTo) {
        getStateHelper().put(PropertyKeys.appendTo, appendTo);
    }

    public String getResultsMessage() {
        return (String) getStateHelper().eval(PropertyKeys.resultsMessage, null);
    }

    public void setResultsMessage(String resultsMessage) {
        getStateHelper().put(PropertyKeys.resultsMessage, resultsMessage);
    }

    public Object getGroupBy() {
        return getStateHelper().eval(PropertyKeys.groupBy, null);
    }

    public void setGroupBy(Object groupBy) {
        getStateHelper().put(PropertyKeys.groupBy, groupBy);
    }

    public String getQueryEvent() {
        return (String) getStateHelper().eval(PropertyKeys.queryEvent, null);
    }

    public void setQueryEvent(String queryEvent) {
        getStateHelper().put(PropertyKeys.queryEvent, queryEvent);
    }

    public String getDropdownMode() {
        return (String) getStateHelper().eval(PropertyKeys.dropdownMode, null);
    }

    public void setDropdownMode(String dropdownMode) {
        getStateHelper().put(PropertyKeys.dropdownMode, dropdownMode);
    }

    public boolean isAutoHighlight() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoHighlight, true);
    }

    public void setAutoHighlight(boolean autoHighlight) {
        getStateHelper().put(PropertyKeys.autoHighlight, autoHighlight);
    }

    public int getSelectLimit() {
        return (Integer) getStateHelper().eval(PropertyKeys.selectLimit, Integer.MAX_VALUE);
    }

    public void setSelectLimit(int selectLimit) {
        getStateHelper().put(PropertyKeys.selectLimit, selectLimit);
    }

    public String getInputStyle() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyle, null);
    }

    public void setInputStyle(String inputStyle) {
        getStateHelper().put(PropertyKeys.inputStyle, inputStyle);
    }

    public String getInputStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
    }

    public void setInputStyleClass(String inputStyleClass) {
        getStateHelper().put(PropertyKeys.inputStyleClass, inputStyleClass);
    }

    public String getGroupByTooltip() {
        return (String) getStateHelper().eval(PropertyKeys.groupByTooltip, null);
    }

    public void setGroupByTooltip(String groupByTooltip) {
        getStateHelper().put(PropertyKeys.groupByTooltip, groupByTooltip);
    }

    public String getMy() {
        return (String) getStateHelper().eval(PropertyKeys.my, null);
    }

    public void setMy(String my) {
        getStateHelper().put(PropertyKeys.my, my);
    }

    public String getAt() {
        return (String) getStateHelper().eval(PropertyKeys.at, null);
    }

    public void setAt(String at) {
        getStateHelper().put(PropertyKeys.at, at);
    }

    public boolean isActive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.active, true);
    }

    public void setActive(boolean active) {
        getStateHelper().put(PropertyKeys.active, active);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, "text");
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    public String getMoreText() {
        return (String) getStateHelper().eval(PropertyKeys.moreText, "...");
    }

    public void setMoreText(String moreText) {
        getStateHelper().put(PropertyKeys.moreText, moreText);
    }

    public boolean isUnique() {
        return (Boolean) getStateHelper().eval(PropertyKeys.unique, false);
    }

    public void setUnique(boolean unique) {
        getStateHelper().put(PropertyKeys.unique, unique);
    }

    public boolean isDynamic() {
        return (Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, dynamic);
    }

    public boolean isAutoSelection() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoSelection, true);
    }

    public void setAutoSelection(boolean autoSelection) {
        getStateHelper().put(PropertyKeys.autoSelection, autoSelection);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}