# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 13.0.0

### PrimeFaces

* DataGrid
    * Added attribute `rowTitle` to support row-specific titles.
* DataTable
    * Added attribute `rowTitle` to support row-specific titles.
* DataView
    * Added attribute `gridRowTitle` to support row-specific titles.
* TreeTable
    * Added attribute `rowTitle` to support row-specific titles.
* SpeedDial
    * Added attribute `ariaLabel` to allow screen reader support on button and `title` for tooltip.
* MenuItem
    * Added attribute `ariaLabel` to allow screen reader support on menu items that do not have text or tooltip.
* VirusScan
    * Added default configuration for host and port (localhost:3310) of the ClamDeamonScanner such that no explicit configuration in web.xml is needed anymore when these defaults are fine.
    * Generalized ClamDeamonScanner such that the implementation of a custom ClamDeamonScanner can now access all virus scan parameters from the ClamDaemonClient.


Look into [migration guide](https://primefaces.github.io/primefaces/13_0_0/#/../migrationguide/13_0_0) for more enhancements and changes.
Or check the list of TODO+ issues closed for
[13.0.0](https://github.com/primefaces/primefaces/issues?q=is%3Aclosed+milestone%3A13.0.0).

### PrimeFaces Selenium 

* ...
