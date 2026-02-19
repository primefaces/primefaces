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
package org.primefaces.component.datatable;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.api.UITable;
import org.primefaces.component.api.Widget;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "page", event = PageEvent.class, description = "Fired when pagination occurs"),
    @FacesBehaviorEvent(name = "sort", event = SortEvent.class, description = "Fired when column sorting is applied"),
    @FacesBehaviorEvent(name = "filter", event = FilterEvent.class, description = "Fired when data is filtered"),
    @FacesBehaviorEvent(name = "rowSelect", event = SelectEvent.class, description = "Fired when a row is selected"),
    @FacesBehaviorEvent(name = "rowUnselect", event = UnselectEvent.class, description = "Fired when a row is unselected"),
    @FacesBehaviorEvent(name = "rowEdit", event = RowEditEvent.class, description = "Fired when row edit is completed"),
    @FacesBehaviorEvent(name = "rowEditInit", event = RowEditEvent.class, description = "Fired when row edit is initiated"),
    @FacesBehaviorEvent(name = "rowEditCancel", event = RowEditEvent.class, description = "Fired when row edit is cancelled"),
    @FacesBehaviorEvent(name = "colResize", event = ColumnResizeEvent.class, description = "Fired when a column is resized"),
    @FacesBehaviorEvent(name = "toggleSelect", event = ToggleSelectEvent.class, description = "Fired when selectAll checkbox is toggled"),
    @FacesBehaviorEvent(name = "colReorder", event = AjaxBehaviorEvent.class, description = "Fired when a column is reordered"),
    @FacesBehaviorEvent(name = "contextMenu", event = NodeSelectEvent.class, description = "Fired when context menu is invoked on a node"),
    @FacesBehaviorEvent(name = "rowSelectRadio", event = SelectEvent.class, description = "Fired when a row is selected using radio button"),
    @FacesBehaviorEvent(name = "rowSelectCheckbox", event = SelectEvent.class, description = "Fired when a row is selected using checkbox"),
    @FacesBehaviorEvent(name = "rowUnselectCheckbox", event = SelectEvent.class, description = "Fired when a row is unselected using checkbox"),
    @FacesBehaviorEvent(name = "rowDblselect", event = SelectEvent.class, description = "Fired when a row is double-clicked"),
    @FacesBehaviorEvent(name = "rowToggle", event = ToggleEvent.class, description = "Fired when a row is expanded or collapsed"),
    @FacesBehaviorEvent(name = "cellEdit", event = CellEditEvent.class, description = "Fired when cell edit is completed"),
    @FacesBehaviorEvent(name = "cellEditInit", event = CellEditEvent.class, description = "Fired when cell edit is initiated"),
    @FacesBehaviorEvent(name = "cellEditCancel", event = CellEditEvent.class, description = "Fired when cell edit is cancelled"),
    @FacesBehaviorEvent(name = "rowReorder", event = ReorderEvent.class, description = "Fired when a row is reordered"),
    @FacesBehaviorEvent(name = "tap", event = SelectEvent.class, description = "Fired when a node is selected"),
    @FacesBehaviorEvent(name = "taphold", event = SelectEvent.class, description = "Fired when a node is double-clicked"),
    @FacesBehaviorEvent(name = "virtualScroll", event = PageEvent.class, description = "Fired when a node is expanded"),
    @FacesBehaviorEvent(name = "liveScroll", event = PageEvent.class, description = "Fired when a node is collapsed")
})
public abstract class DataTableBase extends UIPageableData implements Widget, RTLAware, StyleAware, UITable<DataTableState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataTableRenderer";

    public DataTableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Empty message content of the datatable.")
    public abstract UIComponent getEmptyMessageFacet();

    @Property(description = "List to keep the filtered data if filtering is enabled.")
    public abstract Object getFilteredValue();

    @Property(defaultValue = "false", description = "Makes data scrollable with fixed header.")
    public abstract boolean isScrollable();

    @Property(description = "Height for scrollable data.")
    public abstract String getScrollHeight();

    @Property(description = "Width for scrollable data.")
    public abstract String getScrollWidth();

    @Property(implicitDefaultValue = "Detected though the \"selection\" value expression.",
            description = "Enables data selection, valid values are \"single\" and \"multiple\".")
    public abstract String getSelectionMode();

    @Property(description = "Reference to the selection data.")
    public abstract Object getSelection();

    @Property(defaultValue = "false", description = "Enables live scrolling.")
    public abstract boolean isLiveScroll();

    @Property(description = "Style class for each row.")
    public abstract String getRowStyleClass();

    @Property(description = "Title for each row.")
    public abstract String getRowTitle();

    @Property(description = "Client side callback to execute before row expansion.")
    public abstract String getOnExpandStart();

    @Property(description = "Decides whether datatable columns are resizable or not.")
    public abstract boolean isResizableColumns();

    @Property(defaultValue = "0", description = "Number of rows to display in virtual scrolling mode.")
    public abstract int getScrollRows();

    @Property(description = "Unique identifier of row data.")
    public abstract String getRowKey();

    @Property(implicitDefaultValue = "keyup", description = "Client side event to invoke filtering."
            + " If \"enter\" it will only filter after ENTER key is pressed.")
    public abstract String getFilterEvent();

    @Property(defaultValue = "Integer.MAX_VALUE", implicitDefaultValue = "300",
            description = "Delay to wait in milliseconds before sending each filter query.")
    public abstract int getFilterDelay();

    @Property(description = "Inline style of the table element.")
    public abstract String getTableStyle();

    @Property(description = "Style class of the table element.")
    public abstract String getTableStyleClass();

    @Property(description = "Decides whether datatable columns can be reordered using dragdrop.")
    public abstract boolean isDraggableColumns();

    @Property(defaultValue = "false", description = "Controls incell editing.")
    public abstract boolean isEditable();

    @Property(defaultValue = "multiple", description = "Defines sorting mode, valid values are \"single\" and \"multiple\".")
    public abstract String getSortMode();

    @Property(defaultValue = "false", description = "Defines whether columns are allowed to be unsorted.")
    public abstract boolean isAllowUnsorting();

    @Property(defaultValue = "row", description = "Defines edit mode, valid values are \"row\" and \"cell\".")
    public abstract String getEditMode();

    @Property(defaultValue = "false", description = "Defines if cell editors of row should be displayed as editable or not. False means display mode.")
    public abstract boolean isEditingRow();

    @Property(description = "Separator text to use in output mode of editable cells with multiple components.")
    public abstract String getCellSeparator();

    @Property(description = "Summary attribute for WCAG.")
    public abstract String getSummary();

    @Property(defaultValue = "0", description = "Number of rows to freeze starting from the beginning.")
    public abstract int getFrozenRows();

    @Property(defaultValue = "false", description = "Columns are resized live in this mode without using a resize helper.")
    public abstract boolean isLiveResize();

    @Property(defaultValue = "false", description = "Sticky header stays in window viewport during scrolling.")
    public abstract boolean isStickyHeader();

    @Property(defaultValue = "false", description = "Defines if row should be rendered as expanded by default.")
    public abstract boolean isExpandedRow();

    @Property(defaultValue = "false", description = "Disables row selection when true.")
    public abstract boolean isSelectionDisabled();

    @Property(defaultValue = "new", description = "Indicates how rows of a DataTable may be selected, when clicking on the row"
            + " itself (not the checkbox / radiobutton from p:column)."
            + " The value `new` always unselects other rows, `add` preserves the currently selected rows, and `none` disables row selection.")
    public abstract String getSelectionRowMode();

    @Property(defaultValue = "multiple", description = "Defines row expand mode, valid values are \"single\" and \"multiple\".")
    public abstract String getRowExpandMode();

    @Property(defaultValue = "false", description = "In native mode, treetable uses native checkboxes.")
    public abstract boolean isNativeElements();

    @Property(defaultValue = "0", description = "Number of columns to freeze")
    public abstract int getFrozenColumns();

    @Property(defaultValue = "left", description = "Defines the alignment of frozen columns, valid values are 'left' and 'right'.")
    public abstract String getFrozenColumnsAlignment();

    @Property(defaultValue = "false", description = "When enabled, rows can be reordered using dragdrop.")
    public abstract boolean isDraggableRows();

    @Property(defaultValue = "false",
            description = "Ignores processing of children during lifecycle, improves performance if table only has output components.")
    public abstract boolean isSkipChildren();

    @Property(defaultValue = "true", description = "Disables text selection on row click.")
    public abstract boolean isSelectionTextDisabled();

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Property(defaultValue = "false", description = "Reflow mode is a responsive mode to display columns as stacked depending on screen size.")
    public abstract boolean isReflow();

    @Property(defaultValue = "0", description = "Percentage height of the buffer between the bottom of the page and the scroll position to initiate"
            + " the load for the new chunk. Value is defined in integer.")
    public abstract int getLiveScrollBuffer();

    @Property(defaultValue = "false", description = "Adds hover effect to rows, default is false. Hover is always on when selection is enabled.")
    public abstract boolean isRowHover();

    @Property(implicitDefaultValue = "fit", description = "Defines the resize behavior, valid values are \"fit\" and expand.")
    public abstract String getResizeMode();

    @Property(description = "Label to read by screen readers on checkbox and radio selection.")
    public abstract String getAriaRowLabel();

    @Property(defaultValue = "true", description = "Saves the changes in cell editing on blur, when set to false changes are discarded.")
    public abstract boolean isSaveOnCellBlur();

    @Property(defaultValue = "false", description = "Caches the next page asynchronously.")
    public abstract boolean isClientCache();

    @Property(defaultValue = "eager", description = "Defines the cell edit behavior, valid values are \"eager\" and \"lazy\".")
    public abstract String getCellEditMode();

    @Property(defaultValue = "false", description = "Loads data on demand as the scrollbar gets close to the bottom.")
    public abstract boolean isVirtualScroll();

    @Property(defaultValue = "false", description = "Whether to display striped rows to visually separate content.")
    public abstract boolean isStripedRows();

    @Property(defaultValue = "false", description = "When enables, cell borders are displayed.")
    public abstract boolean isShowGridlines();

    @Property(defaultValue = "regular", description = "Size of the table content, valid values are \"small\", \"regular\" and \"large\".")
    public abstract String getSize();

    @Property(implicitDefaultValue = "td,span:not(.ui-c)", description = "Defines the element used to reorder rows using dragdrop.")
    public abstract String getRowDragSelector();

    @Property(description = "Method expression to execute after dragging row.")
    public abstract jakarta.el.MethodExpression getDraggableRowsFunction();

    @Property(description = "Client side callback to execute after clicking row.")
    public abstract String getOnRowClick();

    @Property(defaultValue = "click", description = "Defines a client side event to open cell on editable table.")
    public abstract String getEditInitEvent();

    @Property(description = "Client side check if rowclick triggered row click event not a clickable element in row content.")
    public abstract String getRowSelector();

    @Property(defaultValue = "false", description = "Decides whether to disable context menu or not if a table has no records.")
    public abstract boolean isDisableContextMenuIfEmpty();

    @Property(defaultValue = "true", description = "Defines if headerText and footerText values on columns are escaped or not.")
    public abstract boolean isEscapeText();

    @Property(defaultValue = "eager", description = "Defines the row edit behavior, valid values are \"eager\" and \"lazy\".")
    public abstract String getRowEditMode();

    @Property(description = "Selector to position on the page according to other fixing elements on the top of the table.")
    public abstract String getStickyTopAt();

    @Property(defaultValue = "false", description = "Render facets even if their children are not rendered.")
    public abstract boolean isRenderEmptyFacets();

    @Property(defaultValue = "true", description = "When using paginator and checkbox selection mode the select all checkbox in header"
            + " will select the current page if true or all rows if false.")
    public abstract boolean isSelectionPageOnly();

    @Property(defaultValue = "true", description = "When disabled, it updates the whole table instead of updating a specific field such"
            + " as body element in the client requests of the dataTable.")
    public abstract boolean isPartialUpdate();

    @Property(defaultValue = "true", description = "Whether to show the select all checkbox inside the column's header.")
    public abstract boolean isShowSelectAll();

    @Property(description = "If XML data exporter in use, this allows customization of the row tag in the XML.")
    public abstract String getExportRowTag();

    @Property(description = "If XML data exporter in use, this allows customization of the document tag in the XML.")
    public abstract String getExportTag();

    @Property(defaultValue = "false", description = "When enabled, toggle select will only apply on filtered items.")
    public abstract boolean isSelectAllFilteredOnly();

    @Property(defaultValue = "true", description = "Enables cell navigation with the keyboard for WCAG and screen reader compliance.")
    public abstract boolean isCellNavigation();

}
