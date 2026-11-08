# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 17.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0) for more enhancements and changes.

* Theming
    * New [token based theming](/core/theming.md) with the themes Aura, Lara, Nora, Material, Metal and Glass: pure CSS design tokens, light and dark mode, primary color, density and icons customizable with CSS variables. The themes are part of the `primefaces` jar and replace the SASS based themes (`primefaces-themes`).
    * Works together with [Tailwind CSS](/core/tailwind.md) v4: the utilities use the tokens of the theme.
    * [User settings](/core/theming.md?id=user-settings) (`primefaces.THEME_CONFIGURATOR`): color scheme, density, input style and tokens chosen by the user are applied before the first paint, preset and direction come from cookies. The composite component [themeConfigurator](/core/theming.md?id=theme-configurator) (`xmlns:pc="jakarta.faces.composite/primefaces"`) lets the user change them; `#{p:theme()}` and `#{p:dir()}` return the preset and direction of the request.
* MoveScriptsToBottom
    * Scripts with the attribute `data-p-no-move` are not moved to the bottom.
* Style classes
    * The style classes of the components use the prefix `p-` instead of the jQuery UI prefix `ui-` (e.g. `ui-widget` -> `p-widget`, `ui-state-active` -> `p-state-active`, `ui-datatable` -> `p-datatable`), only the Grid CSS (`ui-g`, `ui-grid`, ...) keeps its names, see [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0?id=style-classes).
* jQuery UI removed
    * PrimeFaces does not ship jQuery UI and jQuery UI Touch Punch anymore. Drag and drop, sorting and resizing are PrimeFaces' own implementation based on pointer events (mouse, pen and touch): `PrimeFaces.interactions`; positioning and effects are `PrimeFaces.utils.position` and `PrimeFaces.effects` (Web Animations API), see [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0?id=jquery-ui).
    * The resource `jquery/jquery-plugins.js` was removed, its remaining jQuery extensions are part of `core.js`.
* Calendar removed
    * `p:calendar` (based on the jQuery UI datepicker) was removed, use [DatePicker](/components/datepicker.md) instead, see [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0?id=calendar).
* Tag
    * Added `outlined` property to display the tag with an outlined style.
* TreeTable
    * Added `stripedRows` property to display striped rows, like DataTable.
* AutoComplete, SelectOneMenu, SelectOneListbox, SelectManyMenu
    * Row variants `p-rows-striped` (list and table mode), `p-rows-gridlines`, `p-rows-sm` and `p-rows-lg` (table mode with `p:column` children), set with `panelStyleClass` (AutoComplete, SelectOneMenu) or `styleClass` (SelectOneListbox, SelectManyMenu).
* SelectManyMenu
    * Added `showToggleAll` property to display a checkbox that selects or unselects all items, and `showSelectionCount` property to display the number of selected items.
* Media
    * Added `title` property (accessible name of the `object` element) and `fallback` facet (content shown when the media cannot be displayed).
* DatePicker
    * Added `builtInValidation` property to skip the server side validation of `mindate` / `maxdate`, `disabledDates`, `enabledDates`, `disabledDays` and the order of the range dates.
* New components
    * [InputOtp](/components/inputotp.md): input for one-time passwords and PINs, one input per character.
    * [InputPhone](/components/inputphone.md): input for international phone numbers with a country selector, the value is the number in the E.164 format, validated in the browser (also with client side validation) and, with Google's libphonenumber, on the server.
    * [TriStateManyCheckbox](/components/tristatemanycheckbox.md): group of checkboxes with three states each, the value is a Map of the item values to their states (moved from PrimeFaces Extensions).
    * [Callback](/components/callback.md): client behavior that executes JavaScript on an event of a component instead of an AJAX request (moved from PrimeFaces Extensions as `pe:javascript`).
