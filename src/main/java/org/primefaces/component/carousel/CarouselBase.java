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
package org.primefaces.component.carousel;

import org.primefaces.component.api.UIData;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class CarouselBase extends UIData implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CarouselRenderer";

    public enum PropertyKeys {

        widgetVar,
        firstVisible,
        numVisible,
        circular,
        vertical,
        autoPlayInterval,
        pageLinks,
        effect,
        easing,
        effectDuration,
        dropdownTemplate,
        style,
        styleClass,
        itemStyle,
        itemStyleClass,
        headerText,
        footerText,
        responsive,
        breakpoint,
        toggleable,
        toggleSpeed,
        collapsed,
        stateful
    }

    public CarouselBase() {
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

    public int getFirstVisible() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.firstVisible, 0);
    }

    public void setFirstVisible(int _firstVisible) {
        getStateHelper().put(PropertyKeys.firstVisible, _firstVisible);
    }

    public int getNumVisible() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.numVisible, 3);
    }

    public void setNumVisible(int _numVisible) {
        getStateHelper().put(PropertyKeys.numVisible, _numVisible);
    }

    public boolean isCircular() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.circular, false);
    }

    public void setCircular(boolean _circular) {
        getStateHelper().put(PropertyKeys.circular, _circular);
    }

    public boolean isVertical() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.vertical, false);
    }

    public void setVertical(boolean _vertical) {
        getStateHelper().put(PropertyKeys.vertical, _vertical);
    }

    public int getAutoPlayInterval() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.autoPlayInterval, 0);
    }

    public void setAutoPlayInterval(int _autoPlayInterval) {
        getStateHelper().put(PropertyKeys.autoPlayInterval, _autoPlayInterval);
    }

    @Override
    public int getPageLinks() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.pageLinks, 3);
    }

    @Override
    public void setPageLinks(int _pageLinks) {
        getStateHelper().put(PropertyKeys.pageLinks, _pageLinks);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public java.lang.String getEasing() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.easing, null);
    }

    public void setEasing(java.lang.String _easing) {
        getStateHelper().put(PropertyKeys.easing, _easing);
    }

    public int getEffectDuration() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectDuration, java.lang.Integer.MIN_VALUE);
    }

    public void setEffectDuration(int _effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
    }

    public java.lang.String getDropdownTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.dropdownTemplate, "{page}");
    }

    public void setDropdownTemplate(java.lang.String _dropdownTemplate) {
        getStateHelper().put(PropertyKeys.dropdownTemplate, _dropdownTemplate);
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

    public java.lang.String getItemStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.itemStyle, null);
    }

    public void setItemStyle(java.lang.String _itemStyle) {
        getStateHelper().put(PropertyKeys.itemStyle, _itemStyle);
    }

    public java.lang.String getItemStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.itemStyleClass, null);
    }

    public void setItemStyleClass(java.lang.String _itemStyleClass) {
        getStateHelper().put(PropertyKeys.itemStyleClass, _itemStyleClass);
    }

    public java.lang.String getHeaderText() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.headerText, null);
    }

    public void setHeaderText(java.lang.String _headerText) {
        getStateHelper().put(PropertyKeys.headerText, _headerText);
    }

    public java.lang.String getFooterText() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.footerText, null);
    }

    public void setFooterText(java.lang.String _footerText) {
        getStateHelper().put(PropertyKeys.footerText, _footerText);
    }

    public boolean isResponsive() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.responsive, false);
    }

    public void setResponsive(boolean _responsive) {
        getStateHelper().put(PropertyKeys.responsive, _responsive);
    }

    public int getBreakpoint() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.breakpoint, 640);
    }

    public void setBreakpoint(int _breakpoint) {
        getStateHelper().put(PropertyKeys.breakpoint, _breakpoint);
    }

    public boolean isToggleable() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
    }

    public void setToggleable(boolean _toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, _toggleable);
    }

    public int getToggleSpeed() {
        return (java.lang.Integer) getStateHelper().eval(PropertyKeys.toggleSpeed, 500);
    }

    public void setToggleSpeed(int _toggleSpeed) {
        getStateHelper().put(PropertyKeys.toggleSpeed, _toggleSpeed);
    }

    public boolean isCollapsed() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean _collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, _collapsed);
    }

    public boolean isStateful() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stateful, false);
    }

    public void setStateful(boolean _stateful) {
        getStateHelper().put(PropertyKeys.stateful, _stateful);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}