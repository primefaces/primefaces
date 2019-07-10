# OutputPanel

OutputPanel is a panel component with the ability to deferred loading.

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
style | null | String | Style of the html container element
styleClass | null | String | StyleClass of the html container element
layout | block | String | Shortcut for the css display property, valid values are block(default) and inline.
deferred | false | Boolean | Deferred mode loads the contents after page load to speed up page load.
deferredMode | load | String | Defines deferred loading mode, valid values are "load" (after page load) and "visible" (once the panel is visible on scroll).

OutputPanel has various uses cases such as placeholder, deferred loading and auto update.

## PlaceHolder
When a JSF component is not rendered, no markup is rendered so for components with conditional
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

## Layout
OutputPanel has two layout modes;

- block (default): Renders a div
- inline: Renders a span

## AutoUpdate (Deprecated)
When auto update is enabled, outputPanel component is updated with each ajax request
automatically. Note: Use p:autopUpdate instead.

## Skinning
_style_ and _styleClass_ attributes are used to style the outputPanel, by default _.ui-outputpanel_ css class
is added to element and _.ui-outputpanel-loading_ when content is loading in deferred loading case.

