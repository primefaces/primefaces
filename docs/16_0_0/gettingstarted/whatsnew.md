# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 16.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/16_0_0/#/../migrationguide/16_0_0) for more enhancements and changes.

* GMap
   * Added support for asynchronous loading of Google Maps API using Dynamic Library Import API.
   * Added `apiKey` attribute for asynchronous loading when Google Maps is not pre-loaded.
   * Added `apiVersion` attribute to specify API version (weekly, beta, alpha, or specific version).
   * Added `libraries` attribute to load additional Google Maps libraries (e.g., places, geometry).
   * Widget automatically detects if Google Maps is already loaded and supports both static and async loading methods.
* Spinner
    * Added `dir="rtl"` right to left support.
* Sticky
    * Added `stickyTopAt` attribute for elements fixed at the top of the page whose height should be considered when positioning the sticky element.
* TreeTable
  * Added `resizeMode` attribute to support column resizing using the "expand" mode.
