# JavaScript API Docs for PrimeFaces

This is the documentation for the client-side JavaScript part of PrimeFaces, please visit
[PrimeFaces.org](https://www.primefaces.org) for more information. You may also  be interested in the
[user guide](https://primefaces.github.io/primefaces/).

[![PrimeFaces Logo](https://www.primefaces.org/wp-content/uploads/2016/10/prime_logo_new.png)](https://www.primefaces.org/showcase)

* [Type declarations file](#type-declarations-file)
* [API Overview](#api-overview)
    - [Core](#core)
    - [Widgets](#widgets)
    - [JQuery extensions](#jquery-extensions)
    - [Third-party libraries](#third-party-libraries)

## Type declarations file

To enable code autocompletion or type checking etc, a type declaration file
(`*.d.ts`) is available as well:

* You can download it from here: [main](../PrimeFaces.d.ts) and [sub](../PrimeFaces-module.d.ts)
* It references some types from other libraries. You may need to install:

<details>
<summary>Click to see required types from NPM packages </summary>

```sh
npm install --save-dev  \
  @fullcalendar/common \
  @fullcalendar/core \
  @fullcalendar/daygrid
  @fullcalendar/interaction 
  @fullcalendar/list 
  @fullcalendar/moment \
  @fullcalendar/timegrid \
  @types/chart.js \
  @types/download.js \
  @types/googlemaps \
  @types/hammerjs \
  @types/inputmask \
  @types/jquery \
  @types/jqueryui \
  @types/js-cookie \
  @types/quill \
  @types/raphael \
  autonumeric \
  cropperjs \
  jsplumb \
  moment \
  moment-timezone \
  vis-data \
  vis-timeline \
  vis-util
```

</details>

PrimeFaces is a global library that adds methods and constants in the global window scope. To use the type declarations
in a JavaScript or TypeScript file, use a
[triple-slash directive](https://www.typescriptlang.org/docs/handbook/triple-slash-directives.html#-reference-path-)
like this (must be at the top of the file):

```javascript
/// <reference path="./path/to/PrimeFaces.d.ts" />

// Now your IDE or transpiler can issue a warning that "search" does
// not exist on a jQuery object. You also get autocompletion.
PF("dataTable").jq.search("tbody td");
```

Make sure the `PrimeFaces-module.d.ts` is in the same directory. See the
[TypeScript page](https://www.typescriptlang.org/docs/handbook/declaration-files/introduction.html) for more info on
type declaration files.

## API Overview

The main entry point of the PrimeFaces client-side JavaScript API is the global object
[window.PrimeFaces](./modules/src_primefaces.primefaces.html) (or just `PrimeFaces` for short). It contains several
utility methods as well as the [widget registry](./modules/src_primefaces.primefaces.widget.html) with all available
PrimeFaces widgets.

### Core

The core includes all the generic functionality required by the PrimeFaces framework.

* [PrimeFaces](./modules/src_primefaces.primefaces.html) The global object itself contains some utility methods, such as `scrollTo` or
`escapeHTML`.
* [PrimeFaces.ajax](./modules/src_primefaces.primefaces.ajax.html) The AJAX module with functionality related to AJAX requests. These
requests are made, for example, when a form or a component is updated.
* [PrimeFaces.clientwindow](./modules/src_primefaces.primefaces.clientwindow.html) The module for enabling multiple window support in
  PrimeFaces applications.
* [PrimeFaces.csp](./modules/src_primefaces.primefaces.csp.html) Functions for enabling CSP. For example, to use CSP
you must use `Primefaces.csp.eval` instead of `$.globalEval`.
* [PrimeFaces.dialog](./modules/src_primefaces.primefaces.dialog.html) The dialog module for working with dialogs.
* [PrimeFaces.expressions](./modules/src_primefaces.primefaces.expressions.html) The search expressions module. It contains some
methods to resolve search expressions such as `@form` on the client.
* [PrimeFaces.resources](./modules/src_primefaces.primefaces.resources.html) The resources submodule. It contains some methods to
generate JSF resource URLs.
* [PrimeFaces.settings](./modules/src_primefaces.primefaces.settings.html) The settings module which contains the locale
(translations) shared by all widgets.
* [PrimeFaces.utils](./modules/src_primefaces.primefaces.utils.html) The utility module with more advanced utility methods.
* [PrimeFaces.validation](./modules/src_primefaces.primefaces.validation.html) The validation submodule. It contains methods for 
handling client side validation.

### Widgets

* [PrimeFaces.widget](./modules/src_primefaces.primefaces.widget.html) contains all available widget classes. A widget is
usually instantiated by PrimeFaces, but you may use this, for example, to check the type of a widget instance via
`instanceof`. You can also use these as a base class when you create your own widgets.
* [`PrimeFaces.widgets`](./modules/src_primefaces.primefaces.html#widgets) is an object with all instantiated widgets that
are available on the current page.
* [window.PF](./modules/src_primefaces.html#pf) is a method for finding a widget by its widget variable. When the widget
does not exist, it logs a warning.

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

### JQuery extensions

PrimeFaces relies heavily on [JQuery](https://jquery.com/) for interacting with the DOM. Many JQuery plugins are
included that are used by the various widgets. You can make use of these plugins, but please note that they are not
parts of the officially supported API and may be removed or replaced at any time.

Also, as most JQuery plugins are specific to a certain widget, please note that some plugins may only be available when
that widget is included in the page.

* See the [JQuery](./interfaces/src_primefaces.jquery-1.html) interface for methods that can be called on JQuery instances
(`$.fn`). This includes methods from jQuery plugins added by the various libraries used by PrimeFaces.
* See the [JQueryStatic](./interfaces/src_primefaces.jquerystatic.html) interface for constants and methods available on the
global JQuery object (`$` / `jQuery`).
* See the [TypeToTriggeredEventMap](./interfaces/src_primefaces.jquery.typetotriggeredeventmap.html) for additional events
defined by JQuery extensions.
* Convention for JQuery plugins for which the type declarations are provided by PrimeFaces: Types and interfaces
  required by the JQuery extension are declared in a their own namespace `JQuery*`. These namespaces are only for the
  types and do not contain any objects that are available at runtime. 

### Third-party libraries

PrimeFaces makes use of many third-party libraries for providing advanced features, such charts and masked inputs. Many
of these do not have typescript declarations. But fear not, PrimeFaces added them for you. For your convenience, the
API docs for all third-party libraries are included in this documentation.

Please note that these third-party libraries may be exchanged at any time by a different library. Some of the most
notable ones are:

* [AutoNumeric.js](./classes/node_modules_autonumeric.export_-1.html)
    * The [AutoNumeric.js library](http://autonumeric.org/) that automatically formats your numbers and currencies.
* [autosize](./modules/src_primefaces.html#autosize-1)
    * The [Autosize library](https://github.com/jackmoore/autosize), a small, stand-alone script to automatically adjust
      the height of a textarea to fit the text. 
* [chart.js](./classes/node_modules__types_chart_js.export_-1.html)
    * The [chart.js](https://www.chartjs.org/) library for drawing diagrams.
* [ContentFlow](./classes/src_primefaces.contentflow.html)
    * [The ContentFlow library](https://web.archive.org/web/20120108070056/http://www.jacksasylum.eu/ContentFlow/index.php)
      a flexible ImageFlow like flow written in javascript, which can handle any kind of content.
* [Cropper.js](./classes/node_modules_cropperjs_types.cropper-1.html)
    * The [Cropper.js](https://fengyuanchen.github.io/cropperjs/) library, a visual JavaScript image cropper.
* [FullCalendar](./classes/node_modules__fullcalendar_core_main.calendar.html)
    * The [FullCalendar library](https://fullcalendar.io/), a full-sized drag & drop JavaScript event calendar.
* [Google Maps](./modules/node_modules__types_googlemaps.google.maps.html)
    * The [Google Maps JavaScript API](https://developers.google.com/maps/documentation/javascript/tutorial) for
      including maps in a web page.
* [jsplumb](./modules/node_modules_jsplumb.export_.html)
    * The [jsPlumb library](https://github.com/jsplumb/jsplumb), to visually connect elements on web pages.
* [JuxtaposeJS](./modules/src_primefaces.juxtapose.html)
    * The [JuxtaposeJs library](https://juxtapose.knightlab.com/), a simple, open source tool for creating before or
      after image sliders.
* [moment.js](./modules/node_modules_moment_ts3_1_typings_moment.html)
    * The [Moment.js](https://momentjs.com/) library to parse, validate, manipulate, and display dates and times in
      JavaScript.
* [Quill](./modules/node_modules__types_quill.html)
    * The [Quill Editor](https://quilljs.com/), a modern rich text editor for the web.
* [Raphaël](./interfaces/node_modules__types_raphael.raphaelstatic.html)
    * The [Raphaël vector graphics library](https://dmitrybaranovskiy.github.io/raphael/) for drawing onto a canvas.
* [Timeline / Graph2D](./modules/node_modules_vis_timeline_declarations.html)
    * The [Timeline / Timeline](https://github.com/visjs/vis-timeline) is an interactive visualization chart to visualize
    data in time.
* [WebcamJS](./modules/src_primefaces.webcam.html)
    * The [WebcamJS library](https://github.com/jhuckaby/webcamjs) for accessing a camera.
