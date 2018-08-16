/*
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
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.Constants;
import org.primefaces.component.column.Column;
import javax.faces.component.UIComponent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.el.ValueExpression;
import javax.faces.convert.Converter;
import javax.faces.component.behavior.Behavior;


public abstract class AutoCompleteBase extends HtmlInputText implements org.primefaces.component.api.Widget,org.primefaces.component.api.InputHolder,org.primefaces.component.api.MixedClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.AutoComplete";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.AutoCompleteRenderer";

	public enum PropertyKeys {

		placeholder
		,widgetVar
		,completeMethod
		,var
		,itemLabel
		,itemValue
		,itemStyleClass
		,maxResults
		,minQueryLength
		,queryDelay
		,forceSelection
		,scrollHeight
		,effect
		,effectDuration
		,dropdown
		,panelStyle
		,panelStyleClass
		,multiple
		,itemtipMyPosition
		,itemtipAtPosition
		,cache
		,cacheTimeout
		,emptyMessage
		,appendTo
		,resultsMessage
		,groupBy
		,queryEvent
		,dropdownMode
		,autoHighlight
		,selectLimit
		,inputStyle
		,inputStyleClass
		,groupByTooltip
		,my
		,at
		,active
		,type
		,moreText
		,unique
		,dynamic
		,autoSelection;
	}

	public AutoCompleteBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getPlaceholder() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
	}
	public void setPlaceholder(java.lang.String _placeholder) {
		getStateHelper().put(PropertyKeys.placeholder, _placeholder);
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public javax.el.MethodExpression getCompleteMethod() {
		return (javax.el.MethodExpression) getStateHelper().eval(PropertyKeys.completeMethod, null);
	}
	public void setCompleteMethod(javax.el.MethodExpression _completeMethod) {
		getStateHelper().put(PropertyKeys.completeMethod, _completeMethod);
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

	public java.lang.String getItemStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemStyleClass, null);
	}
	public void setItemStyleClass(java.lang.String _itemStyleClass) {
		getStateHelper().put(PropertyKeys.itemStyleClass, _itemStyleClass);
	}

	public int getMaxResults() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.maxResults, java.lang.Integer.MAX_VALUE);
	}
	public void setMaxResults(int _maxResults) {
		getStateHelper().put(PropertyKeys.maxResults, _maxResults);
	}

	public int getMinQueryLength() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.minQueryLength, 1);
	}
	public void setMinQueryLength(int _minQueryLength) {
		getStateHelper().put(PropertyKeys.minQueryLength, _minQueryLength);
	}

	public int getQueryDelay() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.queryDelay, 300);
	}
	public void setQueryDelay(int _queryDelay) {
		getStateHelper().put(PropertyKeys.queryDelay, _queryDelay);
	}

	public boolean isForceSelection() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.forceSelection, false);
	}
	public void setForceSelection(boolean _forceSelection) {
		getStateHelper().put(PropertyKeys.forceSelection, _forceSelection);
	}

	public int getScrollHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MAX_VALUE);
	}
	public void setScrollHeight(int _scrollHeight) {
		getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
	}

	public int getEffectDuration() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectDuration, 400);
	}
	public void setEffectDuration(int _effectDuration) {
		getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
	}

	public boolean isDropdown() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dropdown, false);
	}
	public void setDropdown(boolean _dropdown) {
		getStateHelper().put(PropertyKeys.dropdown, _dropdown);
	}

	public java.lang.String getPanelStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyle, null);
	}
	public void setPanelStyle(java.lang.String _panelStyle) {
		getStateHelper().put(PropertyKeys.panelStyle, _panelStyle);
	}

	public java.lang.String getPanelStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.panelStyleClass, null);
	}
	public void setPanelStyleClass(java.lang.String _panelStyleClass) {
		getStateHelper().put(PropertyKeys.panelStyleClass, _panelStyleClass);
	}

	public boolean isMultiple() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.multiple, false);
	}
	public void setMultiple(boolean _multiple) {
		getStateHelper().put(PropertyKeys.multiple, _multiple);
	}

	public java.lang.String getItemtipMyPosition() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemtipMyPosition, null);
	}
	public void setItemtipMyPosition(java.lang.String _itemtipMyPosition) {
		getStateHelper().put(PropertyKeys.itemtipMyPosition, _itemtipMyPosition);
	}

	public java.lang.String getItemtipAtPosition() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemtipAtPosition, null);
	}
	public void setItemtipAtPosition(java.lang.String _itemtipAtPosition) {
		getStateHelper().put(PropertyKeys.itemtipAtPosition, _itemtipAtPosition);
	}

	public boolean isCache() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, false);
	}
	public void setCache(boolean _cache) {
		getStateHelper().put(PropertyKeys.cache, _cache);
	}

	public int getCacheTimeout() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.cacheTimeout, 300000);
	}
	public void setCacheTimeout(int _cacheTimeout) {
		getStateHelper().put(PropertyKeys.cacheTimeout, _cacheTimeout);
	}

	public java.lang.String getEmptyMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.emptyMessage, null);
	}
	public void setEmptyMessage(java.lang.String _emptyMessage) {
		getStateHelper().put(PropertyKeys.emptyMessage, _emptyMessage);
	}

	public java.lang.String getAppendTo() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.appendTo, null);
	}
	public void setAppendTo(java.lang.String _appendTo) {
		getStateHelper().put(PropertyKeys.appendTo, _appendTo);
	}

	public java.lang.String getResultsMessage() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.resultsMessage, null);
	}
	public void setResultsMessage(java.lang.String _resultsMessage) {
		getStateHelper().put(PropertyKeys.resultsMessage, _resultsMessage);
	}

	public java.lang.Object getGroupBy() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.groupBy, null);
	}
	public void setGroupBy(java.lang.Object _groupBy) {
		getStateHelper().put(PropertyKeys.groupBy, _groupBy);
	}

	public java.lang.String getQueryEvent() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.queryEvent, null);
	}
	public void setQueryEvent(java.lang.String _queryEvent) {
		getStateHelper().put(PropertyKeys.queryEvent, _queryEvent);
	}

	public java.lang.String getDropdownMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dropdownMode, null);
	}
	public void setDropdownMode(java.lang.String _dropdownMode) {
		getStateHelper().put(PropertyKeys.dropdownMode, _dropdownMode);
	}

	public boolean isAutoHighlight() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoHighlight, true);
	}
	public void setAutoHighlight(boolean _autoHighlight) {
		getStateHelper().put(PropertyKeys.autoHighlight, _autoHighlight);
	}

	public int getSelectLimit() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.selectLimit, java.lang.Integer.MAX_VALUE);
	}
	public void setSelectLimit(int _selectLimit) {
		getStateHelper().put(PropertyKeys.selectLimit, _selectLimit);
	}

	public java.lang.String getInputStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.inputStyle, null);
	}
	public void setInputStyle(java.lang.String _inputStyle) {
		getStateHelper().put(PropertyKeys.inputStyle, _inputStyle);
	}

	public java.lang.String getInputStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.inputStyleClass, null);
	}
	public void setInputStyleClass(java.lang.String _inputStyleClass) {
		getStateHelper().put(PropertyKeys.inputStyleClass, _inputStyleClass);
	}

	public java.lang.String getGroupByTooltip() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.groupByTooltip, null);
	}
	public void setGroupByTooltip(java.lang.String _groupByTooltip) {
		getStateHelper().put(PropertyKeys.groupByTooltip, _groupByTooltip);
	}

	public java.lang.String getMy() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.my, null);
	}
	public void setMy(java.lang.String _my) {
		getStateHelper().put(PropertyKeys.my, _my);
	}

	public java.lang.String getAt() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.at, null);
	}
	public void setAt(java.lang.String _at) {
		getStateHelper().put(PropertyKeys.at, _at);
	}

	public boolean isActive() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.active, true);
	}
	public void setActive(boolean _active) {
		getStateHelper().put(PropertyKeys.active, _active);
	}

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, "text");
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
	}

	public java.lang.String getMoreText() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.moreText, "...");
	}
	public void setMoreText(java.lang.String _moreText) {
		getStateHelper().put(PropertyKeys.moreText, _moreText);
	}

	public boolean isUnique() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.unique, false);
	}
	public void setUnique(boolean _unique) {
		getStateHelper().put(PropertyKeys.unique, _unique);
	}

	public boolean isDynamic() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
	}
	public void setDynamic(boolean _dynamic) {
		getStateHelper().put(PropertyKeys.dynamic, _dynamic);
	}

	public boolean isAutoSelection() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.autoSelection, true);
	}
	public void setAutoSelection(boolean _autoSelection) {
		getStateHelper().put(PropertyKeys.autoSelection, _autoSelection);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}