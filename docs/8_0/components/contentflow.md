# ContentFlow

ContentFlow is a horizontal content gallery component with a slide animation.

## Info

| Name | Value |
| --- | --- |
| Tag | contentFlow
| Component Class | org.primefaces.component.contentflow.ContentFlow
| Component Type | org.primefaces.component.ContentFlow
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ContentFlowRenderer
| Renderer Class | org.primefaces.component.contentflow.ContentFlowRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| widgetVar | null | String | Name of the client side widget.
| value | null | String | Collection of items to display.
| var | null | String | Name of the iterator to display an item.
| style | null | String | Inline style of the component.
| styleClass | null | String | Style class of the component.


## Getting Started with ContentFlow
ContentFlow requires content as children that can either be defined dynamically using iteration or
one by one. Each item must have the content style class applied as well.

#### Static Images

```xhtml
<p:contentFlow>
    <p:graphicImage value="/images/photo1.jpg" styleClass="content" />
    <p:graphicImage value="/images/photo2.jpg" styleClass="content" />
    <p:graphicImage value="/images/photo2.jpg" styleClass="content" />
</p:contentFlow>
```
#### Dynamic Images

```xhtml
<p:contentFlow var="image" value="#{bean.images}">
    <p:graphicImage value="/images/#{image}" styleClass="content" />
</p:contentFlow>
```
## Caption
To present a caption along with an item, embed a div with "caption" style class inside.

```xhtml
<p:contentFlow var="image" value="#{bean.images}">
    <p:graphicImage value="#{image.path}" styleClass="content" />
    <div class="caption">#{image.title}</div>
</p:contentFlow>
```
## Skinning
ContentFlow resides in a container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-contentflow | Container element.
| .flow | Container of item list
| .item | Item container
| .caption | Caption element