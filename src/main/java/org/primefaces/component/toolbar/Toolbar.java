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
package org.primefaces.component.toolbar;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css")
})
public class Toolbar extends ToolbarBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Toolbar";

    public static final String CONTAINER_CLASS = "ui-toolbar ui-widget ui-widget-header ui-corner-all ui-helper-clearfix";
    public static final String SEPARATOR_CLASS = "ui-separator";
    public static final String SEPARATOR_ICON_CLASS = "ui-icon ui-icon-grip-dotted-vertical";
}