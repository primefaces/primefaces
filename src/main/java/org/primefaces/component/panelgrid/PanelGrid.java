/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.panelgrid;

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
public class PanelGrid extends PanelGridBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.PanelGrid";

    public static final String CONTAINER_CLASS = "ui-panelgrid ui-widget";
    public static final String CONTENT_CLASS = "ui-panelgrid-content ui-widget-content ui-grid ui-grid-responsive";
    public static final String FLEX_CONTENT_CLASS = "ui-panelgrid-content ui-widget-content p-grid";
    public static final String GRID_ROW_CLASS = "ui-g";
    public static final String HEADER_CLASS = "ui-panelgrid-header";
    public static final String FOOTER_CLASS = "ui-panelgrid-footer";
    public static final String TABLE_ROW_CLASS = "ui-widget-content";
    public static final String EVEN_ROW_CLASS = "ui-panelgrid-even";
    public static final String ODD_ROW_CLASS = "ui-panelgrid-odd";
    public static final String CELL_CLASS = "ui-panelgrid-cell";
}