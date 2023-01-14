# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 13.0.0

### PrimeFaces

* BlockUI
    * Added attribute `delay` to delay displaying similar to AjaxStatus.
* Captcha
    * Added attribute `sourceUrl` to override the Google JS location for countries that do not have access to Google.
* DataGrid
    * Added attribute `rowTitle` to support row-specific titles.
* DataTable
    * Added attribute `rowTitle` to support row-specific titles.
    * Added attribute `title` column/columns to support cell specific titles.
* DataView
    * Added attribute `gridRowTitle` to support row-specific titles.
* TabView
    * Added `focusOnLastActiveTab` if you want to focus on the tab that the user last activated.
* TreeTable
    * Added attribute `rowTitle` to support row-specific titles.
    * Added attribute `title` column/columns to support cell specific titles.
* Schedule
    * Added event `eventDblSelect` to allow an event to be double clicked.
* SelectCheckboxMenu
    * Added attribute `var` to support custom content in overlay panel.
* SpeedDial
    * Added attribute `ariaLabel` to allow screen reader support on button and `title` for tooltip.
* Menu
    * Added attribute `appendTo` to support custom append of the menu instead of default `@(body)`.
* MenuItem
    * Added attribute `ariaLabel` to allow screen reader support on menu items that do not have text or tooltip.
* VirusScan
    * Generalized ClamDeamonScanner such that the implementation of a custom ClamDeamonScanner can now access all virus scan parameters from the ClamDaemonClient.


Look into [migration guide](https://primefaces.github.io/primefaces/13_0_0/#/../migrationguide/13_0_0) for more enhancements and changes.
Or check the list of TODO+ issues closed for
[13.0.0](https://github.com/primefaces/primefaces/issues?q=is%3Aclosed+milestone%3A13.0.0).

### PrimeFaces Selenium 

* ...
