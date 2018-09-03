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
package org.primefaces.component.scrollpanel;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "scrollpanel/scrollpanel.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "scrollpanel/scrollpanel.js")
})
public class ScrollPanel extends ScrollPanelBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.ScrollPanel";


    public static final String SCROLL_PANEL_CLASS = "ui-scrollpanel ui-widget ui-widget-content";
    public static final String SCROLL_PANEL_NATIVE_CLASS = "ui-scrollpanel ui-scrollpanel-native ui-widget ui-widget-content ui-corner-all";
    public static final String SCROLL_PANEL_CONTAINER_CLASS = "ui-scrollpanel-container";
    public static final String SCROLL_PANEL_WRAPPER_CLASS = "ui-scrollpanel-wrapper";
    public static final String SCROLL_PANEL_CONTENT_CLASS = "ui-scrollpanel-content";
    public static final String SCROLL_PANEL_HBAR_CLASS = "ui-scrollpanel-hbar ui-widget-header ui-corner-bottom";
    public static final String SCROLL_PANEL_VBAR_CLASS = "ui-scrollpanel-vbar ui-widget-header ui-corner-right";
    public static final String SCROLL_PANEL_HANDLE_CLASS = "ui-scrollpanel-handle ui-state-default ui-corner-all";
    public static final String SCROLL_PANEL_BLEFT_CLASS = "ui-scrollpanel-bl ui-state-default ui-corner-bl";
    public static final String SCROLL_PANEL_BRIGHT_CLASS = "ui-scrollpanel-br ui-state-default ui-corner-br";
    public static final String SCROLL_PANEL_BTOP_CLASS = "ui-scrollpanel-bt ui-state-default ui-corner-tr";
    public static final String SCROLL_PANEL_BBOTTOM_CLASS = "ui-scrollpanel-bb ui-state-default ui-corner-br";
    public static final String SCROLL_PANEL_VGRIP_CLASS = "ui-icon ui-icon-grip-solid-vertical";
    public static final String SCROLL_PANEL_HGRIP_CLASS = "ui-icon ui-icon-grip-solid-horizontal";
    public static final String SCROLL_PANEL_IWEST_CLASS = "ui-icon ui-icon-triangle-1-w";
    public static final String SCROLL_PANEL_IEAST_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String SCROLL_PANEL_INORTH_CLASS = "ui-icon ui-icon-triangle-1-n";
    public static final String SCROLL_PANEL_ISOUTH_CLASS = "ui-icon ui-icon-triangle-1-s";
}