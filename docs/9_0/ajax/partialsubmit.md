# Partial Submit

Core JSF Ajax implementation and by default PrimeFaces serializes the whole form to build the
post data in ajax requests so the same data is posted just like in a non-ajax request. 

This has a downside in large views where you only need to process/execute a minor part of the view.
Assume you have a form with 100 input fields, there is an input field with ajaxbehavior attached processing
only itself(@this) and then updates another field onblur. Although only a particular input field is
processed, whole form data will be posted with the unnecessary information that would be ignored
during server side processing but consume resources.

PrimeFaces provides partialSubmit feature to reduce the network traffic and computing on client
side. When partialSubmit is enabled, only data of components that will be partially processed on the
server side are serialized. By default partialSubmit is disabled and you can enable it globally using a
context parameter.

```xml
<context-param>
    <param-name>primefaces.SUBMIT</param-name>
    <param-value>partial</param-value>
</context-param>
```
Components like buttons and behaviors like p:ajax are equipped with partialSubmit option so you
can override the global setting per component.

```xhtml
<p:commandButton value="Submit" partialSubmit="true|false" />
```