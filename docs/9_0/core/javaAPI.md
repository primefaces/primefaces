# Java API

## PrimeFaces.current()

PrimeFaces.current() is a simple utility that provides useful goodies such as adding parameters to
ajax callback functions. It's available in both ajax and non-ajax requests. Scope
of the instance is thread local.

Instance can be obtained similarly to the FacesContext or CDI.

```java
PrimeFaces instance = PrimeFaces.current();
```

### Methods

| Method | Description |
| --- | --- |
executeScript(String script) | Executes script after ajax request completes or on page load.
isAjaxRequest() | Returns a boolean value if current request is a PrimeFaces ajax request.
scrollTo(String clientId) | Scrolls to the component with given clientId after ajax request completes.
focus(String expression) | Focus the input(s) targeted by the given search expression.
resetInputs(Collection<String>/String... expressions) | Resets all UIInput targeted by the search expression(s).
multiViewState().clearAll() | Removes all multiViewState within the current session.
multiViewState().clearAll(String viewId) | Removes all multiViewState in specific view within the current session.
multiViewState().clear(String viewId, Consumer<String> clientIdConsumer) | Removes all multiViewState in specific view within the current session.
multiViewState().clear(String viewId, String clientId) | Removes multiViewState of a component in specific view within the current session.
multiViewState().get(String viewId, String clientId, boolean create, Supplier<T> supplier) | Gets multiview state bean attached to a component in a specific view.
ajax().addCallBackParam(String name, Object value) | Adds parameters to ajax callbacks like oncomplete.
ajax().update(Collection<String>/String... expressions); | Specifies component(s) to update at runtime.


### Callback Parameters

There may be cases where you need values from backing beans in ajax callbacks. Callback
parameters are serialized to JSON and provided as an argument in ajax callbacks for this.

```xhtml
<p:commandButton action="#{bean.validate}" oncomplete="handleComplete(xhr, status, args, data)" />
```
```java
public void validate() {
    //isValid = calculate isValid
    PrimeFaces.current().ajax().addCallbackParam("isValid", true or false);
}
```
_isValid_ parameter will be available in handleComplete callback as;

```js
<script type="text/javascript">
    function handleComplete(xhr, status, args) {
        var isValid = args.isValid;
    }
</script>
```

You can add as many callback parameters as you want with addCallbackParam API. Each parameter
is serialized as JSON and accessible through args parameter so pojos are also supported just like
primitive values. Following example sends a pojo called _User_ that has properties like firstname and
lastname to the client in addition to _isValid_ boolean value.

```java
public void validate() {
    //isValid = calculate isValid
    PrimeFaces.current().ajax().addCallbackParam("isValid", true or false);
    PrimeFaces.current().ajax().addCallbackParam("user", user);
}
```
```js
<script type="text/javascript">
    function handleComplete(xhr, status, args) {
        var firstname = args.user.firstname;
        var lastname = args.user.lastname;
    }
</script>
```
By default _validationFailed_ callback parameter is added implicitly if validation fails.



### Execute Javascript

PrimeFaces provides a way to execute javascript when the ajax request completes, this
approach is easier compared to passing callback params and execute conditional javascript.
Example below hides the dialog when ajax request completes;

```java
public void save() {
    PrimeFaces.current().executeScript(“dialog.hide()”);
}
```