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
package org.primefaces.component.log;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "log/log.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "log/log.js")
})
public class Log extends LogBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Log";

    public static final String CONTAINER_CLASS = "ui-log ui-widget ui-widget-content ui-corner-all";
    public static final String HEADER_CLASS = "ui-log-header ui-widget-header ui-helper-clearfix";
    public static final String CONTENT_CLASS = "ui-log-content";
    public static final String ITEMS_CLASS = "ui-log-items";
    public static final String CLEAR_BUTTON_CLASS = "ui-log-button ui-log-clear ui-corner-all";
    public static final String ALL_BUTTON_CLASS = "ui-log-button ui-log-all ui-corner-all";
    public static final String INFO_BUTTON_CLASS = "ui-log-button ui-log-info ui-corner-all";
    public static final String DEBUG_BUTTON_CLASS = "ui-log-button ui-log-debug ui-corner-all";
    public static final String WARN_BUTTON_CLASS = "ui-log-button ui-log-warn ui-corner-all";
    public static final String ERROR_BUTTON_CLASS = "ui-log-button ui-log-error ui-corner-all";
}