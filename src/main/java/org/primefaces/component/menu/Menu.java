/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Menu extends MenuBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Menu";

    public static final String STATIC_CONTAINER_CLASS = "ui-menu ui-widget ui-widget-content ui-corner-all ui-helper-clearfix";
    public static final String DYNAMIC_CONTAINER_CLASS = "ui-menu ui-menu-dynamic ui-widget ui-widget-content ui-corner-all ui-helper-clearfix ui-shadow";
    public static final String CONTAINER_MAXHEIGHT_CLASS = "ui-menu-dynamic-maxheight";
    public static final String SUBMENU_TITLE_CLASS = "ui-widget-header ui-corner-all";
    public static final String EXPANDED_SUBMENU_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String COLLAPSED_SUBMENU_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String TOGGLEABLE_MENU_CLASS = "ui-menu-toggleable";
    public static final String SUBMENU_CHILD_CLASS = "ui-submenu-child";
}