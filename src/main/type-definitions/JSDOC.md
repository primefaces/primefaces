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
  @types/chart.js \
  @types/googlemaps \
  @types/jquery \
  @types/jqueryui \
  @types/jquery.cleditor
  @types/moment-timezone \
  @types/quill \
  @types/raphael \
  @fullcalendar/core \
  @fullcalendar/daygrid \
  @fullcalendar/interaction \
  @fullcalendar/list \
  @fullcalendar/moment \
  @fullcalendar/timegrid \
  autonumeric \
  cropperjs \
  moment \
  vis-data \
  vis-timeline
```

</details>

PrimeFaces is a global library that adds methods and constants t the global window scope. To use the type declarations
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
[window.PrimeFaces]((./modules/primefaces.html)) (or just `PrimeFaces` for short). It contains several utility methods
as well as the [widget registry]((./modules/primefaces.widget.html)) with all available PrimeFaces widgets.

### Core

The core includes all the generic functionality required by the PrimeFaces framework.

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

### JQuery extensions

PrimeFaces relies heavily on [JQuery](https://jquery.com/) for interacting with the DOM. Many JQuery plugins are
included that are used by the various widgets. You can make use of these plugins, but please note that they are not
parts of the officially supported API and may be removed or replaced at any time.

Also, as most JQuery plugins are specific to a certain widget, please note that some plugins may only be available when
that widget is included in the page.

* See the [JQuery](./modules/jquery.html) interface for methods that can be called on JQuery instances (`$.fn`)
* See the [JQueryStatic](./interfaces/jquerystatic.html) interface for constants and methods available on the global
  JQuery object (`$` / `jQuery`).
* See the [TypeToTriggeredEventMap](./interfaces/jquery.typetotriggeredeventmap.html) for additional events defined
  by JQuery extensions. 
* Convention for JQuery plugins for for which the type declarations were written PrimeFaces: Types and interfaces
  required by the JQuery extension are declared in a their own namespace `JQuery*`. These namespaces are only for the
  types and do not contain any objects that are available at runtime. 

### Third-party libraries

PrimeFaces makes use of many third-party libraries for providing advanced features, such charts and masked inputs. Many
of these do not have typescript declarations. But fear not, PrimeFaces added them for you. For your convenience, the
API docs for all third-party libraries are included in this documentation.

Please note that these third-party libraries may be exchanged at any time by a different library. Some of the most
notable ones are:

* [AutoNumeric.js](./classes/autonumeric.html)
    * The [AutoNumeric.js library](http://autonumeric.org/) that automatically formats your numbers and currencies.
* [autosize](./modules/autosize.html)
    * The [Autosize library](https://github.com/jackmoore/autosize), a small, stand-alone script to automatically adjust
      the height of a textarea to fit the text. 
* [chart.js](./classes/chart.html)
    * The [chart.js](https://www.chartjs.org/) library for drawing diagrams.
* [ContentFlow](./classes/contentflow.html)
    * [The ContentFlow library](https://web.archive.org/web/20120108070056/http://www.jacksasylum.eu/ContentFlow/index.php)
      a flexible ImageFlow like flow written in javascript, which can handle any kind of content.
* [Cropper.js](./classes/cropper.html)
    * The [Cropper.js](https://fengyuanchen.github.io/cropperjs/) library, a visual JavaScript image cropper.
* [FullCalendar](./modules/__fullcalendar_core_calendar_.html)
    * The [FullCalendar library](https://fullcalendar.io/), a full-sized drag & drop JavaScript event calendar.
* [Google Maps](./modules/google.maps.html)
    * The [Google Maps JavaScript API](https://developers.google.com/maps/documentation/javascript/tutorial) for
      including maps in a web page.
* [jsplumb](./modules/jsplumb.html)
    * The [jsPlumb library](https://github.com/jsplumb/jsplumb), to visually connect elements on web pages.
* [JuxtaposeJS](./modules/juxtapose.html)
    * The [JuxtaposeJs library](https://juxtapose.knightlab.com/), a simple, open source tool for creating before or
      after image sliders.
* [moment.js](./modules/moment.html)
    * The [Moment.js](https://momentjs.com/) library to parse, validate, manipulate, and display dates and times in
      JavaScript.
* [Quill](./classes/quill.html)
    * The [Quill Editor](https://quilljs.com/), a modern rich text editor for the web.
* [Raphaël](./interfaces/raphaelstatic.html) ([plugins](./modules/_raphael_.html))
    * The [Raphaël vector graphics library](https://dmitrybaranovskiy.github.io/raphael/) for drawing onto a canvas.
* [Timeline/Graph2D](./classes/timeline.html)
    * The [Timeline/Graph2D](https://github.com/visjs/vis-timeline) is an interactive visualization chart to visualize
    data in time.
* [WebcamJS](./modules/webcam.html)
    * The [WebcamJS library](https://github.com/jhuckaby/webcamjs) for accessing a camera.
