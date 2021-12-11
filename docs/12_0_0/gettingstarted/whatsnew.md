# Whats new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 11.0.0

  * AutoComplete: added `footer` facet which will be added to the suggestion list.
  * Avatar: added `saturation`, `lightness`, `alpha` to control dynamic colors.
  * Badge: added `visible` attribute to hide the badge but render the children.
  * Carousel: completely overhauled by PrimeTek to be in line with PrimeReact/Vue/NG carousel.
  * DataExporter
    * TreeTable now supports exporting.
    * Excel export now supports "currency" cells formatted properly in Excel.
  * DataScroller: added `loading` facet to show UI while loading is in progress.
  * DataTable
    * New built-in `JpaLazyDataModel`, which provides basic functionality for JPA users and lazy loading.
    * New negation filter MatchModes.
  * Galleria: completely overhauled by PrimeTek to be in line with PrimeReact/Vue/NG galleria.
  * FileUpload: added `dropZone` attribute to create a custom drop zone. See https://primefaces.github.io/primefaces/11_0_0/#/components/fileupload?id=custom-drop-zone
  * Hotkey: added `bindMac` to use alternative binding on macOS.
  * Inplace: added `tabindex` for keyboard focus and toggle support.
  * InputText, InputTextarea: counter can count bytes instead of characters.
  * MenuButton: added `buttonStyle` and `buttonStyleClass` attributes.
  * OutputPanel
    * Added `loading` facet to show UI while deferred loading is in progress.
    * Added `loaded` attribute to indicate that deferred loading is not needed, making deferred possible in Ajax requests.
  * Project
    * Refactored into a multi-module build.
    * Updated to work under JDK LTS 8, 11, and 17.
  * Rating: added `tabindex` for keyboard focus and toggle support.
  * Selenium Support
    * PrimeSelenium standalone library for testing PrimeFaces applications.
    * Integration test suite verifying every pull request with 490 passing tests using PrimeSelenium.
  * SpeedDial: new component designed as a floating action button.
  * Spinner
    * Added buttons modes: `horizontal`, `horizontal-after` and `vertical`.
    * Added support to align input text using classes: `text-left`, `text-center`, `text-right`.

Look into [migration guide](https://primefaces.github.io/primefaces/11_0_0/#/../migrationguide/11_0_0) for more enhancements and changes.
Or check the list of 300+ issues closed for
[11.0.0-RC1](https://github.com/primefaces/primefaces/issues?q=is%3Aclosed+milestone%3A11.0.0-RC1),
[11.0.0-RC2](https://github.com/primefaces/primefaces/issues?q=is%3Aclosed+milestone%3A11.0.0-RC2)
and [11.0.0](https://github.com/primefaces/primefaces/issues?q=is%3Aclosed+milestone%3A11.0.0).
