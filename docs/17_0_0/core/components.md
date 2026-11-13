# Client Side Components

The client side part of a component (its JavaScript object) has various responsibilities like progressive enhancement of the markup, 
communication with the server side via AJAX and more. For example, it can style the component in the DOM instead of rendering it with the markup, 
this helps reducing page size and reduce CPU cycles.

Most of the PrimeFaces components have the same structure, HTML markup and a script. Script is actually a JavaScript object that belongs to `PrimeFaces.component.*`  namespace.

## Accessing a component

The `ref` attribute gives the client side component a name, which is used to access it via `PF('name')`:

```xhtml
<p:dialog ref="dlg" header="Header Text">...</p:dialog>
<p:commandButton value="Show" type="button" onclick="PF('dlg').show()" />
```

Without `ref` the name is generated from the client id (`pf_` + client id, `:` and `-` replaced with `_`).
`widgetVar` is the deprecated former name of `ref`, it still works; if both are set, `ref` is used.

## Functions and Properties

**Functions**

| Function | Description |
| --- | --- |
| init() | Called when constructing the component to initialize it.
| refresh() | Refreshes the component after configuration changes such as a AJAX update.
| destroy() | Destroys the component which removes the association to the DOM element.
| getJQ() | A wrapper function to the jQuery object, as if selecting with `$()`.

**Properties**

| Property | Description |
| --- | --- |
| id | The generated ID of the component. If the component is a child of a naming container (ex. form) it would be `formId:componentId`.
| cfg | A JSON object of configuration. This object define exactly how the component should behave.
| jq | The main jQuery object. `getJQ()` would return it.
| jqId | The fully qualified jQuery Id to be used inside `$()` function or `jQuery()`  (ex. `#formId\:componentId` ).

## Component Lifecycle Events

For any component you can attach client side Javascript to the component lifecycle events for 
`PreConstruct`, `PostConstruct`, `PostRefresh`, or `PreDestroy` of the component.
Simply assign the correct `f:attribute` to add your script.

| Event | Attribute | Parameters | Execution |
| --- | --- | --- | --- |
| PreConstruct | `clientPreConstruct` | cfg | Always called either on page load or Ajax update of component before the component is created.
| PostConstruct | `clientPostConstruct` | component | Always called either on page load or Ajax update of component after the component is created.
| PostRefresh | `clientPostRefresh` | component | Only called after Ajax update of the component
| PreDestroy | `clientPreDestroy` | component | Only called before the component is actually being destroyed

The callbacks receive the component also as parameter `widget` (former name). The former attribute names
`widgetPreConstruct`, `widgetPostConstruct`, `widgetPostRefresh` and `widgetPreDestroy` still work but are deprecated.

```xhtml
<p:inputText id="name" ref="name" value="#{basicView.text}">
    <f:attribute name="clientPreConstruct" value="alert('Component initialized with config: ' + cfg.id);" />  
    <f:attribute name="clientPostConstruct" value="alert('Component created with id: ' + component.id);" />  
    <f:attribute name="clientPostRefresh" value="alert('Component refreshed! ' + component.id);" />  
    <f:attribute name="clientPreDestroy" value="alert('Component destroyed! ' + component.id);" />  
</p:inputText>
```
