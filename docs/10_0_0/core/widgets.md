# Widgets

Widgets are the client side binding of a component that has various responsibilities like progressive enhancement of the markup, 
communication with the server side via AJAX and more. For example, it can style the component in the DOM instead of rendering it with the markup, 
this helps reducing page size and reduce CPU cycles.

Most of the PrimeFaces components have the same structure, HTML markup and a script. Script is actually a JavaScript object that belongs to `PrimeFaces.widget.*`  namespace.

## Functions and Properties

**Functions**

| Function | Description |
| --- | --- |
| init() | Called when constructing the component to initialize it.
| refresh() | Refreshes the component after configuration changes such as a AJAX update.
| destroy() | Destroys the widget which removes the association to the DOM element.
| getJQ() | A wrapper funtion to the jQuery object, as if selecting with `$()`.

**Properties**

| Property | Description |
| --- | --- |
| id | The generated ID of the component. If the component is a child of a naming container (ex. form) it would be `formId:componentId`.
| cfg | A JSON object of configuration. This object define exactly how the component should behave.
| jq | The main jQuery object. `getJQ()` would return it.
| jqId | The fully qualified jQuery Id to be used inside `$()` function or `jQuery()`  (ex. `#formId\:componentId` ).

## Widget Lifecycle Events

For any component you can attach client side Javascript to the widget lifecycle events for 
`PostConstruct`, `PostRefresh`, or `PreDestroy` of the widget.
Simply assign the correct `f:attribute` to add your script.

| Event | Execution |
| --- | --- |
| PostConstruct | Always called either on page load or Ajax update of component
| PostRefresh | Only called after Ajax update of the component
| PreDestroy | Only called before the widget is actually being destroyed

```xhtml
<p:inputText id="name" widgetVar="wgtName" value="#{basicView.text}">
    <f:attribute name="widgetPostConstruct" value="alert('Widget created!');" />  
    <f:attribute name="widgetPostRefresh" value="alert('Widget refreshed!');" />  
    <f:attribute name="widgetPreDestroy" value="alert('Widget destroyed!');" />  
</p:inputText>
```