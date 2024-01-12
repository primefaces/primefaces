/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.component.galleria;

import java.util.List;
import javax.faces.component.behavior.ClientBehaviorHolder;
import org.primefaces.component.api.UITabPanel;

import org.primefaces.component.api.Widget;
import org.primefaces.model.ResponsiveOption;

public abstract class GalleriaBase extends UITabPanel implements Widget, ClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GalleriaRenderer";

    public enum PropertyKeys {

        widgetVar,
        value,
        style,
        styleClass,
        activeIndex,
        fullScreen,
        closeIcon,
        numVisible,
        responsiveOptions,
        showThumbnails,
        showIndicators,
        showIndicatorsOnItem,
        showCaption,
        showItemNavigators,
        showThumbnailNavigators,
        showItemNavigatorsOnHover,
        changeItemOnIndicatorHover,
        circular,
        autoPlay,
        transitionInterval,
        thumbnailsPosition,
        verticalViewPortHeight,
        indicatorsPosition,
        tabindex
    }

    public GalleriaBase() {
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

    @Override
    public Object getValue() {
        return getStateHelper().eval(PropertyKeys.value, null);
    }

    @Override
    public void setValue(Object value) {
        getStateHelper().put(PropertyKeys.value, value);
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

    public int getActiveIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.activeIndex, 0);
    }

    public void setActiveIndex(int activeIndex) {
        getStateHelper().put(PropertyKeys.activeIndex, activeIndex);
    }

    public boolean isFullScreen() {
        return (Boolean) getStateHelper().eval(PropertyKeys.fullScreen, false);
    }

    public void setFullScreen(boolean fullScreen) {
        getStateHelper().put(PropertyKeys.fullScreen, fullScreen);
    }

    public String getCloseIcon() {
        return (String) getStateHelper().eval(PropertyKeys.closeIcon, null);
    }

    public void setCloseIcon(String closeIcon) {
        getStateHelper().put(PropertyKeys.closeIcon, closeIcon);
    }

    public int getNumVisible() {
        return (Integer) getStateHelper().eval(PropertyKeys.numVisible, 3);
    }

    public void setNumVisible(int numVisible) {
        getStateHelper().put(PropertyKeys.numVisible, numVisible);
    }

    public List<ResponsiveOption> getResponsiveOptions() {
        return (List<ResponsiveOption>) getStateHelper().eval(PropertyKeys.responsiveOptions, null);
    }

    public void setResponsiveOptions(List<ResponsiveOption> responsiveOptions) {
        getStateHelper().put(PropertyKeys.responsiveOptions, responsiveOptions);
    }

    public boolean isShowThumbnails() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showThumbnails, true);
    }

    public void setShowThumbnails(boolean showThumbnails) {
        getStateHelper().put(PropertyKeys.showThumbnails, showThumbnails);
    }

    public boolean isShowIndicators() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showIndicators, false);
    }

    public void setShowIndicators(boolean showIndicators) {
        getStateHelper().put(PropertyKeys.showIndicators, showIndicators);
    }

    public boolean isShowIndicatorsOnItem() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showIndicatorsOnItem, false);
    }

    public void setShowIndicatorsOnItem(boolean showIndicatorsOnItem) {
        getStateHelper().put(PropertyKeys.showIndicatorsOnItem, showIndicatorsOnItem);
    }

    public boolean isShowCaption() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showCaption, false);
    }

    public void setShowCaption(boolean showCaption) {
        getStateHelper().put(PropertyKeys.showCaption, showCaption);
    }

    public boolean isShowItemNavigators() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showItemNavigators, false);
    }

    public void setShowItemNavigators(boolean showItemNavigators) {
        getStateHelper().put(PropertyKeys.showItemNavigators, showItemNavigators);
    }

    public boolean isShowThumbnailNavigators() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showThumbnailNavigators, true);
    }

    public void setShowThumbnailNavigators(boolean showThumbnailNavigators) {
        getStateHelper().put(PropertyKeys.showThumbnailNavigators, showThumbnailNavigators);
    }

    public boolean isShowItemNavigatorsOnHover() {
        return (Boolean) getStateHelper().eval(PropertyKeys.showItemNavigatorsOnHover, false);
    }

    public void setShowItemNavigatorsOnHover(boolean showItemNavigatorsOnHover) {
        getStateHelper().put(PropertyKeys.showItemNavigatorsOnHover, showItemNavigatorsOnHover);
    }

    public boolean isChangeItemOnIndicatorHover() {
        return (Boolean) getStateHelper().eval(PropertyKeys.changeItemOnIndicatorHover, false);
    }

    public void setChangeItemOnIndicatorHover(boolean changeItemOnIndicatorHover) {
        getStateHelper().put(PropertyKeys.changeItemOnIndicatorHover, changeItemOnIndicatorHover);
    }

    public boolean isCircular() {
        return (Boolean) getStateHelper().eval(PropertyKeys.circular, false);
    }

    public void setCircular(boolean circular) {
        getStateHelper().put(PropertyKeys.circular, circular);
    }

    public boolean isAutoPlay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.autoPlay, false);
    }

    public void setAutoPlay(boolean autoPlay) {
        getStateHelper().put(PropertyKeys.autoPlay, autoPlay);
    }

    public int getTransitionInterval() {
        return (Integer) getStateHelper().eval(PropertyKeys.transitionInterval, 4000);
    }

    public void setTransitionInterval(int transitionInterval) {
        getStateHelper().put(PropertyKeys.transitionInterval, transitionInterval);
    }

    public String getThumbnailsPosition() {
        return (String) getStateHelper().eval(PropertyKeys.thumbnailsPosition, "bottom");
    }

    public void setThumbnailsPosition(String thumbnailsPosition) {
        getStateHelper().put(PropertyKeys.thumbnailsPosition, thumbnailsPosition);
    }

    public String getVerticalViewPortHeight() {
        return (String) getStateHelper().eval(PropertyKeys.verticalViewPortHeight, "450px");
    }

    public void setVerticalViewPortHeight(String verticalViewPortHeight) {
        getStateHelper().put(PropertyKeys.verticalViewPortHeight, verticalViewPortHeight);
    }

    public String getIndicatorsPosition() {
        return (String) getStateHelper().eval(PropertyKeys.indicatorsPosition, "bottom");
    }

    public void setIndicatorsPosition(String indicatorsPosition) {
        getStateHelper().put(PropertyKeys.indicatorsPosition, indicatorsPosition);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }
}
