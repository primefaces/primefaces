# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 12.0.0

* AutoComplete: added `lazyModel` attribute for fetching autocomplete-suggestions (instead of completeMethod).
* CommandButton:
  * added `disableOnAjax` attribute to disable the button during Ajax requests triggered by it.
  * added `inlineAjaxStatus` attribute to show a loading icon in the button during Ajax requests triggered by it.
* CommandLink
  * Now has a widget and can be enabled and disabled by its widget.
  * Added `disableOnAjax` attribute to disable the link during Ajax requests triggered by it.
* MenuButton
  * Now can be enabled and disabled by its widget.
  * Added `disableOnAjax` attribute to disable the button during Ajax requests triggered by its menu items.
* Printer: added `configuration` attribute for passing custom JSON options to PrintJS.
* SplitButton
  * Now can be enabled and disabled by its widget.
  * Added `disableOnAjax` attribute to disable the button during Ajax requests triggered by it or its menu items.
* Tree: added `filterFunction` attribute for custom filtering.

Look into [migration guide](https://primefaces.github.io/primefaces/12_0_0/#/../migrationguide/12_0_0) for more enhancements and changes.

## Exporter
  * Added new options for `visibleOnly`, `exportHeader` and `exportFooter` to give better control over output
  
