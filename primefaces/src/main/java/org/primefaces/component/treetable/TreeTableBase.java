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
package org.primefaces.component.treetable;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Pageable;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UITable;
import org.primefaces.component.api.UITreeImpl;
import org.primefaces.component.api.Widget;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.TreeNode;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "contextMenu", event = NodeSelectEvent.class, description = "Fired when context menu is invoked on a node"),
    @FacesBehaviorEvent(name = "select", event = NodeSelectEvent.class, description = "Fired when a node is selected"),
    @FacesBehaviorEvent(name = "dblselect", event = NodeSelectEvent.class, description = "Fired when a node is double-clicked"),
    @FacesBehaviorEvent(name = "unselect", event = NodeUnselectEvent.class, description = "Fired when a node is unselected"),
    @FacesBehaviorEvent(name = "expand", event = NodeExpandEvent.class, description = "Fired when a node is expanded"),
    @FacesBehaviorEvent(name = "collapse", event = NodeCollapseEvent.class, description = "Fired when a node is collapsed"),
    @FacesBehaviorEvent(name = "colResize", event = ColumnResizeEvent.class, description = "Fired when a column is resized"),
    @FacesBehaviorEvent(name = "sort", event = SortEvent.class, description = "Fired when column sorting is applied"),
    @FacesBehaviorEvent(name = "filter", event = FilterEvent.class, description = "Fired when data is filtered"),
    @FacesBehaviorEvent(name = "rowEdit", event = RowEditEvent.class, description = "Fired when row edit is completed"),
    @FacesBehaviorEvent(name = "rowEditInit", event = RowEditEvent.class, description = "Fired when row edit is initiated"),
    @FacesBehaviorEvent(name = "rowEditCancel", event = RowEditEvent.class, description = "Fired when row edit is cancelled"),
    @FacesBehaviorEvent(name = "cellEdit", event = CellEditEvent.class, description = "Fired when cell edit is completed"),
    @FacesBehaviorEvent(name = "cellEditInit", event = CellEditEvent.class, description = "Fired when cell edit is initiated"),
    @FacesBehaviorEvent(name = "cellEditCancel", event = CellEditEvent.class, description = "Fired when cell edit is cancelled"),
    @FacesBehaviorEvent(name = "page", event = PageEvent.class, description = "Fired when pagination occurs")
})
public abstract class TreeTableBase extends UITreeImpl implements Widget, Pageable, StyleAware, UITable<TreeTableState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TreeTableRenderer";
    public static final String FILTER_PRUNE_DESCENDANTS = "descendants";

    protected enum InternalPropertyKeys {
        filterByAsMap,
        sortByAsMap,
        columnMeta,
        width;
    }

    public TreeTableBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public Object getLocalSelectedNodes() {
        return getStateHelper().get(UITreeImpl.PropertyKeys.selection);
    }

    @Override
    @Property(defaultValue = "0", description = "Number of rows to display per page. 0 means to display all data available.")
    public abstract int getRows();

    @Override
    @Property(defaultValue = "0", description = "Index of the first data to display.")
    public abstract int getFirst();

    @Property(defaultValue = "false", description = "Makes data scrollable with fixed header.")
    public abstract boolean isScrollable();

    @Property(description = "Height for scrollable data.")
    public abstract String getScrollHeight();

    @Property(description = "Width for scrollable data.")
    public abstract String getScrollWidth();

    @Property(description = "Inline style of the table element.")
    public abstract String getTableStyle();

    @Property(description = "Style class of the table element.")
    public abstract String getTableStyleClass();

    @Property(defaultValue = "false", description = "Defines if columns can be resized or not.")
    public abstract boolean isResizableColumns();

    @Property(description = "Style class for each row.")
    public abstract String getRowStyleClass();

    @Property(description = "Title for each row.")
    public abstract String getRowTitle();

    @Property(defaultValue = "false", description = "Columns are resized live in this mode without using a resize helper.")
    public abstract boolean isLiveResize();

    @Property(defaultValue = "false", description = "In native mode, treetable uses native checkboxes.")
    public abstract boolean isNativeElements();

    @Property(defaultValue = "children", description = "Updates children only when set to \"children\" or"
            + " the node itself with children when set to \"self\" on node expand.")
    public abstract String getExpandMode();

    @Property(defaultValue = "false", description = "Sticky header stays in window viewport during scrolling.")
    public abstract boolean isStickyHeader();

    @Property(defaultValue = "false", description = "Controls incell editing.")
    public abstract boolean isEditable();

    @Property(defaultValue = "row", description = "Defines edit mode, valid values are \"row\" and \"cell\".")
    public abstract String getEditMode();

    @Property(defaultValue = "false", description = "Defines if cell editors of row should be displayed as editable or not. False means display mode.")
    public abstract boolean isEditingRow();

    @Property(description = "Separator text to use in output mode of editable cells with multiple components.")
    public abstract String getCellSeparator();

    @Property(defaultValue = "true", description = "Disables text selection on row click.")
    public abstract boolean isDisabledTextSelection();

    @Property(implicitDefaultValue = "fit", description = "Defines the resize behavior, valid values are \"fit\" and expand.")
    public abstract String getResizeMode();

    @Property(description = "Node to keep the filtered nodes if filtering is enabled.")
    public abstract TreeNode<?> getFilteredValue();

    @Property(implicitDefaultValue = "keyup", description = "Client side event to invoke filtering."
            + " If \"enter\" it will only filter after ENTER key is pressed.")
    public abstract String getFilterEvent();

    @Property(defaultValue = "Integer.MAX_VALUE", implicitDefaultValue = "300",
            description = "Delay to wait in milliseconds before sending each filter query.")
    public abstract int getFilterDelay();

    @Property(defaultValue = "none", description = "Controls pruning during filtering. \"none\" keeps children of matching nodes;"
                    + " \"descendants\" prunes non-matching children unless they or their descendants match.")
    public abstract String getFilterPrune();

    public boolean isFilterPruneDescendants() {
        return FILTER_PRUNE_DESCENDANTS.equals(getFilterPrune());
    }

    @Property(defaultValue = "eager", description = "Defines the cell edit behavior, valid values are \"eager\" and \"lazy\".")
    public abstract String getCellEditMode();

    @Property(defaultValue = "click", description = "Defines a client side event to open cell on editable table.")
    public abstract String getEditInitEvent();

    @Property(defaultValue = "false", description = "Defines whether columns are allowed to be unsorted.")
    public abstract boolean isAllowUnsorting();

    @Property(defaultValue = "multiple", description = "Defines sorting mode, valid values are \"single\" and \"multiple\".")
    public abstract String getSortMode();

    @Property(defaultValue = "false", description = "Defines if nodes should be cloned on filter via Cloneable interface or"
            + " Copy-Constructor (CustomNode(CustomNode original) or CustomNode(String type, Object data, TreeNode parent))."
            + " Normally the filtered nodes are new instanceof of DefaultTreeNode.")
    public abstract boolean isCloneOnFilter();

    @Property(defaultValue = "true", description = "Saves the changes in cell editing on blur, when set to false changes are discarded.")
    public abstract boolean isSaveOnCellBlur();

    @Property(defaultValue = "false", description = "When enables, cell borders are displayed.")
    public abstract boolean isShowGridlines();

    @Property(defaultValue = "regular", description = "Size of the table content, valid values are \"small\", \"regular\" and \"large\".")
    public abstract String getSize();

    @Property(description = "If XML data exporter in use, this allows customization of the row tag in the XML.")
    public abstract String getExportRowTag();

    @Property(description = "If XML data exporter in use, this allows customization of the document tag in the XML.")
    public abstract String getExportTag();
}
