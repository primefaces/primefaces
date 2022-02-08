# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 12.0.0

### PrimeFaces

* AutoComplete: added `lazyModel` attribute for fetching autocomplete-suggestions (instead of completeMethod).
* CommandButton: added `disableOnAjax` attribute to disable the button during Ajax requests triggered by it.
* CommandLink
  * Now has a widget and can be enabled and disabled by its widget.
  * Added `disableOnAjax` attribute to disable the link during Ajax requests triggered by it.
* Exporter: added new options for `visibleOnly`, `exportHeader` and `exportFooter` to give better control over output.
* MenuButton
  * Now can be enabled and disabled by its widget.
  * Added `disableOnAjax` attribute to disable the button during Ajax requests triggered by its menu items.
* Printer: added `configuration` attribute for passing custom JSON options to PrintJS.
* SelectOneRadio: improved accessibility of custom layout via `custom` facet.
* Spinner: added `modifyValueOnWheel` to increment or decrement the element value with the mouse wheel.
* SplitButton
  * Now can be enabled and disabled by its widget.
  * Added `disableOnAjax` attribute to disable the button during Ajax requests triggered by it or its menu items.
* Tree: added `filterFunction` attribute for custom filtering.

#### `LocalDateTime` and `Date` filtering
In case you want to filter `LocalDateTime` or `Date` values in for example a `DataTable` component, use the converters
`org.primefaces.filter.LocalDateTime` or `org.primefaces.filter.Date` on the filtering `DatePicker` component.

Look into [migration guide](https://primefaces.github.io/primefaces/12_0_0/#/../migrationguide/12_0_0) for more enhancements and changes.

### PrimeFaces Selenium 

* Switched from Selenium 3.x to Selenium 4.x
  * Most adaptions are done by PrimeFaces Selenium behind the scenes.
  * For usecases where you directly interact with Selenium without PrimeFaces Selenium abstraction look into https://www.selenium.dev/documentation/webdriver/getting_started/upgrade_to_selenium_4/ for details.