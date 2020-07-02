# MultiViewState

MultiViewState feature enables components to maintain their state across pages by setting multiViewState attribute to true. 

Following components support MultiViewState:

- [DataTable](/components/datatable.md)
- [DataList](/components/datalist.md)
- [DataGrid](/components/datagrid.md)
- [DataView](/components/dataview.md)
- [TabView](/components/tabview.md)

Per default MultiViewState is stored per session. As an alternative it can bis stored per client window to support multi-window usage.

This can be configured via
```xml
<context-param>
    <param-name>primefaces.MULTI_VIEW_STATE_STORE</param-name>
    <param-value>client-window</param-value>
</context-param>
```

For multi-window usage JSF (version 2.2 and up) also must be configured to support client window mode.
```xml
<context-param>
    <param-name>javax.faces.CLIENT_WINDOW_MODE</param-name>
    <param-value>url</param-value>
</context-param>
```

This adds jfwid-parameter to all url´s rendererd by JSF and PrimeFaces.
Be aware, this is a rather simplistic and limited mechanism. PrimeFaces may come with a
better solution some time into the future. (Maybe a mechanism inspired by Apache DeltaSpike.)
