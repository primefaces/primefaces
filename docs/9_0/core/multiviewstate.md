# MultiViewState

MultiViewState feature enables components to maintain their state across pages by setting multiViewState attribute to true. 

Following components support MultiViewState:

- [DataTable](/components/datatable.md)
- [DataList](/components/datalist.md)
- [DataGrid](/components/datagrid.md)
- [DataView](/components/dataview.md)
- [TabView](/components/tabview.md)

Per default MultiViewState is stored per session. As an alternative it can bis stored per client window to support multi-window usage.

This can be configured in web.xml
```xml
<context-param>
    <param-name>primefaces.MULTI_VIEW_STATE_STORE</param-name>
    <param-value>client-window</param-value>
</context-param>
```

For multi-window usage PrimeFaces also must be configured to support Prime Client Window mode in faces-config.xml.
```xml
<faces-config>
    ...
    <factory>
        <lifecycle-factory>org.primefaces.clientwindow.PrimeClientWindowLifecycleFactory</lifecycle-factory>
    </factory>
</faces-config>
```

See [Client Window](client.md) for details.
