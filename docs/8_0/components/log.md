# Log

Log component is a visual console to display logs on JSF pages.

## Info

| Name | Value |
| --- | --- |
| Tag | log
| Component Class | org.primefaces.component.log.Log
| Component Type | org.primefaces.component.Log
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.LogRenderer
| Renderer Class | org.primefaces.component.log.LogRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean

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
<script type=”text/javascript”>
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
