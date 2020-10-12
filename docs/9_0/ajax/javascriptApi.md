# AJAX Javascript API

PrimeFaces AJAX Javascript API is powered by jQuery and optimized for JSF.
The whole API consists of three properly namespaced simple javascript functions.

[See the AJAX Javascript API Docs.](../jsdocs/modules/primefaces.ajax.html)

## PrimeFaces.ajax.Request
Sends AJAX requests that execute JSF lifecycle and retrieve partial output.
[Function signature](../jsdocs/interfaces/primefaces.ajax.request.html#handle)
is as follows:

```js
PrimeFaces.ajax.Request.handle(cfg);
```

**Configuration Options**

| Option | Description |
| --- | --- |
[formId](../jsdocs/interfaces/primefaces.ajax.configuration.html#formid) | Id of the form element to serialize, if not defined parent form of source is used.
[async](../jsdocs/interfaces/primefaces.ajax.configuration.html#async) | Flag to define whether request should go in ajax queue or not, default is false.
[global](../jsdocs/interfaces/primefaces.ajax.configuration.html#global) | Flag to define if p:ajaxStatus should be triggered or not, default is true.
[update](../jsdocs/interfaces/primefaces.ajax.configuration.html#update) | Component(s) to update with ajax.
[process](../jsdocs/interfaces/primefaces.ajax.configuration.html#process) | Component(s) to process in partial request.
[source](../jsdocs/interfaces/primefaces.ajax.configuration.html#source) | Client id of the source component causing the request.
[params](../jsdocs/interfaces/primefaces.ajax.configuration.html#params) | Additional parameters to send in ajax request.
[onstart()](../jsdocs/interfaces/primefaces.ajax.configuration.html#onstart) | Javascript callback to process before sending the ajax request, return false to cancel the request.
[onsuccess(data, status, xhr)](../jsdocs/interfaces/primefaces.ajax.configuration.html#onsuccess) | Javascript callback to process when ajax request returns with success code. Takes three arguments, xml response, status code and xmlhttprequest.
[onerror(xhr, status, error)](../jsdocs/interfaces/primefaces.ajax.configuration.html#onerror) | Javascript callback to process when ajax request fails. Takes three arguments, xmlhttprequest, status string and exception thrown if any.
[oncomplete(xhr, status, args, data)](../jsdocs/interfaces/primefaces.ajax.configuration.html#oncomplete) | Javascript callback to process when ajax request completes. Takes three arguments, xmlhttprequest, status string and optional arguments provided by PrimeFaces.current() API.


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

We highly recommend using [p:remoteCommand](/components/remotecommand) instead of low level javascript API as it generates
the same with much less effort and less possibility to do an error.

## PrimeFaces.ajax.Response
[PrimeFaces.ajax.Response.handle()](../jsdocs/interfaces/primefaces.ajax.response.html#handle) updates the specified
components if any and synchronizes the client side JSF state. DOM updates are implemented using jQuery which uses a very
fast algorithm.

## Abort
Use the [abort API](../jsdocs/interfaces/primefaces.ajax.queue.html#abortall) in case you'd like to cancel all the ongoing requests:

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

### Events:

| Name | Arguments | Description |
| --- | --- | --- |
| [pfAjaxStart](../jsdocs/interfaces/jquery.typetotriggeredeventmap.html#pfajaxstart) | | Executed when entering the AJAX lifecycle, before preparing all settings for the XHR.
| [pfAjaxSend](../jsdocs/interfaces/jquery.typetotriggeredeventmap.html#pfajaxsend) | xhr, settings | Executed before sending the XHR.
| [pfAjaxError](../jsdocs/interfaces/jquery.typetotriggeredeventmap.html#pfajaxerror) | xhr, settings, error | Executed when sending the request or receiving the response failed.
| [pfAjaxSuccess](../jsdocs/interfaces/jquery.typetotriggeredeventmap.html#pfajaxsuccess) | xhr, settings | Executed after the response was received but before processing the response / replace DOM elements.
| [pfAjaxComplete](../jsdocs/interfaces/jquery.typetotriggeredeventmap.html#pfajaxcomplete) | xhr, settings | Executed after the AJAX lifecycle has been completed, independent of success or error.

