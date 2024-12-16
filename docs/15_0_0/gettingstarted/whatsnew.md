# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 15.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/15_0_0/#/../migrationguide/15_0_0) for more enhancements and changes.

* Core
    * Added `widgetPreConstruct` attribute to support custom logic before widget creation. See [widgets](../core/widgets.md) for more information.
    
* AutoComplete
    * Added property `highlightSelector=""` so you can delcare the jQuery selector for what to find and highlight. See https://github.com/primefaces/primefaces/issues/11822 
    
* Captcha
    * Added [hCaptcha](https://www.hcaptcha.com/) support
    
* Chart
    * Added `canvasStyle` and `canvasStyleClass` attributes to support custom styling of the canvas element
    
* ConfirmDialog/ConfirmPopup
    * `yesButtonLabel`: overrides label of 'Yes' button (and restores it before the global confirm dialog is reused elsewhere)
    * `yesButtonClass`: adds given class to 'Yes' button (and removes it before the global confirm dialog is reused elsewhere)
    * `yesButtonIcon`: overrides icon of 'Yes' button (and removes it before the global confirm dialog is reused elsewhere)
    * `noButtonLabel`: overrides label of 'No' button (and restores it before the global confirm dialog is reused elsewhere)
    * `noButtonClass`: adds given class to 'No' button (and removes it before the global confirm dialog is reused elsewhere)
    * `noButtonIcon`: overrides icon of 'No' button (and removes it before the global confirm dialog is reused elsewhere)
    * `confirmMessage`: facet on the parent component of the `p:confirm` behavior. Can be used as message content instead of the `p:confirm` `message` attribute.
  
* Dashboard
    *  `scope`: Scope for dashboard drag and drop behaviour. Items can be dragged between multiple dashboards with the same scope. (js default: dashboard)

* DataTable 
    * Added attribute `frozenColumnsAlignment` to support alignment of frozen columns left and right
    * Added `expandIcon`/`collapseIcon` attributes for RowToggler
    * Added `rowsPerPage` in the PageEvent AJAX event
    * Added `filterNormalize` attribute to normalize the filter values (remove accents)
    * Added `toggleFilter()` JS Widget function to show/hide filter components (see showcase example for usage).

* DatePicker
    * Added `defaultHour`, `defaultMinute`, `defaultSecond`, `defaultMillisec` attributes to match legacy `Calendar` component
    * Added ability to pick weeks by `view="week"`
    * Added `showLongMonthNames` attribute to display long month names instead of short names in month picker and month navigator

* FeedReader
    * Added `podcast="true"` property if [Apple Itunes Podcast](https://help.apple.com/itc/podcasts_connect/#/itcb54353390) parsing and specific tags 

* FileUpload
    * Added new messages to support localization
        * `primefaces.FileValidator.FILENAME_INVALID_CHAR=Invalid filename: {0} contains invalid character: {1}`
        * `primefaces.FileValidator.FILENAME_INVALID_LINUX=Invalid Linux filename: {0}`
        * `primefaces.FileValidator.FILENAME_INVALID_WINDOWS=Invalid Windows filename: {0}`
        * `primefaces.FileValidator.FILENAME_EMPTY=Filename cannot be empty or null`
    * Added `empty` facet to add placeholder content

* InputNumber
    * Added `modifyValueOnUpDownArrow` which allows the user to increment or decrement the element value with the up and down arrow keys. Default is true.

* Paginator 
    * Added `rowsPerPage` in the PageEvent AJAX event

* SelectOneMenu
    * Added `clear` AJAX event when in `editable="true"` and you clear out a value with BACKSPACE/DELETE

* Signature
    * Ability to type your signature
    * Added `textValue`, `fontSize`, `fontFamily` attributes to support typing of signature
    * ARIA accessibility support with `role="img"` and `aria-label`

* StaticMessage
    * Added `severity="success"` to align with React/Vue/Angular

* Tree
    * Added `filterPlaceholder` property

* TreeTable
    * Added `filterNormalize` attribute to normalize the filter values (remove accents)

    