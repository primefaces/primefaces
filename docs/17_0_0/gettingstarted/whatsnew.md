# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 17.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0) for more enhancements and changes.

* Theming
    * New [token based theming](/core/theming.md) with the themes Aura, Lara, Nora, Metal and Glass: pure CSS design tokens, light and dark mode, primary color, density and icons customizable with CSS variables. The themes are part of the `primefaces` jar and replace the SASS based themes (`primefaces-themes`).
    * Works together with [Tailwind CSS](/core/tailwind.md) v4: the utilities use the tokens of the theme.
* Style classes
    * The style classes of the components use the prefix `p-` instead of the jQuery UI prefix `ui-` (e.g. `ui-widget` -> `p-widget`, `ui-state-active` -> `p-state-active`, `ui-datatable` -> `p-datatable`), only the Grid CSS (`ui-g`, `ui-grid`, ...) keeps its names, see [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0?id=style-classes).
* jQuery UI removed
    * PrimeFaces does not ship jQuery UI and jQuery UI Touch Punch anymore. Drag and drop, sorting and resizing are PrimeFaces' own implementation based on pointer events (mouse, pen and touch): `PrimeFaces.interactions`; positioning and effects are `PrimeFaces.utils.position` and `PrimeFaces.effects` (Web Animations API), see [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0?id=jquery-ui).
    * The resource `jquery/jquery-plugins.js` was removed, its remaining jQuery extensions are part of `core.js`.
* Calendar removed
    * `p:calendar` (based on the jQuery UI datepicker) was removed, use [DatePicker](/components/datepicker.md) instead, see [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0?id=calendar).
* Tag
    * Added `outlined` property to display the tag with an outlined style.
* New components
    * [InputOtp](/components/inputotp.md): input for one-time passwords and PINs, one input per character.
    * [InputPhone](/components/inputphone.md): input for international phone numbers with a country selector, the value is the number in the E.164 format, validated in the browser (also with client side validation) and, with Google's libphonenumber, on the server.
    * [TriStateManyCheckbox](/components/tristatemanycheckbox.md): group of checkboxes with three states each, the value is a Map of the item values to their states (moved from PrimeFaces Extensions).
