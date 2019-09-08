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
package org.primefaces.component.carousel;

import org.primefaces.component.api.UIData;
import org.primefaces.component.api.Widget;

public abstract class CarouselBase extends UIData implements Widget {

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

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public int getFirstVisible() {
        return (Integer) getStateHelper().eval(PropertyKeys.firstVisible, 0);
    }

    public void setFirstVisible(int firstVisible) {
        getStateHelper().put(PropertyKeys.firstVisible, firstVisible);
    }

    public int getNumVisible() {
        return (Integer) getStateHelper().eval(PropertyKeys.numVisible, 3);
    }

    public void setNumVisible(int numVisible) {
        getStateHelper().put(PropertyKeys.numVisible, numVisible);
    }

    public boolean isCircular() {
        return (Boolean) getStateHelper().eval(PropertyKeys.circular, false);
    }

    public void setCircular(boolean circular) {
        getStateHelper().put(PropertyKeys.circular, circular);
    }

    public boolean isVertical() {
        return (Boolean) getStateHelper().eval(PropertyKeys.vertical, false);
    }

    public void setVertical(boolean vertical) {
        getStateHelper().put(PropertyKeys.vertical, vertical);
    }

    public int getAutoPlayInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.autoPlayInterval, 0);
    }

    public void setAutoPlayInterval(int autoPlayInterval) {
        getStateHelper().put(PropertyKeys.autoPlayInterval, autoPlayInterval);
    }

    @Override
    public int getPageLinks() {
        return (Integer) getStateHelper().eval(PropertyKeys.pageLinks, 3);
    }

    @Override
    public void setPageLinks(int pageLinks) {
        getStateHelper().put(PropertyKeys.pageLinks, pageLinks);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, null);
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEasing() {
        return (String) getStateHelper().eval(PropertyKeys.easing, null);
    }

    public void setEasing(String easing) {
        getStateHelper().put(PropertyKeys.easing, easing);
    }

    public int getEffectDuration() {
        return (Integer) getStateHelper().eval(PropertyKeys.effectDuration, Integer.MIN_VALUE);
    }

    public void setEffectDuration(int effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, effectDuration);
    }

    public String getDropdownTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.dropdownTemplate, "{page}");
    }

    public void setDropdownTemplate(String dropdownTemplate) {
        getStateHelper().put(PropertyKeys.dropdownTemplate, dropdownTemplate);
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

    public String getItemStyle() {
        return (String) getStateHelper().eval(PropertyKeys.itemStyle, null);
    }

    public void setItemStyle(String itemStyle) {
        getStateHelper().put(PropertyKeys.itemStyle, itemStyle);
    }

    public String getItemStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.itemStyleClass, null);
    }

    public void setItemStyleClass(String itemStyleClass) {
        getStateHelper().put(PropertyKeys.itemStyleClass, itemStyleClass);
    }

    public String getHeaderText() {
        return (String) getStateHelper().eval(PropertyKeys.headerText, null);
    }

    public void setHeaderText(String headerText) {
        getStateHelper().put(PropertyKeys.headerText, headerText);
    }

    public String getFooterText() {
        return (String) getStateHelper().eval(PropertyKeys.footerText, null);
    }

    public void setFooterText(String footerText) {
        getStateHelper().put(PropertyKeys.footerText, footerText);
    }

    public boolean isResponsive() {
        return (Boolean) getStateHelper().eval(PropertyKeys.responsive, false);
    }

    public void setResponsive(boolean responsive) {
        getStateHelper().put(PropertyKeys.responsive, responsive);
    }

    public int getBreakpoint() {
        return (Integer) getStateHelper().eval(PropertyKeys.breakpoint, 640);
    }

    public void setBreakpoint(int breakpoint) {
        getStateHelper().put(PropertyKeys.breakpoint, breakpoint);
    }

    public boolean isToggleable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleable, false);
    }

    public void setToggleable(boolean toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, toggleable);
    }

    public int getToggleSpeed() {
        return (Integer) getStateHelper().eval(PropertyKeys.toggleSpeed, 500);
    }

    public void setToggleSpeed(int toggleSpeed) {
        getStateHelper().put(PropertyKeys.toggleSpeed, toggleSpeed);
    }

    public boolean isCollapsed() {
        return (Boolean) getStateHelper().eval(PropertyKeys.collapsed, false);
    }

    public void setCollapsed(boolean collapsed) {
        getStateHelper().put(PropertyKeys.collapsed, collapsed);
    }

    public boolean isStateful() {
        return (Boolean) getStateHelper().eval(PropertyKeys.stateful, false);
    }

    public void setStateful(boolean stateful) {
        getStateHelper().put(PropertyKeys.stateful, stateful);
    }
}