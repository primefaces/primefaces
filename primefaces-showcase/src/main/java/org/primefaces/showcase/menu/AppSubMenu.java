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
package org.primefaces.showcase.menu;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class AppSubMenu {

    @Inject
    private AppMenu appMenu;

    private MenuItem currentMenuItem;
    private MenuItem parentMenuItem;

    @PostConstruct
    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null || facesContext.getViewRoot() == null) {
            return;
        }

        String viewId = facesContext.getViewRoot().getViewId();
        String currentUrl = viewId.endsWith(".xhtml") ? viewId.substring(0, viewId.length() - 6) : viewId;

        currentMenuItem = appMenu.findMenuItemByUrl(currentUrl);
        if (currentMenuItem != null) {
            MenuItem parent = currentMenuItem.getParent();
            // Only use parent as the submenu group when it is a plain MenuItem with
            // children (e.g. "DatePicker"), never when it is a MenuCategory
            // (top-level grouping like "Data" or "Form").
            if (parent != null && !(parent instanceof MenuCategory) && parent.getMenuItems() != null) {
                parentMenuItem = parent;
            }
        }
    }

    /**
     * Returns all sub-menu items for the current view's parent group (e.g. all
     * DatePicker sub-pages when viewing any DatePicker page).
     * Returns an empty list when the current view has no sibling sub-pages.
     */
    public List<MenuItem> getSubMenuItems() {
        if (parentMenuItem != null) {
            return parentMenuItem.getMenuItems();
        }
        return Collections.emptyList();
    }

    /**
     * Returns the menu item that exactly matches the current view URL.
     */
    public MenuItem getCurrentMenuItem() {
        return currentMenuItem;
    }

    /**
     * Returns the parent group item (e.g. the "DatePicker" MenuItem) when the
     * current view belongs to a grouped set of sub-pages.
     */
    public MenuItem getParentMenuItem() {
        return parentMenuItem;
    }

    /**
     * Convenience check: true when the current view is part of a sub-menu group.
     */
    public boolean isHasSubMenuItems() {
        return parentMenuItem != null && !parentMenuItem.getMenuItems().isEmpty();
    }
}
