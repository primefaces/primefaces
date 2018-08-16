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
package org.primefaces.component.tabview;

import org.primefaces.component.api.UITabPanel;
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
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.BehaviorEvent;


public abstract class TabViewBase extends UITabPanel implements org.primefaces.component.api.Widget,org.primefaces.component.api.RTLAware,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.TabView";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.TabViewRenderer";

	public enum PropertyKeys {

		widgetVar
		,activeIndex
		,effect
		,effectDuration
		,cache
		,onTabChange
		,onTabShow
		,style
		,styleClass
		,orientation
		,onTabClose
		,dir
		,scrollable
		,tabindex;
	}

	public TabViewBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public int getActiveIndex() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.activeIndex, 0);
	}
	public void setActiveIndex(int _activeIndex) {
		getStateHelper().put(PropertyKeys.activeIndex, _activeIndex);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
	}

	public java.lang.String getEffectDuration() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effectDuration, "normal");
	}
	public void setEffectDuration(java.lang.String _effectDuration) {
		getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
	}

	public boolean isCache() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
	}
	public void setCache(boolean _cache) {
		getStateHelper().put(PropertyKeys.cache, _cache);
	}

	public java.lang.String getOnTabChange() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabChange, null);
	}
	public void setOnTabChange(java.lang.String _onTabChange) {
		getStateHelper().put(PropertyKeys.onTabChange, _onTabChange);
	}

	public java.lang.String getOnTabShow() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabShow, null);
	}
	public void setOnTabShow(java.lang.String _onTabShow) {
		getStateHelper().put(PropertyKeys.onTabShow, _onTabShow);
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

	public java.lang.String getOrientation() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.orientation, "top");
	}
	public void setOrientation(java.lang.String _orientation) {
		getStateHelper().put(PropertyKeys.orientation, _orientation);
	}

	public java.lang.String getOnTabClose() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onTabClose, null);
	}
	public void setOnTabClose(java.lang.String _onTabClose) {
		getStateHelper().put(PropertyKeys.onTabClose, _onTabClose);
	}

	public java.lang.String getDir() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
	}
	public void setDir(java.lang.String _dir) {
		getStateHelper().put(PropertyKeys.dir, _dir);
	}

	public boolean isScrollable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.scrollable, false);
	}
	public void setScrollable(boolean _scrollable) {
		getStateHelper().put(PropertyKeys.scrollable, _scrollable);
	}

	public java.lang.String getTabindex() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, null);
	}
	public void setTabindex(java.lang.String _tabindex) {
		getStateHelper().put(PropertyKeys.tabindex, _tabindex);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
	public boolean isRTL() {
		return "rtl".equalsIgnoreCase(getDir());
	}
}