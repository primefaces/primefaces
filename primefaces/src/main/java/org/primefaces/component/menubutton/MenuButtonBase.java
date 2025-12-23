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
package org.primefaces.component.menubutton;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

@FacesComponentBase
public abstract class MenuButtonBase extends AbstractMenu implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MenuButtonRenderer";

    public MenuButtonBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    @Property(description = "MenuModel instance to create menus programmatically")
    public abstract org.primefaces.model.menu.MenuModel getModel();

    @Property(description = "Label of the button")
    public abstract String getValue();

    @Property(defaultValue = "false", description = "Disables or enables the button.")
    public abstract boolean isDisabled();

    @Property(description = "Icon of the menu button.")
    public abstract String getIcon();

    @Property(defaultValue = "left", description = "Position of the icon, valid values are left and right.")
    public abstract String getIconPos();

    @Property(defaultValue = "@(body)", description = "Appends the overlay to the element defined by search expression. Defaults to document body.")
    public abstract String getAppendTo();

    @Property(description = "Style class of the overlay menu element.")
    public abstract String getMenuStyleClass();

    @Property(description = "Advisory tooltip information.")
    public abstract String getTitle();

    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Property(defaultValue = "flip",
        description = "Applied only when overlay is set to true. When the overlay menu overflows the window in some direction, move it to " +
        "an alternative position. Supported values are flip, fit, flipfit and none. See https://api.jqueryui.com/position/ for more details." +
        " Defaults to flip. When you the body of your layout does not scroll, you may also want to set the option maxHeight.")
    public abstract String getCollision();

    @Property(description = "The maximum height of the menu. May be either a number (such as 200), which is interpreted as a height in pixels. " +
        "Alternatively, may also be a CSS length such as 90vh or 10em. Often used when overlay is set to true, but also works when it is set " +
        " to false. Useful in case the body of your layout does not scroll, especially in combination with the collision property.")
    public abstract String getMaxHeight();

    @Property(defaultValue = "0", description = "Delay in milliseconds before displaying the submenu. Default is 0 meaning immediate.")
    public abstract int getDelay();

    @Property(description = "Style of the button")
    public abstract String getButtonStyle();

    @Property(description = "Style class of the button")
    public abstract String getButtonStyleClass();

    @Property(defaultValue = "true", description = "If true, the button will be disabled during Ajax requests triggered by its menu items.")
    public abstract boolean isDisableOnAjax();

    @Property(description = "Icon class to display in place of the button label. If specified, the icon will be shown instead of the value (label).")
    public abstract String getButtonIcon();

}
