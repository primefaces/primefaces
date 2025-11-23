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
package org.primefaces.component.tabview;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.TouchAware;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.component.api.Widget;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import jakarta.faces.component.UIComponent;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "tabChange", event = TabChangeEvent.class, description = "Fires when a tab is changed.", defaultEvent = true),
    @FacesBehaviorEvent(name = "tabClose", event = TabCloseEvent.class, description = "Fires when a tab is closed.")
})
public abstract class TabViewBase extends UITabPanel implements Widget, RTLAware, TouchAware, PrimeClientBehaviorHolder,
        MultiViewStateAware<TabViewState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TabViewRenderer";

    public TabViewBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows to place HTML in the footer. Alternative to footerText.")
    public abstract UIComponent getFooterFacet();

    @Facet(description = "Allows to add custom action to the tab header.")
    public abstract UIComponent getActionsFacet();

    @Property(defaultValue = "0", description = "Index of the active tab. Default is 0.")
    public abstract int getActiveIndex();

    @Property(description = "Animation effect to use when changing tabs.")
    public abstract String getEffect();

    @Property(defaultValue = "normal", description = "Duration of the animation effect. Default is normal.")
    public abstract String getEffectDuration();

    @Property(defaultValue = "true", description = "When enabled, dynamically loaded tab contents are cached. Default is true.")
    public abstract boolean isCache();

    @Property(description = "Client side callback to execute when a tab is changed.")
    public abstract String getOnTabChange();

    @Property(description = "Client side callback to execute when a tab is shown.")
    public abstract String getOnTabShow();

    @Property(description = "Inline style of the tab view.")
    public abstract String getStyle();

    @Property(description = "Style class of the tab view.")
    public abstract String getStyleClass();

    @Property(defaultValue = "top", description = "Specifies the position of tab headers. Valid values are \"top\", \"bottom\", \"left\" and \"right\"."
        + " Default is top.")
    public abstract String getOrientation();

    @Property(description = "Client side callback to execute when a tab is closed.")
    public abstract String getOnTabClose();

    @Property(defaultValue = "false", description = "When enabled, tabs can be scrolled. Default is false.")
    public abstract boolean isScrollable();

    @Property(description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Property(defaultValue = "false", description = "When enabled, focuses on the tab containing the first error on validation failure. Default is false.")
    public abstract boolean isFocusOnError();

    @Property(defaultValue = "false", description = "When enabled, focuses on the last active tab when the component is rendered. Default is false.")
    public abstract boolean isFocusOnLastActiveTab();
}
