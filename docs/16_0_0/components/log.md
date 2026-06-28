# Log

Log component is a visual console to display logs on Jakarta Faces pages.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Log-1.html)

## Getting started with Log
Log component is used simply as adding the component to the page.

```xhtml
<p:log />
```

## Log API
PrimeFaces uses client side log apis internally, for example you can use log component to see
details of an ajax request. Log API is also available via global PrimeFaces object in case you’d like
to use the log component to display your logs.

```js
<script type="text/javascript">
    PrimeFaces.info(‘Info message’);
    PrimeFaces.debug(‘Debug message’);
    PrimeFaces.warn(‘Warning message’);
    PrimeFaces.error(‘Error message’);
</script>
```
If project stage is development, log messages are also available at browser console.

## Client Side API
Widget: _PrimeFaces.widget.Log_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
show() | - | void | Show the container element.
hide() | - | void | Hides the container element.
