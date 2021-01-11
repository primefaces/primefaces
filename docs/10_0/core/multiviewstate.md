# MultiViewState

MultiViewState feature enables components to maintain their state across pages by setting multiViewState attribute to true. 

Following components support MultiViewState:

- [DataTable](/components/datatable.md)
- [DataList](/components/datalist.md)
- [DataGrid](/components/datagrid.md)
- [DataView](/components/dataview.md)
- [TabView](/components/tabview.md)

Per default MultiViewState is stored per session. As an alternative it can be stored per client window to support multi-window usage.

This can be configured in web.xml
```xml
<context-param>
    <param-name>primefaces.MULTI_VIEW_STATE_STORE</param-name>
    <param-value>client-window</param-value>
</context-param>
```

## Requirements

For multi-window usage you need an implementation that adds jfwid-parameter to all request.

For this you can choose between: 

### Option 1 - Prime Client Window
Enabled in faces-config.xml.
```xml
<faces-config>
    ...
    <factory>
        <lifecycle-factory>org.primefaces.clientwindow.PrimeClientWindowLifecycleFactory</lifecycle-factory>
    </factory>
</faces-config>
```

See [Client Window](client.md) for details.

### Option 2 - JSF Client Window Mode (requires JSF 2.2 and up)
Enabled in web.xml
```xml
<context-param>
    <param-name>javax.faces.CLIENT_WINDOW_MODE</param-name>
    <param-value>url</param-value>
</context-param>
```
### Option 3 - Apache DeltaSpike Multi-Window Handling
see https://deltaspike.apache.org/documentation/jsf.html#Multi-WindowHandling 