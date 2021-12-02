/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.List;

import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.TouchAware;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.component.api.Widget;
import org.primefaces.model.ResponsiveOption;

public abstract class CarouselBase extends UITabPanel implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, TouchAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CarouselRenderer";

    public enum PropertyKeys {
        widgetVar,
        page,
        circular,
        autoplayInterval,
        numVisible,
        numScroll,
        responsiveOptions,
        orientation,
        verticalViewPortHeight,
        style,
        styleClass,
        contentStyleClass,
        containerStyleClass,
        indicatorsContentStyleClass,
        headerText,
        footerText,
        touchable,
        onPageChange
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

    public int getPage() {
        return (Integer) getStateHelper().eval(PropertyKeys.page, 0);
    }

    public void setPage(int page) {
        getStateHelper().put(PropertyKeys.page, page);
    }

    public boolean isCircular() {
        return (Boolean) getStateHelper().eval(PropertyKeys.circular, false);
    }

    public void setCircular(boolean circular) {
        getStateHelper().put(PropertyKeys.circular, circular);
    }

    public int getAutoplayInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.autoplayInterval, 0);
    }

    public void setAutoplayInterval(int autoplayInterval) {
        getStateHelper().put(PropertyKeys.autoplayInterval, autoplayInterval);
    }

    public int getNumVisible() {
        return (Integer) getStateHelper().eval(PropertyKeys.numVisible, 1);
    }

    public void setNumVisible(int numVisible) {
        getStateHelper().put(PropertyKeys.numVisible, numVisible);
    }

    public int getNumScroll() {
        return (Integer) getStateHelper().eval(PropertyKeys.numScroll, 1);
    }

    public void setNumScroll(int numScroll) {
        getStateHelper().put(PropertyKeys.numScroll, numScroll);
    }

    public List<ResponsiveOption> getResponsiveOptions() {
        return (List<ResponsiveOption>) getStateHelper().eval(PropertyKeys.responsiveOptions, null);
    }

    public void setResponsiveOptions(List<ResponsiveOption> responsiveOptions) {
        getStateHelper().put(PropertyKeys.responsiveOptions, responsiveOptions);
    }

    public String getOrientation() {
        return (String) getStateHelper().eval(PropertyKeys.orientation, "horizontal");
    }

    public void setOrientation(String orientation) {
        getStateHelper().put(PropertyKeys.orientation, orientation);
    }

    public String getVerticalViewPortHeight() {
        return (String) getStateHelper().eval(PropertyKeys.verticalViewPortHeight, "300px");
    }

    public void setVerticalViewPortHeight(String verticalViewPortHeight) {
        getStateHelper().put(PropertyKeys.verticalViewPortHeight, verticalViewPortHeight);
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

    public String getContentStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.contentStyleClass, null);
    }

    public void setContentStyleClass(String contentStyleClass) {
        getStateHelper().put(PropertyKeys.contentStyleClass, contentStyleClass);
    }

    public String getContainerStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.containerStyleClass, null);
    }

    public void setContainerStyleClass(String containerStyleClass) {
        getStateHelper().put(PropertyKeys.containerStyleClass, containerStyleClass);
    }

    public String getIndicatorsContentStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.indicatorsContentStyleClass, null);
    }

    public void setIndicatorsContentStyleClass(String indicatorsContentStyleClass) {
        getStateHelper().put(PropertyKeys.indicatorsContentStyleClass, indicatorsContentStyleClass);
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

    @Override
    public boolean isTouchable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.touchable, false);
    }

    @Override
    public void setTouchable(boolean touchable) {
        getStateHelper().put(PropertyKeys.touchable, touchable);
    }

    public String getOnPageChange() {
        return (String) getStateHelper().eval(PropertyKeys.onPageChange, null);
    }

    public void setOnPageChange(String onPageChange) {
        getStateHelper().put(PropertyKeys.onPageChange, onPageChange);
    }
}
