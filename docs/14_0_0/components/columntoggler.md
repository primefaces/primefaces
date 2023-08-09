# ColumnToggler

ColumnToggler is a helper component for datatable to toggle visibility of columns.

## Info

| Name | Value |
| --- | --- |
| Tag | columnToggler
| Component Class | org.primefaces.component.columngroup.ColumnGroup
| Component Type | org.primefaces.component. ColumnGroup
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ColumnTogglerRenderer
| Renderer Class | org.primefaces.component.columntoggler.ColumnTogglerRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| widgetVar | null | String | Name of the client side widget.
| trigger | null | String | A search expression resolving to a component to get attached to.
| datasource | null | String | A search expression resolving to a DataTable component whose columns to be toggled.

## Getting Started with ColumnToggler
See column toggler section in datatable documentation for detailed information.

## Ajax Behavior Events
The following AJAX behavior events are available for this component. If no event is specified the default event is called.  
  
**Default Event:** `toggle`  
**Available Events:** `toggle, close`

In order to react to changes of the visible columns on the server side the AJAX behavior can be used. You can either listen to
single toggle events which are fired once the checkbox of a specific column is checked or unchecked, or to a summarized AJAX event
containing a list of all currently visible columns once the column toggle is closed again.
For this to contain meaningful ids your columns should define an id attribute otherwise the automatically generated ids will be returned.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| toggle | org.primefaces.event.ToggleEvent | On item selection/unselection. |
| close | org.primefaces.event.ToggleCloseEvent | On closing of toggler popup. |
