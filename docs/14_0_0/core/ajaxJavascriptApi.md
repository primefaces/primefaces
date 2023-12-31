# AJAX Javascript API

PrimeFaces AJAX Javascript API is powered by jQuery and optimized for JSF.
The whole API consists of three properly namespaced simple JavaScript functions.

[See the AJAX Javascript API Docs.](../jsdocs/modules/src_PrimeFaces.PrimeFaces.ajax.html)

## PrimeFaces.ajax.Request

Sends AJAX requests that execute JSF lifecycle and retrieve partial output.

See the [APIDocs for PrimeFaces.ajax.Request](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Request.html#handle)
for details. Briefly, the function signature is as follows:

```js
PrimeFaces.ajax.Request.handle(cfg);
```

This also returns a promise you can use to access the response data.

**Configuration Options**

All available options are described in the
[APIDocs for PrimeFaces.ajax.Configuration](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html).
The most common options are:

| Option | Description |
| --- | --- |
[formId](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#formId) | Id of the form element to serialize, if not defined parent form of source is used.
[async](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#async) | Flag to define whether request should go in ajax queue or not, default is false.
[global](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#global) | Flag to define if p:ajaxStatus should be triggered or not, default is true.
[update](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#update) | Component(s) to update with ajax.
[process](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#process) | Component(s) to process in partial request.
[source](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#source) | Client id of the source component causing the request.
[params](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#params) | Additional parameters to send in ajax request.
[onstart()](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#onstart) | Javascript callback to process before sending the ajax request, return false to cancel the request.
[onsuccess(data, status, xhr)](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#onsuccess) | Javascript callback to process when ajax request returns with success code. Takes three arguments, xml response, status code and xmlhttprequest.
[onerror(xhr, status, error)](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#onerror) | Javascript callback to process when ajax request fails. Takes three arguments, xmlhttprequest, status string and exception thrown if any.
[oncomplete(xhr, status, args, data)](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Configuration.html#oncomplete) | Javascript callback to process when ajax request completes. Takes three arguments, xmlhttprequest, status string and optional arguments provided by PrimeFaces.current() API.

### Examples
Suppose you have a JSF page called _createUser_ with a simple form and some input components.

```xhtml
<h:form id="userForm">
    <h:inputText id="username" value="#{userBean.user.name}" />
    ... More components
</h:form>
```
You can post all the information with AJAX using;

```js
PrimeFaces.ajax.Request.handle({
    formId:’userForm’
    ,source:’userForm’
    ,process:’userForm’
});
```
More complex example with additional options;

```js
PrimeFaces.ajax.Request.handle({
    formId: 'userForm',
    source: 'userForm',
    process: 'userForm',
    update: 'msgs',
    params: [
        {name: 'param_name1', value: 'value1'},
        {name: 'param_name2', value: 'value2'},
    ],
    oncomplete:function(xhr, status) {alert('Done');}
});
```

We highly recommend using [p:remoteCommand](/components/remotecommand) instead of low level JavaScript API whenever
possible as it achieves the same with much less effort and less possibility of an error.

## PrimeFaces.ajax.Response

[PrimeFaces.ajax.Response.handle()](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Response.html#handle) updates
the specified components if any and synchronizes the client side JSF state. DOM updates are implemented using jQuery
which uses a very fast algorithm.

## Abort

Use the [abort API](../jsdocs/interfaces/src_PrimeFaces.PrimeFaces.ajax.Queue.html#abortAll) in case you'd like to cancel all the ongoing requests:

```js
PrimeFaces.ajax.Queue.abortAll()
```

## Lifecycle events

It's possible to catch the events of the AJAX lifecycle via jQuery, but only when `global` is set to `true`.

```js
$(document).on('pfAjaxComplete', function() {
    console.log('hey!');
});
```

### Events

| Name | Arguments | Description |
| --- | --- | --- |
| [pfAjaxStart](../jsdocs/interfaces/src_PrimeFaces.JQuery.TypeToTriggeredEventMap.html#pfAjaxStart) |  | Executed when entering the AJAX lifecycle, before preparing all settings for the XHR.
| [pfAjaxSend](../jsdocs/interfaces/src_PrimeFaces.JQuery.TypeToTriggeredEventMap.html#pfAjaxSend) | xhr, settings | Executed before sending the XHR.
| [pfAjaxError](../jsdocs/interfaces/src_PrimeFaces.JQuery.TypeToTriggeredEventMap.html#pfAjaxError) | xhr, settings, error | Executed when sending the request or receiving the response failed.
| [pfAjaxSuccess](../jsdocs/interfaces/src_PrimeFaces.JQuery.TypeToTriggeredEventMap.html#pfAjaxSuccess) | xhr, settings | Executed after the response was received but before processing the response / replace DOM elements.
| [pfAjaxUpdated](../jsdocs/interfaces/src_PrimeFaces.JQuery.TypeToTriggeredEventMap.html#pfAjaxUpdated) | xhr, settings, args | Executed after the response was received but after the DOM elements have been updated.
| [pfAjaxComplete](../jsdocs/interfaces/src_PrimeFaces.JQuery.TypeToTriggeredEventMap.html#pfAjaxComplete) | xhr, settings, args | Executed after the AJAX lifecycle has been completed, independent of success or error.

## Inline load animation

Some components support an inline load animation. To avoid flickering on short loading times, a minimum animation
duration is defined. It's 500 milliseconds by default, but you can customize it. For example

```js
PrimeFaces.ajax.minLoadAnim = 250;
```
