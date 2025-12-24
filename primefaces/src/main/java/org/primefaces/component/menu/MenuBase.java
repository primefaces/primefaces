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
package org.primefaces.component.menu;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class MenuBase extends AbstractMenu implements Widget, OverlayMenu, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MenuRenderer";

    public MenuBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    @Property(description = "A menu model instance to create menu programmatically.")
    public abstract org.primefaces.model.menu.MenuModel getModel();

    @Override
    @Property(description = "Target component to attach the overlay menu.")
    public abstract String getTrigger();

    @Override
    @Property(description = "Corner of menu to align with trigger element.")
    public abstract String getMy();

    @Override
    @Property(description = "Corner of trigger to align with menu element.")
    public abstract String getAt();

    @Property(defaultValue = "false", description = "Defines positioning type of menu, either static or overlay.")
    public abstract boolean isOverlay();

    @Override
    @Property(defaultValue = "click", description = "Event to show the dynamic positioned menu.")
    public abstract String getTriggerEvent();

    @Property(defaultValue = "false", description = "Defines whether clicking the header of a submenu toggles the visibility of children menuitems.")
    public abstract boolean isToggleable();

    @Property(defaultValue = "false",
        description = "When enabled, menu toggleable state is saved globally across pages. If disabled then state is stored per view/page.")
    public abstract boolean isStatefulGlobal();

    @Property(defaultValue = "flip",
        description = "Applied only when overlay is set to true. When the overlay menu overflows the window in some direction, move it to " +
        "an alternative position. Supported values are flip, fit, flipfit and none. See https://api.jqueryui.com/position/ for more details." +
        " When the the body of your layout does not scroll, you may also want to set the option maxHeight.")
    public abstract String getCollision();

    @Property(description = "The maximum height of the menu. May be either a number (such as 200), which is interpreted as a height in pixels. " +
        "Alternatively, may also be a CSS length such as 90vh or 10em. Often used when overlay is set to true, but also works when it is set " +
        " to false. Useful in case the body of your layout does not scroll, especially in combination with the collision property.")
    public abstract String getMaxHeight();

    @Property(defaultValue = "@(body)", description = "Search expression for the element to which the menu overlay is appended.")
    public abstract String getAppendTo();

}