# Javascript API

PrimeFaces renders unobstrusive javascript which cleanly separates behavior from the html. Client side engine is powered by jQuery.

## PrimeFaces Core (PrimeFaces.core.*)

_PrimeFaces.core_ is the main javascript object providing utilities and namespace.

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

## PrimeFaces Environment (PrimeFaces.env.*)

_PrimeFaces.env_ is the namespace for detecting the used client and features.

| Method | Description |
| --- | --- |
PrimeFaces.env.mobile | If the used client is a mobile browes
PrimeFaces.env.touch | If the client supports touch
PrimeFaces.env.ios | If the client is iOS
PrimeFaces.env.isIE(version) | If the client is IE and the version matches the parameter
PrimeFaces.env.isLtIE(version) | If the client is IE and the version is lesser than the parameter

## PrimeFaces Widget (PrimeFaces.widget.*)

Contains custom PrimeFaces widgets like;

- PrimeFaces.widget.DataTable
- PrimeFaces.widget.Tree
- PrimeFaces.widget.Poll
- and more...

Most of the components have a corresponding client side widget with same name.





