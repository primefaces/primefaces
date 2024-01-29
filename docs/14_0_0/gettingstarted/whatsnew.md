# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 14.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/14_0_0/#/../migrationguide/14_0_0) for more enhancements and changes.

* Core
    * `MOVE_SCRIPTS_TO_BOTTOM` adds new option `defer` to defer loading scripts
    * OS settings for `prefers-reduced-motion: reduce` is now respected and PF disables all animations

* ConfirmDialog
   * Added `ajax=false` support.

* Accordion
    * Added `toggleSpeed` for toggle speed animation duration.
    * Added `scrollIntoView` to allow the active tab to be scrolled into the viewport
    
* Badge
    * Added `icon` and `iconPos` to allow icons to be used in badge
    * Added 'onclick` to allow client side click of badge. If not set it delegates the click to its target element

* CSV - Client Side Validation / CommandButton
    * Added `enabledByValidateClient` to enable CommandButton after CSV was successful (used together with `<p:clientValidator event="..." />` on all relevant input-components)  
      See https://primefaces.github.io/primefaces/14_0_0/#/core/csv?id=immediate-csv

* DataExporter
    * Added `bufferSize` to control how many items are fetched at a time when `DataTable#lazy` is enabled

* DataTable
    * JPALazyDataModel now supports case insensitive filters with `setCaseSensitive(false);`
    * JPALazyDataModel now supports wildcard filters with `setWildcardSupport(true);` so you can use `*`, `%`, `_` or `?` in filter
    * JPALazyDataModel now supports builder pattern for constructor.
    * Added `filterPlaceholder` for `Column` and `Columns`
    * Added `rowData` to `CellEditEvent` which contains the entire row from the cell being edited.

* Messages
    * Added `clearMessages` widget method to clear all current messages.
    
* MenuBar
    * Added `showDelay` delay in milliseconds before displaying the submenu. Default is 0 meaning immediate.
    * Added `hideDelay` delay in milliseconds before hiding the submenu., if 0 not hidden until document.click(). Default is 0.
    * Added `dir` for right-to-left support. Default is ltr.
    
* SelectCheckBoxMenu
    * Added `selectedLabel` to display a label when items selected similar to `emptyMessage`.
    
* StaticMessage
    * Added `closable` to add a close icon to hide the message similar to `Messages`.

* TieredMenu
    * Added `showDelay` delay in milliseconds before displaying the submenu. Default is 0 meaning immediate.
    * Added `hideDelay` delay in milliseconds before hiding the submenu., if 0 not hidden until document.click(). Default is 0.
    * Added `dir` for right-to-left support. Default is ltr.
    
* TreeTable
    * Added `filterPlaceholder` for `Column` and `Columns`
    
* Wizard
    * Added `disableOnAjax` attribute to disable next and back navigation buttons during Ajax requests triggered by them.
  
### PrimeFaces Selenium 

* ...
