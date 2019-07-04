# Javascript API

PrimeFaces renders unobstrusive javascript which cleanly separates behavior from the html. Client side engine is powered by jQuery.

## PrimeFaces Namespace

_PrimeFaces_ is the main javascript object providing utilities and namespace.

| Method | Description |
| --- | --- |
escapeClientId(id) | Escaped JSF ids with semi colon to work with jQuery.
addSubmitParam(el, name, param) | Adds request parameters dynamically to the element.
getCookie(name) | Returns cookie with given name.
setCookie(name, value, cfg) | Sets a cookie with given name, value and options. e.g. PrimeFaces.setCookie('name', 'test'); PrimeFaces.setCookie('name','test',{expires:7, path:'/'}) Second example creates cookie for entire site that expires in 7 days.
deleteCookie(name, cfg) | Deletes a cookie with given and and options.
skinInput(input) | Progressively enhances an input element with theming.
info(msg), debug(msg), warn(msg), error(msg) | Client side log API.
changeTheme(theme) | Changes theme on the fly with no page refresh.
cleanWatermarks() | Watermark component extension, cleans all watermarks on page before submitting the form.
showWatermarks() | Shows watermarks on form.
getWidgetById(clientid) | Returns the widget instance from the client id

To be compatible with other javascript entities on a page, PrimeFaces defines two javascript
namespaces;

**PrimeFaces.widget.**

Contains custom PrimeFaces widgets like;

- PrimeFaces.widget.DataTable
- PrimeFaces.widget.Tree
- PrimeFaces.widget.Poll
- and more...

Most of the components have a corresponding client side widget with same name.





