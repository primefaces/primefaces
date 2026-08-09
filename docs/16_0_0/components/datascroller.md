# DataScroller

DataScroller displays a collection of data with on demand loading using scrolling.

## Getting started with the DataScroller
DataScroller requires a collection of data to display, when the page is scrolled down, datascroller
will do a request with ajax to fetch the new chunk of data and append them at the bottom.

```xhtml
<p:dataScroller value="#{carBean.cars}" var="car" chunkSize="10">
    #{car.manufacturer}
    //more content
</p:dataScroller>
```
## Scroll Mode
Default element whose scrollbar is monitored is page itself, _mode_ option is used to customize the
scroll target. Two possible options for the mode attribute are “document” and “inline”. Document
mode is default and _inline_ mode listens to the scroll event of the datascroller container itself.


```xhtml
<p:dataScroller value="#{carBean.cars}" var="car" mode="inline" chunkSize="10">
    #{car.manufacturer}
    //more content
</p:dataScroller>
```
## Loader
In case of scrolling, a UI element such as button can defined as the loader so that new data is loaded
when the loader element is clicked. Loader component is defined using `loader` facet.

```xhtml
<p:dataScroller value="#{carBean.cars}" var="car" mode="inline" chunkSize="10">
    #{car.manufacturer}
    //more content
    <f:facet name="loader">
        <p:commandButton type="button" value="View More" />
    </f:facet>
</p:dataScroller>
```

## Loading
The `loading` facet can be used to show UI while data is being loaded.

```xhtml
<p:dataScroller ...>
    <f:facet name="loading">
        <p:skeleton style="width:300px;height:200px"/>
    </f:facet>
    <img style="width:300px;height:200px" .../>
</p:dataScroller>
```

## Lazy Loading
Lazy loading is enabled by enabling the lazy attribute and providing a LazyDataModel instance as
the value. Refer to lazy load example in DataTable for an example about LazyDataModel.

```xhtml
<p:dataScroller value="#{carBean.lazyModel}" var="car" lazy="true">
    #{car.manufacturer}
    //more content
</p:dataScroller>
```
## Header
Header of the component is defined using header facet.

```xhtml
<p:dataScroller value="#{carBean.lazyModel}" var="car">
    <f:facet name="header">Cars</f:facet>
    #{car.manufacturer}
    //more content
</p:dataScroller>
```

## Client Side API
Widget: _PrimeFaces.widget.DataScroller_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| load() | - | void | Loads the next chunk.

## Skinning
DataScroller resides in a main div container which style and styleClass attributes apply. Following
is the list of structural style classes;

| Class | Applies |
| --- | --- |
| .ui-datascroller | Main container element
| .ui-datascroller-inline | Main container element in inline mode
| .ui-datalist-header | Header element
| .ui-datalist-content | Content element
| .ui-datascroller-list | List element container
| .ui-datascroller-item | Container of each item in the list
| .ui-datascroller-loader | Container of custom loader element.
| .ui-datascroller-loading | Built-in load status indicator

As skinning style classes are global, see the main theming section for more information.