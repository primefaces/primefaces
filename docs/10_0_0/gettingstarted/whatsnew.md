# Whats new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 10.0.0

  * new themes: saga (new default theme), arya and vela
  * new components: Avatar, Badge, Card, CascadeSelect, Chip, Chronoline, ConfirmPopop, Divider, ScrollTop, Skeleton, Splitter, SplitterPanel, Tag
  * FileUpload: supports chunking and resume (see https://primefaces.github.io/primefaces/10_0_0/#/components/fileupload?id=chunking-and-resume)
  * FileDownload: supports AJAX (so `ajax="false"` is not required anymore) (see https://primefaces.github.io/primefaces/10_0_0/#/components/filedownload?id=ajax-downloading)
  * MultiViewState: supports client window mode (see https://primefaces.github.io/primefaces/10_0_0/#/core/multiviewstate)
  * Prime Client Window: improved implementation of JSF Client Window mode (see https://primefaces.github.io/primefaces/10_0_0/#/core/clientwindow)
  * DatePicker
    * added (lazy) metamodel to set disabled and style class (see https://primefaces.github.io/primefaces/10_0_0/#/components/datepicker?id=date-metadata-model)
    * new attribute `inputTime` allows direct input in time field
    * support for a mask provided by attributes `mask`, `maskAutoClear`, `maskSlotChar`
    * showTime- and timeOnly-attributes are detected automatically based on value-bindings to LocalDateTime respectively LocalTime.
  * Audio: new component (see https://primefaces.github.io/primefaces/10_0_0/#/components/audio)
  * Video: new component (see https://primefaces.github.io/primefaces/10_0_0/#/components/video)
  * Observer/Event based p:autoUpdate, inspired by https://github.com/encoway/edu
  * DataTable
    * supports new filter constraint "range" allowing to filter within a provided range
    * refactoring of filter and sort (see https://primefaces.github.io/primefaces/10_0_0/#/../migrationguide/10_0_0?id=datatable)
    * additional quick way of declaring columns with way less markup (`<p:column field="category" headerText="Category" />`, see https://primefaces.github.io/primefaces/10_0_0/#/components/datatable?id=getting-started-with-the-datatable)
  * TreeTable
    * refactoring of filter and sort (see https://primefaces.github.io/primefaces/10_0_0/#/../migrationguide/10_0_0?id=treetable)
  * DataExporter: new layout-features and AJAX-support (so `ajax="false"` is not required anymore) (see https://primefaces.github.io/primefaces/10_0_0/#/../migrationguide/10_0_0?id=dataexporter)
  * new JavaScript API Docs based on TypeDoc: https://primefaces.github.io/primefaces/jsdocs/
    * This includes a type declarations file which may help your and your IDE when writing JavaScript- or TypeScript client-side code. It also enables you to statically check your code against the client side API and be warned when you update PrimeFaces and the API changes.
  * supports JSF 3.0 (Jakarta Server Faces, package jakarta.faces)
  * new context-param `primefaces.CLIENT_SIDE_LOCALISATION` to automatically add client side language JS file

Look into [migration guide](https://primefaces.github.io/primefaces/10_0_0/#/../migrationguide/10_0_0?id=datatable) for more enhancements and changes.
Or check the list of 600+ issues closed for [10.0.0-RC1](https://github.com/primefaces/primefaces/milestone/12?closed=1), [10.0.0-RC2](https://github.com/primefaces/primefaces/milestone/17?closed=1) and [10.0.0(-Final)](https://github.com/primefaces/primefaces/milestone/18?closed=1).
