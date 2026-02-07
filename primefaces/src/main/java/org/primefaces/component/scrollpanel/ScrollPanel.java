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
package org.primefaces.component.scrollpanel;

import org.primefaces.cdk.api.FacesComponentDescription;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = ScrollPanel.COMPONENT_TYPE, namespace = ScrollPanel.COMPONENT_FAMILY)
@FacesComponentDescription("ScrollPanel is used to display scrollable content with theme aware scrollbars instead of native browser scrollbars.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "scrollpanel/scrollpanel.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "scrollpanel/scrollpanel.js")
public class ScrollPanel extends ScrollPanelBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.ScrollPanel";


    public static final String SCROLL_PANEL_CLASS = "ui-scrollpanel ui-widget ui-widget-content";
    public static final String SCROLL_PANEL_NATIVE_CLASS = "ui-scrollpanel ui-scrollpanel-native ui-widget ui-widget-content";
    public static final String SCROLL_PANEL_CONTAINER_CLASS = "ui-scrollpanel-container";
    public static final String SCROLL_PANEL_WRAPPER_CLASS = "ui-scrollpanel-wrapper";
    public static final String SCROLL_PANEL_CONTENT_CLASS = "ui-scrollpanel-content";
    public static final String SCROLL_PANEL_HBAR_CLASS = "ui-scrollpanel-hbar ui-widget-header";
    public static final String SCROLL_PANEL_VBAR_CLASS = "ui-scrollpanel-vbar ui-widget-header";
    public static final String SCROLL_PANEL_HANDLE_CLASS = "ui-scrollpanel-handle ui-state-default";
    public static final String SCROLL_PANEL_BLEFT_CLASS = "ui-scrollpanel-bl ui-state-default";
    public static final String SCROLL_PANEL_BRIGHT_CLASS = "ui-scrollpanel-br ui-state-default";
    public static final String SCROLL_PANEL_BTOP_CLASS = "ui-scrollpanel-bt ui-state-default";
    public static final String SCROLL_PANEL_BBOTTOM_CLASS = "ui-scrollpanel-bb ui-state-default";
    public static final String SCROLL_PANEL_VGRIP_CLASS = "ui-icon ui-icon-grip-solid-vertical";
    public static final String SCROLL_PANEL_HGRIP_CLASS = "ui-icon ui-icon-grip-solid-horizontal";
    public static final String SCROLL_PANEL_IWEST_CLASS = "ui-icon ui-icon-triangle-1-w";
    public static final String SCROLL_PANEL_IEAST_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String SCROLL_PANEL_INORTH_CLASS = "ui-icon ui-icon-triangle-1-n";
    public static final String SCROLL_PANEL_ISOUTH_CLASS = "ui-icon ui-icon-triangle-1-s";
}