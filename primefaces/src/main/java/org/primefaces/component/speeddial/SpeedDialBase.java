/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.speeddial;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

@FacesComponentBase
public abstract class SpeedDialBase extends AbstractMenu implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SpeedDialRenderer";

    public SpeedDialBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    @Property(description = "MenuModel instance to create menus programmatically")
    public abstract org.primefaces.model.menu.MenuModel getModel();

    @Property(defaultValue = "false", description = "Whether the component is disabled.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "Specifies the visibility of the overlay.")
    public abstract boolean isVisible();

    @Property(defaultValue = "up",
        description = "Specifies the opening direction of actions. Valid values are up, down, left, right, up-left, up-right, down-left and down-right.")
    public abstract String getDirection();

    @Property(defaultValue = "30", description = "Transition delay step for each action item.")
    public abstract int getTransitionDelay();

    @Property(defaultValue = "linear",
        description = "Specifies the opening type of actions. Valid values are linear, circle, semi-circle and quarter-circle.")
    public abstract String getType();

    @Property(defaultValue = "0", description = "Radius for circle types.")
    public abstract int getRadius();

    @Property(defaultValue = "false", description = "Whether to show a mask element behind the speed dial.")
    public abstract boolean isMask();

    @Property(defaultValue = "true", description = "Whether the actions close when clicked outside.")
    public abstract boolean isHideOnClickOutside();

    @Property(description = "Inline style of the button element.")
    public abstract String getButtonStyle();

    @Property(description = "Style class of the button element.")
    public abstract String getButtonStyleClass();

    @Property(description = "Inline style of the mask element.")
    public abstract String getMaskStyle();

    @Property(description = "Style class of the mask element.")
    public abstract String getMaskStyleClass();

    @Property(defaultValue = "pi pi-plus", description = "Icon of the button when speed dial is closed.")
    public abstract String getShowIcon();

    @Property(description = "Icon of the button when speed dial is opened.")
    public abstract String getHideIcon();

    @Property(defaultValue = "true", description = "Whether to show rotate animation when button state changes.")
    public abstract boolean isRotateAnimation();

    @Property(description = "Client side callback to execute when visibility changes.")
    public abstract String getOnVisibleChange();

    @Property(description = "Client side callback to execute when button is clicked.")
    public abstract String getOnClick();

    @Property(description = "Client side callback to execute when speed dial is shown.")
    public abstract String getOnShow();

    @Property(description = "Client side callback to execute when speed dial is hidden.")
    public abstract String getOnHide();

    @Property(defaultValue = "false", description = "Whether to keep the speed dial open after clicking an action.")
    public abstract boolean isKeepOpen();

    @Property(description = "Badge value to display on the button.")
    public abstract Object getBadge();

    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Property(description = "Advisory tooltip information.")
    public abstract String getTitle();

}
