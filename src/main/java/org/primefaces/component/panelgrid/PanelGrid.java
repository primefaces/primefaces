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
package org.primefaces.component.panelgrid;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css")
})
public class PanelGrid extends PanelGridBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.PanelGrid";

    public static final String CONTAINER_CLASS = "ui-panelgrid ui-widget";
    public static final String CONTENT_CLASS = "ui-panelgrid-content ui-widget-content ui-grid ui-grid-responsive";
    public static final String GRID_ROW_CLASS = "ui-g";
    public static final String HEADER_CLASS = "ui-panelgrid-header";
    public static final String FOOTER_CLASS = "ui-panelgrid-footer";
    public static final String TABLE_ROW_CLASS = "ui-widget-content";
    public static final String EVEN_ROW_CLASS = "ui-panelgrid-even";
    public static final String ODD_ROW_CLASS = "ui-panelgrid-odd";
    public static final String CELL_CLASS = "ui-panelgrid-cell";
}