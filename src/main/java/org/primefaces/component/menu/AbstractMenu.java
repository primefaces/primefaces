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
package org.primefaces.component.menu;

import org.primefaces.component.api.MenuItemAware;
import org.primefaces.model.menu.MenuModel;

import javax.faces.component.UIPanel;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import java.util.List;

public abstract class AbstractMenu extends UIPanel implements MenuItemAware {

    public static final String LIST_CLASS = "ui-menu-list ui-helper-reset";
    public static final String MENUITEM_CLASS = "ui-menuitem ui-widget ui-corner-all";
    public static final String MENUITEM_LINK_CLASS = "ui-menuitem-link ui-corner-all";
    public static final String MENUITEM_TEXT_CLASS = "ui-menuitem-text";
    public static final String MENUITEM_ICON_CLASS = "ui-menuitem-icon ui-icon";
    public static final String TIERED_SUBMENU_CLASS = "ui-widget ui-menuitem ui-corner-all ui-menu-parent";
    public static final String TIERED_CHILD_SUBMENU_CLASS = "ui-widget-content ui-menu-list ui-corner-all ui-helper-clearfix ui-menu-child ui-shadow";
    public static final String SUBMENU_RIGHT_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String SUBMENU_DOWN_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String SUBMENU_LINK_CLASS = "ui-menuitem-link ui-submenu-link ui-corner-all";
    public static final String SEPARATOR_CLASS = "ui-separator ui-state-default";
    public static final String OPTIONS_CLASS = "ui-menuitem ui-menubar-options ui-widget ui-corner-all";

    public enum PropertyKeys {
        tabindex
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    @Override
    public List getElements() {
        MenuModel model = getModel();
        if (model != null) {
            return model.getElements();
        }
        else {
            return getChildren();
        }
    }

    public int getElementsCount() {
        List elements = getElements();

        return (elements == null) ? 0 : elements.size();
    }

    public abstract MenuModel getModel();

    public boolean isDynamic() {
        return getValueExpression("model") != null;
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        broadcastMenuActionEvent(event, getFacesContext(), super::broadcast);
    }
}
