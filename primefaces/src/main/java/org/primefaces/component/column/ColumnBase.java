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
package org.primefaces.component.column;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.model.menu.MenuColumn;

import jakarta.faces.component.UIColumn;

@FacesComponentBase
public abstract class ColumnBase extends UIColumn implements org.primefaces.component.api.UIColumn, MenuColumn, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ColumnRenderer";

    public ColumnBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Property to be used for sorting.")
    public abstract Object getSortBy();

    @Property(description = "Custom pluggable sortFunction.")
    public abstract jakarta.el.MethodExpression getSortFunction();

    @Property(description = "Property to be used for filtering.")
    public abstract Object getFilterBy();

    @Property(description = "Inline style of the filter element.")
    public abstract String getFilterStyle();

    @Property(description = "Style class of the filter element.")
    public abstract String getFilterStyleClass();

    @Property(description = "Match mode for filtering.",
            implicitDefaultValue = "startsWith")
    public abstract String getFilterMatchMode();

    @Property(description = "Location of the column filter with respect to header content. Options are 'bottom'(default) and 'top'.",
            defaultValue = "bottom")
    public abstract String getFilterPosition();

    @Property(description = "Placeholder for plain input text style filters.")
    public abstract String getFilterPlaceholder();

    @Property(description = "Defines the number of rows the column spans.",
            defaultValue = "1")
    public abstract int getRowspan();

    @Property(description = "Defines the number of columns the column spans.",
            defaultValue = "1")
    public abstract int getColspan();

    @Property(description = "An EL expression or a literal text that defines a converter for the component."
            + " When it's an EL expression, it's resolved to a converter instance."
            + " In case it's a static text, it must refer to a converter id.")
    public abstract Object getConverter();

    @Property(description = "Shortcut for header facet.")
    public abstract String getHeaderText();

    @Property(description = "Shortcut for footer facet.")
    public abstract String getFooterText();

    @Property(description = "Indicates whether or not the column should contain a radiobutton or checkbox for selection",
            defaultValue = "false")
    public abstract boolean isSelectionBox();

    @Property(description = "Maximum number of characters for an input filter.",
            defaultValue = "Integer.MAX_VALUE")
    public abstract int getFilterMaxLength();

    @Property(description = "Specifies resizable feature at column level. Datatable's resizableColumns must be enabled to use this option.",
            defaultValue = "true")
    public abstract boolean isResizable();

    @Property(description = "Defines if the column should be exported by dataexporter.",
            defaultValue = "true")
    public abstract boolean isExportable();

    @Property(description = "Value of the filter field.")
    public abstract Object getFilterValue();

    @Property(description = "Width of the column in pixels or percentage.")
    public abstract String getWidth();

    @Property(description = "Defines if column is toggleable by columnToggler component. Default value is true and a false value marks the column as static.",
            defaultValue = "true")
    public abstract boolean isToggleable();

    @Property(description = "Defines if column is draggable if draggableColumns is set. Default true.",
            defaultValue = "true")
    public abstract boolean isDraggable();

    @Property(description = "Custom implementation to filter a value against a constraint.")
    public abstract jakarta.el.MethodExpression getFilterFunction();

    @Property(description = "Name of the field associated to bean \"var\".")
    public abstract String getField();

    @Property(description = "Responsive priority of the column, lower values have more priority.",
            defaultValue = "0")
    public abstract int getResponsivePriority();

    @Property(description = "Boolean value to mark column as sortable.",
            defaultValue = "true")
    public abstract boolean isSortable();

    @Property(description = "Boolean value to mark column as filterable.",
            defaultValue = "true")
    public abstract boolean isFilterable();

    @Property(description = "Controls the visibility of the column.",
            defaultValue = "true")
    public abstract boolean isVisible();

    @Property(description = "Whether clicking the column selects the row when datatable has row selection enabled.",
            defaultValue = "true")
    public abstract boolean isSelectRow();

    @Property(description = "Accessible label for screen readers. IMPORTANT: Overrides headerText and headerFacet if specified."
            + " Only necessary when the column header is not human readable (e.g. empty header or icon-only header).")
    public abstract String getAriaHeaderText();

    @Property(description = "Custom pluggable exportFunction.")
    public abstract jakarta.el.MethodExpression getExportFunction();

    @Property(description = "Specifies whether to group rows based on the column data.",
            defaultValue = "false")
    public abstract boolean isGroupRow();

    @Property(description = "Defines the value of the cell to be exported if something other than the cell contents or exportFunction.")
    public abstract Object getExportValue();

    @Property(description = "Defines the number of rows the column spans to be exported.",
            defaultValue = "0")
    public abstract int getExportRowspan();

    @Property(description = "Defines the number of columns the column spans to be exported.",
            defaultValue = "0")
    public abstract int getExportColspan();

    @Property(description = "Defines if the header value of column to be exported.")
    public abstract Object getExportHeaderValue();

    @Property(description = "Defines if the footer value of column to be exported.")
    public abstract Object getExportFooterValue();

    @Property(description = "Sets default sorting order. Possible values \"asc\", \"desc\" or null")
    public abstract String getSortOrder();

    @Property(description = "Sets default sorting priority over the other columns. Lower values have more priority.",
            defaultValue = "org.primefaces.model.SortMeta.MIN_PRIORITY")
    public abstract int getSortPriority();

    @Property(description = "Defines where the null values are placed in ascending sort order."
            + " \"1\" means null values are placed at the end in ascending mode and at beginning in descending mode. Set to \"-1\" for the opposite behavior.",
            defaultValue = "1")
    public abstract int getNullSortOrder();

    @Property(description = "Case sensitivity for sorting, insensitive by default.",
            defaultValue = "false")
    public abstract boolean isCaseSensitiveSort();

    @Property(description = "Defines the display priority, in which order the columns should be displayed.",
            defaultValue = "0")
    public abstract int getDisplayPriority();

    @Property(description = "Advisory tooltip information.")
    public abstract String getTitle();

    @Property(description = "If XML data exporter in use, this allows customization of the column tag in the XML.")
    public abstract String getExportTag();
}