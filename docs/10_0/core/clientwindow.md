# Client Window

Prime Client Window provides a improved version of JSF´s (version 2.2 and up ) CLIENT_WINDOW_MODE.
This allows for separating state not only per session but also on a per client-window-base.
(Or call it per-browser-tab-base.)


Prime Client Window is activated in faces-config.xml
```xml
<faces-config>
    ...
    <factory>
        <lifecycle-factory>org.primefaces.clientwindow.PrimeClientWindowLifecycleFactory</lifecycle-factory>
    </factory>
</faces-config>
```

It´s a replacement for the standard JSF-mechanism activated in web.xml. So only add PrimeClientWindowLifecycleFactory
but not add javax.faces.CLIENT_WINDOW_MODE!
```xml
<context-param>
    <param-name>javax.faces.CLIENT_WINDOW_MODE</param-name>
    <param-value>url</param-value>
</context-param>
```

All PrimeFaces MultiViewState-aware components support client-window-mode.
See [MultiViewState](multiviewstate.md) for details.

Prime Client Window adds jfwid-parameter to all url´s rendered by JSF and PrimeFaces.
Prime Client Window offers improved handling compared to JSF´s built in mechanism in following points:
* JSF doesnt append the url param on initial redirect, so f5 creates a new windowId
* JSF doesnt validate if the tab was initialy openend with another windowId
* it uses sessionStorage to save and validate the windowId
* the workflow is the same as Apache DeltaSpike lazy mode

## Requirements
1. JSF 2.2 and up
2. JSF - implementations
   1. Mojarra 2.3 and up
   2. MyFaces 2.2.13 (and up), 2.3.7 (and up), 3.0 and up


## Further Notes
JSF 4.0 also may add ClientWindowScoped OOTB - see https://github.com/eclipse-ee4j/faces-api/issues/1509