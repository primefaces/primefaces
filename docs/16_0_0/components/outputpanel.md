# OutputPanel

OutputPanel is a panel component with the ability to deferred loading, which is also the key feature of this component.

**NOTE**
If you use the OutputPanel just as simple placeholder, it's better to use another approach since Jakarta Faces 2.2:
```xhtml
<div faces:id="..." faces:rendered="#{...}">...</div>
```

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.OutputPanel-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | outputPanel
| Component Class | org.primefaces.component.outputpanel.OutputPanel
| Component Type | org.primefaces.component.OutputPanel
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.OutputPanelRenderer
| Renderer Class | org.primefaces.component.output.OutputPanelRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
style | null | String | Style of the HTML container element
styleClass | null | String | StyleClass of the HTML container element
layout | block | String | Shortcut for the css display property, valid values are block(default) and inline.
deferred | false | Boolean | Deferred mode loads the contents after page load to speed up page load.
deferredMode | load | String | Defines deferred loading mode, valid values are "load" (after page load) and "visible" (once the panel is visible on scroll).
loaded | false | Boolean | Indicates that deferred loading is not needed.

OutputPanel has various uses cases such as placeholder, deferred loading and auto update.

## PlaceHolder
When a Jakarta Faces component is not rendered, no markup is rendered so for components with conditional
rendering, regular update mechanism may not work since the markup to update on page does not
exist. OutputPanel is useful in this case to be used as a placeholder.

Suppose the rendered condition on bean is false when page if loaded initially and search method on
bean sets the condition to be true meaning datatable will be rendered after a page submit. The
problem is although partial output is generated, the markup on page cannot be updated since it
doesn’t exist.

```xhtml
<p:dataTable id="tbl" rendered="#{bean.condition}" ...>
    //columns
</p:dataTable>
<p:commandButton update="tbl" action="#{bean.search}" />
```
Solution is to use the outputPanel as a placeHolder.

```xhtml
<p:outputPanel id="out">
    <p:dataTable id="tbl" rendered="#{bean.condition}" ...>
        //columns
    </p:dataTable>
</p:outputPanel>
<p:commandButton update="out" action="#{bean.list}" />
```
**Note** that you won’t need an outputPanel if commandButton has no update attribute specified, in
this case parent form will be updated partially implicitly making an outputPanel use obselete.

## Deferred Loading
When this feature option is enabled, content of panel is not loaded along with the page but loaded
after the page on demand. Initially panel displays a loading animation after page load to indicate
more content is coming up and displays content with ajax update. Using _deferredMode_ option, it is
possible to load contents not just after page load (default mode) but when it becomes visible on
page scroll as well. This feature is very useful to increase page load performance, assume you have
one part of the page that has components dealing with backend and taking time, with deferred mode
on, rest of the page is loaded instantly and time taking process is loaded afterwards with ajax.

### Loading facet
Deferred loading can be used with the `loading` facet to show UI while the data is being loaded. This
is normally combined with the load ajax event with a listener which loads the data you want to show
in your panel. For example:

```xhtml
<p:outputPanel deferred="true">
    <p:ajax event="load" listener="#{bean.loadData}"/>
    <f:facet name="loading">
        <p:skeleton width="200px" height="21px" class="p-mb-4"/>
        <p:skeleton width="100%" height="63px"/>
    </f:facet>
    <h5>#{bean.data.title}</h5>
    <p>#{bean.data.body}</p>
</p:outputPanel>
```

### Ajax requests
By default deferred loading does not work with Ajax requests. As a solution you can use the `loaded` boolean
attribute to enforce loading (even in Ajax requests). Normally you would use an expression here checking if your data
is empty. For example:

```xhtml
<p:outputPanel deferred="true" loaded="#{not empty bean.data}">
    <p:ajax event="load" listener="#{bean.loadData}"/>
    ...
</p:outputPanel>
```

## Layout
OutputPanel has two layout modes;

- block (default): Renders a div
- inline: Renders a span

## Skinning
_style_ and _styleClass_ attributes are used to style the outputPanel, by default _.ui-outputpanel_ css class
is added to element and _.ui-outputpanel-loading_ when content is loading in deferred loading case.

