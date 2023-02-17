# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 13.0.0

### PrimeFaces

* Core
    * Added attribute `primefaces.HTML5_COMPLIANCE` if you know your site is HTML5 doctype so PF won't render certain non-HTML5 compliant values like `text/javascript` on scripts.
* BlockUI
    * Added attribute `delay` to delay displaying similar to AjaxStatus.
* Button
* CommandButton
* LinkButton
* MenuButton
* SplitButton
* SelectBooleanButton
    * Added attribute `aria-label` for icon only buttons (will use `title` first if available).
* Captcha
    * Added attribute `sourceUrl` to override the Google JS location for countries that do not have access to Google.
* Charts
    * Added model `subtitle` exactly the same configuration options with title are available for subtitle.
    * Added model `responsive`, `maintainAspectRatio`, `aspectRatio` responsive options.
    * Added attribute `ariaLabel` to all Charts to allow accessibility but defaults to the Charts title.
* Column/Columns
    * Added attribute `exportTag` to allow finer grain control over XML export.
* ColorPicker
    * Brand new widget featuring full accessibility, RTL, Alpha support and more.
* DataGrid
    * Added attribute `rowTitle` to support row-specific titles.
* DatePicker
    * Added attribute `flex` to support PrimeFlex CSS instead of legacy Grid CSS.
* Dashboard
    * Added attribute `responsive` to support responsive modern dashboards.
* DataTable
    * Added attribute `rowTitle` to support row-specific titles.
    * Added attribute `title` column/columns to support cell specific titles.
    * Added attribute `exportTag` and `exportRowTag` to allow finer grain control over XML export.
* DataView
    * Added attribute `gridRowTitle` to support row-specific titles.
* OverlayPanel
    * Added attribute `cache` similar to Dialog when `dynamic=true`.
* OrderList
    * Added attribute `flex` to support PrimeFlex CSS instead of legacy Grid CSS.
* PickList
    * Added attribute `filterNormalize` to allow normalized filtering (without accents).
    * Added attributes `sourceFilterPlaceholder` and `targetFilterPlaceholder` for accessibility.
* TabView
    * Added `focusOnLastActiveTab` if you want to focus on the tab that the user last activated.
    * Added `footer` facet to add a footer to the whole TabView not per tab.
* TreeTable
    * Added attribute `rowTitle` to support row-specific titles.
    * Added attribute `title` column/columns to support cell specific titles.
    * Added attribute `exportTag` and `exportRowTag` to allow finer grain control over XML export.
* Schedule
    * Added event `eventDblSelect` to allow an event to be double clicked.
* SelectCheckboxMenu
    * Added attribute `var` to support custom content in overlay panel.
    * Added support for facet `footer` in overlay panel.
* SelectManyCheckbox
    * Added attribute `flex` to support PrimeFlex CSS instead of legacy Grid CSS.
* SelectManyMenu
    * Added attribute `filterNormalize` to allow normalized filtering (without accents).
* SelectOneListbox
    * Added attribute `filterNormalize` to allow normalized filtering (without accents).
* SelectOneMenu
    * Added attribute `filterNormalize` to allow normalized filtering (without accents).
* SelectOneRadio
    * Added attribute `flex` to support PrimeFlex CSS instead of legacy Grid CSS.
* SpeedDial
    * Added attribute `ariaLabel` to allow screen reader support on button and `title` for tooltip.
* SplitButton
    * Added attribute `filterNormalize` to allow normalized filtering (without accents).
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
