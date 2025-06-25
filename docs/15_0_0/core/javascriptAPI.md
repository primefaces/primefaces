# Javascript API

PrimeFaces renders unobtrusive JavaScript which cleanly separates behavior from the HTML. Client side engine is powered
by jQuery.

The available methods and constants are fully documented,
[click here to access the JavaScript API Docs](../jsdocs/index.html).

To enable features such as code autocompletion or type checking, a type declaration file (`*.d.ts`) is available as
well:

* Install it via NPM (`npm install -D primefaces`) or via yarn `yarn add -D primefaces`.
* Use it either with a
  [triple-slash directive](https://www.typescriptlang.org/docs/handbook/triple-slash-directives.html#-reference-path-):
  `/// <reference path="./path/to/PrimeFaces.d.ts" />`
* Or by adding `primefaces` to the `compilerOptions.types` array in the `tsconfig.json`.
* Alternatively, you can also download the type declarations file directly from here:
  [main](../PrimeFaces.d.ts) and [sub](../PrimeFaces-module.d.ts)

## PrimeFaces Core (PrimeFaces.*)

[PrimeFaces](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html) is the main JavaScript object providing utilities and namespace.

| Method | Description |
| --- | --- |
[PrimeFaces.addSubmitParam(el, name, param)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#addSubmitParam) | Adds request parameters dynamically to the element.
[PrimeFaces.changeTheme(theme)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#changeTheme) | Changes theme on the fly with no page refresh.
[PrimeFaces.debounce(fn, delay)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#debounce) | Creates a debounced version of the provided function that delays invoking the function until after the specified delay. The debounced function will only execute once the delay has elapsed and no additional function calls were made. Each new function call resets the delay timer.
[PrimeFaces.deleteCookie(name, cfg)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#deleteCookie) | Deletes a cookie with given and and options.
[PrimeFaces.escapeClientId(id)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#escapeClientId) | Escaped Jakarta Faces ids with semi colon to work with jQuery.
[PrimeFaces.getCookie(name)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#getCookie) | Returns cookie with given name.
[PrimeFaces.getWidgetById(clientid)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#getWidgetById) | Returns the widget instance from the client id
[PrimeFaces.getWidgetsByType(type)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#getWidgetsByType) | Returns an array of widget instances of the specified type, where type is any concrete class of [`PrimeFaces.widget.*`](../jsdocs/modules/src_PrimeFaces.PrimeFaces.widget.html).
[PrimeFaces.info(msg)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#info), [debug(msg)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#debug), [warn(msg)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#warn), [error(msg)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#error) | Client side log API.
[PrimeFaces.queueTask(fn, delay)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#queueTask) | Queues a microtask if delay is 0 or less and setTimeout if > 0.
[PrimeFaces.scrollTo(scrollTarget, duration, topOffset)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#scrollTo) | Scrolls to a component with given client id or jQuery element. The scroll animation can be customized with a duration and an optional offset from the top of the target element.
[PrimeFaces.setCookie(name, value, cfg)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#setCookie) | Sets a cookie with given name, value and options. e.g. PrimeFaces.setCookie('name', 'test'); PrimeFaces.setCookie('name','test',{expires:7, path:'/'}) Second example creates cookie for entire site that expires in 7 days.
[PrimeFaces.setGlobalLocaleValue(name, value)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#setGlobalLocaleValue) | Overwrites the given locale setting for all locales: PrimeFaces.setGlobalLocaleValue('firstDayOfWeek', 1);
[PrimeFaces.skinInput(input)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.html#skinInput) | Progressively enhances an input element with theming.

## PrimeFaces Utilities (PrimeFaces.utils.*)

[PrimeFaces.utils](../jsdocs/modules/src_PrimeFaces.PrimeFaces.utils.html) is the namespace for various utilities needed by PrimeFaces.

| Method | Description |
| --- | --- |
[PrimeFaces.utils.isVisibleInViewport(element, offset)](../jsdocs/modules/src_PrimeFaces.PrimeFaces.utils.html#isVisibleInViewport) | Checks if an element is currently visible within the browser viewport, with an optional offset. Returns true if the element is visible within the viewport, false otherwise.
[PrimeFaces.utils.disableAnimations()](../jsdocs/modules/src_PrimeFaces.PrimeFaces.utils.html#disableAnimations) | Disables all CSS and jQuery animations.
[PrimeFaces.utils.enableAnimations()](../jsdocs/modules/src_PrimeFaces.PrimeFaces.utils.html#enableAnimations) | Enables all CSS and jQuery animations.
[PrimeFaces.utils.killswitch()](../jsdocs/modules/src_PrimeFaces.PrimeFaces.utils.html#killswitch) | Killswitch that stops all AJAX requests, running Pollers and IdleMonitors.


## PrimeFaces Environment (PrimeFaces.env.*)

[PrimeFaces.env](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html) is the namespace for detecting the used client and features.

| Method | Description |
| --- | --- |
[PrimeFaces.env.mobile](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#mobile) | If the used client is a mobile browes
[PrimeFaces.env.touch](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#touch) | If the client supports touch
[PrimeFaces.env.ios](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#ios) | If the client is iOS
[PrimeFaces.env.android](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#android) | If the client is Android
[PrimeFaces.env.browser](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#browser) | The browser name
[PrimeFaces.env.prefersReducedMotion](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#prefersReducedMotion) | If the client prefers reduced motion
[PrimeFaces.env.preferredColorSchemeDark](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#preferredColorSchemeDark) | If the client prefers dark color scheme
[PrimeFaces.env.preferredColorSchemeLight](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#preferredColorSchemeLight) | If the client prefers light color scheme
[PrimeFaces.env.getTheme()](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#getTheme) | Gets the currently loaded PrimeFaces theme
[PrimeFaces.env.getThemeContrast()](../jsdocs/modules/src_PrimeFaces.PrimeFaces.env.html#getThemeContrast) | Gets the current theme contrast mode (light or dark)


## PrimeFaces Widget (PrimeFaces.widget.*)

Contains custom [PrimeFaces widget classes](../jsdocs/modules/src_PrimeFaces.PrimeFaces.widget.html) like;

- [PrimeFaces.widget.DataTable](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.DataTable-1.html)
- [PrimeFaces.widget.VerticalTree](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.VerticalTree-1.html)
- [PrimeFaces.widget.Poll](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Poll-1.html)
- and more...

Most of the components have a corresponding client side widget with same name.

_PrimeFaces.widgets_ is an object with all current widget instances, with the key being the widget var.

## PrimeFaces AJAX (PrimeFaces.ajax.*)

See: [AJAX / JavaScript API](/core/ajaxJavascriptApi.md)

## PrimeFaces Client Side Validation (CSV) (PrimeFaces.validation.*)

See: [Client Side Validation](/core/csvJavascriptAPI.md)


