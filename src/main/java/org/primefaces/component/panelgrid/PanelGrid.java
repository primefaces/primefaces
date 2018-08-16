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
package org.primefaces.component.panelgrid;

import javax.faces.component.UIPanel;
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
	@ResourceDependency(library="primefaces", name="components.css")
})
public class PanelGrid extends PanelGridBase {



    public final static String CONTAINER_CLASS = "ui-panelgrid ui-widget";
    public static final String CONTENT_CLASS = "ui-panelgrid-content ui-widget-content ui-grid ui-grid-responsive";
    public static final String GRID_ROW_CLASS = "ui-g";
    public final static String HEADER_CLASS = "ui-panelgrid-header";
    public final static String FOOTER_CLASS = "ui-panelgrid-footer";
    public final static String TABLE_ROW_CLASS = "ui-widget-content";
    public static final String EVEN_ROW_CLASS = "ui-panelgrid-even";
    public static final String ODD_ROW_CLASS = "ui-panelgrid-odd";
    public final static String CELL_CLASS = "ui-panelgrid-cell";
}