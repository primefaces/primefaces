# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 14.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/14_0_0/#/../migrationguide/14_0_0) for more enhancements and changes.

* Accordion
    * Added `toggleSpeed` for toggle speed animation duration.
    * Added `scrollIntoView` to allow the active tab to be scrolled into the viewport
    
* Messages
    * Added `clearMessages` widget method to clear all current messages.
    
* SelectCheckBoxMenu
    * Added `selectedLabel` to display a label when items selected similar to `emptyMessage`.

* DataExporter
    * Added `bufferSize` to control how many items are fetched at a time when `DataTable#lazy` is enabled

* DataTable
    * JPALazyDataModel now supports case insensitive filters with `setCaseSensitive(false);`
    * JPALazyDataModel now supports wildcard filters with `setWildcardSupport(true);` so you can use `*`, `%`, `_` or `?` in filter
    * JPALazyDataModel now supports builder pattern for constructor.
  
### PrimeFaces Selenium 

* ...
