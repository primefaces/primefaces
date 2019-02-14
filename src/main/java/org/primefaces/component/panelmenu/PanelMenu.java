/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class PanelMenu extends PanelMenuBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.PanelMenu";

    public static final String CONTAINER_CLASS = "ui-panelmenu ui-widget";
    public static final String INACTIVE_HEADER_CLASS = "ui-widget ui-panelmenu-header ui-state-default ui-corner-all";
    public static final String ACTIVE_HEADER_CLASS = "ui-widget ui-panelmenu-header ui-state-default ui-state-active ui-corner-top";
    public static final String INACTIVE_TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String ACTIVE_TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String INACTIVE_ROOT_SUBMENU_CONTENT = "ui-panelmenu-content ui-widget-content ui-helper-hidden";
    public static final String ACTIVE_ROOT_SUBMENU_CONTENT = "ui-panelmenu-content ui-widget-content";
    public static final String MENUITEM_CLASS = "ui-menuitem ui-widget ui-corner-all";
    public static final String DESCENDANT_SUBMENU_CLASS = "ui-widget ui-menuitem ui-corner-all ui-menu-parent";
    public static final String DESCENDANT_SUBMENU_EXPANDED_ICON_CLASS = "ui-panelmenu-icon ui-icon ui-icon-triangle-1-s";
    public static final String DESCENDANT_SUBMENU_COLLAPSED_ICON_CLASS = "ui-panelmenu-icon ui-icon ui-icon-triangle-1-e";
    public static final String DESCENDANT_SUBMENU_EXPANDED_LIST_CLASS = "ui-menu-list ui-helper-reset";
    public static final String DESCENDANT_SUBMENU_COLLAPSED_LIST_CLASS = "ui-menu-list ui-helper-reset ui-helper-hidden";
    public static final String PANEL_CLASS = "ui-panelmenu-panel";
    public static final String MENUITEM_LINK_WITH_ICON_CLASS = "ui-menuitem-link ui-menuitem-link-hasicon ui-corner-all";

}