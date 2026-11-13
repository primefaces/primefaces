# Dashboard

Dashboard provides a portal like layout with drag&drop based reorder capabilities.

## Getting started with Dashboard
Dashboard is backed by a DashboardModel and consists of panel components.

```xhtml
<p:dashboard model="#{bean.model}">
    <p:panel id="sports">
        //Sports Content
    </p:panel>
    <p:panel id="finance">
        //Finance Content
    </p:panel>
    //more panels like lifestyle, weather, politics...
</p:dashboard>
```
The dashboard model defines the columns and the items (panels) of each column, an item references its panel by id.
See the end of this section for the detailed Dashboard API.

```java
public class Bean {
    private DashboardModel model;

    public Bean() {
        model = new DefaultDashboardModel();
        model.addColumn(new DefaultDashboardColumn("sports", "finance"));
        model.addColumn(new DefaultDashboardColumn("lifestyle", "weather"));
        model.addColumn(new DefaultDashboardColumn("politics"));
    }
}
```

## Responsive
Dashboard can be switched to `responsive="true"` mode to allow using PrimeFlex CSS.  The advantages and differences of this mode
are 1) drag and drop swaps panel locations 2) different size panels 3) responsive reordering based on browser size.

```xhtml
<p:dashboard model="#{bean.model}" responsive="true">
    <p:panel id="bar">
        //Bar Chart Content
    </p:panel>
    <p:panel id="stacked">
        //Stacked Chart Content
    </p:panel>
    //more panels like lifestyle, weather, politics...
</p:dashboard>
```
In this mode each item is a cell of the grid, its style class gives the PrimeFlex responsive CSS options. `addItem` adds
the items to one column, the items of all columns are rendered as cells.

```java
public class Bean {
    private DashboardModel model;

    public Bean() {
        model = new DefaultDashboardModel();
        model.addItem(new DefaultDashboardItem("bar", "col-12 lg:col-6 xl:col-4"));
        model.addItem(new DefaultDashboardItem("stacked", "col-12 lg:col-6 xl:col-8"));
    }
}
```

## Dynamic
An item can have a `value`, which is available via the `var` of the dashboard while its panel is rendered. The value
belongs to the panel and moves with it.

```xhtml
<p:dashboard model="#{bean.model}" var="dash" responsive="true">
    <p:panel id="bar" rendered="#{dash.visible}">
        #{dash.name}
    </p:panel>
    <p:panel id="stacked" rendered="#{dash.visible}">
        #{dash.name}
    </p:panel>
    //more panels like lifestyle, weather, politics...
</p:dashboard>
```

```java
public class Bean {
    private DashboardModel model;

    public Bean() {
        model = new DefaultDashboardModel();
        model.addItem(new DefaultDashboardItem("bar", "col-12 lg:col-6 xl:col-4", yourObject1));
        model.addItem(new DefaultDashboardItem("stacked", "col-12 lg:col-6 xl:col-8", yourObject2));
    }
}
```

## State
Dashboard is a stateful component, whenever a panel is reordered the dashboard model is
updated, by persisting the user changes so you can easily create a stateful dashboard.

## Listen to reorder
“reorder” is the one and only ajax behavior event provided by dashboard, this event is fired when
dashboard panels are reordered. A defined listener will be invoked by passing an
_org.primefaces.event.DashboardReorderEvent_ instance containing information about reorder.

Following dashboard displays a message about the reorder event


```xhtml
<p:dashboard model="#{bean.model}">
    <p:ajax event="reorder" update="messages" listener="#{bean.handleReorder}" />
    //panels
</p:dashboard>
<p:growl id="messages" />
```
```java
public class Bean {
    ...
    public void handleReorder(DashboardReorderEvent event) {
        String panelId = event.getPanelId();
        int columnIndex = event.getColumnIndex();
        int itemIndex = event.getItemIndex();
        int senderColumnIndex = event.getSenderColumnIndex();
        int senderItemIndex = event.getSenderItemIndex();
        //Add facesmessage
    }
}
```
_columnIndex_ / _itemIndex_ are the position of the panel after the reorder, _senderColumnIndex_ / _senderItemIndex_
the position before. In the responsive mode the dragged panel swaps its position with the panel it was dropped on.
When the listener is invoked, dashboard has already updated its model.

## Disabling Dashboard
If you’d like to disable reordering feature, set _disabled_ option to true.

```xhtml
<p:dashboard disabled="true" ...>
    //panels
</p:dashboard>
```
## Toggle, Close and Options Menu
Panels presented in dashboard can be closable, toggleable and have options menu as well,
dashboard doesn’t implement these by itself as these features are already provided by the panel
component. See panel component section for more information.

```xhtml
<p:dashboard model="#{dashboardBean.model}">
    <p:panel id="sports" closable="true" toggleable="true">
        //Sports Content
    </p:panel>
</p:dashboard>
```
## New Panels
Draggable component is used to add new panels to the dashboard (classic mode only). This way you can add new
panels from outside of the dashboard.


```xhtml
<p:dashboard model="#{dashboardBean.model}" id="board">
    //panels
</p:dashboard>
<p:panel id="newpanel" />
<p:draggable for="newpanel" helper="clone" dashboard="board" />
```
## Skinning
Dashboard resides in a container element which style and styleClass options apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .p-dashboard | Container element of dashboard
| .p-dashboard-column | Each column in dashboard
| .p-dashboard-panel | Each cell in responsive mode
| .p-dashboard-active | In responsive mode highlights which components are allowed to be dropped upon
| .p-dashboard-hover | In responsive mode change styling of the active drop zone when hovering
| div.p-state-hover | Placeholder (in legacy mode)

As skinning style classes are global, see the main theming section for more information. Here is an
example based on a different theme;

**Tips**: Provide a column width using _p-dashboard-column_ style class otherwise empty columns might
    not receive new panels.

## Dashboard Model API
_org.primefaces.model.dashboard.DashboardModel_ ( _org.primefaces.model.dashboard.DefaultDashboardModel_ is the default implementation)

| Method | Description |
| --- | --- |
| List<DashboardColumn> getColumns() | Returns the columns of the dashboard
| void addColumn(DashboardColumn column) | Adds a column to the dashboard
| DashboardColumn getColumn(int index) | Returns the column at the given index
| int getColumnCount() | Returns the number of columns
| void addItem(DashboardItem item) | Adds the item to the last column, creates a column when there is none (responsive mode)
| DashboardItem getItem(String panelId) | Returns the item of the panel with the given id or null
| void moveItem(DashboardItem item, int columnIndex, int itemIndex) | Moves the item to the given index of the given column
| void swapItems(DashboardItem item1, DashboardItem item2) | Swaps the positions of two items

_org.primefaces.model.dashboard.DashboardColumn_ ( _org.primefaces.model.dashboard.DefaultDashboardColumn_ is the default
implementation, `new DefaultDashboardColumn("sports", "finance")` creates a column with an item for each panel id)

| Method | Description |
| --- | --- |
| List<DashboardItem> getItems() | Returns the items of the column in the order they are rendered
| void addItem(DashboardItem item) | Adds an item to the column
| String getStyle() / getStyleClass() | Inline style and style class of the column (classic mode)

_org.primefaces.model.dashboard.DashboardItem_ ( _org.primefaces.model.dashboard.DefaultDashboardItem_ is the default
implementation)

| Method | Description |
| --- | --- |
| String getPanelId() | Returns the id of the panel of the item
| String getStyle() / getStyleClass() | Inline style and style class of the cell (responsive mode, e.g. `col-12 lg:col-6`)
| Object getValue() | Value of the item, available via `var` while its panel is rendered

## Client Side API
Component: _PrimeFaces.component.Dashboard_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| disable() | - | void | Disables dashboard
| enable() | - | void | Enables dashboard