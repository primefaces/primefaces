# DataScroller

DataScroller displays a collection of data with on demand loading using scrolling.

## Info

| Name | Value |
| --- | --- |
| Tag | dataScroller
| Component Class | org.primefaces.component.datascroller.DataScroller
| Component Type | org.primefaces.component.DataScroller
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DataScrollerRenderer
| Renderer Class | org.primefaces.component.datascroller.DataScrollerRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| value | null | Object | Data to display.
| var | null | String | Name of the request-scoped variable used to refer each data.
| rows | null | Integer | Number of rows to display per page.
| first | 0 | Integer | Index of the first row to be displayed
| widgetVar | null | String | Name of the client side widget.
| style | null | String | Inline style of the main container.
| styleClass | null | String | Style class of the main container.
| chunkSize | 0 | Integer | Number of items to fetch in each load.
| rowIndexVar | null | String | Name of iterator to refer each row index.
| mode | document | String | Defines the target to listen for scroll event, valid values are "document" (default) and "inline".
| scrollHeight | null | String | Defines pixel height of the viewport in inline mode.
| lazy | false | Boolean | Defines if lazy loading is enabled for the data component.
| buffer | 10 | Integer | Percentage height of the buffer between the bottom of the page and the scroll position to initiate the load for the new chunk. Value is defined in integer and default is 10 meaning load would happen after 90% of the viewport is scrolled down.

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
when the loader element is clicked. Loader component is defined using "loader" facet.

```xhtml
<p:dataScroller value="#{carBean.cars}" var="car" mode="inline" chunkSize="10">
    #{car.manufacturer}
    //more content
    <f:facet name="loader">
        <p:commandButton type="button" value="View More" />
    </f:facet>
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