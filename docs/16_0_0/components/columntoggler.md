# ColumnToggler

ColumnToggler is a helper component for datatable to toggle visibility of columns.

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
| toggle | org.primefaces.event.ColumnToggleEvent | On item selection/unselection. |
| close | org.primefaces.event.ToggleCloseEvent | On closing of toggler popup. |

## Client Side API
Widget: _PrimeFaces.widget.ColumnToggler_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| checkAll() | - | void | Selects all columns for display. |
| uncheckAll() | - | void | Selects all columns to hide. |
| toggleAll() | - | void | Either select or unselect all columns. |
