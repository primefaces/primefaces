# Whats new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 10.0

  * new themes: saga (new default theme), arya and vela
  * new components: Avatar, Badge, Chip, Divider, ScrollTop, Skeleton, Splitter, SplitterPanel, Tag
  * FileUpload: supports chunking and resume (see https://primefaces.github.io/primefaces/10_0/#/components/fileupload?id=chunking-and-resume)
  * FileDownload: supports AJAX (so `ajax="false"` is not required anymore) (see https://primefaces.github.io/primefaces/10_0/#/components/filedownload?id=ajax-downloading)
  * MultiViewState: supports client window mode (see https://primefaces.github.io/primefaces/10_0/#/core/multiviewstate)
  * Prime Client Window: improved implementation of JSF Client Window mode (see https://primefaces.github.io/primefaces/10_0/#/core/clientwindow)
  * DatePicker: added (lazy) metamodel to set disabled and style class (see https://primefaces.github.io/primefaces/10_0/#/components/datepicker?id=date-metadata-model)
  * Audio: new component (see https://primefaces.github.io/primefaces/10_0/#/components/audio)
  * Video: new component (see https://primefaces.github.io/primefaces/10_0/#/components/video)
  * Observer/Event based p:autoUpdate, inspired by https://github.com/encoway/edu
  * DataTable
    * supports new filter constraint "range" allowing to filter within a provided range
    * refactoring of filter and sort (see https://primefaces.github.io/primefaces/10_0/#/../migrationguide/10_0?id=datatable)
    * additional quick way of declaring columns with way less markup (`<p:column field="category" headerText="Category" />`, see https://primefaces.github.io/primefaces/10_0/#/components/datatable?id=getting-started-with-the-datatable)
  * TreeTable
    * refactoring of filter and sort (see https://primefaces.github.io/primefaces/10_0/#/../migrationguide/10_0?id=treetable)
  * DataExporter: new layout-features and AJAX-support (so `ajax="false"` is not required anymore) (see https://primefaces.github.io/primefaces/10_0/#/../migrationguide/10_0?id=dataexporter)
  * new JavaScript API Docs based on JSDoc: https://primefaces.github.io/primefaces/jsdocs/
    * this includes type declarations file which may help your and your IDE when writing JavaScript- or TypeScript-client-side code
  * supports JSF 3.0 (Jakarta Server Faces, package jakarta.faces)

Look into [migration guide](https://primefaces.github.io/primefaces/10_0/#/../migrationguide/10_0?id=datatable) for more enhancements and changes.
Or check the list of [500+ issues](https://github.com/primefaces/primefaces/milestone/12?closed=1) closed for 10.0.