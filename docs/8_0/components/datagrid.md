# DataGrid

DataGrid displays a collection of data in a grid layout.

> DataGrid is deprecated, use  [DataView](/components/dataview.md) instead.

## Info

| Name | Value |
| --- | --- |
| Tag | dataGrid
| Component Class | org.primefaces.component.datagrid.DataGrid
| Component Type | org.primefaces.component.DataGrid
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.DataGridRenderer
| Renderer Class | org.primefaces.component.datagrid.DataGridRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
value | null | Object | Data to display.
var | null | String | Name of the request-scoped variable used torefer each data.
rows | null | Integer | Number of rows to display per page.
first | 0 | Integer | Index of the first row to be displayed
widgetVar | null | String | Name of the client side widget.
columns | 3 | Integer | Number of columns in grid.
paginator | false | Boolean |  Enables pagination.
paginatorTemplate | null | String | Template of the paginator.
rowsPerPageTemplate | null | String | Template of the rowsPerPage dropdown.
currentPageReportTemplate | null | String | Template of the currentPageReport UI.
pageLinks | 10 | Integer | Maximum number of page links to display.
paginatorPosition | both | String | Position of the paginator.
paginatorAlwaysVisible | true | Boolean | Defines if paginator should be hidden if total data count is less than number of rows per page.
style | null | String | Inline style of the datagrid.
styleClass | null | String | Style class of the datagrid.
rowIndexVar | null | String | Name of the iterator to refer each row index.
lazy | false | Boolean | Defines if lazy loading is enabled for the data component. 
emptyMessage | No records found. | String | Text to display when there is no data to display.
layout | tabular | String | Layout approach to use, valid values are "tabular" and "grid" for responsive grid.

## Getting started with the DataGrid
A list of cars will be used throughout the datagrid, datalist and datatable examples.

```java
public class Car {
    private String model;
    private int year;
    private String manufacturer;
    private String color;
    ...
}
```

The code for CarBean that would be used to bind the datagrid to the car list.

```java
public class CarBean {
    private List<Car> cars;

    public CarBean() {
        cars = new ArrayList<Car>();
        cars.add(new Car("myModel",2005,"ManufacturerX","blue"));
        //add more cars
    }
    public List<Car> getCars() {
        return cars;
    }
}
```
```xhtml
<p:dataGrid var="car" value="#{carBean.cars}" columns="3" rows="12">
    <p:column>
        <p:panel header="#{car.model}">
            <h:panelGrid columns="1">
                <p:graphicImage value="/images/cars/#{car.manufacturer}.jpg"/>
                <h:outputText value="#{car.year}" />
            </h:panelGrid>
        </p:panel>
    </p:column>
</p:dataGrid>
```
This datagrid has 3 columns and 12 rows. As datagrid extends from standard UIData, rows
correspond to the number of data to display not the number of rows to render so the actual number
of rows to render is rows/columns = 4. As a result datagrid is displayed as;


## Ajax Pagination
DataGrid has a built-in paginator that is enabled by setting paginator option to true.

```xhtml
<p:dataGrid var="car" value="#{carBean.cars}" columns="3" rows="12" paginator="true">
    ...
</p:dataGrid>
```
## Paginator Template
Paginator is customized using paginatorTemplateOption that accepts various keys of UI controls.
**Note** that this section applies to dataGrid, dataList and dataTable.

- FirstPageLink
- LastPageLink
- PreviousPageLink
- NextPageLink
- PageLinks
- CurrentPageReport
- RowsPerPageDropdown
- JumpToPageDropdown
- JumpToPageInput

**Note** that _{RowsPerPageDropdown}_ has it’s own template, options to display is provided via
rowsPerPageTemplate attribute (e.g. rowsPerPageTemplate="9,12,15").

Also _{CurrentPageReport}_ has it’s own template defined with currentPageReportTemplate option.
You can use _{currentPage},{totalPages},{totalRecords},{startRecord},{endRecord}_ keyword
within currentPageReportTemplate. Default is "_{currentPage}_ of _{totalPages}_". Default UI is;

which corresponds to the following template.

- _{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink}_

Here are more examples based on different templates;

- _{CurrentPageReport} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}_

- _{PreviousPageLink} {CurrentPageReport} {NextPageLink}_

## Paginator Position
Paginator can be positoned using _paginatorPosition_ attribute in three different locations, "top",
"bottom" or "both" (default).


## Custom Content in Paginator
Custom content can be placed inside a paginator using a facet name matching a token in the
template.

```xhtml
<p:dataTable paginatorTemplate="{CurrentPageReport} {MyContent} ...">
    <f:facet name="{MyContent}">
        //Any content here
    </f:facet>
    //...
</p:dataTable>
```
## Selecting Data
Selection of data displayed in datagrid is very similar to row selection in datatable, you can access
the current data using the var reference. Here is an example to demonstrate how to select data from
datagrid and display within a dialog with ajax.

```xhtml
<h:form id="carForm">
    <p:dataGrid var="car" value="#{carBean.cars}" columns="3" rows="12">
        <p:panel header="#{car.model}">
            <p:commandLink update=":carForm:display" oncomplete="PF('dlg').show()">
                <f:setPropertyActionListener value="#{car}" target="#{carBean.selectedCar}"
                <h:outputText value="#{car.model}" />
            </p:commandLink>
        </p:panel>
    </p:dataGrid>
    <p:dialog modal="true" widgetVar="dlg">
        <h:panelGrid id="display" columns="2">
            <f:facet name="header">
                <p:graphicImage value="/images/cars/#{car.manufacturer}.jpg"/>
            </f:facet>
            <h:outputText value="Model:" />
            <h:outputText value="#{carBean.selectedCar.year}" />
            //more selectedCar properties
        </h:panelGrid>
    </p:dialog>
</h:form>
```
```java
public class CarBean {
    private List<Car> cars;
    private Car selectedCar;
    //getters and setters
}
```

## Layout Modes
DataGrid has two layout modes, "tabular" mode uses a table element and "grid" mode uses
PrimeFaces Grid CSS create a responsive ui.

On a smaller screen, grid mode adjusts the content for the optimal view.


## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| page | org.primefaces.event.data.PageEvent | On pagination.

```xhtml
<p:dataGrid var="car" value="#{carBean.model}">
    <p:ajax event="page" update=”anothercomponent" />
    //content
</p:dataGrid>
```

## Client Side API
Widget: _PrimeFaces.widget.DataGrid_


| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| getPaginator() | - | Paginator | Returns the paginator widget.

## Skinning
DataGrid resides in a main div container which style and styleClass attributes apply. Following is
the list of structural style classes;


| Class | Applies | 
| --- | --- | 
| .ui-datagrid | Main container element
| .ui-datagrid-content | Content container.
| .ui-datagrid-data | Table element containing data
| .ui-datagrid-row | A row in grid
| .ui-datagrid-column | A column in grid

As skinning style classes are global, see the main theming section for more information.

**Tips**:

- DataGrid supports lazy loading data via LazyDataModel, see DataTable lazy loading section.
- DataGrid provides two facets named _header_ and _footer_ that you can use to provide custom content
    at these locations.
