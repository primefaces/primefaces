# DataList

DataList presents a collection of data in list layout with several display types.

> DataGrid is deprecated, use  [DataView](/components/dataview.md) instead.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.DataList.html)

## Getting started with the DataList
Since DataList is a data iteration component, it renders it’s children for each data represented with
_var_ option. See itemType section for more information about the possible values.

```xhtml
<p:dataList value="#{carBean.cars}" var="car" itemType="disc">
    #{car.manufacturer}, #{car.year}
</p:dataList>
```
## Ordered Lists
DataList displays the data in unordered format by default, if you’d like to use ordered display set
_type_ option to "ordered".


```xhtml
<p:dataList value="#{carBean.cars}" var="car" type="ordered">
    #{car.manufacturer}, #{car.year}
</p:dataList>
```
## Item Type
_itemType_ defines the bullet type of each item. For ordered lists, in addition to commonly used
_decimal_ type, following item types are available;

- A
- a
- i

And for unordered lists, available values are;

- disc
- circle
- square

## Definition Lists
Third type of dataList is definition lists that display inline description for each item, to use
definition list set _type_ option to _"definition"_. Detail content is provided with the facet called
_"description"_.

```xhtml
<p:dataList value="#{carBean.cars}" var="car" type="definition">
    Model: #{car.model}, Year: #{car.year}
    <f:facet name="description">
        <p:graphicImage value="/images/cars/#{car.manufacturer}.jpg"/>
    </f:facet>
</p:dataList>
```
## Ajax Pagination
DataList has a built-in paginator that is enabled by setting paginator option to true.

```xhtml
<p:dataList value="#{carBean.cars}" var="car" paginator="true" rows="10">
    #{car.manufacturer}, #{car.year}
</p:dataList>
```
Pagination configuration and usage is same as dataGrid, see pagination section in dataGrid
documentation for more information and examples.

```xhtml
<p:dataList var="car" value="#{carBean.model}">
    <p:ajax event="page" update="anothercomponent" />
    //content
</p:dataList>
```

## Selecting Data
Data selection can be implemented same as in dataGrid, see selecting data section in dataGrid
documentation for more information and an example.

## Client Side API
Widget: _PrimeFaces.widget.DataList_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| getPaginator() | - | Paginator | Returns the paginator widget.

## Skinning
DataList resides in a main div container which style and styleClass attributes apply. Following is the
list of structural style classes;

| Class | Applies | 
| --- | --- | 
| .ui-datalist | Main container element
| .ui-datalist-content | Content container
| .ui-datalist-data | Data container
| .ui-datalist-item | Each item in list

As skinning style classes are global, see the main theming section for more information.

**Tips**:

- DataList supports lazy loading data via LazyDataModel, see DataTable lazy loading section.
- If you need full control over list type markup, set type to “none”. With this setting, datalist does
    not render item tags like li and behaves like ui:repeat.
- DataList provides two facets named _header_ and _footer_ that you can use to provide custom content
    at these locations.