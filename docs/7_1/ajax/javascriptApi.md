# AJAX Javascript API

PrimeFaces AJAX Javascript API is powered by jQuery and optimized for JSF.
The whole API consists of three properly namespaced simple javascript functions.

## PrimeFaces.ajax.Request
Sends ajax requests that execute JSF lifecycle and retrieve partial output. Function signature is as
follows;

```js
PrimeFaces.ajax.Request.handle(cfg);
```

**Configuration Options**

| Option | Description |
| --- | --- |
formId | Id of the form element to serialize, if not defined parent form of source is used.
async | Flag to define whether request should go in ajax queue or not, default is false.
global | Flag to define if p:ajaxStatus should be triggered or not, default is true.
update | Component(s) to update with ajax.
process | Component(s) to process in partial request.
source | Client id of the source component causing the request.
params | Additional parameters to send in ajax request.
onstart() | Javascript callback to process before sending the ajax request, return false to cancel the request.
onsuccess(data, status, xhr) | Javascript callback to process when ajax request returns with success code. Takes three arguments, xml response, status code and xmlhttprequest.
onerror(xhr, status, error) | Javascript callback to process when ajax request fails. Takes three arguments, xmlhttprequest, status string and exception thrown if any.
oncomplete(xhr, status, args, data) | Javascript callback to process when ajax request completes. Takes three arguments, xmlhttprequest, status string and optional arguments provided by PrimeFaces.current() API.


### Examples
Suppose you have a JSF page called _createUser_ with a simple form and some input components.

```xhtml
<h:form id="userForm">
    <h:inputText id="username" value="#{userBean.user.name}" />
    ... More components
</h:form>
```
You can post all the information with ajax using;

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

We highly recommend using p:remoteComponent instead of low level javascript API as it generates
the same with much less effort and less possibility to do an error.

## PrimeFaces.ajax.Response
PrimeFaces.ajax.Response.handle() updates the specified components if any and synchronizes the
client side JSF state. DOM updates are implemented using jQuery which uses a very fast algorithm.

## Abort
Use the abort API in case you'd like to cancel all the ongoing requests;

```js
PrimeFaces.ajax.Queue.abortAll()
```