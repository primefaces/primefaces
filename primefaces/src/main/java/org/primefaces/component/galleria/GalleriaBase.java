/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.UITabPanelBase;
import org.primefaces.component.api.Widget;

import java.util.List;

import jakarta.faces.component.UIComponent;

public abstract class GalleriaBase extends UITabPanelBase implements Widget, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GalleriaRenderer";

    public GalleriaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows to place HTML in the header. Alternative to headerText.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Allows to place HTML in the footer. Alternative to footerText.")
    public abstract UIComponent getFooterFacet();

    @Facet(description = "Allows to place HTML in the caption. Alternative to caption.")
    public abstract UIComponent getCaptionFacet();

    @Facet(description = "Allows to place HTML in the thumbnail. Alternative to thumbnail.")
    public abstract UIComponent getThumbnailFacet();

    @Facet(description = "Allows to place HTML in the indicator. Alternative to indicator.")
    public abstract UIComponent getIndicatorFacet();

    @Property(description = "Value binding expression to a data model.")
    public abstract Object getValue();

    @Property(description = "Inline style of the container element.")
    public abstract String getStyle();

    @Property(description = "Style class of the container element.")
    public abstract String getStyleClass();

    @Property(defaultValue = "0", description = "Index of the first item. Default is 0.")
    public abstract int getActiveIndex();

    @Property(defaultValue = "false", description = "Whether to display the component on fullscreen. Default is false.")
    public abstract boolean isFullScreen();

    @Property(description = "Close icon on fullscreen mode.")
    public abstract String getCloseIcon();

    @Property(defaultValue = "3", description = "Number of items per page. Default is 3.")
    public abstract int getNumVisible();

    @Property(description = "A list of breakpoint ResponsiveOption for responsive design.")
    public abstract List getResponsiveOptions();

    @Property(defaultValue = "true", description = "Whether to display thumbnail container. Default is true.")
    public abstract boolean isShowThumbnails();

    @Property(defaultValue = "false", description = "Whether to display indicator container. Default is false.")
    public abstract boolean isShowIndicators();

    @Property(defaultValue = "false", description = "When enabled, indicator container is displayed on item container. Default is false.")
    public abstract boolean isShowIndicatorsOnItem();

    @Property(defaultValue = "false", description = "Whether to display caption container. Default is false.")
    public abstract boolean isShowCaption();

    @Property(defaultValue = "false", description = "Whether to display item navigators. Default is false.")
    public abstract boolean isShowItemNavigators();

    @Property(defaultValue = "true", description = "Whether to display thumbnail navigators. Default is true.")
    public abstract boolean isShowThumbnailNavigators();

    @Property(defaultValue = "false", description = "Whether to display item navigators on hover. Default is false.")
    public abstract boolean isShowItemNavigatorsOnHover();

    @Property(defaultValue = "false", description = "Whether to change item on indicator hover. Default is false.")
    public abstract boolean isChangeItemOnIndicatorHover();

    @Property(defaultValue = "false", description = "Defines if scrolling would be infinite. Default is false.")
    public abstract boolean isCircular();

    @Property(defaultValue = "false", description = "Enables autoplay for the gallery. Default is false.")
    public abstract boolean isAutoPlay();

    @Property(defaultValue = "4000", description = "Time in milliseconds between item transitions. Default is 4000.")
    public abstract int getTransitionInterval();

    @Property(defaultValue = "bottom", description = "Position of thumbnails. Valid values are \"bottom\", \"top\", \"left\" and \"right\". Default is bottom.")
    public abstract String getThumbnailsPosition();

    @Property(defaultValue = "450px", description = "Height of the viewport in vertical layout. Default is 450px.")
    public abstract String getVerticalViewPortHeight();

    @Property(defaultValue = "bottom", description = "Position of indicators. Valid values are \"bottom\", \"top\", \"left\" and \"right\". Default is bottom.")
    public abstract String getIndicatorsPosition();

    @Property(defaultValue = "0", description = "Specifies the tab order of element in tab navigation. Default is 0.")
    public abstract String getTabindex();
}
