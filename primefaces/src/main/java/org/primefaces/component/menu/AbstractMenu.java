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
package org.primefaces.component.menu;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.MenuItemAware;
import org.primefaces.component.api.RTLAware;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.component.UIPanel;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.FacesEvent;

@FacesComponentBase
public abstract class AbstractMenu extends UIPanel implements MenuItemAware, RTLAware {

    public static final String LIST_CLASS = "ui-menu-list ui-helper-reset";
    public static final String MENU_RTL_CLASS = "ui-menu-rtl";
    public static final String MENUITEM_CLASS = "ui-menuitem ui-widget";
    public static final String MENUITEM_LINK_CLASS = "ui-menuitem-link";
    public static final String MENUITEM_TEXT_CLASS = "ui-menuitem-text";
    public static final String MENUITEM_ICON_CLASS = "ui-menuitem-icon ui-icon";
    public static final String TIERED_SUBMENU_CLASS = "ui-widget ui-menuitem ui-menu-parent";
    public static final String TIERED_CHILD_SUBMENU_CLASS = "ui-widget-content ui-menu-list ui-helper-clearfix ui-menu-child ui-shadow";
    public static final String SUBMENU_RIGHT_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String SUBMENU_LEFT_ICON_CLASS = "ui-icon ui-icon-triangle-1-w";
    public static final String SUBMENU_DOWN_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String SUBMENU_LINK_CLASS = "ui-menuitem-link ui-submenu-link";
    public static final String OPTIONS_CLASS = "ui-menuitem ui-menubar-options ui-widget";
    public static final String START_CLASS = "ui-menuitem ui-menubar-start ui-widget";
    public static final String END_CLASS = "ui-menuitem ui-menubar-end ui-widget";

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Property(defaultValue = "true", description = "Defines whether the first level of submenus will be displayed on mouseover or click event.")
    public abstract boolean isAutoDisplay();

    @Property(defaultValue = "0", description = "Delay in milliseconds before displaying the submenu. Default is 0 meaning immediate.")
    public abstract int getShowDelay();

    @Property(defaultValue = "0", description = "Delay in milliseconds before hiding the submenu, if 0 not hidden until document.click().")
    public abstract int getHideDelay();

    @Property(description = "Event to toggle the submenus, valid values are \"hover\" and \"click\".")
    public abstract String getToggleEvent();

    public abstract MenuModel getModel();

    @Override
    public List<MenuElement> getElements() {
        MenuModel model = getModel();
        if (model != null) {
            return model.getElements();
        }
        else {
            return getChildren().stream()
                        .filter(MenuElement.class::isInstance)
                        .map(MenuElement.class::cast)
                        .collect(Collectors.toList());
        }
    }

    public int getElementsCount() {
        List<MenuElement> elements = getElements();

        return (elements == null) ? 0 : elements.size();
    }

    public boolean isDynamic() {
        return getValueExpression("model") != null;
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        broadcastMenuActionEvent(event, getFacesContext(), super::broadcast);
    }
}
