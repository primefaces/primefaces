# JavaScript API Docs for PrimeFaces

This is the documentation for the client-side JavaScript part of PrimeFaces, please visit [PrimeFaces.org](https://www.primefaces.org) for more information. You may also 
be interested in the [user guide](https://primefaces.github.io/primefaces/).

[![PrimeFaces Logo](https://www.primefaces.org/wp-content/uploads/2016/10/prime_logo_new.png)](https://www.primefaces.org/showcase)

## Type declaration file

To enable code autocompletion or type checking etc, a type declartion file (`*.d.ts`) is available as well:

* You can download it from here: [click](../PrimeFaces.d.ts)
* It references some types from other libraries. You may need to install:

<details>
<summary>Types from NPM packages, click to expand </summary>

```sh
npm install --save-dev @types/jquery
npm install --save-dev @types/jqueryui
npm install --save-dev @types/chart.js
npm install --save-dev @types/googlemaps
npm install --save-dev @types/jquery.fileupload
npm install --save-dev @fullcalendar/core
npm install --save-dev @fullcalendar/daygrid
npm install --save-dev @fullcalendar/interaction
npm install --save-dev @fullcalendar/list
npm install --save-dev @fullcalendar/moment
npm install --save-dev @fullcalendar/timegrid
npm install --save-dev quill
npm install --save-dev moment
npm install --save-dev vis-timeline
npm install --save-dev vis-data
```

</details>

See the [typescript page](https://www.typescriptlang.org/docs/handbook/declaration-files/introduction.html) for more
info on type declaration files.

## Overview

The main entry point of the PrimeFaces client-side JavaScript API is the global object `window.PrimeFaces` (or just
`PrimeFaces` for short). It contains several utilty methods as well as the widget registry of all available PrimeFaces
widgets.

### Core

The core includes all generic functionality required by the PrimeFaces framework.

* [PrimeFaces](./modules/primefaces.html) The global object itself contains some utility methods, such as `scrollTo` or
`escapeHTML`.
* [PrimeFaces.ajax](./modules/primefaces.ajax.html) The AJAX module with functionality related to AJAX requests. These
requests are made, for example, when a form or a component is updated.
* [PrimeFaces.dialog](./modules/primefaces.dialog.html) The dialog module for working with dialogs.
* [PrimeFaces.expressions](./modules/primefaces.expressions.html) The search expressions module. It contains some
methods to resolve search expressions such as `@form` on the client.
* [PrimeFaces.resources](./modules/primefaces.resources.html) The resources submodule. It contains some methods to
generate JSF resource URLs.
* [PrimeFaces.utils](./modules/primefaces.utils.html) The utility module with more advanced utility methods.

### JQuery extensions and third-party libraries

PrimeFaces makes use of many third-party libraries for providing advanced features, such charts and masked inputs. Many
of these do not have typescript declarations, but fear not, PrimeFaces added them for you. For your convenience, the
ApiDocs for all third-party libraries are included in this documentation. Some of the most notable ones are:

* jQuery extensions. Please note that some of these plugins are only available when you use one of the widgets that
required that library.
    * See the [JQuery](./modules/jquery.html) interface for methods that can be called on jQuery instances (`$.fn`)
    * See the [JQueryStatic](./interfaces/jquerystatic.html) interface for constants and methods available on the global
      jQuery object (`$` / `jQuery`).
    * See the [TypeToTriggeredEventMap](./interfaces/jquery.typetotriggeredeventmap.html) for additional events defined
      by jQuery extensions. 
    * Types and interfaces required by the jQuery extension are declared in a their own namespace `JQuery*`. These
      namespaces are only for the types and do not contain any objects that are available at runtime. 
* [chart.js](./classes/chart.html) The [chart.js](https://www.chartjs.org/) library for drawing diagrams.
* [moment](./modules/moment.html) The [Moment.js](https://momentjs.com/) library to parse, validate, manipulate, and
  display dates and times in JavaScript
* [Raphael](./modules/raphael.html) The [raphael](https://dmitrybaranovskiy.github.io/raphael/) vector graphics library
  for drawing onto a canvas.

### Widgets

* [PrimeFaces.widget](./modules/primefaces.widget.html) contains all available widget classes. A widget is usually
instantiated by PrimeFaces, but you may use this, for example, to check the type of a widget instance via `instanceof`.
* `PrimeFaces.widgets` is an object with all instantiated widgets that are available on the current page.
* [window.PF](./globals.html#pf) is a method for finding a widget by its widget variable. When the widget does not
exist, it logs a warning.

For example, if you have got a data table widget defined in your XHTML view like this:

```xml
<p:dataTable id="table" widgetVar="tableVar" />
```

Then you can access the widget instance from JavaScript like this:

```javascript
// Find the widget instance
var widget = PF("tableVar");
// Check whether it is really a data table
if (widget instanceof PrimeFaces.widget.DataTable) {
    // Do something with the data table
    widget.unselectAllRows();
}
```