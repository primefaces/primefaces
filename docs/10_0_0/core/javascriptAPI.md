# Javascript API

PrimeFaces renders unobtrusive JavaScript which cleanly separates behavior from the HTML. Client side engine is powered by jQuery.

The available methods and constants are fully documented, [click here to access the JavaScript API Docs](../jsdocs/index.html).

To enable features such as code autocompletion or type checking, a type declaration file (`*.d.ts`) is available as well:

* You can download it from here: [main](../PrimeFaces.d.ts) and [sub](../PrimeFaces-module.d.ts)
* Use it with a
  [triple-slash directive](https://www.typescriptlang.org/docs/handbook/triple-slash-directives.html#-reference-path-):
  `/// <reference path="./path/to/PrimeFaces.d.ts" />`
* It references some types from other libraries which you may need to install via npm. See the
  [main page of the JavaScript API Docs](../jsdocs/index.html) for more details.

## PrimeFaces Core (PrimeFaces.*)

[PrimeFaces](../jsdocs/modules/src_primefaces.primefaces.html) is the main JavaScript object providing utilities and namespace.

| Method | Description |
| --- | --- |
[PrimeFaces.escapeClientId(id)](../jsdocs/modules/src_primefaces.primefaces.html#escapeclientid#escapeclientid) | Escaped JSF ids with semi colon to work with jQuery.
[PrimeFaces.addSubmitParam(el, name, param)](../jsdocs/modules/src_primefaces.primefaces.html#addsubmitparam) | Adds request parameters dynamically to the element.
[PrimeFaces.getCookie(name)](../jsdocs/modules/src_primefaces.primefaces.html#getcookie) | Returns cookie with given name.
[PrimeFaces.setCookie(name, value, cfg)](../jsdocs/modules/src_primefaces.primefaces.html#setcookie) | Sets a cookie with given name, value and options. e.g. PrimeFaces.setCookie('name', 'test'); PrimeFaces.setCookie('name','test',{expires:7, path:'/'}) Second example creates cookie for entire site that expires in 7 days.
[PrimeFaces.deleteCookie(name, cfg)](../jsdocs/modules/src_primefaces.primefaces.html#deletecookie) | Deletes a cookie with given and and options.
[PrimeFaces.skinInput(input)](../jsdocs/modules/src_primefaces.primefaces.html#skininput) | Progressively enhances an input element with theming.
[PrimeFaces.info(msg)](../jsdocs/modules/src_primefaces.primefaces.html#info), [debug(msg)](../jsdocs/modules/src_primefaces.primefaces.html#debug), [warn(msg)](../jsdocs/modules/src_primefaces.primefaces.html#warn), [error(msg)](../jsdocs/modules/src_primefaces.primefaces.html#error) | Client side log API.
[PrimeFaces.changeTheme(theme)](../jsdocs/modules/src_primefaces.primefaces.html#changetheme) | Changes theme on the fly with no page refresh.
[PrimeFaces.getWidgetById(clientid)](../jsdocs/modules/src_primefaces.primefaces.html#getwidgetbyid) | Returns the widget instance from the client id
[PrimeFaces.getWidgetsByType(type)](../jsdocs/modules/src_primefaces.primefaces.html#getwidgetsbytype) | Returns an array of widget instances of the specified type, where type is any concrete class of [`PrimeFaces.widget.*`](../jsdocs/modules/src_primefaces.primefaces.widget.html).

## PrimeFaces Environment (PrimeFaces.env.*)

[PrimeFaces.env](../jsdocs/modules/src_primefaces.primefaces.env.html) is the namespace for detecting the used client and features.

| Method | Description |
| --- | --- |
[PrimeFaces.env.mobile](../jsdocs/modules/src_primefaces.primefaces.env.html#mobile) | If the used client is a mobile browes
[PrimeFaces.env.touch](../jsdocs/modules/src_primefaces.primefaces.env.html#touch) | If the client supports touch
[PrimeFaces.env.ios](../jsdocs/modules/src_primefaces.primefaces.env.html#ios) | If the client is iOS
[PrimeFaces.env.isIE(version)](../jsdocs/modules/src_primefaces.primefaces.env.html#isie) | If the client is IE and the version matches the parameter
[PrimeFaces.env.isLtIE(version)](../jsdocs/modules/src_primefaces.primefaces.env.html#isltie) | If the client is IE and the version is lesser than the parameter

## PrimeFaces Widget (PrimeFaces.widget.*)

Contains custom [PrimeFaces widget classes](../jsdocs/modules/src_primefaces.primefaces.widget.html) like;

- [PrimeFaces.widget.DataTable](../jsdocs/classes/src_primefaces.primefaces.widget.datatable-1.html)
- [PrimeFaces.widget.Tree](../jsdocs/classes/src_primefaces.primefaces.widget.basetree-1.html)
- [PrimeFaces.widget.Poll](../jsdocs/classes/src_primefaces.primefaces.widget.poll-1.html)
- and more...

Most of the components have a corresponding client side widget with same name.

_PrimeFaces.widgets_ is an object with all current widget instances, with the key being the widget var.

## PrimeFaces AJAX (PrimeFaces.ajax.*)

See: [AJAX / JavaScript API](/core/ajaxJavascriptApi.md)

## PrimeFaces Client Side Validation (CSV) (PrimeFaces.validation.*)

See: [Client Side Validation](/core/csvJavascriptAPI.md)


