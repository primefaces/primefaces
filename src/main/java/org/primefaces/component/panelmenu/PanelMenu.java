/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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