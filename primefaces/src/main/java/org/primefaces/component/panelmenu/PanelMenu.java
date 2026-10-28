/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.panelmenu;

import org.primefaces.cdk.api.FacesComponentInfo;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = PanelMenu.COMPONENT_TYPE, namespace = PanelMenu.COMPONENT_FAMILY)
@FacesComponentInfo(description = "PanelMenu is a hybrid component of accordionPanel and tree components.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class PanelMenu extends PanelMenuBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.PanelMenu";

    public static final String CONTAINER_CLASS = "p-panelmenu p-widget";
    public static final String INACTIVE_HEADER_CLASS = "p-widget p-panelmenu-header p-state-default";
    public static final String ACTIVE_HEADER_CLASS = "p-widget p-panelmenu-header p-state-default p-state-active";
    public static final String TAB_HEADER_ICON_CLASS = "p-icon p-toggle-icon p-panelmenu-header-icon";
    public static final String INACTIVE_ROOT_SUBMENU_CONTENT = "p-panelmenu-content p-widget-content p-hidden";
    public static final String ACTIVE_ROOT_SUBMENU_CONTENT = "p-panelmenu-content p-widget-content";
    public static final String MENUITEM_CLASS = "p-menuitem p-widget";
    public static final String DESCENDANT_SUBMENU_CLASS = "p-widget p-menuitem p-menu-parent";
    public static final String DESCENDANT_SUBMENU_ICON_CLASS = "p-panelmenu-icon p-icon p-toggle-icon p-panelmenu-submenu-icon";
    public static final String DESCENDANT_SUBMENU_EXPANDED_LIST_CLASS = "p-menu-list";
    public static final String DESCENDANT_SUBMENU_COLLAPSED_LIST_CLASS = "p-menu-list p-hidden";
    public static final String PANEL_CLASS = "p-panelmenu-panel";
    public static final String MENUITEM_LINK_WITH_ICON_CLASS = "p-menuitem-link p-menuitem-link-hasicon";

}