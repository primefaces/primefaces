# Client Window

Since 2.2, the Faces specification contains a API to identify a browser tab (client-window) on the server side.
This allows for separating state not only per session but also on a per client-window base.

In PrimeFaces this feature can be used to store the state of MultiViewState-aware components (see [MultiViewState](core/multiviewstate.md) for details). 
Faces 4.0 also contains a new `@ClientWindowScoped`, which enables you to store your data per browser tab.

It can be activated via web.xml:
```xml
<context-param>
    <param-name>javax.faces.CLIENT_WINDOW_MODE</param-name>
    <param-value>url</param-value>
</context-param>
```

## PrimeFaces ClientWindow mode

PrimeFaces provides a improved version of the Faces `url` `ClientWindow`, which is heavily inspired by Apache DeltaSpile `LAZY` mode:
* JSF does NOT append the url param on initial redirect, so F5 creates a new windowId
* JSF does NOT validate if the tab was initially opened with another windowId
* it uses sessionStorage to save and validate the windowId


### Configuration
PrimeFaces `ClientWindow` can be activated in `faces-config.xml`:
```xml
<faces-config>
    ...
    <factory>
        <client-window-factory>org.primefaces.clientwindow.PrimeClientWindowFactory</client-window-factory>
    </factory>
</faces-config>
```

As it´s a replacement for the standard JSF-mechanism, make sure you dont have a `javax.faces.CLIENT_WINDOW_MODE` in your web.xml or configured to `none`.


**Mojarra:**  
For older Mojarra versions you must use the LifeCycleFactory because of this issue: https://github.com/eclipse-ee4j/mojarra/issues/5297
```xml
<faces-config>
    ...
    <factory>
        <lifecycle-factory>org.primefaces.clientwindow.PrimeClientWindowLifecycleFactory</lifecycle-factory>
    </factory>
</faces-config>
```








