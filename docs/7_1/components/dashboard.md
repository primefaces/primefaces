# Dashboard

Dashboard provides a portal like layout with drag&drop based reorder capabilities.

## Info

| Name | Value |
| --- | --- |
| Tag | dashboard
| Component Class | org.primefaces.component.dashboard.Dashboard
| Component Type | org.primefaces.component.Dashboard
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DashboardRenderer
| Renderer Class | org.primefaces.component.dashboard.DashboardRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget
| model | null | DashboardModel | Dashboard model instance representing the layout of the UI.
| disabled | false | Boolean | Disables reordering feature.
| style | null | String | Inline style of the dashboard container
| styleClass | null | String | Style class of the dashboard container

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
Dashboard model simply defines the number of columns and the widgets to be placed in each
column. See the end of this section for the detailed Dashboard API.

```java
public class Bean {
    private DashboardModel model;

    public Bean() {
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        DashboardColumn column3 = new DefaultDashboardColumn();
        column1.addWidget("sports");
        column1.addWidget("finance");
        column2.addWidget("lifestyle");
        column2.addWidget("weather");
        column3.addWidget("politics");
        model.addColumn(column1);
        model.addColumn(column2);
        model.addColumn(column3);
    }
}
```
## State
Dashboard is a stateful component, whenever a widget is reordered dashboard model will be
updated, by persisting the user changes so you can easily create a stateful dashboard.

## Ajax Behavior Events
“reorder” is the one and only ajax behavior event provided by dashboard, this event is fired when
dashboard panels are reordered. A defined listener will be invoked by passing an
_org.primefaces.event.DashboardReorderEvent_ instance containing information about reorder.

Following dashboard displays a message about the reorder event


```xhtml
<p:dashboard model="#{bean.model}">
    <p:ajax event=”reorder” update=”messages” listener=”#{bean.handleReorder}” />
    //panels
</p:dashboard>
<p:growl id="messages" />
```
```java
public class Bean {
    ...
    public void handleReorder(DashboardReorderEvent event) {
        String widgetId = event.getWidgetId();
        int widgetIndex = event.getItemIndex();
        int columnIndex = event.getColumnIndex();
        int senderColumnIndex = event.getSenderColumnIndex();
        //Add facesmessage
    }
}
```
If a widget is reordered in the same column, _senderColumnIndex_ will be null. This field is
populated only when a widget is transferred to a column from another column. Also when the
listener is invoked, dashboard has already updated it’s model.

## Disabling Dashboard
If you’d like to disable reordering feature, set _disabled_ option to true.

```xhtml
<p:dashboard disabled="true" ...>
    //panels
</p:dashboard>
```
## Toggle, Close and Options Menu
Widgets presented in dashboard can be closable, toggleable and have options menu as well,
dashboard doesn’t implement these by itself as these features are already provided by the panel
component. See panel component section for more information.

```xhtml
<p:dashboard model="#{dashboardBean.model}">
    <p:panel id="sports" closable="true" toggleable="true">
        //Sports Content
    </p:panel>
</p:dashboard>
```
## New Widgets
Draggable component is used to add new widgets to the dashboard. This way you can add new
panels from outside of the dashboard.


```xhtml
<p:dashboard model="#{dashboardBean.model}" id="board">
    //panels
</p:dashboard>
<p:panel id="newwidget" />
<p:draggable for="newwidget" helper="clone" dashboard="board" />
```
## Skinning
Dashboard resides in a container element which style and styleClass options apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-dashboard | Container element of dashboard
| .ui-dashboard-column | Each column in dashboard
| div.ui-state-hover | Placeholder

As skinning style classes are global, see the main theming section for more information. Here is an
example based on a different theme;

**Tips**: Provide a column width using _ui-dashboard-column_ style class otherwise empty columns might
    not receive new widgets.

## Dashboard Model API
_org.primefaces.model.DashboardModel_ ( _org.primefaces.model.map.DefaultDashboardModel_ is the default implementation)


| Method | Description |
| --- | --- |
| void addColumn(DashboardColumn column) | Adds a column to the dashboard
| List<DashboardColumn> getColumns() | Returns all columns in dashboard
| int getColumnCount() | Returns the number of columns in dashboard
| DashboardColumn getColumn(int index) | Returns the dashboard column at given index
| void transferWidget(DashboardColumn from, DashboardColumn to, String widgetId, int index) |Relocates the widget identifed with widget id to the given index of the new column from old column.

_org.primefaces.model.DashboardColumn_ ( _org.primefaces.model.map.DefaultDashboardModel_ is
the default implementation)


| Method | Description |
| --- | --- |
| void removeWidget(String widgetId) | Removes the widget with the given id
| List<String> getWidgets() | Returns the ids of widgets in column
| int getWidgetCount() | Returns the count of widgets in column
| String getWidget(int index) | Returns the widget id with the given index
| void addWidget(String widgetId) | Adds a new widget with the given id
| void addWidget(int index, String widgetId) | Adds a new widget at given index
| void reorderWidget(int index, String widgetId) | Updates the index of widget in column