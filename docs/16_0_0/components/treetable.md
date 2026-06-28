# TreeTable

Treetable is is used for displaying hierarchical data in tabular format.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.TreeTable-1.html)

## Getting started with the TreeTable
Similar to the Tree, TreeTable is populated with an _org.primefaces.model.TreeNode_ instance that
corresponds to the root node. TreeNode API has a hierarchical data structure and represents the data
to be populated in tree. For an example, model to be displayed is a collection of documents similar
as in tree section.

```java
public class Document {
    private String name;
    private String size;
    private String type;
    //getters, setters
}
```

```xhtml
<p:treeTable value="#{bean.root}" var="document">
    <p:column>
        <f:facet name="header">
            Name
        </f:facet>
        <h:outputText value="#{document.name}" />
    </p:column>
    //more columns
</p:treeTable>
```
## Selection
Node selection is a built-in feature of tree and it supports two different modes. Selection should be a
TreeNode for single case and an array of TreeNodes for multiple case, tree finds the selected nodes
and assign them to your selection model.

_single_ : Only one at a time can be selected, selection should be a TreeNode reference.
_multiple_ or _checkbox_ : Multiple nodes can be selected, selection should be a TreeNode[] reference.

As checkbox selection have a special hierarchy, use _CheckboxTreeNode_ in checkbox mode.

## Paginator
Node hierarchy can be displayed as paged using paginator. Usage is similar to DataTable, DataList
and DataGrid where paginator is enabled by setting paginator=”true” and defining a rows property
to define the number of nodes to be displayed per page.

## Ajax Behavior Events

TreeTable provides various ajax behavior events to respond user actions.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
expand | org.primefaces.event.NodeExpandEvent | When a node is expanded.
collapse | org.primefaces.event.NodeCollapseEvent | When a node is collapsed.
select | org.primefaces.event.NodeSelectEvent | When a node is selected.
dblselect | org.primefaces.event.NodeSelectEvent | When a node is selected with double click.
unselect | org.primefaces.event.NodeUnselectEvent | When a node is unselected.
contextMenu | org.primefaces.event.NodeSelectEvent | ContextMenu display.
colResize | org.primefaces.event.ColumnResizeEvent | When a column is resized.
sort | org.primefaces.event.SortEvent | When data is sorted.
filter | org.primefaces.event.FilterEvent | When data is filtered.
rowEdit | org.primefaces.event.RowEditEvent | On row edit.
rowEditInit | org.primefaces.event.RowEditEvent | When row edit is initialized.
rowEditCancel | org.primefaces.event.RowEditEvent | When row edit is cancelled.
cellEdit | org.primefaces.event.CellEditEvent | On cell edit.
page | org.primefaces.event.data.PageEvent | On pagination.

## Client Side API
Widget: _PrimeFaces.widget.TreeTable_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| clearFilters() | - | void | Clears all column filters

## Filtering prune mode
Set `filterPrune="descendants"` to hide non-matching children of a matched node unless they (or their descendants) match the filter. Omit the attribute or use `none` to keep children of matched nodes visible.

```xhtml
<p:treeTable value="#{bean.root}"
             var="node"
             filterPrune="descendants">
    <p:column headerText="Name" filterBy="#{node.name}" filterMatchMode="contains">
        #{node.name}
    </p:column>
</p:treeTable>
```

## ContextMenu
TreeTable has special integration with context menu, you can even match different context menus
with different tree nodes using _nodeType_ option of context menu that matches the tree node type.

## Scrolling
Scrollable TreeTable implementation is same as DataTable Scrollable, refer to scrolling part in
DataTable section for detailed information.

## Dynamic Columns
TreeTable supports dynamic columns via p:columns component, refer to dynamic columns in
DataTable section for detailed information.

## Column Order / Priorities
Columns can be ordered via _displayPriority_. Lower value means higher priority.

```xhtml
<p:treeTable value="#{bean.root}" var="document">
    <p:column displayPriority="4">
        <h:outputText value="#{document.name}" />
    </p:column>
    <p:column displayPriority="0">
        <h:outputText value="#{document.type}" />
    </p:column>
</p:treeTable>
```

## Responsive TreeTable
In priority mode, responsiveness is based on column _responsivePriority_ that vary between 1 and 6. Lower value means higher priority.

## Sorting
Sorting is enabled by setting _sortBy_ expressions at column level.

```xhtml
<p:treeTable value="#{bean.root}" var="document">
    <p:column sortBy="#{document.name}">
        <h:outputText value="#{document.name}" />
    </p:column>
    //more columns
</p:treeTable>
```
In case you'd like to display treeTable as sorted on page load use sortBy attribute of treeTable,
optional _sortOrder_ and _sortFunction_ attributes are provided to define the default sort order
(ascending or descinding) and a java method to do the actual sorting respectively. Refer to datatable
sorting section for an example usage of _sortFunction_.

## Editing
Similar to DataTable, TreeTable supports row and cell based editing. Refer to datatable for more
information.

## Skinning
TreeTable content resides in a container element which style and styleClass attributes apply.
Following is the list of structural style classes;

| Class | Applies |
| --- | --- |
.ui-treetable | Main container element.
.ui-treetable-header | Header of treetable.
.ui-treetable-data | Body element of the table containing data

As skinning style classes are global, see the main theming section for more information.
