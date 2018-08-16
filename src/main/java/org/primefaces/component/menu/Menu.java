/*
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
package org.primefaces.component.menu;

import org.primefaces.component.menu.AbstractMenu;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="components.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="core.js"),
	@ResourceDependency(library="primefaces", name="components.js")
})
public class Menu extends MenuBase implements org.primefaces.component.api.Widget,org.primefaces.component.menu.OverlayMenu {



    public static final String STATIC_CONTAINER_CLASS = "ui-menu ui-widget ui-widget-content ui-corner-all ui-helper-clearfix";
    public static final String DYNAMIC_CONTAINER_CLASS = "ui-menu ui-menu-dynamic ui-widget ui-widget-content ui-corner-all ui-helper-clearfix ui-shadow";
    public static final String SUBMENU_TITLE_CLASS = "ui-widget-header ui-corner-all";
    public static final String EXPANDED_SUBMENU_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String COLLAPSED_SUBMENU_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String TOGGLEABLE_MENU_CLASS = "ui-menu-toggleable";
    public static final String SUBMENU_CHILD_CLASS = "ui-submenu-child";
}