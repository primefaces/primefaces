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
package org.primefaces.component.panel;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ToggleEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.component.behavior.ClientBehaviorHolder;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "toggle", event = ToggleEvent.class, description = "Fires when panel is toggled.", defaultEvent = true),
    @FacesBehaviorEvent(name = "close", event = CloseEvent.class, description = "Fires when panel is closed.")
})
public abstract class PanelBase extends UIPanel implements Widget, ClientBehaviorHolder,
        MultiViewStateAware<PanelState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PanelRenderer";

    public PanelBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Built-in support to display a fully customizable popup menu, an icon to display the menu is placed at top-right corner")
    public abstract UIComponent getOptionsFacet();

    @Facet(description = "Allows to place HTML in the header. Alternative to headerText.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Allows to place HTML in the footer. Alternative to footerText.")
    public abstract UIComponent getFooterFacet();

    @Facet(description = "Allows to add custom action to the titlebar.")
    public abstract UIComponent getActionsFacet();

    @Property(description = "Header text.")
    public abstract String getHeader();

    @Property(description = "Footer text.")
    public abstract String getFooter();

    @Property(description = "Makes panel toggleable.")
    public abstract boolean isToggleable();

    @Property(defaultValue = "500", description = "Speed of toggling in milliseconds.")
    public abstract int getToggleSpeed();

    @Property(description = "Style of the panel.")
    public abstract String getStyle();

    @Property(description = "Style class of the panel.")
    public abstract String getStyleClass();

    @Property(description = "Renders a toggleable panel as collapsed.")
    public abstract boolean isCollapsed();

    @Property(description = "Make panel closable.")
    public abstract boolean isClosable();

    @Property(defaultValue = "500", description = "Speed of closing effect in milliseconds.")
    public abstract int getCloseSpeed();

    @Property(defaultValue = "true", description = "Renders panel as hidden.")
    public abstract boolean isVisible();

    @Property(description = "Title label for closer element of closable panel.")
    public abstract String getCloseTitle();

    @Property(description = "Title attribute for toggler element of toggleable panel.")
    public abstract String getToggleTitle();

    @Property(description = "Title attribute for menu element on panel header.")
    public abstract String getMenuTitle();

    @Property(defaultValue = "vertical", description = "Defines the orientation of the toggle animation, valid values are \"vertical\" and \"horizontal\".")
    public abstract String getToggleOrientation();

    @Property(description = "Defines if the panel is toggleable by clicking on the whole panel header.")
    public abstract boolean isToggleableHeader();

    @Property(description = "Render facets even if their children are not rendered.")
    public abstract boolean isRenderEmptyFacets();
}